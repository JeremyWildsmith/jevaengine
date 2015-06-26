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

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.graphics.IImmutableGraphic;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.joystick.InputMouseEvent.MouseEventType;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.ui.style.IUIStyle;
import io.github.jevaengine.util.MutableProcessList;
import io.github.jevaengine.util.Nullable;
import java.awt.Graphics2D;
import java.util.List;

public final class Panel extends Control implements IDisposable
{
	public static final String COMPONENT_NAME = "panel";
	
	private IImmutableGraphic m_frame;

	private int m_desiredWidth;
	private int m_desiredHeight;

	private Control m_activeControl;
	private Control m_lastOver;

	private final List<Control> m_controls = new MutableProcessList<>();

	public Panel(int width, int height)
	{
		super(COMPONENT_NAME);
		m_desiredWidth = width;
		m_desiredHeight = height;
		m_frame = new NullGraphic(width, height);
	}
	
	public Panel(String instanceName, int width, int height)
	{
		super(COMPONENT_NAME, instanceName);
		m_desiredWidth = width;
		m_desiredHeight = height;
		m_frame = new NullGraphic(width, height);		
	}
	
	@Override
	public void dispose()
	{
		for(Control ctrl : m_controls)
			ctrl.dispose();
		
		super.dispose();
	}
	
	public void addControl(Control control)
	{
		addControl(control, null);
	}

	public void addControl(Control control, @Nullable Vector2D location)
	{
		if (!m_controls.contains(control))
		{
			m_controls.add(control);
			control.setParent(this);
		} else
		{
			// Move to top.
			m_controls.remove(control);
			m_controls.add(control);
		}
		
		if (location != null)
			control.setLocation(location);
	}

	public void removeControl(Control control)
	{
		if (m_controls.contains(control))
		{
			if(m_activeControl == control)
			{
				m_activeControl.clearFocus();
				m_activeControl = null;
			}
			
			if(m_lastOver == control)
				m_lastOver = null;
			
			control.setParent(null);
			m_controls.remove(control);
		}
	}

	public void clearControls()
	{
		if(m_activeControl != null)
		{
			m_activeControl.clearFocus();
			m_activeControl = null;
		}
		
		for (Control ctrl : m_controls)
		{
			removeControl(ctrl);
		}
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <T extends Control> T getControl(Class<T> controlClass, String name) throws NoSuchControlException
	{
		for(Control c : m_controls)
		{
			if(c.getInstanceName().equals(name) && controlClass.isAssignableFrom(c.getClass()))
				return (T)c;
		}
		
		throw new NoSuchControlException(controlClass, name);
	}
	
	@Override
	public final void render(Graphics2D g, int x, int y, float scale)
	{
		m_frame.render(g, x, y, scale);

		for (Control control : m_controls)
		{
			if (control.isVisible())
				control.render(g, control.getLocation().x + x, control.getLocation().y + y, scale);
		}
	}

	@Override
	public final boolean onMouseEvent(InputMouseEvent mouseEvent)
	{
		if (mouseEvent.type == MouseEventType.MouseWheelMoved && m_activeControl != null)
		{
			m_activeControl.onMouseEvent(mouseEvent);
			return true;
		} else
		{
			for (int i = m_controls.size() - 1; i >= 0; i--)
			{
				Control control = m_controls.get(i);

				if (control.isVisible())
				{
					Vector2D relativeLocation = mouseEvent.location.difference(control.getAbsoluteLocation());
					boolean isInBounds = control.getBounds().contains(relativeLocation);

					if (isInBounds)
					{
						if (m_lastOver != control)
						{
							if (m_lastOver != null)
								m_lastOver.onLeave();

							m_lastOver = control;
							control.onEnter();
						}

						if (mouseEvent.type == MouseEventType.MouseClicked)
						{
							if(m_activeControl != null)
								m_activeControl.clearFocus();

							m_activeControl = control;
							m_activeControl.setFocus();
						}
						
						if(control.onMouseEvent(mouseEvent))
							return true;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public final boolean onKeyEvent(InputKeyEvent event)
	{
		if (m_activeControl != null)
			return m_activeControl.onKeyEvent(event);
	
		return false;
	}

	@Override
	public final void update(int deltaTime)
	{
		for (Control control : m_controls)
			control.update(deltaTime);
	}

	@Override
	public final Rect2D getBounds()
	{
		return m_frame.getBounds();
	}

	protected final void setWidth(int width)
	{
		m_desiredWidth = width;
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(m_desiredWidth, m_desiredHeight);
	}

	protected final void setHeight(int height)
	{
		m_desiredHeight = height;
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(m_desiredWidth, m_desiredHeight);
	}

	@Override
	protected final void onStyleChanged()
	{
		super.onStyleChanged();
		
		IUIStyle style = getStyle();
		
		for (Control ctrl : m_controls)
			ctrl.setStyle(style);
		
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(m_desiredWidth, m_desiredHeight);
	}
}
