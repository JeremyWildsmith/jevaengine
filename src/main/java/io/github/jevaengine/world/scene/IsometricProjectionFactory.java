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
package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Matrix3X3;

public class IsometricProjectionFactory implements IOrthographicProjectionFactory
{	
	private final int m_tileWidth;
	private final int m_tileHeight;
	
	public IsometricProjectionFactory(int tileWidth, int tileHeight)
	{
		m_tileWidth = tileWidth;
		m_tileHeight = tileHeight;
	}
	
	@Override
	public Matrix3X3 create()
	{
		return new Matrix3X3(m_tileWidth / 2.0F, -m_tileWidth / 2.0F, 0,
							m_tileHeight / 2.0F, m_tileHeight / 2.0F, -m_tileHeight,
							0, 0, 1);

	}
}
