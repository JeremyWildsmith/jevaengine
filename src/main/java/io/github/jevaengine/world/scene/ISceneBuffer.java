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
package io.github.jevaengine.world.scene;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel.ISceneModelComponent;
import java.awt.Graphics2D;
import java.util.Collection;

public interface ISceneBuffer extends IImmutableSceneBuffer
{		
	void addModel(IImmutableSceneModel model, @Nullable IEntity dispatcher, Vector3F location);
	void addModel(IImmutableSceneModel model, Vector3F location);
	void addEffect(ISceneBufferEffect effect);
	void reset();
	
	void translate(Vector2D translation);
	
	public interface ISceneBufferEffect
	{
		IRenderable getUnderlay();
		IRenderable getOverlay();
		
		void preRenderComponent(Graphics2D g, int offsetX, int offsetY, float scale, ISceneBufferEntry subject, Collection<ISceneBufferEntry> beneath);
		void postRenderComponent();
	}
	
	public interface ISceneBufferEntry
	{
		@Nullable
		IEntity getDispatcher();
		
		ISceneModelComponent getComponent();
		
		Rect2D getProjectedAABB();
	}
}
