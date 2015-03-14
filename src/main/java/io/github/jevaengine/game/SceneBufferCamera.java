package io.github.jevaengine.game;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Rect2F;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.scene.IImmutableSceneBuffer;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.scene.ISceneBuffer;
import io.github.jevaengine.world.scene.ISceneBufferFactory;
import io.github.jevaengine.world.scene.NullSceneBuffer;
import io.github.jevaengine.world.scene.ScaledSceneBuffer;

public abstract class SceneBufferCamera implements ICamera
{
	@Nullable
	private World m_world;
	
	private final ISceneBufferFactory m_sceneBufferFactory;

	public SceneBufferCamera(ISceneBufferFactory sceneBufferFactory)
	{
		m_sceneBufferFactory = sceneBufferFactory;
	}
	
	@Override
	public void attach(World world)
	{
		dettach();
		m_world = world;

		onAttach();
	}

	@Override
	public final void dettach()
	{
		if(m_world == null)
			return;

		onDettach();
		
		m_world = null;
	}

	private Rect2F getProjectedView(ISceneBuffer sceneBuffer, Rect2D viewBounds, float boundsDepth)
	{
		Vector2D depthFactor = sceneBuffer.translateScreenToWorld(new Vector3F(0, 0, boundsDepth)).round();
		
		//First we must project the view bound corners into world space. This allows us to determine which tiles are visible (those that are contained
		// by the viewBounds and thus those tiles that are contained within the projected view bounds. )
		Vector2F worldBounds[] = new Vector2F[] {sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x, viewBounds.y, boundsDepth)).difference(depthFactor),
												 sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x + viewBounds.width, viewBounds.y, boundsDepth)).difference(depthFactor),
												 sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x, viewBounds.y + viewBounds.height, boundsDepth)).difference(depthFactor),
												 sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x + viewBounds.width, viewBounds.y + viewBounds.height, boundsDepth)).difference(depthFactor)};

		/*
		 * Now that we have projected the view bounds into world space, we will construct a rect representing the projected view bounds. The correct rect
		 * will contain all points of the projected view bounds rectangle.
		 */
		float minX = Integer.MAX_VALUE;
		float maxX = Integer.MIN_VALUE;
		float minY = Integer.MAX_VALUE;
		float maxY = Integer.MIN_VALUE;
		
		for(Vector2F v : worldBounds)
		{
			minX = Math.min(minX, v.x);
			minY = Math.min(minY, v.y);
			maxX = Math.max(maxX, v.x);
			maxY = Math.max(maxY, v.y);
		}
		
		return new Rect2F(minX, minY, maxX - minX, maxY - minY);
	}
	
	@Override
	public IImmutableSceneBuffer getScene(Rect2D bounds, float scale)
	{
		if(m_world == null)
			return new NullSceneBuffer();
		
		ISceneBuffer sceneBuffer = new ScaledSceneBuffer(scale, m_sceneBufferFactory.create());
		
		Vector3F lookat = getLookAt();
		lookat.x = Math.min(m_world.getBounds().width - 1, Math.max(0, lookat.x));
		lookat.y = Math.min(m_world.getBounds().height - 1, Math.max(0, lookat.y));
		
		
		//We need to construct a new bounds, that is centered over out camera bounds.
		Vector2D lookatScreen = sceneBuffer.translateWorldToScreen(getLookAt());
		
		Rect2D centeredView = new Rect2D(lookatScreen.x - bounds.width / 2, lookatScreen.y - bounds.height / 2, bounds.width, bounds.height);
		
		Rect2F projectedView = getProjectedView(sceneBuffer, centeredView, getLookAt().z);

		sceneBuffer.translate(new Vector2D(-lookatScreen.x + bounds.width / 2, -lookatScreen.y + bounds.height / 2));
		
		m_world.fillScene(sceneBuffer, projectedView);
		
		return sceneBuffer;
	}
	
	protected abstract void onAttach();
	protected abstract void onDettach();
}
