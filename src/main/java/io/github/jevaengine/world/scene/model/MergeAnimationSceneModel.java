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
package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;
import io.github.jevaengine.world.physics.PhysicsBodyShape.PhysicsBodyShapeType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MergeAnimationSceneModel implements IAnimationSceneModel
{
	private final List<IAnimationSceneModel> m_models = new ArrayList<>();
	private final Map<String, MergeAnimationSceneModelAnimationObserverRelay> m_observerRelays = new HashMap<>();
	
	public MergeAnimationSceneModel(IAnimationSceneModel ... models)
	{
		m_models.addAll(Arrays.asList(models));
	}
	
	public MergeAnimationSceneModel() { }

	@Override
	public void dispose()
	{
		for(IAnimationSceneModel m : new ArrayList<>(m_models))
		{
			remove(m);
			m.dispose();
		}
	}
	
	public void add(IAnimationSceneModel model)
	{
		//If there are other models in our merge set, align this one with them, otherwise just leave it.
		if(!m_models.isEmpty())
			model.setDirection(getDirection());
		
		m_models.add(model);
		
		for(MergeAnimationSceneModelAnimationObserverRelay r : m_observerRelays.values())
			r.addedModel(model);
	}
	
	public void remove(IAnimationSceneModel model)
	{
		for(MergeAnimationSceneModelAnimationObserverRelay r : m_observerRelays.values())
			r.removedModel(model);
		
		m_models.remove(model);
	}
	
	@Override
	public IAnimationSceneModel clone()
	{
		IAnimationSceneModel coppies[] = new IAnimationSceneModel[m_models.size()];
		
		for(int i = 0; i < m_models.size(); i++)
			coppies[i] = m_models.get(i).clone();
		
		return new MergeAnimationSceneModel(coppies);
	}

	@Override
	public Collection<ISceneModelComponent> getComponents(Matrix3X3 projection)
	{
		Map<String, List<ISceneModelComponent>> componentBuffer = new HashMap<>();
		
		for(ISceneModel m : m_models)
		{
			for(ISceneModelComponent c : m.getComponents(projection))
			{
				String name = c.getName().split("!")[0];
				if(componentBuffer.containsKey(name))
					componentBuffer.get(name).add(c);
				else
				{
					ArrayList<ISceneModelComponent> relatedComponents = new ArrayList<>();
					relatedComponents.add(c);
					componentBuffer.put(name, relatedComponents);
				}
			}
		}
		
		List<ISceneModelComponent> components = new ArrayList<>();
		for(Map.Entry<String, List<ISceneModelComponent>> e : componentBuffer.entrySet())
		{
			if(e.getValue().size() <= 1)
				components.add(e.getValue().get(0));
			else
				components.add(new MergeSceneModelComponent(e.getKey(), e.getValue()));
		}
		
		return components;
	}

	@Override
	public Rect3F getAABB()
	{
		Rect3F aabbs[] = new Rect3F[m_models.size()];
		
		for(int i = 0; i < m_models.size(); i++)
			aabbs[i] = m_models.get(i).getAABB();
		
		return Rect3F.getAABB(aabbs);
	}

	
	@Override
	public PhysicsBodyShape getBodyShape()
	{
		return new PhysicsBodyShape(PhysicsBodyShapeType.Box, getAABB());
	}
	
	@Override
	public void update(int deltaTime)
	{
		for(ISceneModel m : m_models)
			m.update(deltaTime);
	}

	@Override
	public Direction getDirection()
	{
		Direction d = null;
		
		for(ISceneModel m : m_models)
		{
			if(d == null)
				d = m.getDirection();
			else if(m.getDirection() != d)
				return Direction.Zero;
		}
		
		return d;
	}

	@Override
	public void setDirection(Direction direction)
	{
		for(ISceneModel m : m_models)
			m.setDirection(direction);
	}

	@Override
	public IAnimationSceneModelAnimation getAnimation(final String name)
	{
		return new IAnimationSceneModelAnimation() {
			@Override
			public void setState(AnimationSceneModelAnimationState state)
			{
				for(IAnimationSceneModel m : m_models)
				{
					if(m.hasAnimation(name))
						m.getAnimation(name).setState(state);
				}
			}
			
			@Override
			public AnimationSceneModelAnimationState getState()
			{
				if(m_models.isEmpty())
					return AnimationSceneModelAnimationState.Stop;
				
				for(IAnimationSceneModel m : m_models)
				{
					if(m.hasAnimation(name) && m.getAnimation(name).getState() != AnimationSceneModelAnimationState.Stop)
						return AnimationSceneModelAnimationState.Play;
				}

				return AnimationSceneModelAnimationState.Stop;
			}
			
			@Override
			public IObserverRegistry getObservers()
			{
				if(!m_observerRelays.containsKey(name))
					m_observerRelays.put(name, new MergeAnimationSceneModelAnimationObserverRelay(name, m_models));
				
				return m_observerRelays.get(name).getObservers();
			}
		};
	}

	@Override
	public boolean hasAnimation(String name)
	{
		for(IAnimationSceneModel m : m_models)
			if(m.hasAnimation(name))
				return true;
		
		return false;
	}
	
	private static final class MergeAnimationSceneModelAnimationObserverRelay
	{
		private final String m_actionName;
		private final Observers m_observers = new Observers();
		private final AnimationObserver m_animationObserver = new AnimationObserver();
		
		public MergeAnimationSceneModelAnimationObserverRelay(String animationName, List<IAnimationSceneModel> initialModels)
		{
			m_actionName = animationName;
			
			for(IAnimationSceneModel m : initialModels)
				addedModel(m);
		}
		
		public void addedModel(IAnimationSceneModel model)
		{
			if(model.hasAnimation(m_actionName))
				model.getAnimation(m_actionName).getObservers().add(m_animationObserver);
		}
		
		public void removedModel(IAnimationSceneModel model)
		{
			if(model.hasAnimation(m_actionName))
				model.getAnimation(m_actionName).getObservers().remove(m_animationObserver);
		}
		
		public IObserverRegistry getObservers()
		{
			return m_observers;
		}
		
		private class AnimationObserver implements IAnimationSceneModelAnimationObserver
		{
			AnimationSceneModelAnimationState m_lastState = null;
			
			@Override
			public void event(String name)
			{
				m_observers.raise(IAnimationSceneModelAnimationObserver.class).event(name);
			}

			@Override
			public void stateChanged(AnimationSceneModelAnimationState state)
			{
				if(m_lastState != state)
					m_observers.raise(IAnimationSceneModelAnimationObserver.class).stateChanged(state);
				
				m_lastState = state;
			}
		}
	}
}

