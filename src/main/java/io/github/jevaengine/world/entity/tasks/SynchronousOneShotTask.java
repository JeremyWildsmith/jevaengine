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
package io.github.jevaengine.world.entity.tasks;

import io.github.jevaengine.world.entity.IEntity;

public abstract class SynchronousOneShotTask implements ITask {

	private IEntity m_entity;
	private boolean m_queryCancel = false;

	@Override
	public final void cancel() {
		m_queryCancel = true;
	}


	@Override
	public final void begin(IEntity entity) {
		m_entity = entity;
	}

	@Override
	public final void end() {
	}

	@Override
	public final boolean doCycle(int deltaTime) {
		if (!m_queryCancel)
			run(m_entity);

		return true;
	}

	@Override
	public final boolean isParallel() {
		return false;
	}

	public abstract void run(IEntity world);

}
