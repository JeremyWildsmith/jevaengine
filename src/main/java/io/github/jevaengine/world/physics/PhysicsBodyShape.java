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
package io.github.jevaengine.world.physics;

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.math.Rect3F;

public class PhysicsBodyShape implements ISerializable
{
	public PhysicsBodyShapeType type = PhysicsBodyShapeType.Box;
	public Rect3F aabb = new Rect3F();

	public PhysicsBodyShape() { }
	
	public PhysicsBodyShape(PhysicsBodyShapeType type, Rect3F aabb)
	{
		this.type = type;
		this.aabb = new Rect3F(aabb);
	}
	
	public PhysicsBodyShape(PhysicsBodyShape s)
	{
		this(s.type, s.aabb);
	}
		
	public enum PhysicsBodyShapeType
	{
		Circle,
		Box,
	}

	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		target.addChild("aabb").setValue(aabb);
		target.addChild("type").setValue(type.ordinal());
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException {
		try
		{
			aabb = source.getChild("aabb").getValue(Rect3F.class);

			Integer shapeIndex = source.getChild("type").getValue(Integer.class);

			if(shapeIndex < 0 || shapeIndex > PhysicsBodyShapeType.values().length)
				throw new ValueSerializationException(new IndexOutOfBoundsException("type index is outside of bounds."));

			type = PhysicsBodyShapeType.values()[shapeIndex];		
		} catch (NoSuchChildVariableException e)
		{
			throw new ValueSerializationException(e);
		}
	}
}
