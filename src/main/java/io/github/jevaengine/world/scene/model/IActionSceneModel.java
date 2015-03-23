package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.world.Direction;

import java.util.ArrayList;
import java.util.List;

public interface IActionSceneModel extends ISceneModel
{
	IActionSceneModelAction getAction(String name);
	boolean hasAction(String name);
	
	public interface IActionSceneModelAction
	{
		String getName();
		boolean isQueued();
		boolean isActive();
		boolean isDone();
		void queueTop();
		void queue();
		void dequeue();
		void cancel();
		IObserverRegistry getObservers();
	}
	
	public interface IActionSceneModelActionObserver
	{
		void performed();
		void begin();
		void end();
	}
	
	public static final class NullActionSceneModelAction implements IActionSceneModelAction
	{

		@Override
		public String getName()
		{
			return "null";
		}

		@Override
		public boolean isQueued()
		{
			return false;
		}

		@Override
		public void queueTop() { }

		@Override
		public void queue() { }

		@Override
		public void dequeue() { }

		@Override
		public IObserverRegistry getObservers()
		{
			return new NullObservers();
		}

		@Override
		public boolean isActive()
		{
			return false;
		}

		@Override
		public boolean isDone()
		{
			return true;
		}
		
		@Override
		public void cancel() { }
	}
	
	public static final class NullActionSceneModel implements IActionSceneModel
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
		public IActionSceneModel clone()
		{
			return new NullActionSceneModel();
		}

		@Override
		public IActionSceneModelAction getAction(String name)
		{
			return new NullActionSceneModelAction();
		}

		@Override
		public boolean hasAction(String name)
		{
			return false;
		}
	}
}
