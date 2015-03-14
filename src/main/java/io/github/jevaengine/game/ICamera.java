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
import io.github.jevaengine.world.World;

public interface ICamera
{
	IImmutableSceneBuffer getScene(Rect2D bounds, float scale);
	
	Vector3F getLookAt();
	
	void dettach();
	void attach(World world);
}
