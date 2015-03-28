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

public interface ITask
{
	void begin(IEntity entity);
	void end();

	void cancel();

	boolean doCycle(int deltaTime);

	boolean isParallel();
	
	public static final class NullTask implements ITask
	{
		@Override
		public void begin(IEntity entity) { }

		@Override
		public void end() { }

		@Override
		public void cancel() { }

		@Override
		public boolean doCycle(int deltaTime)
		{
			return true;
		}

		@Override
		public boolean isParallel()
		{
			return false;
		}
	}
}
