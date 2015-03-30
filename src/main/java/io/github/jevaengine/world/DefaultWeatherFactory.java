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
package io.github.jevaengine.world;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.audio.IAudioClip;
import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.audio.IAudioClipFactory.AudioClipConstructionException;
import io.github.jevaengine.audio.NullAudioClip;
import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.graphics.IGraphicFactory;
import io.github.jevaengine.graphics.IGraphicFactory.GraphicConstructionException;
import io.github.jevaengine.graphics.IImmutableGraphic;
import io.github.jevaengine.graphics.IParticleEmitter;
import io.github.jevaengine.graphics.IParticleEmitterFactory;
import io.github.jevaengine.graphics.IParticleEmitterFactory.ParticleEmitterConstructionException;
import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.DefaultWeatherFactory.DefaultWeatherDeclaration.DefaultWeatherPhaseDeclaration;
import io.github.jevaengine.world.scene.ISceneBuffer;
import java.awt.Graphics2D;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

public final class DefaultWeatherFactory implements IWeatherFactory
{
	private static final int NUM_EMITTERS = 5;
	
	private final IConfigurationFactory m_configurationFactory;
	private final IAudioClipFactory m_audioClipFactory;
	private final IParticleEmitterFactory m_particleEmitterFactory;
	private final IGraphicFactory m_graphicFactory;
	
	@Inject
	public DefaultWeatherFactory(IConfigurationFactory configurationFactory, IAudioClipFactory audioClipFactory, IParticleEmitterFactory particleEmitterFactory, IGraphicFactory graphicFactory)
	{
		m_configurationFactory = configurationFactory;
		m_audioClipFactory = audioClipFactory;
		m_particleEmitterFactory = particleEmitterFactory;
		m_graphicFactory = graphicFactory;
	}
	
	@Override
	public IWeather create(URI name) throws WeatherConstructionException
	{
		
		List<IParticleEmitter> emitters = new ArrayList<>();
		List<DefaultWeatherPhase> phases = new ArrayList<>();
		IAudioClip ambientAudioClip = new NullAudioClip();
			
		try
		{
			DefaultWeatherDeclaration decl = m_configurationFactory.create(name).getValue(DefaultWeatherDeclaration.class);
			
			for(DefaultWeatherPhaseDeclaration p : decl.phases)
			{
				IAudioClip clip = p.audio == null ? new NullAudioClip() : m_audioClipFactory.create(name.resolve(new URI(p.audio)));
				IImmutableGraphic overlay = p.overlay == null ? new NullGraphic() : m_graphicFactory.create(name.resolve(new URI(p.overlay)));
				phases.add(new DefaultWeatherPhase(clip, overlay, p.period, p.length));
			}
			
			if(decl.shower != null)
			{
				for(int i = 0; i < NUM_EMITTERS; i++)
					emitters.add(m_particleEmitterFactory.create(name.resolve(new URI(decl.shower))));
			}
			
			if(decl.ambientAudio != null)
				ambientAudioClip = m_audioClipFactory.create(name.resolve(new URI(decl.ambientAudio)));
			
			Vector2D showerDeltaPlacement = decl.showerDeltaPlacement == null ? new Vector2D() : decl.showerDeltaPlacement;
			
			return new DefaultWeather(phases.toArray(new DefaultWeatherPhase[phases.size()]),
																emitters.toArray(new IParticleEmitter[emitters.size()]),
																showerDeltaPlacement, ambientAudioClip);
		} catch (ConfigurationConstructionException |
							ValueSerializationException |
							ParticleEmitterConstructionException |
							AudioClipConstructionException |
							URISyntaxException |
							GraphicConstructionException e)
		{
			
			ambientAudioClip.dispose();
			
			for(DefaultWeatherPhase p : phases)
				p.dispose();
			
			throw new WeatherConstructionException(e);
		}
	}
	
	private static final class DefaultWeather implements IWeather
	{
		private final IParticleEmitter m_shower[];
		private final DefaultWeatherPhase m_phases[];
		private final Vector2D m_showerDeltaPlacement;
		
		private final IAudioClip m_ambientAudio;
		
		public DefaultWeather(DefaultWeatherPhase phases[], IParticleEmitter shower[], Vector2D showerDeltaPlacement, IAudioClip ambientAudio)
		{
			m_phases = phases;
			m_shower = shower;
			m_showerDeltaPlacement = showerDeltaPlacement;
			m_ambientAudio = ambientAudio;
			
			for(IParticleEmitter e : m_shower)
				e.setEmit(true);
			
			for(int i = 0; i < 50; i++)
			{
				for(IParticleEmitter e : m_shower)
					e.update(1000);
			}
			
			m_ambientAudio.repeat();
		}
		
		@Override
		public void update(int deltaTime)
		{
			for(IParticleEmitter e : m_shower)
				e.update(deltaTime);
			
			for(DefaultWeatherPhase p : m_phases)
				p.update(deltaTime);
		}

		@Override
		public void dispose()
		{
			m_ambientAudio.dispose();
		}

