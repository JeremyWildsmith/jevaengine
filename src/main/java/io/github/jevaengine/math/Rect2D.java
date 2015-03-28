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

/**
 *
 * @author Jeremy
 */
public final class Rect2D implements ISerializable
{
	public int x;
	public int y;
	public int width;
	public int height;

	public Rect2D() { }
	
	public Rect2D(Rect2D r)
	{
		this(r.x, r.y, r.width, r.height);
	}
	
	public Rect2D(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Rect2D(int width, int height)
	{
		this(0, 0, width, height);
	}
	
	public Rect2D difference(Vector2D src)
	{
		return new Rect2D(x - src.x, y - src.y, width, height);
	}
	
	public Rect2F difference(Vector2F src)
	{
		return new Rect2F(x - src.x, y - src.y, width, height);
	}

	public Rect2D add(Vector2D v)
	{
		return new Rect2D(x + v.x, y + v.y, width, height);
	}
	
	public Rect2F add(Vector2F v)
	{
		return new Rect2F(x + v.x, y + v.y, width, height);
	}
	
	public Rect2D getOverlapping(Rect2D rect)
	{
		Vector2D upperLeft = new Vector2D(Math.max(x, rect.x), Math.max(y, rect.y));
		Vector2D dimensions = new Vector2D(Math.min(x + width, rect.x + rect.width), Math.min(y + height, rect.y + rect.height)).difference(upperLeft);
	
		if(dimensions.x < 0 || dimensions.y < 0)
			return new Rect2D();
		
		return new Rect2D(upperLeft.x, upperLeft.y, dimensions.x, dimensions.y);
	}
	
	
	public boolean intersects(Rect2D rect)
	{
		float xMax0 = x + width;
		float yMax0 = y + height;
		float xMax1 = rect.x + rect.width;
		float yMax1 = rect.y + rect.height;
		
		return (xMax0 > rect.x &&
				yMax0 > rect.y &&
				x < xMax1 &&
				y < yMax1);
	}
	
	public boolean contains(Vector2F location)
	{
		return (location.x >= x &&
				location.x - x <= width &&
				location.y >= y &&
				location.y - y <= height);
	}
	
	public boolean contains(Vector2D location)
	{
		return (location.x >= x &&
				location.x - x <= width &&
				location.y >= y &&
				location.y - y <= height);
	}
		
	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		if(x != 0 || y != 0)
		{
			target.addChild("x").setValue(x);
			target.addChild("y").setValue(y);
		}
		
		target.addChild("width").setValue(width);
		target.addChild("height").setValue(height);
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException
	{
		try
		{
			if(source.childExists("x"))
				this.x = source.getChild("x").getValue(Integer.class);
			
			if(source.childExists("y"))
				this.y = source.getChild("y").getValue(Integer.class);
			
			this.width = source.getChild("width").getValue(Integer.class);
			this.height = source.getChild("height").getValue(Integer.class);
		} catch(NoSuchChildVariableException e)
		{
			throw new ValueSerializationException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rect2D other = (Rect2D) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
