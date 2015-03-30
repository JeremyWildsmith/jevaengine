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
package io.github.jevaengine.world;

import io.github.jevaengine.IEngineThreadPool;
import io.github.jevaengine.IInitializationProgressMonitor;
import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.ISerializable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.NoSuchChildVariableException;
import io.github.jevaengine.config.NullVariable;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.graphics.ISpriteFactory;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilderFactory;
import io.github.jevaengine.script.IScriptBuilderFactory.ScriptBuilderConstructionException;
import io.github.jevaengine.script.NullScriptBuilder;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.ThreadSafe;
import io.github.jevaengine.world.DefaultWorldFactory.WorldConfiguration.ArtifactPlane;
import io.github.jevaengine.world.DefaultWorldFactory.WorldConfiguration.EntityImportDeclaration;
import io.github.jevaengine.world.DefaultWorldFactory.WorldConfiguration.SceneArtifactDeclaration;
import io.github.jevaengine.world.DefaultWorldFactory.WorldConfiguration.ZoneDeclaration;
import io.github.jevaengine.world.IWeatherFactory.IWeather;
import io.github.jevaengine.world.IWeatherFactory.NullWeather;
import io.github.jevaengine.world.IWeatherFactory.WeatherConstructionException;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntityFactory;
import io.github.jevaengine.world.entity.IEntityFactory.EntityConstructionException;
import io.github.jevaengine.world.entity.IEntityFactory.UnsupportedEntityTypeException;
import io.github.jevaengine.world.entity.SceneArtifact;
import io.github.jevaengine.world.entity.ThreadPooledEntityFactory;
import io.github.jevaengine.world.physics.IPhysicsWorldFactory;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IAnimationSceneModelFactory;
import io.github.jevaengine.world.scene.model.ISceneModelFactory.SceneModelConstructionException;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWorldFactory implements IWorldFactory
{
	private static final float LOADING_PORTION_LAYERS = 0.8F;

	private final Logger m_logger = LoggerFactory.getLogger(DefaultWorldFactory.class);
	
	protected final IEngineThreadPool m_threadPool;
	protected final IEntityFactory m_entityFactory;
	protected final IScriptBuilderFactory m_scriptFactory;
	protected final IConfigurationFactory m_configurationFactory;
	protected final ISpriteFactory m_spriteFactory;
	protected final IAudioClipFactory m_audioClipFactory;
	protected final IPhysicsWorldFactory m_physicsWorldFactory;
	protected final IAnimationSceneModelFactory m_animationSceneModelFactory;
	
	private final IWeatherFactory m_weatherFactory;
	
	@Inject
	public DefaultWorldFactory(IEngineThreadPool threadPool,
								IEntityFactory entityFactory,
								IScriptBuilderFactory scriptFactory,
								IConfigurationFactory configurationFactory, 
								ISpriteFactory spriteFactory,
								IAudioClipFactory audioClipFactory,
								IPhysicsWorldFactory physicsWorldFactory,
								IAnimationSceneModelFactory animationSceneModelFactory,
								IWeatherFactory weatherFactory)
	{
		m_threadPool = threadPool;
		m_entityFactory = entityFactory;
		m_scriptFactory = scriptFactory;
		m_configurationFactory = configurationFactory;
		m_spriteFactory = spriteFactory;
		m_audioClipFactory = audioClipFactory;
		m_physicsWorldFactory = physicsWorldFactory;
		m_animationSceneModelFactory = animationSceneModelFactory;
		m_weatherFactory = weatherFactory;
	}
	
	protected World createBaseWorld(float friction, float metersPerUnit, int worldWidthTiles, int worldHeightTiles, IWeather weather, @Nullable URI worldScript)
	{
		IScriptBuilder scriptBuilder = new NullScriptBuilder();
		
		try
		{
			if(worldScript != null)
				scriptBuilder = m_scriptFactory.create(worldScript);
			else
				scriptBuilder = m_scriptFactory.create();
		} catch (ScriptBuilderConstructionException e) {
			m_logger.error("Unable to instantiate script builder for world, assuming no behavior associated to world.", e);
		}
		
		return new World(worldWidthTiles,
											worldHeightTiles,
											friction,
											metersPerUnit,
											weather,
											m_physicsWorldFactory, 
											new ThreadPooledEntityFactory(m_entityFactory, m_threadPool),
											scriptBuilder);
	}
	
	protected IEntity createSceneArtifact(SceneArtifactDeclaration artifactDecl) throws EntityConstructionException
	{
		try
		{
			IAnimationSceneModel model = m_animationSceneModelFactory.create(new URI(artifactDecl.model));
			model.setDirection(artifactDecl.direction);
			return new SceneArtifact(model, artifactDecl.isTraversable);
		} catch (SceneModelConstructionException | URISyntaxException e)
		{
			throw new EntityConstructionException("World tile", e);
		}
	}
	
	protected IEntity createEntity(EntityImportDeclaration entityConfig) throws EntityConstructionException
	{
		try
		{
			IImmutableVariable auxConfig = entityConfig.auxConfig == null ? new NullVariable() : entityConfig.auxConfig;
			
			Class<? extends IEntity> entityClass = m_entityFactory.lookup(entityConfig.type);
			
			if(entityClass == null)
				throw new EntityConstructionException(new UnsupportedEntityTypeException(entityConfig.type));
			
			if(entityConfig.config == null)
				return m_entityFactory.create(entityClass, 
												entityConfig.name,
												auxConfig);
		
			return m_entityFactory.create(entityClass, 
						entityConfig.name,
						new URI(entityConfig.config),
						auxConfig);
				
		} catch (URISyntaxException e)
		{
			throw new EntityConstructionException(entityConfig.name, e);
		}
	}
	
	private void createTiledPlane(World world, SceneArtifactDeclaration tiles[], ArtifactPlane plane, IInitializationProgressMonitor monitor) throws TileIndexOutOfBoundsException
	{
		Rect2D worldBounds = world.getBounds();
		int locationOffset = 0;
		
		for(int i = 0; i < plane.artifactIndices.length; i++)
		{
			int tileIndex = plane.artifactIndices[i];
	
			if(tileIndex >= 0)
			{
				if(tileIndex >= tiles.length)
					throw new TileIndexOutOfBoundsException();
			
				try
				{
					IEntity tile = createSceneArtifact(tiles[tileIndex]);
					world.addEntity(tile);

					tile.getBody().setLocation(new Vector3F((locationOffset + i) % worldBounds.width, (float) Math.floor((locationOffset + i) / worldBounds.height), plane.planeZ));
				} catch(EntityConstructionException e)
				{
					m_logger.error("Erro constructing tile, assuming tile does not exist", e);
				}
			}else
				locationOffset += -tileIndex - 1;
			
			monitor.statusChanged((float)i / plane.artifactIndices.length, "Tile " + i);
		}
		
		monitor.statusChanged(1.0F, "Completed");
	}
	
	private IWeather createWeather(URI context, WorldConfiguration world)
	{
		try
		{
			if(world.weather == null)
				return new NullWeather();
			
			return m_weatherFactory.create(context.resolve(new URI(world.weather)));
		} catch (WeatherConstructionException | URISyntaxException e)
		{
			m_logger.error("Unable to construct world weather, using null weather instead.", e);
			
			return new NullWeather();
		}
	}
	
	@Override
	@ThreadSafe
	public final World create(URI name, final IInitializationProgressMonitor monitor) throws WorldConstructionException
	{
		try
		{
			final WorldConfiguration worldConfig = m_configurationFactory.create(name).getValue(WorldConfiguration.class);
			
			World world = createBaseWorld(worldConfig.friction, worldConfig.metersPerUnit,
											worldConfig.worldWidth, worldConfig.worldHeight,
											createWeather(name, worldConfig),
											worldConfig.script == null ? null : name.resolve(new URI(worldConfig.script)));
			
			for (int i = 0; i < worldConfig.artifactPlanes.length; i++)
			{
				final int layer = i;
				createTiledPlane(world, worldConfig.artifacts, worldConfig.artifactPlanes[i], new IInitializationProgressMonitor() {
					@Override
					public void statusChanged(float progress, String status)
					{
						monitor.statusChanged(LOADING_PORTION_LAYERS * ((layer <= 0 ? 0 : (float)layer / (worldConfig.artifactPlanes.length)) + progress / (worldConfig.artifactPlanes.length)), 
											  "World Layers, " + status);
					}
				});
			}
			
			for (int i = 0; i < worldConfig.entities.length; i++)
			{
				try
				{
					EntityImportDeclaration entityConfig = worldConfig.entities[i];

					monitor.statusChanged(LOADING_PORTION_LAYERS + ((float)i / worldConfig.entities.length) * (1.0F - LOADING_PORTION_LAYERS), "Entities, " + entityConfig.name);

					IEntity entity = createEntity(entityConfig);
					world.addEntity(entity);

					if(entityConfig.location != null)
						entity.getBody().setLocation(entityConfig.location);

					if(entityConfig.direction != null)
						entity.getBody().setDirection(entityConfig.direction);
				} catch (EntityConstructionException e)
				{
					m_logger.error("Unable to construct entity, assuming it does not exist.", e);
				}
			}
			
			for(ZoneDeclaration z : worldConfig.zones)
				world.addZone(z.name, z.region);
			
			monitor.statusChanged(1.0F, "Completed");
		
			return world;
		}catch(ValueSerializationException | TileIndexOutOfBoundsException | ConfigurationConstructionException | URISyntaxException e)
		{
			throw new WorldConstructionException(name, e);
		}
	}
	
	public static final class TileIndexOutOfBoundsException extends Exception
	{
		private static final long serialVersionUID = 1L;
	
		private TileIndexOutOfBoundsException() { }
	
	}
	
	public static final class WorldConfiguration implements ISerializable
	{	
		@Nullable
		public String script;

		public String weather;
		
		public int worldWidth;
		public int worldHeight;
		
		public float metersPerUnit;
		public float friction;
		
		public SceneArtifactDeclaration[] artifacts = new SceneArtifactDeclaration[0];
		public ArtifactPlane[] artifactPlanes = new ArtifactPlane[0];
		public EntityImportDeclaration[] entities = new EntityImportDeclaration[0];
		public ZoneDeclaration[] zones = new ZoneDeclaration[0];
		
		public WorldConfiguration() {}
		
		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			if(this.script != null)
				target.addChild("script").setValue(this.script);
			
			if(this.weather != null)
				target.addChild("weather").setValue(weather);
			
			target.addChild("worldWidth").setValue(this.worldWidth);
			target.addChild("worldHeight").setValue(this.worldHeight);

			target.addChild("metersPerUnit").setValue(this.metersPerUnit);
			target.addChild("friction").setValue(this.friction);
			
			target.addChild("artifacts").setValue(this.artifacts);
			target.addChild("artifactPlanes").setValue(this.artifactPlanes);
			target.addChild("entities").setValue(this.entities);
			target.addChild("zones").setValue(this.zones);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				if(source.childExists("script"))
					this.script = source.getChild("script").getValue(String.class);
				
				if(source.childExists("weather"))
					this.weather = source.getChild("weather").getValue(String.class);
				
				this.worldWidth = source.getChild("worldWidth").getValue(Integer.class);
				this.worldHeight = source.getChild("worldHeight").getValue(Integer.class);
				
				this.metersPerUnit = source.getChild("metersPerUnit").getValue(Double.class).floatValue();
				this.friction = source.getChild("friction").getValue(Double.class).floatValue();
				
				this.artifacts = source.getChild("artifacts").getValues(SceneArtifactDeclaration[].class);
				this.artifactPlanes = source.getChild("artifactPlanes").getValues(ArtifactPlane[].class);
				this.entities = source.getChild("entities").getValues(EntityImportDeclaration[].class);
				this.zones = source.getChild("zones").getValues(ZoneDeclaration[].class);
			} catch(NoSuchChildVariableException e)
			{
				throw new ValueSerializationException(e);
			}
		}
		
		public static final class ArtifactPlane implements ISerializable
		{
			public float planeZ = 0.0F;
			public int artifactIndices[] = new int[0];
			
			@Override
			public void serialize(IVariable target) throws ValueSerializationException
			{
				target.addChild("planeZ").setValue(this.planeZ);
				target.addChild("artifactIndices").setValue(this.artifactIndices);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException
			{
				try
				{
					this.planeZ = source.getChild("planeZ").getValue(Double.class).floatValue();
					
					Integer indicesBuffer[] = source.getChild("artifactIndices").getValues(Integer[].class);
					
					artifactIndices = new int[indicesBuffer.length];
					
					for(int i = 0; i < artifactIndices.length; i++)
						artifactIndices[i] = indicesBuffer[i];
				} catch(NoSuchChildVariableException e)
				{
					throw new ValueSerializationException(e);
				}
			}
		}
		
		public static final class SceneArtifactDeclaration implements ISerializable
		{
			@Nullable
			public String model;
			
			public boolean isTraversable;
			
			public Direction direction;
			
			public SceneArtifactDeclaration() { }
			
			@Override
			public void serialize(IVariable target) throws ValueSerializationException
			{
				target.addChild("model").setValue(model);
				target.addChild("isTraversable").setValue(this.isTraversable);
				target.addChild("direction").setValue(direction.ordinal());
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException
			{
				try
				{
					this.model = source.getChild("model").getValue(String.class);
					this.isTraversable = source.getChild("isTraversable").getValue(Boolean.class);
					
					Integer dirBuffer = source.getChild("direction").getValue(Integer.class);
					
					if(dirBuffer < 0 || dirBuffer >= Direction.values().length)
						throw new ValueSerializationException(new IndexOutOfBoundsException("Direction ordinal outside of bounds."));

					direction = Direction.values()[dirBuffer];
					
				} catch(NoSuchChildVariableException e)
				{
					throw new ValueSerializationException(e);
				}
			}
		}
		
		public static final class EntityImportDeclaration implements ISerializable
		{
			@Nullable
			public String name;
			
			public String type;
			
			@Nullable
			public Vector3F location;
			
			@Nullable
			public Direction direction;
			
			@Nullable
			public String config;
			
			@Nullable
			public IImmutableVariable auxConfig;
			
			public EntityImportDeclaration() {}

			@Override
			public void serialize(IVariable target) throws ValueSerializationException
			{
				if(this.name != null && type.length() > 0)
					target.addChild("name").setValue(this.name);
				
				target.addChild("type").setValue(this.type);
				
				if(this.location != null)
					target.addChild("location").setValue(this.location);
				
				if(this.direction != null)
					target.addChild("direction").setValue(this.direction.ordinal());
				
				if(this.config != null && config.length() > 0)
					target.addChild("config").setValue(this.config);
				
				if(this.auxConfig != null)
					target.addChild("auxConfig").setValue(this.auxConfig);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException
			{
				try
				{
					if(source.childExists("name"))
						this.name = source.getChild("name").getValue(String.class);
					
					type = source.getChild("type").getValue(String.class);
					
					if(source.childExists("location"))
						this.location = source.getChild("location").getValue(Vector3F.class);
					
					if(source.childExists("direction"))
						this.direction = Direction.values()[source.getChild("direction").getValue(Integer.class)];
					
					if(source.childExists("config"))
						this.config = source.getChild("config").getValue(String.class);
					
					if(source.childExists("auxConfig"))
						this.auxConfig = source.getChild("auxConfig");
				} catch(NoSuchChildVariableException e)
				{
					throw new ValueSerializationException(e);
				}
			}
		}
		
		public static final class ZoneDeclaration implements ISerializable
		{
			@Nullable
			public String name;
			
			@Nullable
			public Rect3F region;
			
			public ZoneDeclaration() {}

			@Override
			public void serialize(IVariable target) throws ValueSerializationException
			{
				target.addChild("name").setValue(name);
				target.addChild("region").setValue(region);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException
			{
				try
				{
					name = source.getChild("name").getValue(String.class);
					region = source.getChild("region").getValue(Rect3F.class);
				} catch(NoSuchChildVariableException e)
				{
					throw new ValueSerializationException(e);
				}
			}
			
		}
	}
}
