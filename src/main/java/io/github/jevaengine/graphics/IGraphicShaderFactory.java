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
package io.github.jevaengine.graphics;

import java.net.URI;

public interface IGraphicShaderFactory
{
	IGraphicShader create(URI name) throws GraphicShaderConstructionException;
	
	public static final class GraphicShaderConstructionException extends Exception
	{
		public GraphicShaderConstructionException(Exception cause)
		{
			super(cause);
		}
	}
	
	public static final class UnrecognizedGraphicShaderException extends Exception
	{
		public UnrecognizedGraphicShaderException(URI name)
		{
			super("Unrecognized shader: " + name.toString());
		}
	}
	
	public static final class GraphicShaderArgumentsParseException extends Exception
	{
		public GraphicShaderArgumentsParseException(Exception cause)
		{
			super("Unable to parse graphic shader arguments.", cause);
		}
	}
	
	public static final class NullGraphicShader implements IGraphicShader
	{
		@Override
		public IImmutableGraphic shade(IImmutableGraphic source)
		{
			return source;
		}
	}
}
