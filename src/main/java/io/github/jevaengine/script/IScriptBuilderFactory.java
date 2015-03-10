package io.github.jevaengine.script;

import io.github.jevaengine.script.rhino.RhinoScriptBuilderFactory;

import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(RhinoScriptBuilderFactory.class)
public interface IScriptBuilderFactory
{
	IScriptBuilder create(URI name) throws ScriptBuilderConstructionException;
	IScriptBuilder create() throws ScriptBuilderConstructionException;

	public static final class ScriptBuilderConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public ScriptBuilderConstructionException(Exception cause)
		{
			super(cause);
		}
	}
}
