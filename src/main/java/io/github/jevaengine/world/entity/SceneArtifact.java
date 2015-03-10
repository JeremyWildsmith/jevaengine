/*******************************************************************************
 * Copyright (c) 2013 Jeremy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * If you'd like to obtain a another license to this code, you may contact Jeremy to discuss alternative redistribution options.
 * 
 * Contributors:
 *     Jeremy - initial API and implementation
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.world.entity;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.NonparticipantPhysicsBody;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.physics.PhysicsBodyDescription;
import io.github.jevaengine.world.physics.PhysicsBodyDescription.PhysicsBodyShape;
import io.github.jevaengine.world.physics.PhysicsBodyDescription.PhysicsBodyType;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.ISceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class SceneArtifact implements IEntity
{
	private static AtomicInteger m_unnamedCount = new AtomicInteger(0);

	private final String m_name;
	
	@Nullable
	private ISceneModel m_model = new NullSceneModel();

	@Nullable
	private final PhysicsBodyDescription m_physicsBodyDescription;
	
	private IPhysicsBody m_body = new NullPhysicsBody();

	@Nullable
	private World m_world;
	
	private final Observers m_observers = new Observers();
	
	private final EntityBridge m_bridge;
	
	public SceneArtifact(ISceneModel model, boolean isTraversable)
	{
		m_name = this.getClass().getName() + m_unnamedCount.getAndIncrement();
		
		m_model = model;

		m_physicsBodyDescription = isTraversable ? null : new PhysicsBodyDescription(PhysicsBodyType.Static, PhysicsBodyShape.Box, model.getAABB(), 0.0F, true, false, 0.0F);
	
		m_bridge = new EntityBridge(this);
	}
	
	@Override
	public void dispose()
	{
		if(m_world != null)
			m_world.removeEntity(this);
		
		m_observers.clear();
	}

	@Override
	public String getInstanceName()
	{
		return m_name;
	}
	
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

		constructPhysicsBody();
		m_observers.raise(IEntityWorldObserver.class).enterWorld();
	}

	@Override
	public final void disassociate()
	{
		if (m_world == null)
			throw new WorldAssociationException("Not associated with world");

		m_observers.raise(IEntityWorldObserver.class).leaveWorld();

		destroyPhysicsBody();
		
		m_world = null;
	}

	private void constructPhysicsBody()
	{
		if(m_physicsBodyDescription == null)
			m_body = new NonparticipantPhysicsBody(this);
		else
			m_body = m_world.getPhysicsWorld().createBody(this, m_physicsBodyDescription);
	}
	
	private void destroyPhysicsBody()
	{
		m_body.destory();
		m_body = new NullPhysicsBody();
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}
	
	@Override
	public final IPhysicsBody getBody()
	{
		return m_body;
	}
	
	@Override
	public void update(int deltaTime)
	{
		m_model.update(deltaTime);
	}
	
	@Override
	@Nullable
	public IImmutableSceneModel getModel()
	{
		return m_model;
	}

	@Override
	public Map<String, Integer> getFlags()
	{
		return new HashMap<>();
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	@Override
	public EntityBridge getBridge()
	{
		return m_bridge;
	}

	@Override
	public IEntityTaskModel getTaskModel()
	{
		return new NullEntityTaskModel();
	}
}
