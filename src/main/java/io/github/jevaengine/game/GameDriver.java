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
package io.github.jevaengine.game;

import io.github.jevaengine.IEngineThreadPool;
import io.github.jevaengine.IEngineThreadPool.Purpose;

import com.google.inject.Inject;

public final class GameDriver
{
	private final IGame m_game;	
	private final IEngineThreadPool m_threadPool;
	private final IRenderer m_renderer;
	
	private volatile boolean m_runGame = true;
	private volatile boolean m_isRunning = false;
	
	@Inject
	public GameDriver(IGameFactory gameFactory, IEngineThreadPool threadPool, IRenderer renderer)
	{
		m_game = gameFactory.create();
		m_threadPool = threadPool;
		m_renderer = renderer;
	}
	
	public void begin()
	{
		if(m_isRunning)
			return;
		
		m_runGame = true;
		m_isRunning = true;
		
		m_threadPool.execute(Purpose.GameLogic, new GameLogicDriver());
	}
	
	public void stop()
	{
		m_runGame = false;
	}
	
	private class GameLogicDriver implements Runnable
	{
		public void run()
		{
			long lastTime;
			
			do
			{
				lastTime = System.currentTimeMillis();
				
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					Thread.interrupted();
				}
				
				m_game.render(m_renderer);
				m_game.update((int)(System.currentTimeMillis() - lastTime));
				
			} while(m_runGame);
			
			m_isRunning = false;
		}
	}
}
