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
package io.github.jevaengine.script;

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Rect2F;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3D;
import io.github.jevaengine.math.Vector3F;

public final class ScriptableImmutableVariable implements IImmutableVariable
{
	private IImmutableVariable m_var;

	public ScriptableImmutableVariable(IImmutableVariable var)
	{
		m_var = var;
	}
	
	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		m_var.serialize(target);
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException
	{
		m_var.deserialize(source);
	}

	@Override
	public <T> T getValue(Class<T> cls) throws ValueSerializationException
	{
		return m_var.getValue(cls);
	}

	@Override
	public <T> T[] getValues(Class<T[]> cls) throws ValueSerializationException
	{
		return m_var.getValues(cls);
	}

	@Override
	public boolean childExists(String name)
	{
		return m_var.childExists(name);
	}

	@Override
	public ScriptableImmutableVariable getChild(String name) throws NoSuchChildVariableException
	{
		return new ScriptableImmutableVariable(m_var.getChild(name));
	}
	
	public Vector2D getValueVec2D() throws ValueSerializationException
	{
		return getValue(Vector2D.class);
	}
	
	public Vector2F getValueVec2F() throws ValueSerializationException
	{
		return getValue(Vector2F.class);
	}
	
	public Vector3D getValueVec3D() throws ValueSerializationException
	{
		return getValue(Vector3D.class);
	}
	
	public Vector3F getValueVec3F() throws ValueSerializationException
	{
		return getValue(Vector3F.class);
	}
	
	public Rect2D getValueRect2D() throws ValueSerializationException
	{
		return getValue(Rect2D.class);
	}
	
	public Rect2F getValueRect2F() throws ValueSerializationException
	{
		return getValue(Rect2F.class);
	}
	
	public int getValueInt() throws ValueSerializationException
	{
		return getValue(Integer.class);
	}
	
	public Integer[] getValueIntArray() throws ValueSerializationException
	{
		return getValues(Integer[].class);
	}
	
	public double getValueDouble() throws ValueSerializationException
	{
		return getValue(Double.class);
	}
	
	public boolean getValueBool() throws ValueSerializationException
	{
		return getValue(Boolean.class);
	}
	
	public String getValueString() throws ValueSerializationException
	{
		return getValue(String.class);
	}
	
	public String[] getValueStringArray() throws ValueSerializationException
	{
		return getValues(String[].class);
	}
	
	public String[] getChildren()
	{
		return m_var.getChildren();
	}
}
