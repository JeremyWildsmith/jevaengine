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
package io.github.jevaengine.audio;

import io.github.jevaengine.audio.IAudioClipFactory.AudioClipConstructionException;
import io.github.jevaengine.util.IObserverRegistry;


public final class AutoDisposedAudioClip implements IAudioClip {
	private IAudioClip m_clip;

	public AutoDisposedAudioClip(IAudioClip clip) {
		m_clip = clip;
		m_clip.getObservers().add(new IAudioClipStateObserver() {

			@Override
			public void end() {
				dispose();
			}

			@Override
			public void begin() {
			}
		});
	}

	@Override
	public AutoDisposedAudioClip create() throws AudioClipConstructionException {
		return new AutoDisposedAudioClip(m_clip.create());
	}

	@Override
	public void dispose() {
		if (m_clip == null)
			return;

		m_clip.dispose();
		m_clip = null;
	}

	@Override
	public void play() {
		m_clip.play();
	}

	@Override
	public void stop() {
		m_clip.stop();
	}

	@Override
	public void repeat() {
		m_clip.repeat();
	}

	@Override
	public void setVolume(float volume) {
		m_clip.setVolume(volume);
	}

	@Override
	public IObserverRegistry getObservers() {
		return m_clip.getObservers();
	}
}
