package io.github.jevaengine.world.entity;

import io.github.jevaengine.FutureResult;
import io.github.jevaengine.IEngineThreadPool;
import io.github.jevaengine.IEngineThreadPool.Purpose;
import io.github.jevaengine.IInitializationMonitor;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.util.Nullable;

import java.net.URI;

public final class ThreadPooledEntityFactory implements IParallelEntityFactory
{
	private final IEntityFactory m_entityFactory;
	private final IEngineThreadPool m_threadPool;
	
	public ThreadPooledEntityFactory(IEntityFactory entityFactory, IEngineThreadPool threadPool)
	{
		m_entityFactory = entityFactory;
		m_threadPool = threadPool;
	}
	
	@Override
	public <T extends IEntity> void create(final Class<T> entityClass, @Nullable final String instanceName, final IImmutableVariable config, final IInitializationMonitor<T, EntityConstructionException> initializationMonitor)
	{
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					initializationMonitor.completed(new FutureResult<T, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName, config)));
				} catch(EntityConstructionException e)
				{
					initializationMonitor.completed(new FutureResult<T, EntityConstructionException>(e));
				}
			}
		});
	}

	@Override
	public <T extends IEntity> void create(final Class<T> entityClass, @Nullable final String instanceName, final URI config, final IInitializationMonitor<T, EntityConstructionException> monitor)
	{
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					monitor.completed(new FutureResult<T, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName, config)));
				} catch(EntityConstructionException e)
				{
					monitor.completed(new FutureResult<T, EntityConstructionException>(e));
				}
			}
		});
	}

	@Override
	public <T extends IEntity> void create(final Class<T> entityClass, @Nullable final String instanceName, final IInitializationMonitor<T, EntityConstructionException> monitor)
	{
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					monitor.completed(new FutureResult<T, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName)));
				} catch(EntityConstructionException e)
				{
					monitor.completed(new FutureResult<T, EntityConstructionException>(e));
				}
			}
		});
	}

	@Override
	@Nullable
	public Class<? extends IEntity> lookup(String className)
	{
		return m_entityFactory.lookup(className);
	}

	@Override
	@Nullable
	public <T extends IEntity> String lookup(Class<T> entityClass)
	{
		return m_entityFactory.lookup(entityClass);
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, URI config) throws EntityConstructionException
	{
		return m_entityFactory.create(entityClass, instanceName, config);
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, IImmutableVariable config) throws EntityConstructionException
	{
		return m_entityFactory.create(entityClass, instanceName, config);
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName) throws EntityConstructionException
	{
		return m_entityFactory.create(entityClass, instanceName);
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName, URI config) throws EntityConstructionException
	{
		return m_entityFactory.create(entityClass, instanceName, config);
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName, IImmutableVariable config) throws EntityConstructionException
	{
		return m_entityFactory.create(entityClass, instanceName, config);
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName) throws EntityConstructionException
	{
		return m_entityFactory.create(entityClass, instanceName);
	}

	@Override
	public void create(final String entityClass, @Nullable final String instanceName, final URI config, final IInitializationMonitor<IEntity, EntityConstructionException> monitor)
	{
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName, config)));
				} catch(EntityConstructionException e)
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(e));
				}
			}
		});	
	}

	@Override
	public void create(final String entityClass, @Nullable final String instanceName, final IInitializationMonitor<IEntity, EntityConstructionException> monitor)
	{
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName)));
				} catch(EntityConstructionException e)
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(e));
				}
			}
		});	
	}

	@Override
	public void create(final String entityClass, final @Nullable String instanceName, final IImmutableVariable config, final IInitializationMonitor<IEntity, EntityConstructionException> monitor)
	{
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName, config)));
				} catch(EntityConstructionException e)
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(e));
				}
			}
		});	
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass,
			@Nullable String instanceName, URI config,
			IImmutableVariable auxConfig) throws EntityConstructionException {
		return m_entityFactory.create(entityClass, instanceName, config, auxConfig);
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName,
			URI config, IImmutableVariable auxConfig)
			throws EntityConstructionException
	{
		return m_entityFactory.create(entityClass, instanceName, config, auxConfig);
	}

	@Override
	public <T extends IEntity> void create(final Class<T> entityClass,
			final @Nullable String instanceName, final URI config,
			final IImmutableVariable auxConfig,
			final IInitializationMonitor<T, EntityConstructionException> monitor) {

		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					monitor.completed(new FutureResult<T, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName, config, auxConfig)));
				} catch(EntityConstructionException e)
				{
					monitor.completed(new FutureResult<T, EntityConstructionException>(e));
				}
			}
		});
	}

	@Override
	public void create(final String entityClass, @Nullable final String instanceName,
			final URI config, final IImmutableVariable auxConfig,
			final IInitializationMonitor<IEntity, EntityConstructionException> monitor) {
		m_threadPool.execute(Purpose.Loading, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(m_entityFactory.create(entityClass, instanceName, config, auxConfig)));
				} catch(EntityConstructionException e)
				{
					monitor.completed(new FutureResult<IEntity, EntityConstructionException>(e));
				}
			}
		});		
	}
}
