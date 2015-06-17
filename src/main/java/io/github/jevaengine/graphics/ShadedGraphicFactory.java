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

import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.NullVariable;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.graphics.IGraphicShaderFactory.GraphicShaderConstructionException;
import io.github.jevaengine.util.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;

public final class ShadedGraphicFactory implements IGraphicFactory
{
	private final IConfigurationFactory m_configurationFactory;
	private final IGraphicShaderFactory m_shaderFactory;
	private final IGraphicFactory m_baseGraphicFactory;

	@Inject
	public ShadedGraphicFactory(IGraphicShaderFactory shaderFactory, IGraphicFactory baseGraphicFactory, IConfigurationFactory configurationFactory)
	{
		m_shaderFactory = shaderFactory;
		m_baseGraphicFactory = baseGraphicFactory;
		m_configurationFactory = configurationFactory;
	}

	@Override
	public IGraphic create(int width, int height)
	{
		return m_baseGraphicFactory.create(width, height);
	}

	@Override
	public IImmutableGraphic create(URI name) throws GraphicConstructionException
	{
		try
		{
			ShadedGraphicDeclaration decl = m_configurationFactory.create(name).getValue(ShadedGraphicDeclaration.class);
			
			URI sourceName = name.resolve(new URI(decl.texture));
			IImmutableGraphic source = m_baseGraphicFactory.create(sourceName);

			URI shaderName = name.resolve(new URI(decl.shader));
			source = m_shaderFactory.create(shaderName).shade(source);
			
			return source;
		} catch (ConfigurationConstructionException | 
				ValueSerializationException | 
				URISyntaxException | 
				GraphicConstructionException | 
				GraphicShaderConstructionException e)
		{
			throw new GraphicConstructionException(name, e);
		}
	}
	
	public static final class ShadedGraphicDeclaration implements ISerializable
	{
		public String shader;
		public String texture;

		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("shader").setValue(shader);
			target.addChild("texture").setValue(texture);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				shader = source.getChild("shader").getValue(String.class);
				texture = source.getChild("texture").getValue(String.class);
			} catch (NoSuchChildVariableException e)
			{
				throw new ValueSerializationException(e);
			}
		}
	}
}