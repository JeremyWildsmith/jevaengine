/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.graphics;

import java.awt.Graphics2D;

/**
 *
 * @author Jeremy
 */
public interface IParticleEmitter extends IRenderable
{
	void update(int deltaTime);
	void setEmit(boolean emit);
	
	public static final class NullParticleEmitter implements IParticleEmitter
	{
		@Override
		public void update(int deltaTime) { }

		@Override
		public void setEmit(boolean emit) { }

		@Override
		public void render(Graphics2D g, int x, int y, float scale) { }
	}
}
