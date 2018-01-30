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
package io.github.jevaengine.script.rhino;

import io.github.jevaengine.script.IScriptArray;
import org.mozilla.javascript.NativeArray;

public final class RhinoArray implements IScriptArray {
	private NativeArray m_array;

	public RhinoArray(NativeArray array) {
		m_array = array;
	}

	@Override
	public int getLength() {
		return (int) m_array.getLength();
	}

	@Override
	public Object getElement(int index) {
		return m_array.get(index, null);
	}
}
