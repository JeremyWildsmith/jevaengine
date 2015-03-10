package io.github.jevaengine.world.steering;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;

import java.util.ArrayList;
import java.util.Arrays;

public final class SteeringBehaviorList extends ArrayList<ISteeringBehavior> implements ISteeringBehavior
{
	private static final long serialVersionUID = 1L;

	public SteeringBehaviorList(ISteeringBehavior ... behaviors)
	{
		super(Arrays.asList(behaviors));
	}
	
	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{
		Vector2F steerDirection = new Vector2F();
		
		for(ISteeringBehavior b : this)
			steerDirection = b.direct(subject, steerDirection);
		
		return steerDirection.isZero() ? steerDirection : steerDirection.normalize();
	}
}
