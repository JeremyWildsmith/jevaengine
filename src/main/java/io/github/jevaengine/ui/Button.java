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

import io.github.jevaengine.graphics.IFont;
import io.github.jevaengine.graphics.IImmutableGraphic;
import io.github.jevaengine.graphics.NullFont;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.joystick.InputMouseEvent.MouseButton;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.ui.style.ComponentStateStyle;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import java.awt.Graphics2D;

public final class Button extends Control
{
	public static final String COMPONENT_NAME = "button";
	
	private String m_text;

	private ComponentState m_state = ComponentState.Default;

	private IFont m_font = new NullFont();
	private IImmutableGraphic m_frame = new NullGraphic();
	
	private final Observers m_observers = new Observers();
	
	public Button(String text)
	{
		super(COMPONENT_NAME);
		m_text = text;
	}
	
	public Button(String instanceName, String text)
	{
		super(COMPONENT_NAME, instanceName);
		m_text = text;
	}
	
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	private void enterState(ComponentState state)
	{
		m_state = state;
		
		ComponentStateStyle stateStyle = getComponentStyle().getStateStyle(m_state);
		stateStyle.playEnter();
		
		m_font = stateStyle.getFont();
		
		Rect2D textBounds = m_font.getTextBounds(m_text, 1.0F);
		m_frame = stateStyle.createFrame(textBounds.width, textBounds.height);
	}
	
	@Override
	protected void onEnter()
	{
		enterState(ComponentState.Enter);
	}

	@Override
	protected void onLeave()
	{
		enterState(ComponentState.Default);
	}

	public String getText()
	{
		return m_text;
	}
	
	public void setText(String text)
	{
		m_text = text;
		enterState(ComponentState.Default);
	}
	
	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent)
	{
		if (mouseEvent.mouseButton == MouseButton.Left)
		{
			if(mouseEvent.type == InputMouseEvent.MouseEventType.MousePressed)
			{
				enterState(ComponentState.Activated);
			} else if(mouseEvent.type == InputMouseEvent.MouseEventType.MouseReleased)
			{
				enterState(ComponentState.Enter);
			} else if(mouseEvent.type == InputMouseEvent.MouseEventType.MouseClicked)
				m_observers.raise(IButtonPressObserver.class).onPress();
		}
		
		return true;
	}

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent)
	{
		return false;
	}

	@Override
	public void update(int deltaTime) { }
	
	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		m_frame.render(g, x, y, scale);
		
		Rect2D stringBounds = m_font.getTextBounds(m_text, 1.0F);

		int textAnchorX = m_frame.getBounds().width / 2 - stringBounds.width / 2;
		int textAnchorY = m_frame.getBounds().height / 2 - stringBounds.height / 2;
		
		m_font.drawText(g, textAnchorX + x, textAnchorY + y, scale, m_text);
	}

	protected void onStyleChanged()
	{
		enterState(m_state);
	}

	@Override
	public Rect2D getBounds()
	{
		return m_frame.getBounds();
	}

	public interface IButtonPressObserver
	{
		void onPress();
	}
}
