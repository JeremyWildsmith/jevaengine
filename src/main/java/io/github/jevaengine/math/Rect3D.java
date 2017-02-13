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

public class Rect3D implements ISerializable
{
	public static final float TOLERANCE = 0.0000001F;
	
	public int x;
	public int y;
	public int z;
	
	public int width;
	public int height;
	public int depth;
	
	public Rect3D(Vector3D location, int _width, int _height, int _depth)
	{
		x = location.x;
		y = location.y;
		z = location .z;
		
		width = _width;
		height = _height;
		depth = _depth;
	}
	
	public Rect3D(int _x, int _y, int _z, int _width, int _height, int _depth)
	{
		x = _x;
		y = _y;
		z = _z;
		
		width = _width;
		height = _height;
		depth = _depth;
	}
	
	public Rect3D(int _width, int _height)
	{
		this(_width, _height, 0);
	}
	
	public Rect3D(int _width, int _height, int _depth)
	{
		width = _width;
		height = _height;
		depth = _depth;
	}
	
	public Rect3D(Rect3D src)
	{
		x = src.x;
		y = src.y;
		z = src.z;
		
		width = src.width;
		height = src.height;
		depth = src.depth;
	}
	
	public Rect3D()
	{
		x = 0;
		y = 0;
		z = 0;
		
		width = 0;
		height = 0;
		depth = 0;
	}
	
	public boolean hasVolume()
	{
		return width > TOLERANCE && height > TOLERANCE && depth > TOLERANCE;
	}
	
	public Vector3F min()
	{
		return getPoint(0, 0, 0);
	}
	
	public Vector3F max()
	{
		return getPoint(1.0F, 1.0F, 1.0F);
	}
	
	public Vector3F getPoint(float widthRatio, float heightRatio, float depthRatio)
	{
		return new Vector3F(x + width * widthRatio, y + height * heightRatio, z + depth * depthRatio);
	}
	
	public Circle3F getBoundingCircle()
	{
		return new Circle3F(x, y, z, Math.max(depth, Math.max(width, height)) / 2.0F);
	}
	
	public Rect2F getXy()
	{
		return new Rect2F(x, y, width, height);
	}
	
	public Rect3D add(Vector3D v)
	{
		return new Rect3D(x + v.x, y + v.y, z + v.z, width, height, depth);
	}
	
	public boolean contains(Vector3D location)
	{
		return (location.x >= x &&
				location.x - x <= width &&
				location.y >= y &&
				location.y - y <= height &&
				location.z >= z &&
				location.z - z <= depth);
	}
	
	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		target.addChild("x").setValue(x);
		target.addChild("y").setValue(y);
		target.addChild("z").setValue(z);
		
		target.addChild("width").setValue(width);
		target.addChild("height").setValue(height);
		target.addChild("depth").setValue(depth);
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
			
			if(source.childExists("z"))
				this.z = source.getChild("z").getValue(Integer.class);
			
			this.width = source.getChild("width").getValue(Integer.class);
			this.height = source.getChild("height").getValue(Integer.class);
			this.depth = source.getChild("depth").getValue(Integer.class);
		} catch(NoSuchChildVariableException e)
		{
			throw new ValueSerializationException(e);
		}
	}
	
	public static Rect3D getAABB(Rect3D ... rects)
	{
		if(rects.length == 0)
			return new Rect3D();
		
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;
		
		for(Rect3D r : rects)
		{
			minX = Math.min(minX, r.x);
			minY = Math.min(minY, r.y);
			minZ = Math.min(minZ, r.z);
			
			maxX = Math.max(maxX, r.x + r.width);
			maxY = Math.max(maxY, r.y + r.height);
			maxZ = Math.max(maxZ, r.z + r.depth);
		}
		
		return new Rect3D(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
	}
}
