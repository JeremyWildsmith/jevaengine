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
package io.github.jevaengine.ui;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.ui.style.ComponentStyle;
import io.github.jevaengine.ui.style.IUIStyle;

public interface IImmutableControl extends IRenderable {
	boolean hasFocus();

	String getInstanceName();

	Vector2D getLocation();

	Vector2D getAbsoluteLocation();

	ComponentStyle getComponentStyle();

	IUIStyle getStyle();

	IImmutableControl getParent();

	boolean isVisible();

	boolean onMouseEvent(InputMouseEvent mouseEvent);

	boolean onKeyEvent(InputKeyEvent keyEvent);

	Rect2D getBounds();

	void update(int deltaTime);
}
