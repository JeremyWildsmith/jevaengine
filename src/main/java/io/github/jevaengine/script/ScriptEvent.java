package io.github.jevaengine.script;

import io.github.jevaengine.util.MutableProcessList;

import java.util.Collection;

import javax.inject.Inject;


public final class ScriptEvent
{
	private Collection<IFunction> m_listeners = new MutableProcessList<>();
	
	private IFunctionFactory m_functionFactory;
	
	@Inject
	public ScriptEvent(IFunctionFactory functionFactory)
	{
		m_functionFactory = functionFactory;
	}
	
	public void add(Object function) throws UnrecognizedFunctionException
	{
		m_listeners.add(m_functionFactory.wrap(function));
	}
	
	public void remove(Object function) throws UnrecognizedFunctionException
	{
		m_listeners.remove(m_functionFactory.wrap(function));
	}
	
	@ScriptHiddenMember
	public void fire(final Object ... arguments) throws ScriptExecuteException
	{
		for(final IFunction f : m_listeners)
			f.call(arguments);
	}
}
