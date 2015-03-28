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
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;

import java.awt.Graphics2D;

public final class Window extends Control implements IDisposable
{
	private static final String COMPONENT_NAME = "window";
	
	private boolean m_isMovable;
	private boolean m_isFocusable;

	private WindowManager m_manager;
	
	private Panel m_rootPanel;
	
	private final Observers m_observers = new Observers();
	
	public Window(int width, int height)
	{
		super(COMPONENT_NAME);
		
		m_rootPanel = new Panel(width, height);
		m_rootPanel.setParent(this);
		m_isFocusable = true;
		m_isMovable = true;
	}
	
	@Override
	public void dispose()
	{
		if(m_manager != null)
			m_manager.removeWindow(this);
		
		m_rootPanel.dispose();
	}
	
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	public boolean isMovable()
	{
		return m_isMovable;
	}

	public boolean isFocusable()
	{
		return m_isFocusable;
	}

	public void setMovable(boolean isMovable)
	{
		m_isMovable = isMovable;
	}

	public void setFocusable(boolean isFocusable)
	{
		m_isFocusable = isFocusable;
	}
	
	public void addControl(Control control)
	{
		m_rootPanel.addControl(control);
	}
	
	public void addControl(Control control, Vector2D location)
	{
		m_rootPanel.addControl(control, location);
	}
	
	public void removeControl(Control control)
	{
		m_rootPanel.removeControl(control);
	}

	@Nullable
	public <T extends Control> T getControl(Class<T> controlClass, String name) throws NoSuchControlException
	{
		return m_rootPanel.getControl(controlClass, name);
	}
	
	void setManager(WindowManager manager)
	{
		m_manager = manager;
	}
	
	@Nullable
	public WindowManager getManager()
	{
		return m_manager;
	}
	
	public void center()
	{
		if(m_manager != null)
			m_manager.centerWindow(this);
	}
	
	public void remove()
	{
		if(m_manager != null)
			m_manager.removeWindow(this);
	}
	
	public void focus()
	{
		if(isFocusable() && isVisible() && m_manager != null)
		{
			m_manager.setFocusedWindow(this);
		}
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		m_rootPanel.render(g, x, y, scale);
	}

	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent)
	{
		m_observers.raise(IWindowInputObserver.class).onMouseEvent(mouseEvent);
		return m_rootPanel.onMouseEvent(mouseEvent);
	}

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent)
	{
		m_observers.raise(IWindowInputObserver.class).onKeyEvent(keyEvent);
		return m_rootPanel.onKeyEvent(keyEvent);
	}

	@Override
	public Rect2D getBounds()
	{
		return m_rootPanel.getBounds();
	}

	@Override
	public void update(int deltaTime)
	{
		m_rootPanel.update(deltaTime);
	}
	
	@Override
	protected void onStyleChanged()
	{
		m_rootPanel.setStyle(getStyle());
	}

	@Override
	protected void onFocusChanged()
	{
		m_observers.raise(IWindowFocusObserver.class).onFocusChanged(hasFocus());
	}
	
	public interface IWindowInputObserver
	{
		void onKeyEvent(InputKeyEvent event);
		void onMouseEvent(InputMouseEvent event);
	}
	
	public interface IWindowFocusObserver
	{
		void onFocusChanged(boolean hasFocus);
	}
}
