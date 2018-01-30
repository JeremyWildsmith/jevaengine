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

import io.github.jevaengine.graphics.IGraphic;
import io.github.jevaengine.graphics.IGraphicFactory;
import io.github.jevaengine.graphics.IImmutableGraphic;
import io.github.jevaengine.graphics.Sprite;

import java.awt.*;

public final class BackgroundFrameFactory implements IFrameFactory {
	private IGraphicFactory m_graphicFactory;
	private Sprite m_background;

	public BackgroundFrameFactory(IGraphicFactory graphicFactory, Sprite background) {
		m_graphicFactory = graphicFactory;
		m_background = background;
	}

	@Override
	public IImmutableGraphic create(int desiredWidth, int desiredHeight) {
		IGraphic background = m_graphicFactory.create(m_background.getBounds().width, m_background.getBounds().height);

		Graphics2D g = background.createGraphics();
		m_background.render(g, 0, 0, 1.0F);
		g.dispose();

		return background;
	}

}