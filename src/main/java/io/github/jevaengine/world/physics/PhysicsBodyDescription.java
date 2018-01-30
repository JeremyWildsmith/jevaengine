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

import io.github.jevaengine.config.*;

public final class PhysicsBodyDescription implements ISerializable {
	public PhysicsBodyType type = PhysicsBodyType.Static;
	public PhysicsBodyShape shape = new PhysicsBodyShape();
	public float density;
	public boolean isFixedRotation;
	public boolean isSensor;
	public float friction;
	public Class<?>[] collisionExceptions;

	public PhysicsBodyDescription() {
	}

	public PhysicsBodyDescription(PhysicsBodyDescription d) {
		type = d.type;
		shape = new PhysicsBodyShape(d.shape);
		density = d.density;
		isFixedRotation = d.isFixedRotation;
		isSensor = d.isSensor;
		friction = d.friction;
		collisionExceptions = d.collisionExceptions;
	}

	public PhysicsBodyDescription(PhysicsBodyType _type, PhysicsBodyShape _shape, float _density, boolean _isFixedRotation, boolean _isSensor, float _friction, Class<?>... _collisionExceptions) {
		type = _type;
		shape = _shape;
		density = _density;
		isFixedRotation = _isFixedRotation;
		isSensor = _isSensor;
		friction = _friction;
		collisionExceptions = _collisionExceptions;
	}

	@Override
	public void serialize(IVariable target) throws ValueSerializationException {
		target.addChild("type").setValue(type.ordinal());
		target.addChild("shape").setValue(shape);
		target.addChild("density").setValue(density);
		target.addChild("isFixedRotation").setValue(isFixedRotation);
		target.addChild("isSensor").setValue(isSensor);
		target.addChild("friction").setValue("friction");
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException {
		try {
			shape = source.getChild("shape").getValue(PhysicsBodyShape.class);
			density = source.getChild("density").getValue(Double.class).floatValue();

			if (source.childExists("isFixedRotation"))
				isFixedRotation = source.getChild("isFixedRotation").getValue(Boolean.class);

			if (source.childExists("isSensor"))
				isSensor = source.getChild("isSensor").getValue(Boolean.class);

			friction = source.getChild("friction").getValue(Double.class).floatValue();
		} catch (NoSuchChildVariableException e) {
			throw new ValueSerializationException(e);
		}
	}

	public enum PhysicsBodyType {
		Static,
		Dynamic,
	}
}
