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
package io.github.jevaengine.world.entity;

import io.github.jevaengine.world.entity.tasks.ITask;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public final class DefaultEntityTaskModel implements IEntityTaskModel
{
	private ArrayList<ITask> m_pendingTasks = new ArrayList<ITask>();
	private ArrayList<ITask> m_runningTasks = new ArrayList<ITask>();

	private IEntity m_host;
	
	public DefaultEntityTaskModel(IEntity host)
	{
		m_host = host;
	}
	
	public void addTask(ITask task)
	{
		m_pendingTasks.add(task);
	}

	public void cancelTasks()
	{
		for (ITask task : m_runningTasks)
			task.cancel();

		m_pendingTasks.clear();
	}

	public void cancelTask(ITask task)
	{
		if (!m_runningTasks.contains(task))
			throw new NoSuchElementException();

		task.cancel();
	}

	public boolean isTaskActive(ITask task)
	{
		return m_runningTasks.contains(task);
	}

	public boolean isTaskBlocking()
	{
		for (ITask task : m_runningTasks)
		{
			if (!task.isParallel())
				return true;
		}

		return false;
	}
	
	public void update(int deltaTime)
	{
		ArrayList<ITask> garbageTasks = new ArrayList<ITask>();

		LinkedList<ITask> reassignList = new LinkedList<ITask>();

		boolean isBlocking = isTaskBlocking();
		
		for (ITask task : m_pendingTasks)
		{
			if (task.isParallel())
				reassignList.add(task);
			else if(!isBlocking)
			{
				isBlocking = true;
				reassignList.add(task);
			}
		}

		for (ITask task : reassignList)
		{
			task.begin(m_host);
			m_runningTasks.add(task);
			m_pendingTasks.remove(task);
		}

		for (ITask task : m_runningTasks)
		{
			if (task.doCycle(deltaTime))
				garbageTasks.add(task);
		}

		for (ITask task : garbageTasks)
		{
			task.end();
			m_runningTasks.remove(task);
		}
	}
}
