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
package io.github.jevaengine.game;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntity.IEntityWorldObserver;
import io.github.jevaengine.world.scene.IImmutableSceneBuffer;
import io.github.jevaengine.world.scene.ISceneBufferFactory;

import java.lang.ref.WeakReference;

public final class FollowCamera implements ICamera
{
	private final ControlledCamera m_camera;
	private final EntityObserver m_observer = new EntityObserver();

	private WeakReference<IEntity> m_target;	
	
	public FollowCamera(ISceneBufferFactory sceneBufferFactory)
	{
		m_camera = new ControlledCamera(sceneBufferFactory);
		m_target = new WeakReference<>(null);
	}

	public void setTarget(@Nullable IEntity target)
	{
		if(m_target.get() != null)
			m_target.get().getObservers().remove(m_observer);
		
		m_target = new WeakReference<>(target);
		
		if(target != null)
			target.getObservers().add(m_observer);
	}

	@Override
	public Vector3F getLookAt()
	{
		if (m_target.get() == null)
			return new Vector3F();
		else
			return m_target.get().getBody().getLocation();
	}

	@Override
	public IImmutableSceneBuffer getScene(Rect2D bounds, float scale)
	{
		Vector3F target = m_target.get() == null ? new Vector3F() : m_target.get().getBody().getLocation();
		m_camera.lookAt(target);
		
		return m_camera.getScene(bounds, scale);
	}

	@Override
	public void dettach()
	{
		m_camera.dettach();
	}

	@Override
	public void attach(World world)
	{
		m_camera.attach(world);
	}
	
	private class EntityObserver implements IEntityWorldObserver
	{
		@Override
		public void leaveWorld()
		{
			setTarget(null);
		}

		@Override
		public void enterWorld() { }
	}
}
