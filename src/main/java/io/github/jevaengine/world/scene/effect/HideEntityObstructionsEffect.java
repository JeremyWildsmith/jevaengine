/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.world.scene.effect;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.ISceneBuffer;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEffect;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEntry;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.Collection;

/**
 *
 * @author Jeremy
 */
public final class HideEntityObstructionsEffect implements ISceneBufferEffect
{
	private final IEntity m_entity;
	private final Composite m_effectComposite;
	private Graphics2D m_lastGraphics;
	private Composite m_lastComposite;

	public HideEntityObstructionsEffect(IEntity entity, float alphaBlend)
	{
		m_entity = entity;
		m_effectComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaBlend);
	}
	
	@Override
	public IRenderable getUnderlay()
	{
		return new NullGraphic();
	}

	@Override
	public IRenderable getOverlay()
	{
		return new NullGraphic();
	}
	
	@Override
	public void preRenderComponent(Graphics2D g, int offsetX, int offsetY, float scale, ISceneBuffer.ISceneBufferEntry subject, Collection<ISceneBuffer.ISceneBufferEntry> beneath)
	{
		for(ISceneBufferEntry e : beneath)
		{
			if(e.getDispatcher() == m_entity && subject.getDispatcher() != m_entity)
			{
				m_lastGraphics = g;
				m_lastComposite = g.getComposite();
				g.setComposite(m_effectComposite);
				return;
			}
		}
	}

	@Override
	public void postRenderComponent()
	{
		if(m_lastGraphics == null)
			return;
		
		m_lastGraphics.setComposite(m_lastComposite);
		
		m_lastGraphics = null;
		m_lastComposite = null;
	}
}
