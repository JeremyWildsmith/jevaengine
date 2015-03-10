package io.github.jevaengine.world.steering;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.pathfinding.Route;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;

public class TraverseRouteBehavior implements ISteeringBehavior
{
	private final SeekBehavior m_seekBehavior;
	private final PointSubject m_seekTarget = new PointSubject(new Vector2F());
	private final Route m_route;
	private final float m_arrivalTolorance;
	private final float m_waypointTolorance;
	
	public TraverseRouteBehavior(float influence, Route route, float arrivaleTolorance, float waypointTolorance)
	{
		m_seekBehavior = new SeekBehavior(influence, m_seekTarget);
		m_route = new Route(route);
		m_arrivalTolorance = arrivaleTolorance;
		m_waypointTolorance = waypointTolorance;
	}
	
	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{	
		Vector2F currentTarget = m_route.getCurrentTarget();
		
		if(currentTarget == null)
			return currentDirection;
		
		float tolorance = m_route.hasNext() ? m_waypointTolorance : m_arrivalTolorance;
		if(currentTarget.difference(subject.getLocation().getXy()).getLength() < tolorance)
		{
			m_route.nextTarget();
			return direct(subject, currentDirection);
		}else
		{
			m_seekTarget.setLocation(currentTarget);
			return m_seekBehavior.direct(subject, currentDirection);
		}
	}
}
