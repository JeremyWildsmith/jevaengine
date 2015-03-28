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
package io.github.jevaengine.world.scene.model.sprite;

import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.audio.IAudioClipFactory.AudioClipConstructionException;
import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.graphics.ISpriteFactory;
import io.github.jevaengine.graphics.ISpriteFactory.SpriteConstructionException;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.AnimationSceneModelAnimationState;
import io.github.jevaengine.world.scene.model.IAnimationSceneModelFactory;
import io.github.jevaengine.world.scene.model.sprite.SpriteSceneModel.SpriteSceneModelAnimation;
import io.github.jevaengine.world.scene.model.sprite.SpriteSceneModelFactory.DefaultSceneModelDeclaration.DefaultSceneModelAnimationDeclaration;
import io.github.jevaengine.world.scene.model.sprite.SpriteSceneModelFactory.DefaultSceneModelDeclaration.DefaultSceneModelAnimationDeclaration.DefaultSceneModelAnimationAudioDeclaration;
import io.github.jevaengine.world.scene.model.sprite.SpriteSceneModelFactory.DefaultSceneModelDeclaration.DefaultSceneModelAnimationDeclaration.DefaultSceneModelAnimationDirectionDeclaration;
import io.github.jevaengine.world.scene.model.sprite.SpriteSceneModelFactory.DefaultSceneModelDeclaration.DefaultSceneModelAnimationDeclaration.DefaultSceneModelAnimationDirectionDeclaration.DefaultSceneModelAnimationComponentDeclaration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SpriteSceneModelFactory implements IAnimationSceneModelFactory
{
	private static final AtomicInteger m_unnamedComponentCount = new AtomicInteger();
	private final Logger m_logger = LoggerFactory.getLogger(SpriteSceneModelFactory.class);
	private final IConfigurationFactory m_configurationFactory;
	private final ISpriteFactory m_spriteFactory;
	private final IAudioClipFactory m_audioClipFactory;
	
	@Inject
	public SpriteSceneModelFactory(IConfigurationFactory configurationFactory, ISpriteFactory spriteFactory, IAudioClipFactory audioClipFactory)
	{
		m_configurationFactory = configurationFactory;
		m_spriteFactory = spriteFactory;
		m_audioClipFactory = audioClipFactory;
 	}
	
	@Override
	public SpriteSceneModel create(URI name) throws SceneModelConstructionException
	{
		try
		{
			DefaultSceneModelDeclaration modelDecl = m_configurationFactory.create(name).getValue(DefaultSceneModelDeclaration.class);
		
			SpriteSceneModel model = new SpriteSceneModel();
			
			for(DefaultSceneModelAnimationDeclaration a : modelDecl.animations)
			{
				SpriteSceneModelAnimation animation = model.new SpriteSceneModelAnimation();
				
				for(DefaultSceneModelAnimationDirectionDeclaration d : a.sets)
				{
					for(Direction direction : d.directions)
					{
						for(DefaultSceneModelAnimationComponentDeclaration c : d.components)
						{
							try
							{
								SpriteSceneModelComponent component = new SpriteSceneModelComponent(
													c.name != null ? c.name : SpriteSceneModelFactory.class.getName() + m_unnamedComponentCount.getAndIncrement(),
													m_spriteFactory.create(name.resolve(new URI(c.sprite))),
													c.animation,
													c.bounds,
													c.origin
												);

								animation.addComponent(direction, component);
							} catch (SpriteConstructionException | URISyntaxException e) {
								m_logger.error("Error occured constructing sprite component, assuming sprite component does not exist.", e);
							}
						}
					}
				}
				
				for(DefaultSceneModelAnimationAudioDeclaration audio : a.audio)
				{
					try
					{
						animation.addEventAudio(audio.trigger, m_audioClipFactory.create(name.resolve(new URI(audio.audio))));
					} catch (URISyntaxException | AudioClipConstructionException e)
					{
						m_logger.error("Error creating animation audio event " + audio.audio + ". Assuming no such audio event exists", e);
					}
				}
				
				model.addAnimation(a.name, animation);
			}
			
			if(model.hasAnimation(modelDecl.defaultAnimation))
				model.getAnimation(modelDecl.defaultAnimation).setState(AnimationSceneModelAnimationState.Play);
			else
				m_logger.error("Default animation of " + modelDecl.defaultAnimation + " was not found in model.");
			
			return model;
		} catch (ConfigurationConstructionException | ValueSerializationException e)
		{
			throw new SceneModelConstructionException(name, e);
		}
	}
	
	public static final class DefaultSceneModelDeclaration implements ISerializable
	{
		public DefaultSceneModelAnimationDeclaration[] animations;
		public String defaultAnimation;
		
		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("animations").setValue(animations);
			target.addChild("defaultAnimation").setValue(defaultAnimation);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try {
				animations = source.getChild("animations").getValues(DefaultSceneModelAnimationDeclaration[].class);
				defaultAnimation = source.getChild("defaultAnimation").getValue(String.class);
			} catch (NoSuchChildVariableException e) {
				throw new ValueSerializationException(e);
			}
		}
		
		public static final class DefaultSceneModelAnimationDeclaration implements ISerializable
		{
			public String name;
			public DefaultSceneModelAnimationDirectionDeclaration[] sets;
			public DefaultSceneModelAnimationAudioDeclaration[] audio = new DefaultSceneModelAnimationAudioDeclaration[0];
			
			@Override
			public void serialize(IVariable target) throws ValueSerializationException
			{
				target.addChild("name").setValue(name);
				target.addChild("sets").setValue(sets);
				
				if(audio.length > 0)
					target.addChild("audio").setValue(audio);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException
			{
				try
				{
					name = source.getChild("name").getValue(String.class);
					sets = source.getChild("sets").getValues(DefaultSceneModelAnimationDirectionDeclaration[].class);
				
					if(source.childExists("audio"))
						audio = source.getChild("audio").getValues(DefaultSceneModelAnimationAudioDeclaration[].class);
					
				} catch (NoSuchChildVariableException e) {
					throw new ValueSerializationException(e);
				}
			}
			
			public static final class DefaultSceneModelAnimationAudioDeclaration implements ISerializable
			{
				public String audio;
				public String trigger;
				
				@Override
				public void serialize(IVariable target) throws ValueSerializationException
				{
					target.addChild("audio").setValue(audio);
					target.addChild("trigger").setValue(trigger);
				}

				@Override
				public void deserialize(IImmutableVariable source) throws ValueSerializationException
				{
					try {
						audio = source.getChild("audio").getValue(String.class);
						trigger = source.getChild("trigger").getValue(String.class);
					} catch (NoSuchChildVariableException ex) {
						throw new ValueSerializationException(ex);
					}
				}
				
			}
			
			public static final class DefaultSceneModelAnimationDirectionDeclaration implements ISerializable
			{
				public Direction[] directions;
				public DefaultSceneModelAnimationComponentDeclaration[] components;
				
				@Override
				public void serialize(IVariable target) throws ValueSerializationException
				{
					Integer dirBuffer[] = new Integer[directions.length];
					
					for(int i = 0; i < directions.length; i++)
						dirBuffer[i] = directions[i].ordinal();
					
					target.addChild("directions").setValue(dirBuffer);
					target.addChild("components").setValue(components);
				}
				
				@Override
				public void deserialize(IImmutableVariable source) throws ValueSerializationException
				{
					try
					{
						Integer directionOrdinals[] = source.getChild("directions").getValues(Integer[].class);
						directions = new Direction[directionOrdinals.length];
						
						for(int i = 0; i < directionOrdinals.length; i++)
						{
							if(directionOrdinals[i] < 0 || directionOrdinals[i] > Direction.values().length)
								throw new ValueSerializationException(new IndexOutOfBoundsException("Direction ordinal outside of bounds."));
							
							directions[i] = Direction.values()[directionOrdinals[i]];
						}
						
						components = source.getChild("components").getValues(DefaultSceneModelAnimationComponentDeclaration[].class);
					} catch (NoSuchChildVariableException e) {
						throw new ValueSerializationException(e);
					}
				}
				
				public static final class DefaultSceneModelAnimationComponentDeclaration implements ISerializable
				{
					public String sprite;
					public String animation;
					public String name;
					public Rect3F bounds;
					public Vector3F origin = new Vector3F();
					@Override
					public void serialize(IVariable target) throws ValueSerializationException
					{
						if(name != null)
							target.addChild("name").setValue(name);
						
						if(!origin.isZero())
							target.addChild("origin").setValue(origin);
						
						target.addChild("sprite").setValue(sprite);
						target.addChild("animation").setValue(animation);
						target.addChild("bounds").setValue(bounds);
					}

					@Override
					public void deserialize(IImmutableVariable source) throws ValueSerializationException
					{
						try {
							
							if(source.childExists("name"))
								name = source.getChild("name").getValue(String.class);

							if(source.childExists("origin"))
								origin = source.getChild("origin").getValue(Vector3F.class);
							
							sprite = source.getChild("sprite").getValue(String.class);
							animation = source.getChild("animation").getValue(String.class);
							bounds = source.getChild("bounds").getValue(Rect3F.class);
						} catch (NoSuchChildVariableException e) {
							throw new ValueSerializationException(e);
						}
					}
				}
			}
		}
	}
}
