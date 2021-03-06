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
package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;

import java.util.ArrayList;
import java.util.List;

public final class NullSceneModel implements ISceneModel {

	@Override
	public IObserverRegistry getObservers() {
		return new Observers();
	}

	@Override
	public void dispose() {
	}

	public NullSceneModel clone() {
		return new NullSceneModel();
	}

	@Override
	public List<ISceneModelComponent> getComponents(Matrix3X3 projection) {
		return new ArrayList<>();
	}

	@Override
	public Rect3F getAABB() {
		return new Rect3F();
	}

	@Override
	public PhysicsBodyShape getBodyShape() {
		return new PhysicsBodyShape();
	}

	@Override
	public void update(int deltaTime) {
	}

	@Override
	public Direction getDirection() {
		return Direction.Zero;
	}

	@Override
	public void setDirection(Direction direction) {
	}
}
