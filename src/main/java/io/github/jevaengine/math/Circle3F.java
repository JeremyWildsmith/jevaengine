package io.github.jevaengine.math;

public final class Circle3F
{
	public float x;
	public float y;
	public float z;
	public float radius;

	public Circle3F()
	{
		this(0, 0, 0, 0);
	}
	
	public Circle3F(float _x, float _y, float _z, float _radius)
	{
		x = _x;
		y = _y;
		radius = _radius;
	}
}
