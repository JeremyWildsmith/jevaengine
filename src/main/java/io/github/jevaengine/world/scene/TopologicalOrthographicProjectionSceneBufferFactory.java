package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Matrix3X3;


public final class TopologicalOrthographicProjectionSceneBufferFactory implements ISceneBufferFactory
{
	private final Matrix3X3 m_projection;
	private final boolean m_debugDraw;
	
	public TopologicalOrthographicProjectionSceneBufferFactory(Matrix3X3 projection, boolean debugDraw)
	{
		m_projection = projection;
		m_debugDraw = debugDraw;
	}
	
	public TopologicalOrthographicProjectionSceneBufferFactory(Matrix3X3 projection)
	{
		this(projection, false);
	}
	
	@Override
	public ISceneBuffer create()
	{
		return new TopologicalOrthographicProjectionSceneBuffer(new Matrix3X3(m_projection), m_debugDraw);
	}
}
