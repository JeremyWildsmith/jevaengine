package io.github.jevaengine.script.rhino;

import io.github.jevaengine.script.ScriptExecuteException;
import org.mozilla.javascript.RhinoException;

public class RhinoScriptException extends ScriptExecuteException
{
	private static final long serialVersionUID = 1L;
	
	public RhinoScriptException(RhinoException e)
	{
		super(String.format("%s,\b%s", e.getMessage(), e.getScriptStackTrace()), e);
	}
}
