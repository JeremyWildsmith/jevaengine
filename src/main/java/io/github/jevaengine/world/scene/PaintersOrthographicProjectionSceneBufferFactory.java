package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Matrix3X3;



public class PaintersOrthographicProjectionSceneBufferFactory implements ISceneBufferFactory
{
	private final Matrix3X3 m_projection;

	public PaintersOrthographicProjectionSceneBufferFactory(Matrix3X3 projection)
	{
		m_projection = new Matrix3X3(projection);
	}
	
	@Override
	public ISceneBuffer create()
	{
		return new PaintersOrthographicProjectionSceneBuffer(new Matrix3X3(m_projection));
	}

}
