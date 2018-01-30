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
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DecoratedSceneModel implements ISceneModel {
	private final ISceneModel m_model;

	private final ArrayList<ISceneModelComponent> m_additionalComponents = new ArrayList<>();

	public DecoratedSceneModel(ISceneModel model, ISceneModelComponent... additionalComponents) {
		this(model, Arrays.asList(additionalComponents));
	}

	public DecoratedSceneModel(ISceneModel model, List<ISceneModelComponent> additionalComponents) {
		m_model = model;
		m_additionalComponents.addAll(additionalComponents);
	}

	public DecoratedSceneModel(ISceneModel model) {
		m_model = model;
	}

	@Override
	public void dispose() {
		m_model.dispose();
	}

	@Override
	public DecoratedSceneModel clone() {
		return new DecoratedSceneModel(m_model, m_additionalComponents);
	}

	@Override
	public PhysicsBodyShape getBodyShape() {
		return m_model.getBodyShape();
	}

	@Override
	public List<ISceneModelComponent> getComponents(Matrix3X3 projection) {
		ArrayList<ISceneModelComponent> components = new ArrayList<>();

		components.addAll(m_model.getComponents(projection));
		components.addAll(m_additionalComponents);

		return components;
	}

	@Override
	public Rect3F getAABB() {
		return m_model.getAABB();
	}

	@Override
	public void update(int deltaTime) {
		m_model.update(deltaTime);
	}

	@Override
	public Direction getDirection() {
		return m_model.getDirection();
	}

	@Override
	public void setDirection(Direction direction) {
		m_model.setDirection(direction);
	}

	public void add(ISceneModelComponent component) {
		m_additionalComponents.add(component);
	}

	public void remove(ISceneModelComponent component) {
		m_additionalComponents.remove(component);
	}
}
