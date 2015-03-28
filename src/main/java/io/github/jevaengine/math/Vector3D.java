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
package io.github.jevaengine.math;

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.ValueSerializationException;

public final class Vector3D implements ISerializable
{
	public int x;
	public int y;
	public int z;

	public Vector3D()
	{
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vector3D(Vector2D _v, int _z)
	{
		x = _v.x;
		y = _v.y;
		z = _z;
	}

	public Vector3D(int _x, int _y, int _z)
	{
		x = _x;
		y = _y;
		z = _z;
	}
	
	public int getLengthSquared()
	{
		return x * x + y * y + z * z;
	}
	
	public float getLength()
	{
		return (float)Math.sqrt(getLengthSquared());
	}

	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Vector3D))
			return false;
		else if(o == this)
			return true;
		
		Vector3D other = (Vector3D)o;
		
		return other.x == x && other.y == y && other.z == z;
	}

	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		target.addChild("x").setValue(x);
		target.addChild("y").setValue(y);
		target.addChild("z").setValue(z);
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException
	{
		try
		{
			x = source.getChild("x").getValue(Integer.class);
			y = source.getChild("y").getValue(Integer.class);
			z = source.getChild("z").getValue(Integer.class);
		} catch(NoSuchChildVariableException e)
		{
			throw new ValueSerializationException(e);
		}
	}
}
