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

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.IncompatibleValueTypeException;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.NullVariable;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.config.json.JsonVariable;
import io.github.jevaengine.script.ScriptableImmutableVariable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jeremy
 */
public final class RhinoUtility
{
	private final Logger m_logger = LoggerFactory.getLogger(RhinoUtility.class);
	
	public ScriptableImmutableVariable asVariable(Object o)
	{
		if(!(o instanceof NativeObject))
		{
			m_logger.error("Provided object is not a valid NativeObject. Assuming null object.");
			return new ScriptableImmutableVariable(new NullVariable());
		}
		
		NativeObject nativeObject = (NativeObject)o;
		
		return new ScriptableImmutableVariable(new RhinoVariable(nativeObject));
	}
	
	public static final class RhinoVariable implements IImmutableVariable
	{
		private final Map<String, Object> m_values = new HashMap<>();
		private Object m_value = null;
		
		public RhinoVariable(Object value)
		{
			m_value = value;
		}
		
		public RhinoVariable(NativeObject source)
		{
			for(Object id : NativeObject.getPropertyIds(source))
			{
				m_values.put(id.toString(), NativeObject.getProperty(source, id.toString()));
			}
		}

		private Double parseDouble(Object o)
		{
			//So the engineers of Rhino decided that all numbers in JavaScript are Doubles (as per standard)
			//Except for *random* sometimes, where they're an integer (Which is not standard.) So here I am putting this dumb method
			//to wrap it with some predictable standard. Thanks Rhino.
			
			if(o instanceof Integer)
				return new Double((Integer)o);
			else if(o instanceof Float)
				return new Double((Float)o);
			else
				return (Double)o;
		}
		
		@Override
		public <T> T getValue(Class<T> cls) throws ValueSerializationException
		{
			try	
			{
				if(ISerializable.class.isAssignableFrom(cls))
				{
					Constructor<T> constructor = cls.getDeclaredConstructor();
					constructor.setAccessible(true);

					T instance = constructor.newInstance();

					((ISerializable)instance).deserialize(this);

					return instance;
				}
				else if(m_value == null)
				 throw new ValueSerializationException(new IncompatibleValueTypeException());
				else if(cls.equals(Integer.class))
					return (T)new Integer((parseDouble(m_value)).intValue());
				else if(cls.equals(Float.class))
					return (T)new Float((parseDouble(m_value)).floatValue());
				else if(cls.equals(Double.class))
					return (T)parseDouble(m_value);
				
				return cls.cast(m_value);
			} catch (InstantiationException | 
							IllegalAccessException | 
							IllegalArgumentException | 
							ClassCastException |
							InvocationTargetException |
							NoSuchMethodException e)
			{
				throw new ValueSerializationException(e);
			}
		}

		@Override
		public <T> T[] getValues(Class<T[]> cls) throws ValueSerializationException
		{
			if(m_value == null || !(m_value instanceof NativeArray))
				throw new ValueSerializationException(new IncompatibleValueTypeException());
			
			try
			{
				NativeArray rawValues = (NativeArray)m_value;
				T[] returnBuffer = cls.cast(Array.newInstance(cls.getComponentType(), (int)rawValues.getLength()));

				for(int i = 0; i < rawValues.getLength(); i++)
				{
					Object rawValue = rawValues.get(i, null);
					T valueBuffer;
					
					if(cls.getComponentType().equals(Integer.class))
						valueBuffer = (T)new Integer((parseDouble(rawValue)).intValue());
					else if(cls.getComponentType().equals(Float.class))
						valueBuffer = (T)new Float((parseDouble(rawValue)).floatValue());
					else
						valueBuffer = (T)cls.getComponentType().cast(rawValue);
				
					returnBuffer[i] = valueBuffer;
				}
				
				return returnBuffer;
			} catch (ClassCastException e)
			{
				throw new ValueSerializationException(e);
			}
		}

		@Override
		public boolean childExists(String name)
		{
			return m_values.containsKey(name);
		}

		@Override
		public IImmutableVariable getChild(String name) throws NoSuchChildVariableException
		{
			if(!m_values.containsKey(name))
				throw new NoSuchChildVariableException(name);
			
			return new RhinoVariable(m_values.get(name));
		}

		@Override
		public String[] getChildren()
		{
			Set<String> children = m_values.keySet();
			
			return children.toArray(new String[children.size()]);
		}

		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			for(Map.Entry<String, Object> e : m_values.entrySet())
				target.addChild(e.getKey()).setValue(e.getValue());
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException { }
	}
}
