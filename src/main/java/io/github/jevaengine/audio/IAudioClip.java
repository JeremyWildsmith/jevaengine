package io.github.jevaengine.audio;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.audio.IAudioClipFactory.AudioClipConstructionException;
import io.github.jevaengine.util.IObserverRegistry;

public interface IAudioClip extends IDisposable
{
	IAudioClip create() throws AudioClipConstructionException;
	
	void play();
	void stop();
	void repeat();
	void setVolume(float volume);
	
	IObserverRegistry getObservers();
}
