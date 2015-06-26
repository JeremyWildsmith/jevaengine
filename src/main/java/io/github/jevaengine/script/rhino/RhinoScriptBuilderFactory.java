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
package io.github.jevaengine.script.rhino;

import io.github.jevaengine.IAssetStreamFactory;
import io.github.jevaengine.IAssetStreamFactory.AssetStreamConstructionException;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;

public final class RhinoScriptBuilderFactory implements IScriptBuilderFactory
{
	private static final String SCRIPT_ENCODING = "UTF-8";
	
	private final IAssetStreamFactory m_assetFactory;
	
	@Inject
	public RhinoScriptBuilderFactory(IAssetStreamFactory assetFactory)
	{
		m_assetFactory = assetFactory;
	}

	@Override
	public IScriptBuilder create(URI name) throws ScriptBuilderConstructionException
	{
		try(InputStream source = m_assetFactory.create(name))
		{
			return new RhinoScriptBuilder(name, IOUtils.toString(source, SCRIPT_ENCODING));
		} catch (AssetStreamConstructionException | IOException e)
		{
			throw new ScriptBuilderConstructionException(e);
		}
	}
	
	@Override
	public IScriptBuilder create()
	{
		return new RhinoScriptBuilder();
	}
}
