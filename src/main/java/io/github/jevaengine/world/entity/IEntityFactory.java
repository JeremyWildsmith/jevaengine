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
package io.github.jevaengine.world.entity;

import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.util.Nullable;
import java.net.URI;

public interface IEntityFactory
{
	@Nullable
	Class<? extends IEntity> lookup(String className);
	
	@Nullable
	<T extends IEntity> String lookup(Class<T> entityClass);
	
	<T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, URI config) throws EntityConstructionException;
	<T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, URI config, IImmutableVariable auxConfig) throws EntityConstructionException;
	<T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, IImmutableVariable config) throws EntityConstructionException;
	<T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName) throws EntityConstructionException;

	IEntity create(String entityClass, @Nullable String instanceName, URI config) throws EntityConstructionException;
	IEntity create(String entityClass, @Nullable String instanceName, URI config, IImmutableVariable auxConfig) throws EntityConstructionException;
	IEntity create(String entityClass, @Nullable String instanceName, IImmutableVariable config) throws EntityConstructionException;
	IEntity create(String entityClass, @Nullable String instanceName) throws EntityConstructionException;
	
	public static final class EntityConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public EntityConstructionException(URI assetName, Exception cause) {
			super("Error constructing entity " + assetName.toString(), cause);
		}
		
		public EntityConstructionException(String instanceName, Exception cause)
		{
			super("Error construct entity instance " + instanceName, cause);
		}
		
		public EntityConstructionException(Exception e)
		{
			super(e);
		}
	}
	
	public final class UnsupportedEntityTypeException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		public UnsupportedEntityTypeException(String entityTypeName)
		{
			super("Unsupported entity type: " + entityTypeName);
		}
		
		public UnsupportedEntityTypeException(Class<? extends IEntity> entityClass)
		{
			super("Unsupported entity type: " + entityClass.getName());
		}
	}
}
