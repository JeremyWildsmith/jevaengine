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
import io.github.jevaengine.util.Observers;
import java.io.IOException;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class CachedAudioClip implements IAudioClip
{
	private AudioClipCache m_cache;
	
	private Clip m_clip;
	
	private float m_volume = 0.5F;

	private final Observers m_observers = new Observers();
	
	protected CachedAudioClip(AudioClipCache cache, Clip clip)
	{
		m_cache = cache;
		m_clip = clip;
		clip.addLineListener(new LineListener() {
			@Override
			public void update(LineEvent event) {
				if(event.getType() == Type.START)
					m_observers.raise(IAudioClipStateObserver.class).begin();
				else if(event.getType() == Type.STOP)
					m_observers.raise(IAudioClipStateObserver.class).end();
			}
		});
	}	

	@Override
	public IAudioClip create() throws AudioClipConstructionException
	{
		try
		{
			return new CachedAudioClip(m_cache, m_cache.getClip());
		} catch (IOException | LineUnavailableException | UnsupportedAudioFormatException | UnsupportedAudioFileException e)
		{
			throw new AudioClipConstructionException("Error occured constructing equivalent clip.", e);
		}
	}
	
	@Override
	public void dispose()
	{
		if(m_cache == null)
			return;
		
		m_cache.freeClip(m_clip);
		m_cache = null;
		m_clip = null;
	}

	private void applyVolume()
	{
		if(m_clip == null)
			return;
		
		if(m_clip.isControlSupported(FloatControl.Type.VOLUME))
		{
			FloatControl volumeControl = (FloatControl)m_clip.getControl(FloatControl.Type.VOLUME);
				
			if(m_volume >= 0.5F)
				volumeControl.setValue(volumeControl.getMaximum() * (m_volume - 0.5F) * 2.0F);
			else
				volumeControl.setValue(volumeControl.getMinimum() * (0.5F - m_volume) * 2.0F);	
		}
	}

	@Override
	public void setVolume(float volume)
	{
		m_volume = Math.max(0, Math.min(volume, 1.0F));
	}
	
	@Override
	public void play()
	{
		if(m_clip == null)
			return;

		m_clip.stop();
		m_clip.setMicrosecondPosition(0);
		m_clip.start();
		applyVolume();
	}

	@Override
	public void stop()
	{
		if(m_clip == null)
			return;
		
		if(m_clip != null)
			m_clip.stop();
	}

	@Override
	public void repeat()
	{	
		if(m_clip == null)
			return;
		
		m_clip.setFramePosition(0);
		m_clip.loop(Clip.LOOP_CONTINUOUSLY);
		applyVolume();
	}
	
	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
}
