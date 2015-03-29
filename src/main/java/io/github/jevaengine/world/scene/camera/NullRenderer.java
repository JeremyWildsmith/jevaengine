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
package io.github.jevaengine.world.scene.camera;

import io.github.jevaengine.game.IRenderer;
import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.math.Vector2D;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

public final class NullRenderer implements IRenderer
{
	@Override
	public GraphicsConfiguration getGraphicsConfiguration()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	@Override
	public void render(IRenderable frame) { }

	@Override
	public Vector2D getResolution()
	{
		return new Vector2D();
	}

}