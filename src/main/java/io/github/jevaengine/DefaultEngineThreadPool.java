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
package io.github.jevaengine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DefaultEngineThreadPool implements IEngineThreadPool
{
	private static final int LOADING_THREADS = 5;
	
	private ExecutorService m_daemonExecutor = Executors.newCachedThreadPool(new ThreadFactory() {	
		@Override
		public Thread newThread(Runnable r)
		{
			Thread thread = Executors.defaultThreadFactory().newThread(r);
			thread.setDaemon(true);
			thread.setPriority(Thread.MIN_PRIORITY);
			
			return thread;
		}
	});
	
	private ExecutorService m_loadingExecutor = Executors.newFixedThreadPool(LOADING_THREADS);
	
	private ExecutorService m_longLivingLowPriorityExecutor = Executors.newCachedThreadPool();
	
	private ExecutorService m_engineLogicExector = Executors.newSingleThreadExecutor();
	
	@Override
	public void execute(Purpose purpose, Runnable task)
	{
		switch(purpose)
		{
		case Loading:
			m_loadingExecutor.execute(task);
			break;
		case LongLivingLowPriority:
			m_longLivingLowPriorityExecutor.execute(task);
			break;
		case LongLivingLowPriorityDaemon:
			m_daemonExecutor.execute(task);
			break;
		case GameLogic:
			m_engineLogicExector.execute(task);
			break;
		default:
			throw new UnsupportedOperationException("Unrecognized task purpose.");
		}
	}

}
