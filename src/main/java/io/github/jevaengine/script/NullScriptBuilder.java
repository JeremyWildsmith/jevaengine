package io.github.jevaengine.script;

import java.net.URI;


public final class NullScriptBuilder implements IScriptBuilder
{
	@Override
	public IScript create(Object context) throws ScriptConstructionException
	{
		return new NullScript();
	}

	@Override
	public IScript create() throws ScriptConstructionException
	{
		return new NullScript();
	}
	
	@Override
	public IFunctionFactory getFunctionFactory()
	{
		return new NullFunctionFactory();
	}
	
	@Override
	public URI getUri()
	{
		return URI.create("");
	}
}
