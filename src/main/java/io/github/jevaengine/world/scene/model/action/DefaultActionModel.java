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
package io.github.jevaengine.world.scene.model.action;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.scene.model.IActionSceneModel;
import io.github.jevaengine.world.scene.model.ISceneModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class DefaultActionModel implements IActionSceneModel
{
	private final ISceneModel m_sceneModel;
	
	private final Map<String, DefaultActionModelAction> m_actions = new HashMap<>();
	private final LinkedList<DefaultActionModelAction> m_actionQueue = new LinkedList<>();
	
	@Nullable
	private DefaultActionModelAction m_currentAction;
	
	public DefaultActionModel(ISceneModel sceneModel)
	{
		m_sceneModel = sceneModel;	
	}

	@Override
	public void dispose()
	{
		m_sceneModel.dispose();
	}
	
	public IActionSceneModelAction addAction(IDefaultActionModelBehavior behavior)
	{
		DefaultActionModelAction action = new DefaultActionModelAction(behavior);
		m_actions.put(action.getName(), action);
		
		return action;
	}
	
	@Override
	public ISceneModel clone()
	{
		return m_sceneModel.clone();
	}
	
	@Override
	public Direction getDirection()
	{
		return m_sceneModel.getDirection();
	}
	
	@Override
	public void setDirection(Direction d)
	{
		m_sceneModel.setDirection(d);
	}
	
	@Override
	public Collection<ISceneModelComponent> getComponents(Matrix3X3 projection)
	{
		return m_sceneModel.getComponents(projection);
	}

	@Override
	public Rect3F getAABB()
	{
		return m_sceneModel.getAABB();
	}

	@Override
	public IActionSceneModelAction getAction(String name)
	{
		IActionSceneModelAction action = m_actions.get(name);
		
		if(action == null)
			return new NullActionSceneModelAction();
		
		return action;
	}
	
	@Override
	public boolean hasAction(String name)
	{
		return m_actions.containsKey(name);
	}
	
	private DefaultActionModelAction getNextActiveBehavior()
	{
		for(DefaultActionModelAction a : m_actionQueue)
		{
			if(!a.isPassive())
				return a;
		}
		
		return null;
	}
	
	@Override
	public void update(int deltaTime)
	{
		if((m_currentAction == null || m_currentAction.isDone()) && !m_actionQueue.isEmpty())
		{
			DefaultActionModelAction previousAction = m_currentAction;
			DefaultActionModelAction nextAction = m_actionQueue.peek();
			DefaultActionModelAction nextActive = getNextActiveBehavior();
			
			if(nextAction.isPassive() && nextActive != null)
			{
				m_actionQueue.remove(nextActive);
				m_currentAction = nextActive;
			}else
				m_currentAction = m_actionQueue.poll();
			
			if(m_currentAction != previousAction)
			{
				if(previousAction != null)
					previousAction.leave();
			
				if(m_currentAction != null)
					m_currentAction.enter();
			}
		}
		
		if(m_currentAction != null)
			m_currentAction.update(deltaTime);
		
		m_sceneModel.update(deltaTime);
	}

	private final class DefaultActionModelAction implements IActionSceneModelAction
	{
		private final Observers m_observers = new Observers();
		private final IDefaultActionModelBehavior m_behavior;
		private boolean m_isDone = true;
		
		private DefaultActionModelAction(IDefaultActionModelBehavior behavior)
		{
			m_behavior = behavior;
		}
		
		@Override
		public final String getName()
		{
			return m_behavior.getName();
		}

		@Override
		public boolean isQueued()
		{
			return m_actionQueue.contains(this);
		}
		
		private boolean tryActivate(boolean top)
		{
			if(m_currentAction == this)
			{
				if(m_currentAction.isDone())
				{
					m_currentAction.leave();
					m_currentAction.enter();
				}
				return true;
			}else if((m_currentAction != null || !m_actionQueue.isEmpty()) && !top)
				return false;
			else if(m_currentAction == null || m_currentAction.interrupt())
			{
				if(m_currentAction != null)
					m_currentAction.leave();
		
				m_currentAction = this;
				m_currentAction.enter();
				return true;	
			}
			
			return false;
		}
		
		@Override
		public void queueTop()
		{
			if(!tryActivate(true))
				m_actionQueue.add(0, this);
		}
		
		@Override
		public void queue()
		{	
			if(!tryActivate(false))
				m_actionQueue.add(this);
		}

		@Override
		public void dequeue()
		{
			if(m_currentAction == this)
				interrupt();
			else if(m_actionQueue.contains(this))
				m_actionQueue.remove(this);
		}

		@Override
		public IObserverRegistry getObservers()
		{
			return m_observers;
		}

		void enter()
		{
			m_isDone = false;
			m_behavior.enter();
			m_observers.raise(IActionSceneModelActionObserver.class).begin();
		}
		
		void leave()
		{
			m_isDone = true;
			m_observers.raise(IActionSceneModelActionObserver.class).end();	
		}
		
		@Override
		public boolean isDone()
		{
			return m_isDone;
		}
		
		boolean isPassive()
		{
			return m_behavior.isPassive();
		}
		
		boolean interrupt()
		{
			if(m_currentAction != this)
				return true;
			
			if(m_behavior.interrupt())
			{
				leave();
				return true;
			}
			
			return false;
		}

		void update(int deltaTime)
		{
			if(m_isDone)
				return;
			
			m_isDone = m_behavior.isDone();
			
			if(!m_isDone)
			{
				if(m_behavior.update(deltaTime))
					m_observers.raise(IActionSceneModelActionObserver.class).performed();
			}
		}

		@Override
		public boolean isActive()
		{
			return m_currentAction == this;
		}

		@Override
		public void cancel()
		{
			if(m_currentAction == this)
				interrupt();
			else
				m_actionQueue.remove(this);
		}
	}
	
	public interface IDefaultActionModelBehavior
	{
		String getName();
		void enter();
		boolean isDone();
		boolean isPassive();
		boolean interrupt();
		//Returns true when the action has been performed.
		boolean update(int deltaTime);
	}
}
