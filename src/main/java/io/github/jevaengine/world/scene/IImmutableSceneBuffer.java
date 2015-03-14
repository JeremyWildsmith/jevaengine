package io.github.jevaengine.world.scene;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;

public interface IImmutableSceneBuffer extends IRenderable
{
	Vector2F translateScreenToWorld(Vector3F screenLocation, float scale);
	Vector2F translateScreenToWorld(Vector3F screenLocation);
	Vector2D translateWorldToScreen(Vector3F location, float scale);
	Vector2D translateWorldToScreen(Vector3F location);

	@Nullable
	<T> T pick(Class<T> clazz, int x, int y, float scale);
}
