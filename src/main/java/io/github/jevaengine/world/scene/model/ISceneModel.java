package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.world.Direction;

public interface ISceneModel extends IImmutableSceneModel, IDisposable
{
	void update(int deltaTime);
	void setDirection(Direction direction);
}
