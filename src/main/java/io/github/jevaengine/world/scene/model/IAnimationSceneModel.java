package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.world.Direction;

import java.util.ArrayList;
import java.util.List;

public interface IAnimationSceneModel extends ISceneModel
{
	IAnimationSceneModel clone();
	IAnimationSceneModelAnimation getAnimation(String name);
	boolean hasAnimation(String name);
	
	public interface IAnimationSceneModelAnimation
	{
		AnimationSceneModelAnimationState getState();
		void setState(AnimationSceneModelAnimationState state);
		IObserverRegistry getObservers();
	}
	
	public interface IAnimationSceneModelAnimationObserver
	{
		void event(String name);
		void stateChanged(AnimationSceneModelAnimationState state);
	}
	
	public enum AnimationSceneModelAnimationState
	{
		Play,
		PlayWrap,
		Stop,
		PlayToEnd,
	}
	
	public static final class NullAnimationSceneModelAnimation implements IAnimationSceneModelAnimation
	{
		@Override
		public AnimationSceneModelAnimationState getState()
		{
			return AnimationSceneModelAnimationState.Stop;
		}

		@Override
		public void setState(AnimationSceneModelAnimationState state) { }

		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}
	}
	
	public static final class NullAnimationSceneModel implements IAnimationSceneModel
	{
		@Override
		public void dispose() { }
		
		@Override
		public void update(int deltaTime) { }

		@Override
		public Direction getDirection()
		{
			return Direction.Zero;
		}

		@Override
		public void setDirection(Direction direction) { }

		@Override
		public List<ISceneModelComponent> getComponents(Matrix3X3 projection)
		{
			return new ArrayList<>();
		}

		@Override
		public Rect3F getAABB()
		{
			return new Rect3F();
		}

		@Override
		public IAnimationSceneModel clone()
		{
			return new NullAnimationSceneModel();
		}

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
}
