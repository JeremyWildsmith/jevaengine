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
