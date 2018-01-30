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

import io.github.jevaengine.config.*;

public class Rect3F implements ISerializable {
	public static final float TOLERANCE = 0.0000001F;

	public float x;
	public float y;
	public float z;

	public float width;
	public float height;
	public float depth;

	public Rect3F(Rect3D r) {
		x = r.x;
		y = r.y;
		z = r.z;

		width = r.width;
		height = r.height;
		depth = r.depth;
	}

	public Rect3F(Vector3F location, float _width, float _height, float _depth) {
		x = location.x;
		y = location.y;
		z = location.z;

		width = _width;
		height = _height;
		depth = _depth;
	}

	public Rect3F(float _x, float _y, float _z, float _width, float _height, float _depth) {
		x = _x;
		y = _y;
		z = _z;

		width = _width;
		height = _height;
		depth = _depth;
	}

	public Rect3F(float _width, float _height) {
		this(_width, _height, 0);
	}

	public Rect3F(float _width, float _height, float _depth) {
		width = _width;
		height = _height;
		depth = _depth;
	}

	public Rect3F(Rect3F src) {
		x = src.x;
		y = src.y;
		z = src.z;

		width = src.width;
		height = src.height;
		depth = src.depth;
	}

	public Rect3F() {
		x = 0;
		y = 0;
		z = 0;

		width = 0;
		height = 0;
		depth = 0;
	}

	public static Rect3F getAABB(Rect3F... rects) {
		if (rects.length == 0)
			return new Rect3F();

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minZ = Float.MAX_VALUE;

		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		float maxZ = Float.MIN_VALUE;

		for (Rect3F r : rects) {
			minX = Math.min(minX, r.x);
			minY = Math.min(minY, r.y);
			minZ = Math.min(minZ, r.z);

			maxX = Math.max(maxX, r.x + r.width);
			maxY = Math.max(maxY, r.y + r.height);
			maxZ = Math.max(maxZ, r.z + r.depth);
		}

		return new Rect3F(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
	}

	public boolean hasVolume() {
		return width > TOLERANCE && height > TOLERANCE && depth > TOLERANCE;
	}

	public Vector3F min() {
		return getPoint(0, 0, 0);
	}

	public Vector3F max() {
		return getPoint(1.0F, 1.0F, 1.0F);
	}

	public Vector3F getPoint(float widthRatio, float heightRatio, float depthRatio) {
		return new Vector3F(x + width * widthRatio, y + height * heightRatio, z + depth * depthRatio);
	}

	public Circle3F getBoundingCircle() {
		return new Circle3F(x, y, z, Math.max(depth, Math.max(width, height)) / 2.0F);
	}

	public Rect2F getXy() {
		return new Rect2F(x, y, width, height);
	}

	public Rect3F add(Vector3F v) {
		return new Rect3F(x + v.x, y + v.y, z + v.z, width, height, depth);
	}

	public boolean contains(Vector3F location) {
		return (location.x >= x &&
				location.x - x <= width &&
				location.y >= y &&
				location.y - y <= height &&
				location.z >= z &&
				location.z - z <= depth);
	}

	@Override
	public void serialize(IVariable target) throws ValueSerializationException {
		target.addChild("x").setValue(x);
		target.addChild("y").setValue(y);
		target.addChild("z").setValue(z);

		target.addChild("width").setValue(width);
		target.addChild("height").setValue(height);
		target.addChild("depth").setValue(depth);
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException {
		try {
			if (source.childExists("x"))
				this.x = source.getChild("x").getValue(Double.class).floatValue();

			if (source.childExists("y"))
				this.y = source.getChild("y").getValue(Double.class).floatValue();

			if (source.childExists("z"))
				this.z = source.getChild("z").getValue(Double.class).floatValue();

			this.width = source.getChild("width").getValue(Double.class).floatValue();
			this.height = source.getChild("height").getValue(Double.class).floatValue();
			this.depth = source.getChild("depth").getValue(Double.class).floatValue();
		} catch (NoSuchChildVariableException e) {
			throw new ValueSerializationException(e);
		}
	}
}
