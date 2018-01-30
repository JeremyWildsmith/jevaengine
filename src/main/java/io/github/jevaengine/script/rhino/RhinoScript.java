/*
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.github.jevaengine.script.rhino;

import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScript;
import io.github.jevaengine.script.ScriptExecuteException;
import io.github.jevaengine.script.ScriptHiddenMember;
import io.github.jevaengine.util.Nullable;
import org.mozilla.javascript.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class RhinoScript implements IScript {
	static {
		ContextFactory.initGlobal(new ProtectedContextFactory());
	}

	private final Logger m_logger = LoggerFactory.getLogger(RhinoScript.class);

	private ScriptableObject m_scope;

	private void initEngine() {
		if (m_scope == null) {
			Context context = ContextFactory.getGlobal().enterContext();
			m_scope = context.initStandardObjects();

			try {
				ScriptableObject.defineClass(m_scope, RhinoQueue.class);
			} catch (IllegalAccessException | InstantiationException
					| InvocationTargetException e) {
				m_logger.error("Unable to define Rhino queue class. Resuming without definition.", e);
			}

			Context.exit();
		}
	}

	public final Scriptable getScriptedInterface() {
		initEngine();
		return m_scope;
	}

	public void put(String name, Object o) {
		initEngine();
		m_scope.putConst(name, m_scope, o);
	}

	@Override
	public IFunctionFactory getFunctionFactory() {
		return new RhinoFunctionFactory();
	}

	@Override
	@Nullable
	public final Object evaluate(String expression) throws ScriptExecuteException {
		initEngine();
		Context context = ContextFactory.getGlobal().enterContext();

		try {
			Object returnValue = context.evaluateString(m_scope, expression, "JevaEngine", 0, null);

			return returnValue instanceof Undefined ? null : returnValue;
		} catch (RhinoException e) {
			throw new RhinoScriptException(e);
		} finally {
			Context.exit();
		}
	}

	private static class ProtectedContextFactory extends ContextFactory {
		private static final ProtectedWrapFactory wrapper = new ProtectedWrapFactory();

		@Override
		protected Context makeContext() {
			Context c = super.makeContext();
			c.setWrapFactory(wrapper);

			return c;
		}
	}

	private static class ProtectedWrapFactory extends WrapFactory {
		@Override
		public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
			return new ProtectedNativeJavaObject(scope, javaObject, staticType);
		}
	}

	private static class ProtectedNativeJavaObject extends NativeJavaObject {
		private static final long serialVersionUID = 1L;

		private static final HashMap<Class<?>, ArrayList<String>> CLASS_PROTECTION_CACHE = new HashMap<Class<?>, ArrayList<String>>();

		private ArrayList<String> m_protectedMembers;

		public ProtectedNativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
			super(scope, javaObject, staticType);

			Class<?> clazz = javaObject != null ? javaObject.getClass() : staticType;

			m_protectedMembers = CLASS_PROTECTION_CACHE.get(clazz);

			if (m_protectedMembers == null)
				m_protectedMembers = processClass(clazz);
		}

		private static ArrayList<String> processClass(Class<?> clazz) {
			ArrayList<String> protectedMethods = new ArrayList<String>();

			CLASS_PROTECTION_CACHE.put(clazz, protectedMethods);

			for (Method m : clazz.getMethods()) {
				if (m.getAnnotation(ScriptHiddenMember.class) != null)
					protectedMethods.add(m.getName());
			}

			for (Field f : clazz.getFields()) {
				if (f.getAnnotation(ScriptHiddenMember.class) != null)
					protectedMethods.add(f.getName());
			}
			return protectedMethods;
		}

		@Override
		public boolean has(String name, Scriptable start) {
			if (m_protectedMembers.contains(name))
				return false;
			else
				return super.has(name, start);
		}

		@Override
		public Object get(String name, Scriptable start) {
			if (m_protectedMembers.contains(name))
				return NOT_FOUND;
			else
				return super.get(name, start);
		}
	}
}
