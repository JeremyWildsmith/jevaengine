package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Matrix3X3;

public class IsometricProjectionFactory implements IOrthographicProjectionFactory
{	
	private final int m_tileWidth;
	private final int m_tileHeight;
	
	public IsometricProjectionFactory(int tileWidth, int tileHeight)
	{
		m_tileWidth = tileWidth;
		m_tileHeight = tileHeight;
	}
	
	@Override
	public Matrix3X3 create()
	{
		return new Matrix3X3(m_tileWidth / 2.0F, -m_tileWidth / 2.0F, 0,
							m_tileHeight / 2.0F, m_tileHeight / 2.0F, -m_tileHeight,
							0, 0, 1);

	}
}
