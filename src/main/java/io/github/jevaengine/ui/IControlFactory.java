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
package io.github.jevaengine.ui;

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.util.Nullable;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultControlFactory.class)
public interface IControlFactory
{
	@Nullable
	Class<? extends Control> lookup(String className);
	
	@Nullable
	<T extends Control> String lookup(Class<T> controlClass);
	
	<T extends Control> T create(Class<T> controlClass, @Nullable String instanceName, IImmutableVariable config) throws ControlConstructionException;
	Control create(String controlName, @Nullable String instanceName, IImmutableVariable config) throws ControlConstructionException;

	public static final class ControlConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public ControlConstructionException(String instanceName, Exception cause)
		{
			super("Error constructing control instance " + instanceName, cause);
		}
	}

}
