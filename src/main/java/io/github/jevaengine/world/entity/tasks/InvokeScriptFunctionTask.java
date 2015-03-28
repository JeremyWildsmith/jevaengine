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
package io.github.jevaengine.world.entity.tasks;

import io.github.jevaengine.script.IFunction;
import io.github.jevaengine.script.ScriptExecuteException;
import io.github.jevaengine.world.entity.IEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jeremy
 */
public final class InvokeScriptFunctionTask extends SynchronousOneShotTask
{
	private IFunction m_function;
	
	private Object[] m_arguments;

	private Logger m_logger = LoggerFactory.getLogger(InvokeScriptFunctionTask.class);
	
	public InvokeScriptFunctionTask(IFunction function, Object ... arguments)
	{
		m_function = function;
		m_arguments = arguments;
	}
	
	@Override
	public void run(IEntity entity)
	{
		try
		{
			m_function.call(m_arguments);
		} catch(ScriptExecuteException e)
		{
			m_logger.error("Error invoking callback task on entity" + entity.getInstanceName(), e);
		}
	}
}
