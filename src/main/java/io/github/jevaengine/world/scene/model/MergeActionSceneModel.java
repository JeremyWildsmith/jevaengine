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
import io.github.jevaengine.util.MutableProcessList;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;
import io.github.jevaengine.world.physics.PhysicsBodyShape.PhysicsBodyShapeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class MergeActionSceneModel implements IActionSceneModel {
	private final MutableProcessList<ISceneModel> m_models = new MutableProcessList<>();
	private final IActionSceneModel m_base;
	private final Observers m_observers = new Observers();

	public MergeActionSceneModel(IActionSceneModel base, ISceneModel... merge) {
		m_base = base;
		m_models.addAll(Arrays.asList(merge));
		m_models.add(base);
	}

	@Override
	public void dispose() {
		for (ISceneModel m : new ArrayList<>(m_models)) {
			remove(m);
			m.dispose();
		}
	}

	public void add(ISceneModel model) {
		//If there are other models in our merge set, align this one with them, otherwise just leave it.
		if (!m_models.isEmpty())
			model.setDirection(getDirection());

		m_models.add(model);
	}

	public void remove(ISceneModel model) {
		m_models.remove(model);
	}

	@Override
	public ISceneModel clone() {
		throw new SceneModelNotCloneableException();
	}

	@Override
	public Collection<ISceneModelComponent> getComponents(Matrix3X3 projection) {
		List<ISceneModelComponent> componentBuffer = new ArrayList<>();

		for (ISceneModel m : m_models) {
			for (ISceneModelComponent c : m.getComponents(projection)) {
				componentBuffer.add(c);
			}
		}

		return componentBuffer;
	}

	@Override
	public Rect3F getAABB() {
		Rect3F aabbs[] = new Rect3F[m_models.size()];

		for (int i = 0; i < m_models.size(); i++)
			aabbs[i] = m_models.get(i).getAABB();

		return Rect3F.getAABB(aabbs);
	}


	@Override
	public PhysicsBodyShape getBodyShape() {
		return new PhysicsBodyShape(PhysicsBodyShapeType.Box, getAABB());
	}

	@Override
	public void update(int deltaTime) {
		for (ISceneModel m : m_models)
			m.update(deltaTime);
	}

	@Override
	public Direction getDirection() {
		Direction d = null;

		for (ISceneModel m : m_models) {
			if (d == null || d == Direction.Zero)
				d = m.getDirection();
			else if (m.getDirection() != d)
				return Direction.Zero;
		}

		return d;
	}

	@Override
	public void setDirection(Direction direction) {
		Direction old = getDirection();
		for (ISceneModel m : m_models)
			m.setDirection(direction);

		if(old != getDirection()) {
			m_observers.raise(ISceneModelObserver.class).directionChanged();
		}
	}

	@Override
	public IObserverRegistry getObservers() {
		return m_observers;
	}

	@Override
	public IActionSceneModelAction getAction(String name) {
		return m_base.getAction(name);
	}

	@Override
	public boolean hasAction(String name) {
		return m_base.hasAction(name);
	}
}

