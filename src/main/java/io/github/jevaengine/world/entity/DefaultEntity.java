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
package io.github.jevaengine.world.entity;

import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.audio.NullAudioClipFactory;
import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilder.ScriptConstructionException;
import io.github.jevaengine.script.NullFunctionFactory;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.NonparticipantPhysicsBody;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.physics.PhysicsBodyDescription;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.ISceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;
import java.net.URI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultEntity implements IEntity
{
	private final Logger m_logger = LoggerFactory.getLogger(DefaultEntity.class);
	
	private final String m_name;

	private World m_world;
	
	private HashMap<String, Integer> m_flags = new HashMap<>();
	private PhysicsBodyDescription m_physicsBodyDescription = null;
	
	private DefaultEntityBridge m_bridge;
	private final IEntityTaskModel m_taskModel;
	
	private ISceneModel m_model = new NullSceneModel();
	private IPhysicsBody m_body = new NullPhysicsBody();
	
	private final Observers m_observers = new Observers();
	
	public DefaultEntity(IEntityTaskModelFactory taskModelFactory)
	{
		this(taskModelFactory, null);
	}

	public DefaultEntity(IEntityTaskModelFactory taskModelFactory, String name)
	{
		m_name = name;	
		m_bridge = new DefaultEntityBridge(new NullAudioClipFactory(), new NullFunctionFactory(), URI.create(""));

		m_taskModel = taskModelFactory.create(this);
	}
	
	public DefaultEntity(IEntityTaskModelFactory taskModelFactory, ISceneModel model, IScriptBuilder behavior, IAudioClipFactory audioClipFactory, PhysicsBodyDescription physicsBodyDescription, String name)
	{
		m_name = name;	
		m_model = model;
		m_physicsBodyDescription = physicsBodyDescription;
		m_taskModel = taskModelFactory.create(this);
	
		m_bridge = new DefaultEntityBridge(audioClipFactory, behavior.getFunctionFactory(), behavior.getUri());
		
		try
		{
			behavior.create(m_bridge);
		} catch (ScriptConstructionException e)
		{
			m_logger.error("Unable to instantiate behavior for entity " + m_name + ". Assuming null behavior", e);
		}
	}
	
	@Override
	public void dispose()
	{
		m_observers.clear();
		
		if(m_world != null)
			m_world.removeEntity(this);
	}
	
	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	/*
	 * Physics body routines.
	 */
	private void constructPhysicsBody()
	{
		if(m_physicsBodyDescription == null)
			m_body = new NonparticipantPhysicsBody(this);
		else
			m_body = m_world.getPhysicsWorld().createBody(this, m_physicsBodyDescription);
		
		m_observers.raise(IEntityBodyObserver.class).bodyChanged(new NullPhysicsBody(), m_body);
	}
	
	private void destroyPhysicsBody()
	{
		m_body.destory();
		m_body = new NullPhysicsBody();
		
		//Since we are destroying the old body, there is nothing the observer can
		//or needs to do to the old body (ie, remove observers.) Thus, it is effectivley
		//equivlent to passing NullPhysicsBody.
		m_observers.raise(IEntityBodyObserver.class).bodyChanged(new NullPhysicsBody(), new NullPhysicsBody());
	}
	
	@Override
	public final IPhysicsBody getBody()
	{
		return m_body;
	}
	
	/*
	 * Primitive entity property accessors.
	 */
	
	@Override
	public final String getInstanceName()
	{
		return m_name;
	}
	
	@Override
	public boolean isStatic()
	{
		return false;
	}
	
	@Override
	public IEntityTaskModel getTaskModel()
	{
		return m_taskModel;
	}
	
	/*
	 * Flag operations.
	 */
	public final void setFlag(String name, int value)
	{
		m_flags.put(name, value);
		m_observers.raise(IEntityFlagObserver.class).flagSet(name, value);
	}
		
	public final void setFlag(String name)
	{
		setFlag(name, 0);
	}
		
	public final void clearFlag(String name)
	{
		m_flags.remove(name);
		m_observers.raise(IEntityFlagObserver.class).flagCleared(name);
	}

	public final void clearFlags()
	{
		m_flags.clear();
	}
	
	@Override
	public final Map<String, Integer> getFlags()
	{
		return Collections.unmodifiableMap(m_flags);
	}

	/*
	 * World association routines.
	 */
	@Override
	public final World getWorld()
	{
		return m_world;
	}

	@Override
	public final void associate(World world)
	{
		if (m_world != null)
			throw new WorldAssociationException("Already associated with world");

		m_world = world;

		m_observers.raise(IEntityWorldObserver.class).enterWorld();
		
		constructPhysicsBody();
	}

	@Override
	public final void disassociate()
	{
		if (m_world == null)
			throw new WorldAssociationException("Not associated with world");

		destroyPhysicsBody();
		
		m_taskModel.cancelTasks();

		m_observers.raise(IEntityWorldObserver.class).leaveWorld();

		m_world = null;
	}
	
	/*
	 * Scene model methods...
	 */
	@Override
	public final IImmutableSceneModel getModel()
	{
		return m_model;
	}
	
	@Override
	public final DefaultEntityBridge getBridge()
	{
		return m_bridge;
	}

	@Override
	public final void update(int deltaTime)
	{
		if (m_world == null)
			throw new WorldAssociationException("Entity is unassociated with a world and thus cannot process logic.");

		m_taskModel.update(deltaTime);
		m_model.update(deltaTime);
		doLogic(deltaTime);
	}
	
	protected void doLogic(int deltaTime) { }
	
	public final class DefaultEntityBridge extends EntityBridge
	{	
		private DefaultEntityBridge(IAudioClipFactory audioClipFactory, IFunctionFactory functionFactory, URI scriptUri)
		{
			super(DefaultEntity.this, audioClipFactory, functionFactory, scriptUri);
		}
		public final void setFlag(String name, int value)
		{
			DefaultEntity.this.setFlag(name, value);
		}
		
		public final void clearFlag(String name)
		{
			DefaultEntity.this.clearFlag(name);
		}
		
		public final void clearFlags()
		{
			DefaultEntity.this.clearFlags();
		}
	}
}
