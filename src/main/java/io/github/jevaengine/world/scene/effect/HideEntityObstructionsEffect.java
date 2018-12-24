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
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEffect;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEntry;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneComponentEffect;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public final class HideEntityObstructionsEffect implements ISceneBufferEffect {
	private final IEntity m_entity;
	private final Composite m_effectComposite;

	public HideEntityObstructionsEffect(IEntity entity, float alphaBlend) {
		m_entity = entity;
		m_effectComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaBlend);
	}

	@Override
	public IRenderable getUnderlay(Rect2D bounds, Matrix3X3 projection) {
		return new NullGraphic();
	}

	@Override
	public IRenderable getOverlay(Rect2D bounds, Matrix3X3 projection) {
		return new NullGraphic();
	}

	@Override
	public ISceneComponentEffect[] getComponentEffect(final Graphics2D g, final int offsetX, final int offsetY, final float scale, final Vector2D renderLocation, final Matrix3X3 projection, final ISceneBufferEntry subject, final Collection<ISceneBufferEntry> beneath) {
		return new ISceneComponentEffect[]{
				new ISceneComponentEffect() {
					private Shape m_oldClip;
					private boolean m_applied = false;

					@Override
					public void prerender() {
						for (ISceneBufferEntry e : beneath) {
							if (e.getDispatcher() == m_entity && subject.getDispatcher() != m_entity) {

								final Rect2D aabb = e.getProjectedAABB().add(new Vector2D(offsetX, offsetY));
								final int paddingX = aabb.width;
								final int paddingY = aabb.height / 4;
								final Shape ellipse = new Ellipse2D.Float(aabb.x - paddingX, aabb.y - paddingY, aabb.width + 2 * paddingX, aabb.height + 2 * paddingY);

								m_oldClip = g.getClip();
								Area area = new Area(new Rectangle2D.Float(0, 0, 10000, 10000));
								area.subtract(new Area(ellipse));
								g.setClip(area);
								m_applied = true;
								return;
							}
						}
					}

					@Override
					public void postrender() {
						if (m_applied) {
							g.setClip(m_oldClip);
							m_applied = false;
						}
					}

                    @Override
                    public boolean ignore(IEntity dispatcher, IImmutableSceneModel.ISceneModelComponent c) {
                        return false;
                    }
                },
				new ISceneComponentEffect() {
					private Shape m_oldClip;
					private Composite m_oldComposite;
					private boolean m_applied = false;

					@Override
					public void prerender() {
						for (ISceneBufferEntry e : beneath) {
							if (e.getDispatcher() == m_entity && subject.getDispatcher() != m_entity) {
								final Rect2D aabb = e.getProjectedAABB().add(new Vector2D(offsetX, offsetY));
								final int paddingX = aabb.width;
								final int paddingY = aabb.height / 4;
								final Shape ellipse = new Ellipse2D.Float(aabb.x - paddingX, aabb.y - paddingY, aabb.width + 2 * paddingX, aabb.height + 2 * paddingY);

								m_oldClip = g.getClip();
								m_oldComposite = g.getComposite();
								g.setClip(ellipse);
								g.setComposite(m_effectComposite);
								m_applied = true;
								return;
							}
						}
					}

					@Override
					public void postrender() {
						if (m_applied) {
							g.setClip(m_oldClip);
							g.setComposite(m_oldComposite);
							m_applied = false;
						}
					}

					@Override
					public boolean ignore(IEntity dispatcher, IImmutableSceneModel.ISceneModelComponent c) {
						return false;
					}
				}
		};
	}
}
