package io.github.jevaengine.world.steering;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;

public final class DirectionSteeringBehavior implements ISteeringBehavior
{
	private final Direction m_direction;
	
	public DirectionSteeringBehavior(Direction direction)
	{
		m_direction = direction;
	}

	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{
		return new Vector2F(m_direction.getDirectionVector());
	}
}
