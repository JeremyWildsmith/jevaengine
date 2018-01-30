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
package io.github.jevaengine.ui.style;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.audio.IAudioClip;
import io.github.jevaengine.graphics.IFont;
import io.github.jevaengine.graphics.IImmutableGraphic;

public final class ComponentStateStyle implements IDisposable {
	private IFont m_font;
	private IFrameFactory m_frameFactory;
	private IAudioClip m_enterAudio;

	public ComponentStateStyle(IFont font, IFrameFactory frameFactory, IAudioClip enterAudio) {
		m_font = font;
		m_frameFactory = frameFactory;
		m_enterAudio = enterAudio;
	}

	@Override
	public void dispose() {
		m_enterAudio.dispose();
	}

	public IFont getFont() {
		return m_font;
	}

	public IImmutableGraphic createFrame(int desiredWidth, int desiredHeight) {
		return m_frameFactory.create(desiredWidth, desiredHeight);
	}

	public void playEnter() {
		m_enterAudio.play();
	}
}