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
