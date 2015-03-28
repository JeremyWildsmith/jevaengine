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
package io.github.jevaengine.world.entity.tasks;

import io.github.jevaengine.audio.IAudioClip;
import io.github.jevaengine.audio.IAudioClipStateObserver;
import io.github.jevaengine.world.entity.IEntity;

public class PlayAudioTask implements ITask
{
	private volatile boolean m_isPlaying = true;
	
	private IAudioClip m_clip;
	
	public PlayAudioTask(IAudioClip clip)
	{
		m_clip = clip;
		m_clip.stop();
		m_clip.getObservers().add(new IAudioClipStateObserver() {
			
			@Override
			public void end()
			{
				m_isPlaying = false;
			}
			
			@Override
			public void begin() { }
		});
	}
	
	@Override
	public void begin(IEntity entity)
	{
		m_clip.play();
	}

	@Override
	public void end()
	{
		m_clip.dispose();
	}

	@Override
	public void cancel()
	{
		m_clip.stop();
	}

	@Override
	public boolean doCycle(int deltaTime)
	{
		return !m_isPlaying;
	}

	@Override
	public boolean isParallel()
	{
		return true;
	}
	
}
