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

public final class Matrix4X4 {
	public static final float TOLERANCE = 0.00000001F;
	public static final Matrix4X4 IDENTITY = new Matrix4X4(1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1);
	public final float[][] matrix;

	public Matrix4X4(float x0y0, float x1y0, float x2y0, float x3y0,
	                 float x0y1, float x1y1, float x2y1, float x3y1,
	                 float x0y2, float x1y2, float x2y2, float x3y2,
	                 float x0y3, float x1y3, float x2y3, float x3y3) {
		matrix = new float[][]
				{
						{x0y0, x0y1, x0y2, x0y3},
						{x1y0, x1y1, x1y2, x1y3},
						{x2y0, x2y1, x2y2, x2y3},
						{x3y0, x3y1, x3y2, x3y3},
				};
	}

	public Matrix4X4(Matrix4X4 other) {
		matrix = other.matrix.clone();
	}

	public Matrix4X4() {
		matrix = IDENTITY.matrix.clone();
	}

	public Matrix4X4(Matrix3X3 mat) {
		matrix = mat.matrix.clone();
	}

	public static Matrix4X4 fromColumnMajor(float[] source) {
		Matrix4X4 mat = new Matrix4X4();

		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				mat.matrix[x][y] = source[x * 4 + y];
			}
		}

		return mat;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Matrix4X4))
			return false;

		Matrix4X4 other = (Matrix4X4) obj;

		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				if (Math.abs(matrix[x][y] - other.matrix[x][y]) > TOLERANCE)
					return false;
			}
		}
		return true;
	}

	public float[] columnMajor() {
		return new float[]
				{
						matrix[0][0], matrix[0][1], matrix[0][2], matrix[0][3],
						matrix[1][0], matrix[1][1], matrix[1][2], matrix[1][3],
						matrix[2][0], matrix[2][1], matrix[2][2], matrix[2][3],
						matrix[3][0], matrix[3][1], matrix[3][2], matrix[3][3],
				};
	}
}
