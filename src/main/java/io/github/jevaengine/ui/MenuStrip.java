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
package io.github.jevaengine.ui;

import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.ui.Button.IButtonPressObserver;

import java.awt.Graphics2D;

public final class MenuStrip extends Control
{
	private static final String COMPONENT_NAME = "menuStrip";
	
	private Panel m_container = new Panel(0, 0);
	
	public MenuStrip()
	{
		super(COMPONENT_NAME);
		m_container.setParent(this);
		setVisible(false);
	}

	public void setContext(final String[] commands, final IMenuStripListener listener)
	{
		m_container.clearControls();

		int lastY = 0;
		int largestX = 0;

		for (int i = 0; i < commands.length; i++)
		{
			final String command = commands[i];

			Button cmd = new Button(commands[i]);
			
			cmd.getObservers().add(new IButtonPressObserver() {
				private final String m_command = command;
				@Override
				public void onPress()
				{
					listener.onCommand(m_command);

					MenuStrip.this.setVisible(false);
				}
			});
			
			cmd.setLocation(new Vector2D(4, lastY));
			m_container.addControl(cmd);

			lastY += cmd.getBounds().height + 8;

			largestX = Math.max(largestX, cmd.getBounds().width);
		}

		m_container.setHeight(lastY + 5);
		m_container.setWidth(largestX + 15);
		setVisible(true);
	}

	@Override
	public void onStyleChanged()
	{
		m_container.setStyle(getStyle());
	}
	
	public interface IMenuStripListener
	{
		void onCommand(String command);
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		m_container.render(g, x, y, scale);;
	}

	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent)
	{
		return m_container.onMouseEvent(mouseEvent);
	}

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent)
	{
		return m_container.onKeyEvent(keyEvent);
	}

	@Override
	public Rect2D getBounds()
	{
		return m_container.getBounds();
	}

	@Override
	public void update(int deltaTime)
	{
		m_container.update(deltaTime);
	}
}
