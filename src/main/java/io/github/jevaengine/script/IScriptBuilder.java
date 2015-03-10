package io.github.jevaengine.script;

import java.net.URI;


public interface IScriptBuilder
{
	IScript create(Object context) throws ScriptConstructionException;
	IScript create() throws ScriptConstructionException;
	IFunctionFactory getFunctionFactory();
	URI getUri();
	
	public static class ScriptConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		public ScriptConstructionException(URI name, Exception cause) {
			super("Error constructing script " + name, cause);
		}
		
	}
}
