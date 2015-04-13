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

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.math.Vector3D;
import java.awt.Color;
import java.awt.image.RGBImageFilter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class DefaultGraphicShaderFactory implements IGraphicShaderFactory
{
	public final Map<URI, IShaderConstructor> m_shaders = new HashMap<>();
	
	public DefaultGraphicShaderFactory()
	{
		m_shaders.put(URI.create("shader:/default/blackAndWhite"), new IShaderConstructor() {
			@Override
			public IGraphicShader create(IImmutableVariable arguments) throws GraphicShaderConstructionException {
				return new BlackAndWhiteShader();
			}
		});
		
		m_shaders.put(URI.create("shader:/default/redGreenFilterToneBlueReplace"), new IShaderConstructor() {
			@Override
			public IGraphicShader create(IImmutableVariable arguments) throws GraphicShaderConstructionException {
				try
				{
					Vector3D colourVector = arguments.getChild("replace").getValue(Vector3D.class);
					int redFilter = arguments.getChild("redFilter").getValue(Integer.class);
					int greenFilter = arguments.getChild("greenFilter").getValue(Integer.class);
					
					return new RedGreenFilterToneBlueReplace(redFilter % 256,
															 greenFilter % 256,
															 new Color(colourVector.x % 256, colourVector.y % 256, colourVector.y % 256));
					
				} catch (NoSuchChildVariableException | ValueSerializationException e)
				{
					throw new GraphicShaderConstructionException(new GraphicShaderArgumentsParseException(e));
				}
			}
		});
	}
	
	@Override
	public IGraphicShader create(URI name, IImmutableVariable arguments) throws GraphicShaderConstructionException
	{
		IShaderConstructor shader = m_shaders.get(name);
		
		if(shader == null)
			throw new GraphicShaderConstructionException(new UnrecognizedGraphicShaderException(name));
		
		return shader.create(arguments);
	}

	private interface IShaderConstructor
	{
		IGraphicShader create(final IImmutableVariable arguments) throws GraphicShaderConstructionException;
	}
	
	private static final class BlackAndWhiteShader implements IGraphicShader
	{
		@Override
		public IImmutableGraphic shade(IImmutableGraphic source)
		{
			return source.filterImage(new RGBImageFilter() {
				@Override
				public int filterRGB(int x, int y, int rgb)
				{
					int a = rgb & 0xff000000;
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = rgb & 0xff;
					
					int avg = (r + g + b) / 3;
					r = avg;
					g = avg;
					b = avg;
					return a | (r << 16) | (g << 8) | b;					
				}
			});
		}
	}
	
	private static final class RedGreenFilterToneBlueReplace implements IGraphicShader
	{
		private final int m_rFilter;
		private final int m_gFilter;
		private final Color m_replaceColor;
		
		public RedGreenFilterToneBlueReplace(int rFilter, int gFilter, Color replaceColor)
		{
			m_rFilter = rFilter;
			m_gFilter = gFilter;
			m_replaceColor = replaceColor;
		}
		
		@Override
		public IImmutableGraphic shade(IImmutableGraphic source)
		{
			return source.filterImage(new RGBImageFilter() {
				@Override
				public int filterRGB(int x, int y, int rgb)
				{
					int a = rgb & 0xff000000;
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = rgb & 0xff;
					
					if(r != m_rFilter || g != m_gFilter)
						return rgb;
					
					r = (int)(m_replaceColor.getRed() * (b / 255.0F));
					g = (int)(m_replaceColor.getGreen() * (b / 255.0F));
					b = (int)(m_replaceColor.getBlue() * (b / 255.0F));
					
					return a | (r << 16) | (g << 8) | b;					
				}
			});
		}
	}
}
