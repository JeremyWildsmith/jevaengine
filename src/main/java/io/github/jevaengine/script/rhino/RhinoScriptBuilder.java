package io.github.jevaengine.script.rhino;

import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScript;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.ScriptExecuteException;
import io.github.jevaengine.util.Nullable;
import java.net.URI;

import javax.inject.Inject;

public final class RhinoScriptBuilder implements IScriptBuilder
{
	private final URI m_name;
	private final String m_script;
	
	@Inject
	public RhinoScriptBuilder(URI name, String script)
	{
		m_name = name;
		m_script = script;
	}
	
	public RhinoScriptBuilder()
	{
		m_name = URI.create("");
		m_script = null;
	}

	@Override
	public IScript create(@Nullable Object context) throws ScriptConstructionException
	{
		try
		{
			IScript script = new RhinoScript();
			
			script.put("util", new RhinoUtility());
			
			if(context != null)
				script.put("me", context);
			
			if(m_script != null)
				script.evaluate(m_script);

			return script;
		} catch (ScriptExecuteException e) {
			throw new ScriptConstructionException(m_name, e);
		}
	}

	@Override
	public IScript create() throws ScriptConstructionException
	{
		return create(null);
	}
	
	@Override
	public IFunctionFactory getFunctionFactory()
	{
		return new RhinoFunctionFactory();
	}
	
	@Override
	public URI getUri()
	{
		return m_name;
	}
}
