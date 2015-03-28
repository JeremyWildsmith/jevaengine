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

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.joystick.IInputSource;
import io.github.jevaengine.joystick.IInputSourceProcessor;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.ui.WindowManager;

import java.awt.Graphics2D;

import javax.inject.Inject;

public abstract class DefaultGame implements IGame
{
	private IInputSource m_inputSource;

	private Vector2D m_cursorLocation = new Vector2D();

	private GameInputHandler m_inputHandler = new GameInputHandler();	
	private WindowManager m_windowManager;
	
	@Inject
	public DefaultGame(IInputSource inputSource, Vector2D resolution)
	{
		m_inputSource = inputSource;
		m_windowManager = new WindowManager(resolution);
	}

	@Override
	public final void render(IRenderer r)
	{
		r.render(new IRenderable() {
			@Override
			public void render(Graphics2D g, int x, int y, float scale) {
				getWindowManager().render(g, x, y, 1.0F);
				getCursor().render(g, m_cursorLocation.x, m_cursorLocation.y, 1.0F);				
			}
		});
	}

	@Override
	public final void update(int deltaTime)
	{
		m_inputSource.process(m_inputHandler);
		m_windowManager.update(deltaTime);
		
		doLogic(deltaTime);
	}
	
	@Override
	public final WindowManager getWindowManager()
	{
		return m_windowManager;
	}
	
	protected abstract void doLogic(int deltaTime);
	protected abstract IRenderable getCursor();
	
	private class GameInputHandler implements IInputSourceProcessor
	{
		@Override
		public void mouseMoved(InputMouseEvent e)
		{
			m_cursorLocation = new Vector2D(e.location);

			getWindowManager().onMouseEvent(e);
		}

		@Override
		public void mouseClicked(InputMouseEvent e)
		{
			getWindowManager().onMouseEvent(e);
		}

		@Override
		public void keyUp(InputKeyEvent e)
		{
			getWindowManager().onKeyEvent(e);
		}

		@Override
		public void keyDown(InputKeyEvent e)
		{
			getWindowManager().onKeyEvent(e);
		}

		@Override
		public void keyTyped(InputKeyEvent e)
		{
			getWindowManager().onKeyEvent(e);
		}

		@Override
		public void mouseWheelMoved(InputMouseEvent e)
		{
			getWindowManager().onMouseEvent(e);
		}

		@Override
		public void mouseLeft(InputMouseEvent e) { }

		@Override
		public void mouseEntered(InputMouseEvent e) { }

		@Override
		public void mouseButtonStateChanged(InputMouseEvent e)
		{
			getWindowManager().onMouseEvent(e);
		}
	}
}
