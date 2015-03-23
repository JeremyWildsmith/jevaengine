package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.Direction;

import java.util.Collection;

public interface IImmutableSceneModel extends Cloneable
{
	ISceneModel clone() throws SceneModelNotCloneableException;
	Collection<ISceneModelComponent> getComponents(Matrix3X3 projection);
	Rect3F getAABB();
	Direction getDirection();

	public interface ISceneModelComponent extends IRenderable
	{
		String getName();
		boolean testPick(int x, int y, float scale);
		Rect3F getBounds();
		Vector3F getOrigin();
	}
	
	public static final class SceneModelNotCloneableException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public SceneModelNotCloneableException(Exception cause)
		{
			super(cause);
		}
	}
}
