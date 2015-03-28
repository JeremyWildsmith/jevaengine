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

import java.awt.Graphics2D;
import java.awt.image.RGBImageFilter;

public final class NullGraphic implements IImmutableGraphic
{
	private final int m_width;
	private final int m_height;
	
	public NullGraphic(int width, int height)
	{
		m_width = width;
		m_height = height;
	}
	
	public NullGraphic()
	{
		this(1, 1);
	}
	
	@Override
	public IImmutableGraphic filterImage(RGBImageFilter filter)
	{
		return this;
	}

	@Override
	public boolean pickTest(int x, int y)
	{
		return false;
	}

	@Override
	public Rect2D getBounds()
	{
		return new Rect2D(m_width, m_height);
	}

	@Override
	public void render(Graphics2D g, int dx, int dy, int dw, int dh, int sx, int sy, int sw, int sh) { }

	@Override
	public void render(Graphics2D g, int dx, int dy, float scale) { }

	@Override
	public IImmutableGraphic duplicate()
	{
		return new NullGraphic(m_width, m_height);
	}
}
