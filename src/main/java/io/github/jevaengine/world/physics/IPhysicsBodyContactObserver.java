package io.github.jevaengine.world.physics;

public interface IPhysicsBodyContactObserver
{
	void onBeginContact(IImmutablePhysicsBody other);
	void onEndContact(IImmutablePhysicsBody other);
}
