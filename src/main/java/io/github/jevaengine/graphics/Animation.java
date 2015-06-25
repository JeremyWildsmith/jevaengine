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

import io.github.jevaengine.util.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public final class Animation implements IImmutableAnimation
{
	private List<Frame> m_frames;

	private int m_curIndex;
	private int m_elapsedTime;

	private AnimationState m_state;

	private boolean m_playWrapBackwards = false;
	
	private final String m_name;
	
	@Nullable
	private IAnimationEventListener m_eventHandler;
	
	public Animation(Animation src)
	{
		m_curIndex = 0;
		m_elapsedTime = 0;
		m_frames = src.m_frames;
		m_state = src.m_state;
		m_name = src.m_name;
	}

	public Animation(String name)
	{
		m_curIndex = 0;
		m_elapsedTime = 0;
		m_frames = new ArrayList<>();
		m_state = AnimationState.Stop;
		m_name = name;
	}

	public Animation(String name, Frame... frames)
	{
		m_elapsedTime = 0;
		m_frames = new ArrayList<>();
		m_state = AnimationState.Stop;

		m_frames.addAll(Arrays.asList(frames));
		m_name = name;
	}
	
	private void triggerEvent(String name)
	{
		if(m_eventHandler != null)
			m_eventHandler.onFrameEvent(name);
	}
	
	private void triggerStateEvent()
	{
		if(m_eventHandler != null)
			m_eventHandler.onStateEvent();
	}

	@Override
	public String getName()
	{
		return m_name;
	}
	
	@Override
	public int getCurrentFrameIndex()
	{
		return m_curIndex;
	}
	
	@Override
	public int getTotalFrames()
	{
		return m_frames.size();
	}

	public void addFrame(Frame frame)
	{
		m_frames.add(frame);
	}

	public void setState(AnimationState state)
	{
		setState(state, null);
	}
	
	public void setState(AnimationState state, @Nullable IAnimationEventListener eventHandler)
	{
		m_playWrapBackwards = false;
		m_eventHandler = eventHandler;
		
		if(state != AnimationState.Stop)
			m_curIndex = 0;
		
		m_state = state;
	}
	
	public AnimationState getState()
	{
		return m_state;
	}

	public void update(int deltaTime)
	{
		if (m_frames.isEmpty() || m_state == AnimationState.Stop)
			return;

		m_elapsedTime += deltaTime;
		
		while (m_elapsedTime > getCurrentFrame().getDelay())
		{
			m_elapsedTime -= getCurrentFrame().getDelay();

			switch (m_state)
			{
				case Stop:
					break;
				case PlayToEnd:
					if (m_curIndex == m_frames.size() - 1)
					{
						m_state = AnimationState.Stop;
						triggerStateEvent();
						break;
					}
				case Play:
					if(++m_curIndex == m_frames.size())
					{
						m_curIndex = 0;
						triggerStateEvent();
					}
					break;
				case PlayWrap:
					if(m_curIndex == m_frames.size() - 1)
					{
						m_playWrapBackwards = true;
						triggerStateEvent();
					}else if(m_curIndex == 0) {
						m_playWrapBackwards = false;
						triggerStateEvent();
					}
					
					if(m_playWrapBackwards)
						m_curIndex = Math.max(0, m_curIndex - 1);
					else
						m_curIndex = (m_curIndex + 1) % m_frames.size();
					
					break;
				default:
					throw new UnknownAnimationStateException(m_state);
			}
			
			String event = m_frames.get(m_curIndex).getEvent();
			
			if(event != null)
				triggerEvent(event);
		}
	}

	public Frame getCurrentFrame()
	{
		if (m_curIndex >= m_frames.size())
			throw new NoSuchElementException();

		return m_frames.get(m_curIndex);
	}
	
	public interface IAnimationEventListener
	{
		void onFrameEvent(String name);
		void onStateEvent();
	}
}
