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
import io.github.jevaengine.math.*;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEffect;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEntry;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneComponentEffect;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;

import java.awt.*;
import java.util.Collection;

public final class DebugDrawComponent implements ISceneBufferEffect {
	private Vector2D translateWorldToScreen(Matrix3X3 projection, Vector3F location, float fScale) {
		Vector3D translation = projection.scale(fScale).dot(location).round();
		return new Vector2D(translation.x, translation.y);
	}

	private void debugDrawFront(Graphics2D g, int offsetX, int offsetY, float scale, Rect3F aabb, Matrix3X3 projection) {
		if (!aabb.hasVolume())
			return;

		//bottom face
		Vector2D bfA = translateWorldToScreen(projection, aabb.getPoint(0, 1.0F, 0), scale);
		Vector2D bfB = translateWorldToScreen(projection, aabb.getPoint(1.0F, 1.0F, 0), scale);
		Vector2D bfC = translateWorldToScreen(projection, aabb.getPoint(1.0F, 0, 0), scale);

		//top face
		Vector2D tfA = translateWorldToScreen(projection, aabb.getPoint(0, 1.0F, 1.0F), scale);
		Vector2D tfB = translateWorldToScreen(projection, aabb.getPoint(1.0F, 1.0F, 1.0F), scale);
		Vector2D tfC = translateWorldToScreen(projection, aabb.getPoint(1.0F, 0, 1.0F), scale);

		g.setColor(Color.blue);
		g.drawLine(offsetX + bfA.x, offsetY + bfA.y, offsetX + bfB.x, offsetY + bfB.y);
		g.drawLine(offsetX + bfB.x, offsetY + bfB.y, offsetX + bfC.x, offsetY + bfC.y);

		g.drawLine(offsetX + tfA.x, offsetY + tfA.y, offsetX + tfB.x, offsetY + tfB.y);
		g.drawLine(offsetX + tfB.x, offsetY + tfB.y, offsetX + tfC.x, offsetY + tfC.y);

		g.drawLine(offsetX + bfA.x, offsetY + bfA.y, offsetX + tfA.x, offsetY + tfA.y);
		g.drawLine(offsetX + bfB.x, offsetY + bfB.y, offsetX + tfB.x, offsetY + tfB.y);
		g.drawLine(offsetX + bfC.x, offsetY + bfC.y, offsetX + tfC.x, offsetY + tfC.y);
	}


	private void debugDrawBack(Graphics2D g, int offsetX, int offsetY, float scale, Rect3F aabb, Matrix3X3 projection) {
		if (!aabb.hasVolume())
			return;

		//bottom face
		Vector2D bfA = translateWorldToScreen(projection, aabb.getPoint(0, 1.0F, 0), scale);
		Vector2D bfB = translateWorldToScreen(projection, aabb.getPoint(0.0F, 0.0F, 0), scale);
		Vector2D bfC = translateWorldToScreen(projection, aabb.getPoint(1.0F, 0, 0), scale);

		//top face
		Vector2D tfA = translateWorldToScreen(projection, aabb.getPoint(0, 1.0F, 1), scale);
		Vector2D tfB = translateWorldToScreen(projection, aabb.getPoint(0.0F, 0.0F, 1), scale);
		Vector2D tfC = translateWorldToScreen(projection, aabb.getPoint(1.0F, 0, 1), scale);

		g.setColor(Color.green);
		g.drawLine(offsetX + bfA.x, offsetY + bfA.y, offsetX + bfB.x, offsetY + bfB.y);
		g.drawLine(offsetX + bfB.x, offsetY + bfB.y, offsetX + bfC.x, offsetY + bfC.y);

		g.drawLine(offsetX + tfA.x, offsetY + tfA.y, offsetX + tfB.x, offsetY + tfB.y);
		g.drawLine(offsetX + tfB.x, offsetY + tfB.y, offsetX + tfC.x, offsetY + tfC.y);

		g.drawLine(offsetX + bfA.x, offsetY + bfA.y, offsetX + tfA.x, offsetY + tfA.y);
		g.drawLine(offsetX + bfB.x, offsetY + bfB.y, offsetX + tfB.x, offsetY + tfB.y);
		g.drawLine(offsetX + bfC.x, offsetY + bfC.y, offsetX + tfC.x, offsetY + tfC.y);
	}


	@Override
	public ISceneComponentEffect[] getComponentEffect(final Graphics2D g, final int offsetX, final int offsetY, final float scale, final Vector2D renderLocation, final Matrix3X3 projection, final ISceneBufferEntry subject, final Collection<ISceneBufferEntry> beneath) {
		return new ISceneComponentEffect[]{
				new ISceneComponentEffect() {
					@Override
					public void prerender() {
						debugDrawBack(g, renderLocation.x + offsetX, renderLocation.y + offsetY, scale, subject.getComponent().getBounds(), projection);
					}

					@Override
					public void postrender() {
						debugDrawFront(g, renderLocation.x + offsetX, renderLocation.y + offsetY, scale, subject.getComponent().getBounds(), projection);
					}

                    @Override
                    public boolean ignore(IEntity dispatcher, IImmutableSceneModel.ISceneModelComponent c) {
                        return false;
                    }
                }
		};
	}
}
