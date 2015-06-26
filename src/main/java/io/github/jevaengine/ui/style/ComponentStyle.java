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
package io.github.jevaengine.ui.style;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.ui.ComponentState;
import java.util.NoSuchElementException;

public final class ComponentStyle implements IDisposable
{
	private final ComponentStateStyle m_defaultStateStyle;
	private final ComponentStateStyle m_enterStateStyle;
	private final ComponentStateStyle m_activatedStateStyle;
	
	public ComponentStyle(ComponentStateStyle defaultStateStyle, ComponentStateStyle enterStateStyle, ComponentStateStyle activatedStateStyle)
	{
		m_defaultStateStyle = defaultStateStyle;
		m_enterStateStyle = enterStateStyle;
		m_activatedStateStyle = activatedStateStyle;
	}
	
	@Override
	public void dispose()
	{
		m_defaultStateStyle.dispose();
		m_enterStateStyle.dispose();
		m_activatedStateStyle.dispose();
	}
	
	public ComponentStateStyle getStateStyle(ComponentState state)
	{
		switch(state)
		{
		case Activated:
			return m_activatedStateStyle;
		case Default:
			return m_defaultStateStyle;
		case Enter:
			return m_enterStateStyle;
		default:
			throw new NoSuchElementException();
		}
	}
}