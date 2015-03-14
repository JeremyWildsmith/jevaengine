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
import io.github.jevaengine.world.scene.IImmutableSceneBuffer;
import io.github.jevaengine.world.scene.ISceneBufferFactory;

public final class ControlledCamera extends SceneBufferCamera
{
	private static final float MIN_ZOOM = 0.1F;
	
	private Vector3F m_lookAtTile;
	
	private float m_zoom = 1.0F;
	
	public ControlledCamera(ISceneBufferFactory sceneBufferFactory)
	{
		super(sceneBufferFactory);
		m_lookAtTile = new Vector3F();
	}

	public void setZoom(float zoom)
	{
		m_zoom = Math.max(MIN_ZOOM, zoom);
	}
	
	public float getZoom()
	{
		return m_zoom;
	}
	
	public void lookAt(Vector3F tileLocation)
	{
		m_lookAtTile = new Vector3F(tileLocation);
	}

	public void move(Vector3F delta)
	{
		m_lookAtTile = m_lookAtTile.add(delta);
	}

	@Override
	public Vector3F getLookAt()
	{
		return m_lookAtTile;
	}
	
	
	@Override
	public IImmutableSceneBuffer getScene(Rect2D bounds, float scale)
	{
		return super.getScene(bounds, scale * m_zoom);
	}

	@Override
	protected void onAttach() { }

	@Override
	protected void onDettach() { }
}
