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

import io.github.jevaengine.IInitializationMonitor;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.util.Nullable;
import java.net.URI;

public interface IParallelEntityFactory extends IEntityFactory
{
	<T extends IEntity> void create(Class<T> entityClass, @Nullable String instanceName, final IImmutableVariable config, final IInitializationMonitor<T, EntityConstructionException> monitor);
	<T extends IEntity> void create(Class<T> entityClass, @Nullable String instanceName, final URI config, final IInitializationMonitor<T, EntityConstructionException> monitor);
	<T extends IEntity> void create(Class<T> entityClass, @Nullable String instanceName, final URI config, IImmutableVariable auxConfig, final IInitializationMonitor<T, EntityConstructionException> monitor);
	<T extends IEntity> void create(Class<T> entityClass, @Nullable String instanceName, final IInitializationMonitor<T, EntityConstructionException> monitor);
	
	void create(String entityClass, @Nullable String instanceName, IImmutableVariable config, IInitializationMonitor<IEntity, EntityConstructionException> monitor);
	void create(String entityClass, @Nullable String instanceName, URI config, IInitializationMonitor<IEntity, EntityConstructionException> monitor);
	void create(String entityClass, @Nullable String instanceName, URI config, IImmutableVariable auxConfig, IInitializationMonitor<IEntity, EntityConstructionException> monitor);
	void create(String entityClass, @Nullable String instanceName, IInitializationMonitor<IEntity, EntityConstructionException> monitor);
	
}
