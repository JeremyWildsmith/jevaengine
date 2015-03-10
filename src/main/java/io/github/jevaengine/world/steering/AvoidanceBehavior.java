package io.github.jevaengine.world.steering;

import io.github.jevaengine.math.Circle2F;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;
import io.github.jevaengine.world.physics.RayCastResults;

public final class AvoidanceBehavior implements ISteeringBehavior
{
	private final float m_reactionDistance;
	
	public AvoidanceBehavior(float reactionDistance)
	{
		m_reactionDistance = reactionDistance;
	}
	
	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{
		if(currentDirection.isZero())
			return currentDirection;
		
		Circle2F bounds = subject.getAABB().getXy().getBoundingCircle();
		float reactionDistance = m_reactionDistance + bounds.radius;
		
		float angle = (float)Math.PI / 8;
		Vector2F travelDirection = currentDirection.normalize();
		
		for(int i = 0; i < Math.ceil(2 * Math.PI / angle); i++)
		{
			Vector2F rayDirectionLeft = travelDirection.rotate(-angle);
			Vector2F rayDirectionRight = travelDirection.rotate(angle);
			
			RayCastResults resultsLeft = subject.castRay(new Vector3F(rayDirectionLeft, 0), reactionDistance);
			RayCastResults resultsRight = subject.castRay(new Vector3F(rayDirectionRight, 0), reactionDistance);
			RayCastResults resultsStraight = subject.castRay(new Vector3F(travelDirection, 0), reactionDistance);
			
			if(resultsLeft != null && resultsRight != null && resultsStraight != null)
				return new Vector2F();
			else if(resultsLeft != null && resultsRight != null)
				break;
			else if(resultsLeft != null)
				travelDirection = travelDirection.rotate(angle);
			else if(resultsRight != null)
				travelDirection = travelDirection.rotate(-angle);
			else
				break;
		}
		
		return travelDirection;
	}
}
