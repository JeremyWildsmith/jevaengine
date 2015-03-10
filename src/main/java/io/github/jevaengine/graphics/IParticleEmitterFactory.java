package io.github.jevaengine.graphics;

import com.google.inject.ImplementedBy;
import java.net.URI;

@ImplementedBy(DefaultParticleEmitterFactory.class)
public interface IParticleEmitterFactory
{
	ParticleEmitter create(URI name) throws ParticleEmitterConstructionException;
	
	public static final class ParticleEmitterConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public ParticleEmitterConstructionException(URI assetName, Exception cause) {
			super("Error constructing particle emitter " + assetName.toString(), cause);
		}
	}
}
