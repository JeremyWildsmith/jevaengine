/* 
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.github.jevaengine.math;

public final class Matrix2X2
{

	public final float[][] matrix;

	public static final Matrix2X2 IDENTITY = new Matrix2X2(1, 0, 0, 1);

	public Matrix2X2()
	{
		matrix = new float[][] {
				{ 1, 0 },
				{ 0, 1 }		
		};
	}

	public Matrix2X2(float x0y0, float x1y0, float x0y1, float x1y1)
	{
		matrix = new float[][]
		{
				{ x0y0, x0y1 },
				{ x1y0, x1y1 } };
	}
	
	public Matrix2X2(Matrix2X2 mat)
	{
		matrix = mat.matrix.clone();
	}

	public static Matrix2X2 createRotation(float fRot)
	{
		return new Matrix2X2((float) Math.cos(fRot), (float) -Math.sin(fRot), (float) Math.sin(fRot), (float) Math.cos(fRot));
	}

	public Vector2F dot(Vector2F vec)
	{
		return new Vector2F(vec.x * matrix[0][0] + vec.y * matrix[1][0],
								vec.x * matrix[0][1] + vec.y * matrix[1][1]);
	}

	public Vector2F dot(Vector2D vec)
	{
		return this.dot(new Vector2F(vec));
	}

	public Matrix2X2 scale(float fScale)
	{
		return new Matrix2X2(matrix[0][0] * fScale, matrix[1][0] * fScale, matrix[0][1] * fScale, matrix[1][1] * fScale);
	}

	public float determinant()
	{
		return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
	}

	public Matrix2X2 inverse()
	{
		return new Matrix2X2(matrix[1][1], -matrix[1][0], -matrix[0][1], matrix[0][0]).scale(1 / (matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1]));
	}
}
