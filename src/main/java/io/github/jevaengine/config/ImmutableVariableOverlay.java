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
package io.github.jevaengine.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;

public final class ImmutableVariableOverlay implements IImmutableVariable {
	private IImmutableVariable m_overlay;
	private IImmutableVariable m_underlay;

	public ImmutableVariableOverlay(IImmutableVariable overlay, IImmutableVariable underlay) {
		m_overlay = overlay;
		m_underlay = underlay;
	}

	@Override
	public void serialize(IVariable target) throws ValueSerializationException {
		m_underlay.serialize(target);
		m_overlay.serialize(target);
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException {
		m_underlay.deserialize(source);
		m_overlay.deserialize(source);
	}

	@Override
	public <T> T getValue(Class<T> cls) throws ValueSerializationException {
		if (ISerializable.class.isAssignableFrom(cls)) {
			try {
				Constructor<T> constructor = cls.getDeclaredConstructor();
				constructor.setAccessible(true);

				T instance = constructor.newInstance();

				((ISerializable) instance).deserialize(this);

				return instance;
			} catch (NoSuchMethodException |
					SecurityException |
					IllegalAccessException |
					IllegalArgumentException ex) {
				throw new ValueSerializationException(ex);
			} catch (InstantiationException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		} else
			throw new ValueSerializationException(new IncompatibleValueTypeException());
	}

	@Override
	public <T> T[] getValues(Class<T[]> dest) throws ValueSerializationException {
		return m_overlay.getValues(dest);
	}


	@Override
	public boolean childExists(String name) {
		return m_underlay.childExists(name) || m_overlay.childExists(name);
	}

	@Override
	public IImmutableVariable getChild(String name) throws NoSuchChildVariableException {
		if (m_overlay.childExists(name))
			return m_overlay.getChild(name);
		else
			return m_underlay.getChild(name);
	}

	@Override
	public String[] getChildren() {
		HashSet<String> children = new HashSet<String>();
		children.addAll(Arrays.asList(m_overlay.getChildren()));
		children.addAll(Arrays.asList(m_underlay.getChildren()));

		return children.toArray(new String[children.size()]);
	}
}
