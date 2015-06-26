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

import io.github.jevaengine.IAssetStreamFactory;
import io.github.jevaengine.IAssetStreamFactory.AssetStreamConstructionException;
import io.github.jevaengine.game.IRenderer;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.ThreadSafe;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.imageio.ImageIO;
import javax.inject.Inject;

public final class BufferedImageGraphicFactory implements IGraphicFactory
{
	private final IRenderer m_renderer;
	private final IAssetStreamFactory m_assetFactory;
	
	@Inject
	public BufferedImageGraphicFactory(IRenderer renderer, IAssetStreamFactory assetFactory)
	{
		m_renderer = renderer;
		m_assetFactory = assetFactory;
	}
	
	@Nullable
	private static BufferedImage createCompatibleImage(GraphicsConfiguration graphicsConfiguration, InputStream is) throws IOException
	{
		Image srcImage = ImageIO.read(is);
		
		if(srcImage == null)
			return null;
		
		BufferedImage destImage = graphicsConfiguration.createCompatibleImage(srcImage.getWidth(null), srcImage.getHeight(null), Transparency.TRANSLUCENT);
		Graphics g = destImage.getGraphics();
		g.drawImage(srcImage, 0, 0, null);
		g.dispose();
		return destImage;
	}
	
	@Override
	@ThreadSafe
	public IImmutableGraphic create(URI name) throws GraphicConstructionException
	{		
		try
		{
			BufferedImage img = createCompatibleImage(m_renderer.getGraphicsConfiguration(), m_assetFactory.create(name));
			
			if(img == null)
				throw new GraphicConstructionException(name, new UnsupportedGraphicFormatException());
			
			return new BufferedGraphic(img);
		}catch(AssetStreamConstructionException | IOException e) {
			throw new GraphicConstructionException(name, e);
		}
	}

	@Override
	public IGraphic create(int width, int height)
	{
		return new BufferedGraphic(new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR));
	}
	
	private static final class BufferedGraphic implements IGraphic
	{
		private BufferedImage m_sourceImage;

		public BufferedGraphic(@Nullable BufferedImage sourceImage)
		{
			m_sourceImage = sourceImage;
		}
		
		@Override
		public void render(Graphics2D g, int dx, int dy, int dw, int dh, int sx, int sy, int sw, int sh)
		{
			g.drawImage(m_sourceImage, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh, null);
		}
		
		@Override
		public void render(Graphics2D g, int dx, int dy, float scale)
		{	
			render(g, dx, dy, (int)(m_sourceImage.getWidth() * scale), (int)(m_sourceImage.getHeight() * scale),
						0, 0, m_sourceImage.getWidth(), m_sourceImage.getHeight());
		}
		
		@Override
		public IImmutableGraphic filterImage(RGBImageFilter filter)
		{
			if(m_sourceImage == null)
				return new BufferedGraphic(null);
			else
			{
				Image srcImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(m_sourceImage.getSource(), filter));
				BufferedImage bufferedImage = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D g = bufferedImage.createGraphics();
				g.drawImage(srcImage, 0, 0, null);
				g.dispose();
				
				return new BufferedGraphic(bufferedImage);
			}
		}
		
		@Override
		public boolean pickTest(int x, int y)
		{
			if(x < 0 || y < 0 || x >= m_sourceImage.getWidth(null) || y >= m_sourceImage.getHeight(null))
				return false;
			else
				return ((m_sourceImage.getRGB(x, y) >> 24) & 0xff) != 0;
		}
		
		public Graphics2D createGraphics()
		{
			return (Graphics2D) m_sourceImage.getGraphics();
		}

		@Override
		public Rect2D getBounds()
		{
			return new Rect2D(m_sourceImage.getWidth(), m_sourceImage.getHeight());
		}

		@Override
		public IImmutableGraphic duplicate()
		{
			return new BufferedGraphic(m_sourceImage);
		}
	}
}
