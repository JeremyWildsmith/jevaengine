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

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;

import java.util.Collection;

public interface IImmutableSceneModel extends Cloneable {
	ISceneModel clone() throws SceneModelNotCloneableException;

	Collection<ISceneModelComponent> getComponents(Matrix3X3 projection);

	Rect3F getAABB();

	Direction getDirection();

	PhysicsBodyShape getBodyShape();

	public interface ISceneModelComponent extends IRenderable {
		String getName();

		boolean testPick(int x, int y, float scale);

		Rect3F getBounds();

		Vector3F getOrigin();
	}

	public static final class SceneModelNotCloneableException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public SceneModelNotCloneableException() {
		}

		public SceneModelNotCloneableException(Exception cause) {
			super(cause);
		}
	}
}
