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
package io.github.jevaengine.world;

import io.github.jevaengine.FutureResult;
import io.github.jevaengine.IEngineThreadPool;
import io.github.jevaengine.IEngineThreadPool.Purpose;
import io.github.jevaengine.IInitializationMonitor;
import io.github.jevaengine.world.IWorldFactory.WorldConstructionException;

import java.net.URI;

public final class ThreadPooledWorldFactory implements IParallelWorldFactory {
	private final IWorldFactory m_worldFactory;
	private final IEngineThreadPool m_threadPool;

	public ThreadPooledWorldFactory(IWorldFactory worldFactory, IEngineThreadPool threadPool) {
		m_worldFactory = worldFactory;
		m_threadPool = threadPool;
	}

	@Override
	public void create(final URI name, final IInitializationMonitor<World, WorldConstructionException> monitor) {
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run() {
				try {
					monitor.completed(new FutureResult<World, WorldConstructionException>(m_worldFactory.create(name, monitor)));
				} catch (WorldConstructionException e) {
					monitor.completed(new FutureResult<World, WorldConstructionException>(e));
				}
			}
		});
	}

	@Override
	public IWorldFactory getFactory() {
		return m_worldFactory;
	}
}
