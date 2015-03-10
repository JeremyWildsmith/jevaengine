package io.github.jevaengine.audio;

import io.github.jevaengine.audio.IAudioClipFactory.AudioClipConstructionException;
import io.github.jevaengine.util.IObserverRegistry;


public final class AutoDisposedAudioClip implements IAudioClip
{
	private IAudioClip m_clip;

	public AutoDisposedAudioClip(IAudioClip clip)
	{
		m_clip = clip;
		m_clip.getObservers().add(new IAudioClipStateObserver() {
			
			@Override
			public void end()
			{
				dispose();
			}
			
			@Override
			public void begin() { }
		});
	}
	
	@Override
	public AutoDisposedAudioClip create() throws AudioClipConstructionException
	{
		return new AutoDisposedAudioClip(m_clip.create());
	}
	
	@Override
	public void dispose()
	{
		if(m_clip == null)
			return;
		
		m_clip.dispose();
		m_clip = null;
	}

	@Override
	public void play()
	{
		m_clip.play();
	}

	@Override
	public void stop()
	{
		m_clip.stop();
	}

	@Override
	public void repeat()
	{
		m_clip.repeat();
	}

	@Override
	public void setVolume(float volume)
	{
		m_clip.setVolume(volume);
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return m_clip.getObservers();
	}
}
