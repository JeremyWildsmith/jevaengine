package io.github.jevaengine.audio;

import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;

public final class NullAudioClip implements IAudioClip
{
	@Override
	public IAudioClip create()
	{
		return new NullAudioClip();
	}
	
	@Override
	public void dispose() { }
	
	@Override
	public void play() { }

	@Override
	public void stop() { }

	@Override
	public void repeat() { }

	@Override
	public void setVolume(float volume) { }
	
	@Override
	public IObserverRegistry getObservers()
	{
		return new NullObservers();
	}
}
