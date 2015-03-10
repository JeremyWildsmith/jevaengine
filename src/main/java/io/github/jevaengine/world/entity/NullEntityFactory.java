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
