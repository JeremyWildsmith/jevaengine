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
package io.github.jevaengine.world.scene.model.particle;

import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.graphics.AnimationState;
import io.github.jevaengine.graphics.ISpriteFactory;
import io.github.jevaengine.graphics.ISpriteFactory.SpriteConstructionException;
import io.github.jevaengine.graphics.Sprite;
import io.github.jevaengine.graphics.Sprite.NoSuchSpriteAnimation;
import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel.ISceneModelComponent;
import io.github.jevaengine.world.scene.model.particle.DefaultParticleEmitterFactory.DefaultParticleEmitterDeclaration.DefaultParticleEmitterComponentDeclaration;
import java.awt.Graphics2D;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;

public final class DefaultParticleEmitterFactory implements IParticleEmitterFactory
{
	private final ISpriteFactory m_spriteFactory;
	private final IConfigurationFactory m_configurationFactory;
	
	@Inject
	public DefaultParticleEmitterFactory(ISpriteFactory spriteFactory, IConfigurationFactory configurationFactory)
	{
		m_spriteFactory = spriteFactory;
		m_configurationFactory = configurationFactory;
	}
	
	@Override
	public IParticleEmitter create(URI name) throws SceneModelConstructionException
	{
		try
		{
			DefaultParticleEmitterDeclaration decl = m_configurationFactory.create(name).getValue(DefaultParticleEmitterDeclaration.class);
			
			List<DefaultParticleEmitterComponent> components = new ArrayList<>();
			
			for (DefaultParticleEmitterComponentDeclaration c : decl.components)
				components.add(new DefaultParticleEmitterComponent(m_spriteFactory.create(name.resolve(c.sprite)), c.bounds, decl.baseLife, new Vector3F(), new Vector3F(), new Vector3F(), c.loop));
			
			if(!components.isEmpty())
			{
				for(int i = decl.components.length; i < decl.count; i++)
					components.add(components.get((int)Math.round(Math.random() * (decl.components.length - 1))).clone());
			}
			
			return new DefaultParticleEmitter(decl.baseLife, decl.lifeVariance, decl.baseAcceleration, decl.baseVelocity, decl.magnitudeVariance, decl.angleVariance, components.toArray(new DefaultParticleEmitterComponent[components.size()]));
		} catch(ValueSerializationException |
				SpriteConstructionException |
				ConfigurationConstructionException e)
		{
			throw new SceneModelConstructionException(name, e);
		}
	}
	
	private final class DefaultParticleEmitter implements IParticleEmitter
	{
		private final DefaultParticleEmitterComponent[] m_components;
	
		private final Vector3F m_baseVelocity;
		private final Vector3F m_baseAcceleration;
		
		private final int m_baseLife;
		private final int m_lifeVariance;
		
		private final float m_magnitudeVariance;
		private final float m_angleVariance;
		
		public boolean m_isEmitting = true;
		
		public DefaultParticleEmitter(int baseLife, int lifeVariance, Vector3F baseAcceleration, Vector3F baseVelocity, float magnitudeVariance, float angleVariance, DefaultParticleEmitterComponent ... components)
		{
			m_baseLife = baseLife;
			m_lifeVariance = lifeVariance;
			
			m_baseAcceleration = new Vector3F(baseAcceleration);
			m_baseVelocity = new Vector3F(baseVelocity);
			m_magnitudeVariance = magnitudeVariance;
			m_angleVariance = angleVariance;
			m_components = components;
			
			for(DefaultParticleEmitterComponent c : components)
				reset(c);
		}
		
		private void reset(DefaultParticleEmitterComponent component)
		{
			Vector3F newAcceleration = m_baseAcceleration.add(m_baseAcceleration.normalize().multiply((float)(Math.random() - 0.5F) * m_magnitudeVariance * 2));
			newAcceleration = newAcceleration.rotate((float)(Math.random() - 0.5F) * m_angleVariance * 2, (float)(Math.random() - 0.5F) * m_angleVariance * 2, (float)(Math.random() - 0.5F) * m_angleVariance * 2);
	
			Vector3F newVelocity = m_baseVelocity.add(m_baseVelocity.normalize().multiply((float)(Math.random() - 0.5F) * m_magnitudeVariance * 2));
			newVelocity = newVelocity.rotate((float)(Math.random() - 0.5F) * m_angleVariance * 2, (float)(Math.random() - 0.5F) * m_angleVariance * 2, (float)(Math.random() - 0.5F) * m_angleVariance * 2);

			int newLife = m_baseLife + (int)Math.round((Math.random() - 0.5F) * m_lifeVariance * 2);
			
			component.reset(newLife, newAcceleration, newVelocity, new Vector3F());
		}

		@Override
		public void setEmit(boolean emit)
		{
			m_isEmitting = emit;
		}
		
		@Override
		public void update(int deltaTime)
		{
			for(DefaultParticleEmitterComponent c : m_components)
			{
				if(c.update(deltaTime) && m_isEmitting)
					reset(c);
			}
		}

		@Override
		public void setDirection(Direction direction) { }

		@Override
		public PhysicsBodyShape getBodyShape()
		{
			return new PhysicsBodyShape();
		}
		
		@Override
		public IAnimationSceneModel clone() throws SceneModelNotCloneableException
		{
			DefaultParticleEmitterComponent[] clonedComponents = new DefaultParticleEmitterComponent[m_components.length];
			
			for(int i = 0; i < m_components.length; i++)
				clonedComponents[i] = m_components[i].clone();
			
			return new DefaultParticleEmitter(m_baseLife, m_lifeVariance, m_baseAcceleration, m_baseVelocity, m_magnitudeVariance, m_angleVariance, clonedComponents);
		}

