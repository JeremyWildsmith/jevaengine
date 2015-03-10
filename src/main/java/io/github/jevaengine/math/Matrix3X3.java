/*******************************************************************************
 * Copyright (c) 2013 Jeremy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * If you'd like to obtain a another license to this code, you may contact Jeremy to discuss alternative redistribution options.
 * 
 * Contributors:
 *     Jeremy - initial API and implementation
 ******************************************************************************/
package io.github.jevaengine.math;

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.ValueSerializationException;

public final class Matrix3X3 implements ISerializable
{

	public final float[][] matrix;

	public static final Matrix3X3 IDENTITY = new Matrix3X3(1, 0, 0, 
														   0, 1, 0,
														   0, 0, 1);

	public Matrix3X3(float x0y0, float x1y0, float x2y0, float x0y1, float x1y1, float x2y1, float x0y2, float x1y2, float x2y2)
	{
		matrix = new float[][] {
				{ x0y0, x0y1, x0y2 },
				{ x1y0, x1y1, x1y2 },
				{ x2y0, x2y1, x2y2 }
			};
	}
	
	public Matrix3X3()
	{
		this(1.0F, 0.0F, 0.0F,
			 0.0F, 1.0F, 0.0F,
			 0.0F, 0.0F, 1.0F);
	}

	public Matrix3X3(Matrix3X3 mat)
	{
		matrix = mat.matrix.clone();
	}

	public Vector3F dot(Vector3F v)
	{
		return new Vector3F(v.x * matrix[0][0] + v.y * matrix[1][0] + v.z * matrix[2][0], v.x * matrix[0][1] + v.y * matrix[1][1] + v.z * matrix[2][1], v.x * matrix[0][2] + v.y * matrix[1][2] + v.z * matrix[2][2]);
	}

	public Matrix3X3 scale(float fScale)
	{
		return new Matrix3X3(matrix[0][0] * fScale, matrix[1][0] * fScale, matrix[2][0] * fScale, matrix[0][1] * fScale, matrix[1][1] * fScale, matrix[2][1] * fScale, matrix[0][2] * fScale, matrix[1][2] * fScale, matrix[2][2] * fScale);
	}

	public Matrix3X3 transpose()
	{
		return new Matrix3X3(matrix[0][0], matrix[0][1], matrix[0][2], matrix[1][0], matrix[1][1], matrix[1][2], matrix[2][0], matrix[2][1], matrix[2][2]);
	}

	public Matrix3X3 adjoint()
	{
		float[][] m = new Matrix3X3(this).transpose().matrix;

		return new Matrix3X3(new Matrix2X2(m[1][1], m[2][1], m[1][2], m[2][2]).determinant(), -new Matrix2X2(m[0][1], m[2][1], m[0][2], m[2][2]).determinant(), new Matrix2X2(m[0][1], m[1][1], m[0][2], m[1][2]).determinant(), -new Matrix2X2(m[1][0], m[2][0], m[1][2], m[2][2]).determinant(), new Matrix2X2(m[0][0], m[2][0], m[0][2], m[2][2]).determinant(), -new Matrix2X2(m[0][0], m[1][0], m[0][2], m[1][2]).determinant(), new Matrix2X2(m[1][0], m[2][0], m[1][1], m[2][1]).determinant(), -new Matrix2X2(m[0][0], m[2][0], m[0][1], m[2][1]).determinant(), new Matrix2X2(m[0][0], m[1][0], m[0][1], m[1][1]).determinant());
	}

	public float determinant()
	{
		float[][] m = new Matrix3X3(this).transpose().matrix;

		return m[0][0] * new Matrix2X2(m[1][1], m[2][1], m[1][2], m[2][2]).determinant() - m[1][0] * new Matrix2X2(m[0][1], m[2][1], m[0][2], m[2][2]).determinant() + m[2][0] * new Matrix2X2(m[0][1], m[1][1], m[0][2], m[1][2]).determinant();
	}

	public Matrix3X3 inverse()
	{
		return this.adjoint().scale(1 / determinant());
	}

	@Override
	public void serialize(IVariable target) throws ValueSerializationException
	{
		float linearArray[] = new float[ 3 * 3];
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 3; x++)
				linearArray[y * 3 + x] = matrix[x][y];
		}
		
		target.setValue(linearArray);
	}

	@Override
	public void deserialize(IImmutableVariable source) throws ValueSerializationException
	{
		Double[] values = source.getValues(Double[].class);
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 3; x++)
				 matrix[x][y] = values[y * 3 + x].floatValue();
		}
	}
}