		@Override
		public IRenderable getUnderlay(Rect2D bounds)
		{
			return new NullGraphic();
		}

		@Override
		public IRenderable getOverlay(final Rect2D bounds)
		{
			return new IRenderable() {
				@Override
				public void render(Graphics2D g, int x, int y, float scale)
				{
					if(m_shower.length == 0)
						return;
					
					Vector2D current = new Vector2D();
					Vector2D end = new Vector2D(bounds.width, bounds.height);
					for(int i = 0; current.x < end.x && current.y < end.y; i++)
					{
						m_shower[i % m_shower.length].render(g, x + current.x, y + current.y, scale);
						current = current.add(m_showerDeltaPlacement);
					}
					
					for(DefaultWeatherPhase p : m_phases)
						p.render(g, x, y, scale, bounds);
				}
			};
		}

		@Override
		public void preRenderComponent(Graphics2D g, int offsetX, int offsetY, float scale, ISceneBuffer.ISceneBufferEntry subject, Collection<ISceneBuffer.ISceneBufferEntry> beneath) { }

		@Override
		public void postRenderComponent() { }
	}

	private final class DefaultWeatherPhase implements IDisposable
	{
		private final IAudioClip m_audioClip;
		private final IImmutableGraphic m_overlay;
		private final int m_period;
		private final int m_length;

		private int m_activeTimeOut = 0;
		private int m_idleTimeout = 0;

		public DefaultWeatherPhase(IAudioClip audioClip, IImmutableGraphic overlay, int period, int length)
		{
			m_audioClip = audioClip;
			m_overlay = overlay;
			m_period = period;
			m_length = length;

			//Random phase shift for each phase.
			m_idleTimeout = (int)(m_period * (float)Math.random());
		}

		@Override
		public void dispose()
		{
			m_audioClip.dispose();
		}
		
		public void update(int deltaTime)
		{
			if(m_activeTimeOut > 0)
			{
				m_activeTimeOut -= deltaTime;

				if(m_activeTimeOut <= 0)
					m_idleTimeout = m_period;

				if(m_activeTimeOut < 0)
				{
					int timeout = m_activeTimeOut;
					m_activeTimeOut = 0;
					update(-timeout);
				}
			}else
			{
				m_idleTimeout -= deltaTime;

				if(m_idleTimeout <= 0)
				{
					m_audioClip.play();
					m_activeTimeOut = m_length;
				}
				
				if(m_idleTimeout < 0)
				{
					int timeout = m_idleTimeout;
					m_idleTimeout = 0;
					update(-timeout);
				}
			}
		}

		public void render(Graphics2D g, int x, int y, float scale, Rect2D bounds)
		{
			if(m_activeTimeOut > 0)
				m_overlay.render(g, x + bounds.x, y + bounds.y, bounds.width, bounds.height, 0, 0, m_overlay.getBounds().width, m_overlay.getBounds().height);
		}
	}
			
	public static final class DefaultWeatherDeclaration implements ISerializable
	{
		@Nullable
		public String ambientAudio;
		
		@Nullable
		public String shower;
		
		@Nullable
		public Vector2D showerDeltaPlacement;
		
		public DefaultWeatherPhaseDeclaration[] phases;
		
		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			if(ambientAudio != null)
				target.addChild("ambientAudio").setValue(ambientAudio);
			
			if(shower != null)
				target.addChild("shower").setValue(shower);
			
			if(showerDeltaPlacement != null)
				target.addChild("showerDeltaPlacement").setValue(showerDeltaPlacement);
			
			target.addChild("phases").setValue(phases);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				if(source.childExists("ambientAudio"))
					ambientAudio = source.getChild("ambientAudio").getValue(String.class);
				
				if(source.childExists("shower"))
					shower = source.getChild("shower").getValue(String.class);
				
				if(source.childExists("showerDeltaPlacement"))
					showerDeltaPlacement = source.getChild("showerDeltaPlacement").getValue(Vector2D.class);
				
				phases = source.getChild("phases").getValues(DefaultWeatherPhaseDeclaration[].class);
			} catch (NoSuchChildVariableException e)
			{
				throw new ValueSerializationException(e);
			}
		}

		public static final class DefaultWeatherPhaseDeclaration implements ISerializable
		{
			public String overlay;
			public String audio;
			public int period;
			public int length;
			
			@Override
			public void serialize(IVariable target) throws ValueSerializationException
			{
				if(overlay != null)
					target.addChild("overlay").setValue(overlay);
				
				if(audio != null)
					target.addChild("audio").setValue(audio);
				
				target.addChild("period").setValue(period);
				target.addChild("length").setValue(length);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException
			{
				try
				{
					if(source.childExists("overlay"))
						overlay = source.getChild("overlay").getValue(String.class);
					
					if(source.childExists("audio"))
						audio = source.getChild("audio").getValue(String.class);
					
					period = source.getChild("period").getValue(Integer.class);
					length = source.getChild("length").getValue(Integer.class);
				} catch (NoSuchChildVariableException e)
				{
					throw new ValueSerializationException(e);
				}
			}
			
		}
	}
}
