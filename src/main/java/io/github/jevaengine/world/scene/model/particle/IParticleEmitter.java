/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.world.scene.model.particle;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel.ISceneModelComponent;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel.SceneModelNotCloneableException;
import io.github.jevaengine.world.scene.model.ISceneModel;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Jeremy
 */
public interface IParticleEmitter extends IAnimationSceneModel
{
	void setEmit(boolean emit);
	
	public static final class NullParticleEmitter implements IParticleEmitter
	{
		@Override
		public void update(int deltaTime) { }

		@Override
		public void setEmit(boolean emit) { }

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
			return new NullParticleEmitter();
		}

		@Override
		public Collection<ISceneModelComponent> getComponents(Matrix3X3 projection)
		{
			return new ArrayList<>();
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
		public IAnimationSceneModelAnimation getAnimation(String name) {
			return new NullAnimationSceneModelAnimation();
		}

		@Override
		public boolean hasAnimation(String name) {
			return false;
		}
	}
}
