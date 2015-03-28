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

import io.github.jevaengine.util.ThreadSafe;

import java.net.URI;

public interface IGraphicFactory
{
	@ThreadSafe
	IGraphic create(int width, int height);

	@ThreadSafe
	IImmutableGraphic create(URI name) throws GraphicConstructionException;
	
	public static final class GraphicConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public GraphicConstructionException(URI assetName, Exception cause) {
			super("Error constructing graphic " + assetName.toString(), cause);
		}
	}
}
