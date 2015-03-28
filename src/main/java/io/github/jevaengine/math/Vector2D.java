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

public final class Vector2D implements ISerializable
{
	public int x;
	public int y;

	public Vector2D(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
	
	public Vector2D(Vector2D location)
	{
		this(location.x, location.y);
	}
	
	public Vector2D()
	{
		this(0,0);
	}

	public Vector2D add(Vector2D a)
	{
		return new Vector2D(x + a.x, y + a.y);
	}
	
	public Vector2D multiply(int scale)
	{
		return new Vector2D(x * scale, y * scale);
	}

	public float getLengthSquared()
	{
		return x * x + y * y;
	}

	public float getAngle()
	{
		return (float)Math.atan2(y, x);
	}
	
	public float getLength()
	{
		return (float) Math.sqrt(getLengthSquared());
	}

	public boolean isZero()
	{
		return (x == 0 && y == 0);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Vector2D))
			return false;
		else if (this == o)
			return true;

		Vector2D vec = (Vector2D) o;

		return (vec.x == x && vec.y == y);
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 83 * hash + this.x;
		hash = 83 * hash + this.y;
		return hash;
	}

	public Vector2D difference(Vector2D v)
	{
		return new Vector2D(x - v.x, y - v.y);
	}
	
	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		target.addChild("x").setValue(x);
		target.addChild("y").setValue(y);
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException
	{
		try
		{
			x = source.getChild("x").getValue(Integer.class);
			y = source.getChild("y").getValue(Integer.class);
		} catch(NoSuchChildVariableException e)
		{
			throw new ValueSerializationException(e);
		}
	}
}