		@Override
		public Collection<ISceneModelComponent> getComponents(Matrix3X3 projection)
		{
			return Arrays.asList((ISceneModelComponent[])m_components);
		}

		@Override
		public Rect3F getAABB()
		{
			return new Rect3F();
		}

		@Override
		public Direction getDirection()
		{
			return Direction.XYPlus;
		}

		@Override
		public void dispose() { }

		@Override
		public IAnimationSceneModelAnimation getAnimation(String name)
		{
			return new NullAnimationSceneModelAnimation();
		}

		@Override
		public boolean hasAnimation(String name)
		{
			return false;
		}
	}
	
	private static class DefaultParticleEmitterComponent implements ISceneModelComponent
	{
		private static final AtomicInteger COUNT = new AtomicInteger();
		
		private final Sprite m_sprite;
		private final Rect3F m_bounds;
		private final boolean m_loop;
		
		private Vector3F m_acceleration;
		private Vector3F m_velocity;
		private Vector3F m_location;
		
		private int m_life;
		private final String m_name;
		
		public DefaultParticleEmitterComponent(Sprite sprite, Rect3F bounds, int life, Vector3F acceleration, Vector3F velocity, Vector3F location, boolean loop)
		{
			m_name = COUNT.getAndIncrement() + this.getClass().getName();
			m_sprite = sprite;
			m_bounds = bounds;
			m_loop = loop;

			m_life = life;
			m_acceleration = acceleration;
			m_velocity = velocity;
			m_location = location;
		}
		
		@Override
		public DefaultParticleEmitterComponent clone()
		{
			return new DefaultParticleEmitterComponent(m_sprite.clone(), m_bounds, m_life, m_acceleration, m_velocity, m_location, m_loop);
		}
		
		@Override
		public String getName()
		{
			return m_name;
		}

		public void reset(int life, Vector3F acceleration, Vector3F velocity, Vector3F location)
		{
			try
			{
				m_life = life;
				m_acceleration = new Vector3F(acceleration);
				m_velocity = new Vector3F(velocity);
				m_location = new Vector3F(location);
				
				String animation = m_sprite.getCurrentAnimation();
				
				if(animation != null)
					m_sprite.setAnimation(animation, m_loop ? AnimationState.Play : AnimationState.PlayToEnd);
				
			} catch (NoSuchSpriteAnimation ex) {
				//This would be a programmer error if the current animation was non-existant.
				throw new RuntimeException(ex);
			}
		}
		
		public boolean update(int deltaTime)
		{
			m_sprite.update(deltaTime);
			m_life -= deltaTime;
			m_location = m_location.add(m_velocity.multiply(deltaTime / 1000.0F));
			m_velocity = m_velocity.add(m_acceleration.multiply(deltaTime / 1000.0F));
			
			return m_life < 0;
		}
		
		@Override
		public boolean testPick(int x, int y, float scale)
		{
			return m_sprite.testPick(x, y, scale);
		}

		@Override
		public Rect3F getBounds()
		{
			return new Rect3F(m_bounds);
		}

		@Override
		public Vector3F getOrigin()
		{
			return m_location;
		}

		@Override
		public void render(Graphics2D g, int x, int y, float scale)
		{
			m_sprite.render(g, x, y, scale);
		}
	}

	public static class DefaultParticleEmitterDeclaration implements ISerializable
	{
		public Vector3F baseVelocity;
		public Vector3F baseAcceleration;
		
		public int baseLife;
		public int lifeVariance;
		
		public float magnitudeVariance;
		public float angleVariance;
		
		public int count;
		
		public DefaultParticleEmitterComponentDeclaration[] components;

		public DefaultParticleEmitterDeclaration() { }
		
		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("baseVelocity").setValue(baseVelocity);
			target.addChild("baseAcceleration").setValue(baseAcceleration);
			target.addChild("baseLife").setValue(baseLife);
			target.addChild("lifeVariance").setValue(lifeVariance);
			target.addChild("magnitudeVariance").setValue(magnitudeVariance);
			target.addChild("angleVariance").setValue(angleVariance);
			target.addChild("components").setValue(components);
			target.addChild("count").setValue(count);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				this.baseVelocity = source.getChild("baseVelocity").getValue(Vector3F.class);
				this.baseAcceleration = source.getChild("baseAcceleration").getValue(Vector3F.class);
				this.baseLife = source.getChild("baseLife").getValue(Integer.class);
				this.lifeVariance = source.getChild("lifeVariance").getValue(Integer.class);
				this.magnitudeVariance = source.getChild("magnitudeVariance").getValue(Double.class).floatValue();
				this.angleVariance = source.getChild("angleVariance").getValue(Double.class).floatValue();
				this.components = source.getChild("components").getValues(DefaultParticleEmitterComponentDeclaration[].class);
				this.count = source.getChild("count").getValue(Integer.class);
			} catch(NoSuchChildVariableException e)
			{
				throw new ValueSerializationException(e);
			}
		}
		
		public static final class DefaultParticleEmitterComponentDeclaration implements ISerializable
		{
			public String sprite;
			public Rect3F bounds;
			public boolean loop;
			
			@Override
			public void serialize(IVariable target) throws ValueSerializationException
			{
				target.addChild("sprite").setValue(sprite);
				target.addChild("bounds").setValue(bounds);
				target.addChild("loop").setValue(loop);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException
			{
				try
				{
					sprite = source.getChild("sprite").getValue(String.class);
					bounds = source.getChild("bounds").getValue(Rect3F.class);
					loop = source.getChild("loop").getValue(Boolean.class);
				} catch (NoSuchChildVariableException | ValueSerializationException e)
				{
					throw new ValueSerializationException(e);
				}
			}
		}
	}
	}
