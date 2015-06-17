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

import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.util.IObserverRegistry;

public abstract class WindowBehaviourInjector
{
	private Window m_host;
	
	public final void inject(Window host) throws NoSuchControlException
	{
		m_host = host;
		doInject();
	}
	
	protected final IObserverRegistry getObservers()
	{
		return m_host.getObservers();
	}
	
	protected final void addControl(Control control, Vector2D location)
	{
		m_host.addControl(control, location);
	}
	
	protected final void addControl(Control control)
	{
		m_host.addControl(control);
	}
	
	protected final void removeControl(Control control)
	{
		m_host.removeControl(control);
	}
	
	protected final boolean isVisible()
	{
		return m_host.isVisible();
	}
	
	protected final <T extends Control> T getControl(Class<T> controlClass, String name) throws NoSuchControlException
	{
		return m_host.getControl(controlClass, name);
	}
	
	protected abstract void doInject() throws NoSuchControlException;
}
