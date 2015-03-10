package io.github.jevaengine.audio;

import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(CachedAudioClipFactory.class)
public interface IAudioClipFactory
{
	IAudioClip create(URI name) throws AudioClipConstructionException;

	public static final class AudioClipConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		public AudioClipConstructionException(URI assetName, Exception cause) {
			super("Error constructing audio clip " + assetName.toString(), cause);
		}
		
		public AudioClipConstructionException(String message, Exception cause) {
			super(message, cause);
		}
	}
}
