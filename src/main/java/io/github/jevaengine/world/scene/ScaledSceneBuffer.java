/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import java.awt.Graphics2D;

/**
 *
 * @author Jeremy
 */
public final class ScaledSceneBuffer implements ISceneBuffer
{
	private final float m_scale;
	private final ISceneBuffer m_buffer;
	
	public ScaledSceneBuffer(float scale, ISceneBuffer buffer)
	{
		m_scale = scale;
		m_buffer = buffer;
	}

	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation, float scale)
	{
		return m_buffer.translateScreenToWorld(screenLocation, scale * m_scale);
	}

	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation)
	{
		return m_buffer.translateScreenToWorld(screenLocation, m_scale);
	}

	@Override
	public Vector2D translateWorldToScreen(Vector3F location, float scale) {
		return m_buffer.translateWorldToScreen(location, scale * m_scale);
	}

	@Override
	public Vector2D translateWorldToScreen(Vector3F location)
	{
		return m_buffer.translateWorldToScreen(location, m_scale);
	}

	@Override
	public <T> T pick(Class<T> clazz, int x, int y, float scale)
	{
		return m_buffer.pick(clazz, x, y, scale * m_scale);
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		m_buffer.render(g, x, y, scale * m_scale);
	}

	@Override
	public void addModel(IImmutableSceneModel model, IEntity dispatcher, Vector3F location)
	{
		m_buffer.addModel(model, dispatcher, location);
	}

	@Override
	public void addModel(IImmutableSceneModel model, Vector3F location)
	{
		m_buffer.addModel(model, location);
	}

	@Override
	public void reset()
	{
		m_buffer.reset();
	}

	@Override
	public void translate(Vector2D translation)
	{
		m_buffer.translate(translation);
	}
}
