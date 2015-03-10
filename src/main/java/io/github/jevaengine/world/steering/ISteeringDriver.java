package io.github.jevaengine.world.steering;

import io.github.jevaengine.world.physics.IPhysicsBody;

import java.util.List;

public interface ISteeringDriver
{
	List<ISteeringBehavior> getBehaviors();
	
	void attach(IPhysicsBody target);
	void dettach();
	
	void update(int deltaTime);
	
	boolean isDriving();
}
