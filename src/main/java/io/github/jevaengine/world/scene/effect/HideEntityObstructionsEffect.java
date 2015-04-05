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
package io.github.jevaengine.world.scene.effect;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEffect;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEntry;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneComponentEffect;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.Collection;

public final class HideEntityObstructionsEffect implements ISceneBufferEffect
{
	private final IEntity m_entity;
	private final Composite m_effectComposite;
	
	public HideEntityObstructionsEffect(IEntity entity, float alphaBlend)
	{
		m_entity = entity;
		m_effectComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaBlend);
	}
	
	@Override
	public IRenderable getUnderlay(Rect2D bounds, Matrix3X3 projection)
	{
		return new NullGraphic();
	}

	@Override
	public IRenderable getOverlay(Rect2D bounds, Matrix3X3 projection)
	{
		return new NullGraphic();
	}
	
	@Override
	public ISceneComponentEffect getComponentEffect(final Graphics2D g, final int offsetX, final int offsetY, final float scale, final Matrix3X3 projection, final ISceneBufferEntry subject, final Collection<ISceneBufferEntry> beneath)
	{
		return new ISceneComponentEffect() {
			private Composite m_oldComposite;
			@Override
			public void prerender()
			{
				for(ISceneBufferEntry e : beneath)
				{
					if(e.getDispatcher() == m_entity && subject.getDispatcher() != m_entity)
					{
						m_oldComposite = g.getComposite();
						g.setComposite(m_effectComposite);
						return;
					}
				}
			}

			@Override
			public void postrender()
			{
				if(m_oldComposite != null)
					g.setComposite(m_oldComposite);
				
				m_oldComposite = null;
			}
		};
	}
}
