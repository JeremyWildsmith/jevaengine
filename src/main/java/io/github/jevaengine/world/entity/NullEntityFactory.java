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

import io.github.jevaengine.FutureResult;
import io.github.jevaengine.IInitializationMonitor;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.util.Nullable;
import java.net.URI;

public final class NullEntityFactory implements IEntityFactory, IParallelEntityFactory
{
	@Override
	public <T extends IEntity> void create(Class<T> entityClass, String instanceName, IImmutableVariable config, IInitializationMonitor<T, EntityConstructionException> monitor)
	{
		monitor.completed(new FutureResult<T, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));
	}

	@Override
	public <T extends IEntity> void create(Class<T> entityClass, String instanceName, URI config, IInitializationMonitor<T, EntityConstructionException> monitor)
	{
		monitor.completed(new FutureResult<T, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));
	}
	
	@Override
	public <T extends IEntity> void create(Class<T> entityClass, String instanceName, IInitializationMonitor<T, EntityConstructionException> monitor)
	{
		monitor.completed(new FutureResult<T, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));
	}

	@Override
	public Class<? extends DefaultEntity> lookup(String className)
	{
		return null;
	}

	@Override
	public <T extends IEntity> String lookup(Class<T> entityClass)
	{
		return null;
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass, String instanceName, IImmutableVariable config) throws EntityConstructionException
	{
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass, String instanceName, URI config) throws EntityConstructionException
	{
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}
	
	@Override
	public <T extends IEntity> T create(Class<T> entityClass, String instanceName) throws EntityConstructionException
	{
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}
	
	public static final class NullEntityFactoryCannotConstructEntities extends Exception
	{
		private static final long serialVersionUID = 1L;
	
		private NullEntityFactoryCannotConstructEntities() { }
	}

	@Override
	public void create(String entityClass, @Nullable String instanceName, IImmutableVariable config, IInitializationMonitor<IEntity, EntityConstructionException> monitor)
	{
		monitor.completed(new FutureResult<IEntity, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));
	}

	@Override
	public void create(String entityClass, @Nullable String instanceName, URI config, IInitializationMonitor<IEntity, EntityConstructionException> monitor)
	{
		monitor.completed(new FutureResult<IEntity, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));
	}

	@Override
	public void create(String entityClass, @Nullable String instanceName, IInitializationMonitor<IEntity, EntityConstructionException> monitor)
	{
		monitor.completed(new FutureResult<IEntity, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));	
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName, URI config) throws EntityConstructionException
	{
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName, IImmutableVariable config) throws EntityConstructionException
	{
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName) throws EntityConstructionException
	{
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}

	@Override
	public <T extends IEntity> void create(Class<T> entityClass,
			@Nullable String instanceName, URI config,
			IImmutableVariable auxConfig,
			IInitializationMonitor<T, EntityConstructionException> monitor) {
		monitor.completed(new FutureResult<T, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));	
	}

	@Override
	public void create(String entityClass, @Nullable String instanceName,
			URI config, IImmutableVariable auxConfig,
			IInitializationMonitor<IEntity, EntityConstructionException> monitor) {
		monitor.completed(new FutureResult<IEntity, EntityConstructionException>(new EntityConstructionException(new NullEntityFactoryCannotConstructEntities())));		
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass,
			@Nullable String instanceName, URI config,
			IImmutableVariable auxConfig) throws EntityConstructionException {
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName,
			URI config, IImmutableVariable auxConfig)
			throws EntityConstructionException {
		throw new EntityConstructionException(new NullEntityFactoryCannotConstructEntities());
	}
}
