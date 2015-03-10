package io.github.jevaengine.world.steering;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;

public interface ISteeringBehavior
{
	Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection);
	
	public static final class NullSteeringBehavior implements ISteeringBehavior
	{
		@Override
		public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
		{
			return new Vector2F();
		}	
	}
}
