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
package io.github.jevaengine.graphics;

import io.github.jevaengine.math.Rect2D;

import java.awt.*;

public final class NullFont implements IFont {
	@Override
	public Rect2D getTextBounds(String text, float scale) {
		return new Rect2D(1, 1);
	}

	@Override
	public Rect2D drawText(Graphics2D g, int x, int y, float scale, String text) {
		//Avoids divide by zero exception when being used.
		return new Rect2D(1, 1);
	}

	@Override
	public Rect2D getMaxCharacterBounds() {
		//Avoids divide by zero exception when being used.
		return new Rect2D(1, 1);
	}

	@Override
	public boolean doesMappingExists(char keyChar) {
		return false;
	}
}
