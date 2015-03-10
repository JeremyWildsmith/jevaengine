/*******************************************************************************
 * Copyright (c) 2013 Jeremy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * If you'd like to obtain a another license to this code, you may contact Jeremy to discuss alternative redistribution options.
 * 
 * Contributors:
 *     Jeremy - initial API and implementation
 ******************************************************************************/
package io.github.jevaengine.config.json;

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.IncompatibleValueTypeException;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.UnsupportedValueTypeException;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.util.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public final class JsonVariable implements IVariable
{
	@Nullable
	private Object m_value;
	
	public JsonVariable()
	{
	}
	
	private JsonVariable(Object value)
	{
		m_value = value;
	}
	
	@Override
	public void setValue(ISerializable value) throws ValueSerializationException
	{
		JsonVariable dest = new JsonVariable();
		
		value.serialize(dest);
		
		m_value = dest;
	}
	
	@Override
	public void setValue(ISerializable[] value) throws ValueSerializationException
	{
		
		JsonVariable[] dest = new JsonVariable[value.length];
		
		for(int i = 0; i < value.length; i++)
		{
			dest[i] = new JsonVariable();
			dest[i].setValue(value[i]);
		}
		
		m_value = dest;
	}
	
	@Override
	public void setValue(String value)
	{
		m_value = value;
	}

	@Override
	public void setValue(String[] value)
	{
		m_value = value;
	}

	@Override
	public void setValue(int value)
	{
		m_value = value;
	}

	@Override
	public void setValue(int[] value)
	{
		m_value = value;
	}

	@Override
	public void setValue(double value)
	{
		m_value = value;
	}

	@Override
	public void setValue(double[] value)
	{
		m_value = value;
	}

	@Override
	public void setValue(boolean value)
	{
		m_value = value;
	}

	@Override
	public void setValue(boolean[] value)
	{
		m_value = value;
	}
	
	private static boolean isPrimitiveBoxed(Object o)
	{
		return (o instanceof Float ||
			o instanceof Integer ||
			o instanceof Boolean ||
			o instanceof Character ||
			o instanceof String ||
			o instanceof Long ||
			o instanceof Short ||
			o instanceof Double);
	}
	
	@Override
	public void setValue(Object value) throws ValueSerializationException
	{
		if(value.getClass().isPrimitive() || isPrimitiveBoxed(value))
			m_value = value;
		else if(value.getClass().isArray() && (((Object[])value).length == 0 || isPrimitiveBoxed(((Object[])value)[0])))
			m_value = value;
		else if(value instanceof ISerializable)
			setValue((ISerializable)value);
		else if(value instanceof ISerializable[])
			setValue((ISerializable[])value);
		else 
			throw new ValueSerializationException(new UnsupportedValueTypeException());
	}

	@Override
	@SuppressWarnings("unchecked") //Safe to assume Map is Map<String, JsonVariable> as used internally
	public boolean childExists(String name)
	{
		if(m_value instanceof JsonVariable)
			return ((JsonVariable)m_value).childExists(name);
		else if(m_value instanceof Map)
		{
			JsonVariable value = ((Map<String, JsonVariable>)m_value).get(name);
			return value != null;
		}else
			return false;
	}
	
	@Override
	@SuppressWarnings("unchecked") //Safe to assume Map is Map<String, JsonVariable> as used internally
	public JsonVariable getChild(String name) throws NoSuchChildVariableException
	{
		if(m_value instanceof JsonVariable)
			return ((JsonVariable)m_value).getChild(name);
		else if(m_value instanceof Map)
		{
			JsonVariable value = ((Map<String, JsonVariable>)m_value).get(name);
			
			if(value == null)
				throw new NoSuchChildVariableException(name);
			
			return value;
		} else
			throw new NoSuchChildVariableException(name);
	}
	
	@Override
	@SuppressWarnings("unchecked") //Safe to assume Map is Map<String, JsonVariable> as used internally
	public String[] getChildren()
	{
		if(m_value instanceof JsonVariable)
			return ((JsonVariable)m_value).getChildren();
		else if(m_value instanceof Map)
		{
			Set<String> keys = ((Map<String, JsonVariable>)m_value).keySet();
			return keys.toArray(new String[keys.size()]);
		}else
			return new String[0];
	}
	
	@Override
	@SuppressWarnings("unchecked") //Safe to assume Map is Map<String, JsonVariable> as used internally.
	public JsonVariable addChild(String name)
	{
		if(m_value instanceof JsonVariable)
			return ((JsonVariable)m_value).addChild(name);
		else
		{
			if(!(m_value instanceof Map))
				m_value = new HashMap<String, JsonVariable>();
		
			JsonVariable newChild = new JsonVariable();
		
			((Map<String, JsonVariable>)m_value).put(name, newChild);
		
			return newChild;
		}
	}

	@SuppressWarnings("unchecked") //Safe to assume Map is Map<String, JsonVariable> as used internally
	@Override
	public void removeChild(String name)
	{
		if(!(m_value instanceof Map))
			return;
		
		((Map<String, JsonVariable>)m_value).remove(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(Class<T> cls) throws ValueSerializationException
	{
		if(m_value == null || m_value instanceof Map)
			throw new ValueSerializationException(new IncompatibleValueTypeException());
		
		if(ISerializable.class.isAssignableFrom(cls))
		{
			try
			{
				Constructor<T> constructor = cls.getDeclaredConstructor();
				constructor.setAccessible(true);
				
				T instance = constructor.newInstance();
				
				((ISerializable)instance).deserialize(m_value instanceof JsonVariable ? (JsonVariable) m_value : new JsonVariable(m_value));
				
				return instance;
			} catch (NoSuchMethodException | 
						SecurityException | 
						IllegalAccessException |
						IllegalArgumentException ex)
			{
				throw new ValueSerializationException(ex);
			} catch (InstantiationException | InvocationTargetException ex)
			{
				throw new ValueSerializationException(ex);
			}

		} else if(cls.isAssignableFrom(m_value.getClass()))
		{
			return (T)m_value;
		} else if(cls == Double.class && m_value.getClass().equals(Integer.class))
		{
			return (T)new Double(((Integer)m_value).doubleValue());
		}
		else if(cls.isArray() && m_value.getClass().isArray() && ((Object[])m_value).length == 0)
			return (T)Array.newInstance(cls.getComponentType(), 0);
		else
			throw new ValueSerializationException(new UnsupportedValueTypeException());
	}
	
	private boolean isPrimitive(Object value)
	{
		return (value instanceof Boolean ||
			value instanceof Double ||
			value instanceof Integer ||
			value instanceof String ||
			value instanceof Float);
	}
	
	private boolean isPrimitiveType(Class<?> value)
	{
		return (value.equals(Boolean.class) ||
			value.equals(Double.class) ||
			value.equals(Integer.class) ||
			value.equals(String.class) ||
			value.equals(Float.class));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] getValues(Class<T[]> dest) throws ValueSerializationException
	{
		if(m_value == null)
			throw new ValueSerializationException(new IncompatibleValueTypeException());
		
		if(dest.getClass().isAssignableFrom(m_value.getClass()))
			return (T[])m_value;
		else if(m_value instanceof Object[] && 
			(ISerializable.class.isAssignableFrom(dest.getComponentType()) || isPrimitiveType(dest.getComponentType())))
		{
			try
			{
				Object[] rawValues = (Object[])m_value;
				ArrayList<T> deserializedValues = new ArrayList<T>();
				
				for(Object rawValue : rawValues)
				{
					if(isPrimitive(rawValue))
					{
						deserializedValues.add((T)rawValue);
					}else if(rawValue instanceof JsonVariable)
					{
						Constructor<?> constructor = dest.getComponentType().getDeclaredConstructor();
						constructor.setAccessible(true);

						ISerializable bean = (ISerializable)constructor.newInstance();
						bean.deserialize((JsonVariable)rawValue);
						deserializedValues.add((T)bean);
					}else
						throw new ValueSerializationException(new UnsupportedValueTypeException());
				}
				
				T[] returnBuffer = dest.cast(Array.newInstance(dest.getComponentType(), deserializedValues.size()));
				
				return deserializedValues.toArray(returnBuffer);
				
			} catch (NoSuchMethodException | 
						SecurityException | 
						IllegalAccessException |
						IllegalArgumentException ex)
			{
				throw new ValueSerializationException(ex);
			} catch (InstantiationException | InvocationTargetException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		else
			throw new ValueSerializationException(new UnsupportedValueTypeException());
	}
	
	private static Object[] parseArray(JsonNode node) throws ValueSerializationException
	{
		ArrayList<Object> array = new ArrayList<Object>();
		
		for(int i = 0; node.get(i) != null; i++)
		{
			JsonNode element = node.get(i);
			
			array.add(parse(element));
		}
		
		return array.toArray(new Object[array.size()]);
	}
	
	public static Object parse(JsonNode current) throws ValueSerializationException
	{
		if(current.isBoolean())
			return current.asBoolean();
		else if(current.isInt())
			return current.asInt();
		else if(current.isNull())
			return null;
		else if(current.isDouble())
			return current.asDouble();
		else if(current.isTextual())
			return current.asText();
		else if(current.isArray())
			return parseArray(current);
		else if(current.isObject())
		{
			JsonVariable object = new JsonVariable();
			
			Iterator<Entry<String, JsonNode>> elements = current.getFields();
			
			while(elements.hasNext())
			{
				Entry<String, JsonNode> jsonChild = elements.next();
				
				JsonVariable childVar = object.addChild(jsonChild.getKey());
				childVar.m_value = parse(jsonChild.getValue());
			}
			
			return object;
		}else
			throw new ValueSerializationException(new UnsupportedValueTypeException());
	}
	
	public static JsonVariable create(InputStream is) throws IOException, ValueSerializationException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode jsonRoot = mapper.readTree(is);
		JsonVariable root = new JsonVariable();
		
		root.m_value = parse(jsonRoot);
		
		return root;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		if(m_value == null)
			return;
		
		if(m_value instanceof Map)
		{
			for(Map.Entry<String, IImmutableVariable> e : ((Map<String, IImmutableVariable>)m_value).entrySet())
			{
				e.getValue().serialize(target.addChild(e.getKey()));
			}
		}
		else
			target.setValue(m_value);
	}

	@Override
	public void deserialize(IImmutableVariable source)
	{
		m_value = source;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	private Object serialize(JsonVariable variable)
	{
		if(variable.m_value == null)
			return null;
		else if(variable.m_value instanceof Map)
		{
			Map<String, Object> jsonMapping = new HashMap<String, Object>();

			Map<String, JsonVariable> valueMapping = (Map<String, JsonVariable>)variable.m_value;

			for(Map.Entry<String, JsonVariable> e : valueMapping.entrySet())
			{
				jsonMapping.put(e.getKey(), serialize(e.getValue()));
			}
			
			return jsonMapping;
		}else if(variable.m_value instanceof JsonVariable)
			return serialize((JsonVariable)variable.m_value);
		else if(variable.m_value instanceof JsonVariable[])
		{
			JsonVariable[] source = (JsonVariable[])variable.m_value;
			ArrayList<Object> jsonList = new ArrayList<Object>();
			
			for (JsonVariable component : source)
				jsonList.add(serialize(component));
			
			return jsonList;
		}else
			return variable.m_value;
	}
	
	@SuppressWarnings("unchecked")
	public void serialize(OutputStream out, boolean pretty) throws IOException, ValueSerializationException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		if(m_value == null)
			mapper.writeValue(out, new HashMap<>());
		else if(m_value instanceof JsonVariable)
			((JsonVariable)m_value).serialize(out, pretty);
		else if(!(m_value instanceof Map))
			throw new ValueSerializationException(new UnsupportedValueTypeException());
		else
		{
			HashMap<String, Object> mapping = (HashMap<String, Object>)serialize(this);

			if(pretty)
				mapper.writerWithDefaultPrettyPrinter().writeValue(out, mapping);
			else
				mapper.writeValue(out, mapping);
		}
	}
}
