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

import io.github.jevaengine.math.Vector2F;

import java.awt.Graphics2D;

public final class ParticleEmitter implements IParticleEmitter
{
	private static final int MAX_SPRITES = 25;
	
	private final Sprite[] m_spriteMaps;
	private final Sprite[] m_particleSprites;

	private final Vector2F m_acceleration;

	private final int m_particleCount;

	private final int m_particleLife;

	private final float m_fVariation;

	private final Vector2F m_velocity;

	private final float[] m_particleBuffer;

	private boolean m_isEmitting;

	public ParticleEmitter(Sprite[] spriteMaps, Vector2F acceleration, Vector2F velocity, int particleCount, int particleLife, float fVariation)
	{
		m_spriteMaps = spriteMaps;
		m_acceleration = acceleration;
		m_velocity = velocity;
		m_particleCount = particleCount;
		m_particleLife = particleLife;
		m_fVariation = fVariation;
		m_particleBuffer = new float[m_particleCount * ParticleOffset.Size.ordinal()];
		m_particleSprites = new Sprite[MAX_SPRITES];

		m_isEmitting = false;
	}

	@Override
	public void update(int deltaTime)
	{
		for (int i = 0; i < m_particleCount; i++)
		{
			int particle = (i % m_particleCount);
			int base = particle * ParticleOffset.Size.offset;

			if (m_particleBuffer[base + ParticleOffset.Life.offset] <= 0 && m_isEmitting)
			{
				m_particleBuffer[base + ParticleOffset.Life.offset] = m_particleLife * (float) Math.random();

				m_particleBuffer[base + ParticleOffset.LocationX.offset] = 0;
				m_particleBuffer[base + ParticleOffset.LocationY.offset] = 0;

				Vector2F velocity = m_velocity.multiply(1.0F + m_fVariation * (Math.random() > 0.5 ? -1 : 1)).rotate(m_fVariation * (float) Math.random() * (float) Math.PI * 2);

				m_particleBuffer[base + ParticleOffset.VelocityX.offset] = velocity.x;
				m_particleBuffer[base + ParticleOffset.VelocityY.offset] = velocity.y;

				Vector2F acceleration = m_acceleration.multiply(1.0F + m_fVariation * (Math.random() > 0.5 ? -1 : 1)).rotate(m_fVariation * (float) Math.random() * (float) Math.PI * 2);

				m_particleBuffer[base + ParticleOffset.AccelerationX.offset] = acceleration.x;
				m_particleBuffer[base + ParticleOffset.AccelerationY.offset] = acceleration.y;

				if (m_particleSprites[particle % m_particleSprites.length] == null)
				{
					m_particleSprites[particle % m_particleSprites.length] = new Sprite(m_spriteMaps[((int) (Math.random() * 100) % m_spriteMaps.length)]);
				}
			}

			m_particleBuffer[base + ParticleOffset.LocationX.offset] += m_particleBuffer[base + ParticleOffset.VelocityX.offset] * (float) deltaTime / 1000.0F;
			m_particleBuffer[base + ParticleOffset.LocationY.offset] += m_particleBuffer[base + ParticleOffset.VelocityY.offset] * (float) deltaTime / 1000.0F;

			m_particleBuffer[base + ParticleOffset.VelocityX.offset] += m_particleBuffer[base + ParticleOffset.AccelerationX.offset] * (float) deltaTime / 1000.0F;
			m_particleBuffer[base + ParticleOffset.VelocityY.offset] += m_particleBuffer[base + ParticleOffset.AccelerationY.offset] * (float) deltaTime / 1000.0F;

			m_particleBuffer[base + ParticleOffset.Life.offset] -= deltaTime;
		}
	}

	@Override
	public void render(Graphics2D g, int x, int y, float fScale)
	{
		for (int i = 0; i < m_particleCount; i++)
		{
			int base = i * ParticleOffset.Size.offset;

			if (m_particleSprites[i % m_particleSprites.length] != null && m_particleBuffer[base + ParticleOffset.Life.offset] > 0)
				m_particleSprites[i % m_particleSprites.length].render(g, 
					x + Math.round(m_particleBuffer[base + ParticleOffset.LocationX.offset]), 
					y + Math.round(m_particleBuffer[base + ParticleOffset.LocationY.offset]), 
					fScale * (float) m_particleBuffer[base + ParticleOffset.Life.offset] / (float) m_particleLife);
		}
	}

	@Override
	public void setEmit(boolean emit)
	{
		m_isEmitting = emit;
	}
	
	private static enum ParticleOffset
	{

		LocationX(0),

		LocationY(1),

		VelocityX(2),

		VelocityY(3),

		AccelerationX(4),

		AccelerationY(5),

		Life(6),

		Size(7);

		public int offset;

		ParticleOffset(int _offset)
		{
			offset = _offset;
		}
	}
}
