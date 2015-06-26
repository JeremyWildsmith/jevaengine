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
