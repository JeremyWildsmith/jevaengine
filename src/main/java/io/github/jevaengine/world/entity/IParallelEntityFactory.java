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
