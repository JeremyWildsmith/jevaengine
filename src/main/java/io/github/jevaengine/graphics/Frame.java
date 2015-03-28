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
package io.github.jevaengine.graphics;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.util.Nullable;

public final class Frame
{
	private final Rect2D m_srcRect;
	private final int m_delay;
	private final Vector2D m_origin;
	private final String m_event;
	
	public Frame(Rect2D srcRect, int delay, Vector2D origin, @Nullable String event)
	{
		m_delay = delay;
		m_srcRect = new Rect2D(srcRect);
		m_origin = new Vector2D(origin);
		m_event = event;
	}

	public Frame(Rect2D srcRect, int delay, Vector2D origin)
	{
		this(srcRect, delay, origin, null);
	}
	
	public Rect2D getSourceRect()
	{
		return new Rect2D(m_srcRect);
	}

	public long getDelay()
	{
		return m_delay;
	}

	public Vector2D getOrigin()
	{
		return new Vector2D(m_origin);
	}
	
	@Nullable
	public String getEvent()
	{
		return m_event;
	}
}
