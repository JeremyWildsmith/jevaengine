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
package io.github.jevaengine.util;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SynchronousExecutor
{
	private Queue<ISynchronousTask> m_tasks = new ConcurrentLinkedQueue<>();
	
	public void enqueue(ISynchronousTask task)
	{
		if(task == null)
			throw new NullPointerException();
		
		m_tasks.add(task);
	}
	
	public void execute()
	{
		ArrayList<ISynchronousTask> requeue = new ArrayList<>();
		
		for(ISynchronousTask r; (r = m_tasks.poll()) != null;)
		{
			if(!r.run())
				requeue.add(r);
		}
		
		m_tasks.addAll(requeue);
	}
	
	public interface ISynchronousTask
	{
		boolean run();
	}
}
