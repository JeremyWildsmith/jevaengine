/*******************************************************************************
 * Copyright (c) 2013 Jeremy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * If you'd like to obtain a another license to this code, you may contact Jeremy to discuss alternative redistribution options.
 * 
 * Contributors:
 *     Jeremy - initial API and implementation
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
