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

import io.github.jevaengine.world.scene.model.particle.IParticleEmitter;
import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.NullFunctionFactory;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.NonparticipantPhysicsBody;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ParticleDriver implements IEntity
{
	private final Logger m_logger = LoggerFactory.getLogger(ParticleDriver.class);
	
	private final String m_name;

	private World m_world;
	
	private final HashMap<String, Integer> m_flags = new HashMap<>();
	
	private final ParticleDriverBridge m_bridge;
	
	private IPhysicsBody m_body = new NullPhysicsBody();
	
	private final Observers m_observers = new Observers();

	private final IParticleEmitter m_emitter;
	
	public ParticleDriver(String name, IParticleEmitter emitter)
	{
		m_name = name;	
		m_bridge = new ParticleDriverBridge();
		m_emitter = emitter;
	}
	
	@Override
	public void dispose()
	{
		m_observers.clear();
		
		if(m_world != null)
			m_world.removeEntity(this);
	}
	
	private void createPhysicsBody()
	{
		if(m_world == null)
			return;
		
		m_body = new NonparticipantPhysicsBody(this);

		m_observers.raise(IEntityBodyObserver.class).bodyChanged(new NullPhysicsBody(), m_body);
	}
	
	private void destoryPhysicsBody()
	{
		m_body.destory();
		m_body = new NullPhysicsBody();
		
		m_observers.raise(IEntityBodyObserver.class).bodyChanged(new NullPhysicsBody(), new NullPhysicsBody());
	}
	
	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
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
		return new NullEntityTaskModel();
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
		createPhysicsBody();
	}

	@Override
	public final void disassociate()
	{
		if (m_world == null)
			throw new WorldAssociationException("Not associated with world");

		m_observers.raise(IEntityWorldObserver.class).leaveWorld();
		destoryPhysicsBody();
		m_world = null;
	}
	
	public void setEmit(boolean emit)
	{
		m_emitter.setEmit(emit);
	}
	
	/*
	 * Scene model methods...
	 */
	@Override
	public final IImmutableSceneModel getModel()
	{
		return m_emitter;
	}
	
	@Override
	public final ParticleDriverBridge getBridge()
	{
		return m_bridge;
	}

	@Override
	public final void update(int deltaTime)
	{
		if (m_world == null)
			throw new WorldAssociationException("Entity is unassociated with a world and thus cannot process logic.");

		doLogic(deltaTime);
	}
	
	protected void doLogic(int deltaTime)
	{
		m_emitter.update(deltaTime);
	}
	
	public final class ParticleDriverBridge extends EntityBridge
	{	
		private ParticleDriverBridge()
		{
			super(ParticleDriver.this, new NullFunctionFactory(), URI.create(""));
		}
		
		public void setEmit(boolean emit)
		{
			ParticleDriver.this.setEmit(emit);
		}
		
		public final void setFlag(String name, int value)
		{
			ParticleDriver.this.setFlag(name, value);
		}
		
		public final void clearFlag(String name)
		{
			ParticleDriver.this.clearFlag(name);
		}
		
		public final void clearFlags()
		{
			ParticleDriver.this.clearFlags();
		}
	}
}

