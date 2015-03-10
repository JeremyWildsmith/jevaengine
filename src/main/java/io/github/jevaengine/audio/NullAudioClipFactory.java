package io.github.jevaengine.audio;

import java.net.URI;

public final class NullAudioClipFactory implements IAudioClipFactory
{
	@Override
	public IAudioClip create(URI name)
	{
		return new NullAudioClip();
	}
}
