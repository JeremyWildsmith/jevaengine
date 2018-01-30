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
import io.github.jevaengine.config.*;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.graphics.ISpriteFactory;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilderFactory;
import io.github.jevaengine.script.IScriptBuilderFactory.ScriptBuilderConstructionException;
import io.github.jevaengine.script.NullScriptBuilder;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.ThreadSafe;
import io.github.jevaengine.world.DefaultWorldFactory.WorldConfiguration.EntityImportDeclaration;
import io.github.jevaengine.world.DefaultWorldFactory.WorldConfiguration.SceneArtifactImportDeclaration;
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
import io.github.jevaengine.world.scene.model.ISceneModel;
import io.github.jevaengine.world.scene.model.ISceneModelFactory;
import io.github.jevaengine.world.scene.model.ISceneModelFactory.SceneModelConstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

public class DefaultWorldFactory implements IWorldFactory {
	private static final float LOADING_PORTION_LAYERS = 0.8F;
	protected final IEngineThreadPool m_threadPool;
	protected final IEntityFactory m_entityFactory;
	protected final IScriptBuilderFactory m_scriptFactory;
	protected final IConfigurationFactory m_configurationFactory;
	protected final ISpriteFactory m_spriteFactory;
	protected final IAudioClipFactory m_audioClipFactory;
	protected final IPhysicsWorldFactory m_physicsWorldFactory;
	protected final ISceneModelFactory m_sceneModelFactory;
	protected final IEffectMapFactory m_effectMapFactory;
	private final Logger m_logger = LoggerFactory.getLogger(DefaultWorldFactory.class);
	private final IWeatherFactory m_weatherFactory;

	@Inject
	public DefaultWorldFactory(IEngineThreadPool threadPool,
	                           IEntityFactory entityFactory,
	                           IScriptBuilderFactory scriptFactory,
	                           IConfigurationFactory configurationFactory,
	                           ISpriteFactory spriteFactory,
	                           IAudioClipFactory audioClipFactory,
	                           IPhysicsWorldFactory physicsWorldFactory,
	                           ISceneModelFactory sceneModelFactory,
	                           IWeatherFactory weatherFactory,
	                           IEffectMapFactory effectMapFactory) {
		m_threadPool = threadPool;
		m_entityFactory = entityFactory;
		m_scriptFactory = scriptFactory;
		m_configurationFactory = configurationFactory;
		m_spriteFactory = spriteFactory;
		m_audioClipFactory = audioClipFactory;
		m_physicsWorldFactory = physicsWorldFactory;
		m_sceneModelFactory = sceneModelFactory;
		m_weatherFactory = weatherFactory;
		m_effectMapFactory = effectMapFactory;
	}

	protected World createBaseWorld(float friction, float metersPerUnit, float logicPerUnit, int worldWidthTiles, int worldHeightTiles, IWeather weather, @Nullable URI worldScript) {
		IScriptBuilder scriptBuilder = new NullScriptBuilder();

		try {
			if (worldScript != null)
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
				logicPerUnit,
				weather,
				m_physicsWorldFactory,
				m_effectMapFactory,
				new ThreadPooledEntityFactory(m_entityFactory, m_threadPool),
				scriptBuilder);
	}

	protected IEntity createSceneArtifact(SceneArtifactImportDeclaration artifactDecl, URI context) throws EntityConstructionException {
		try {
			ISceneModel model = m_sceneModelFactory.create(context.resolve(new URI(artifactDecl.model)));
			model.setDirection(artifactDecl.direction);

			return new SceneArtifact(model, artifactDecl.isStatic, artifactDecl.isTraversable);

		} catch (SceneModelConstructionException | URISyntaxException e) {
			throw new EntityConstructionException("Scene Artifact", e);
		}
	}

	protected IEntity createEntity(EntityImportDeclaration entityConfig, URI context) throws EntityConstructionException {
		try {
			IImmutableVariable auxConfig = entityConfig.auxConfig == null ? new NullVariable() : entityConfig.auxConfig;

			Class<? extends IEntity> entityClass = m_entityFactory.lookup(entityConfig.type);

			if (entityClass == null)
				throw new EntityConstructionException(new UnsupportedEntityTypeException(entityConfig.type));

			if (entityConfig.config == null)
				return m_entityFactory.create(entityClass,
						entityConfig.name,
						auxConfig);

			return m_entityFactory.create(entityClass,
					entityConfig.name,
					context.resolve(new URI(entityConfig.config)),
					auxConfig);

		} catch (URISyntaxException e) {
			throw new EntityConstructionException(entityConfig.name, e);
		}
	}

	private IWeather createWeather(URI context, WorldConfiguration world) {
		try {
			if (world.weather == null)
				return new NullWeather();

			return m_weatherFactory.create(context.resolve(new URI(world.weather)));
		} catch (WeatherConstructionException | URISyntaxException e) {
			m_logger.error("Unable to construct world weather, using null weather instead.", e);

			return new NullWeather();
		}
	}

	@Override
	@ThreadSafe
	public final World create(URI name, final IInitializationProgressMonitor monitor) throws WorldConstructionException {
		try {

			final WorldConfiguration worldConfig = m_configurationFactory.create(name).getValue(WorldConfiguration.class);

			final int totalArtifactsToLoad = worldConfig.entities.length + worldConfig.artifactImports.length;

			World world = createBaseWorld(worldConfig.friction, worldConfig.metersPerUnit, worldConfig.logicPerUnit,
					worldConfig.worldWidth, worldConfig.worldHeight,
					createWeather(name, worldConfig),
					worldConfig.script == null ? null : name.resolve(new URI(worldConfig.script)));

			for (int i = 0; i < worldConfig.artifactImports.length; i++) {
				SceneArtifactImportDeclaration artifactDeclaration = worldConfig.artifactImports[i];

				try {
					for (Vector3F location : artifactDeclaration.locations) {
						IEntity tile = createSceneArtifact(artifactDeclaration, name);
						world.addEntity(tile);

						tile.getBody().setLocation(location);
					}
				} catch (EntityConstructionException e) {
					m_logger.error("Error constructing scene artifact. Default to exclusion of artifact.", e);
				}

				monitor.statusChanged(i / (float) totalArtifactsToLoad, "Loading Scene Artifacts.");
			}

			for (int i = 0; i < worldConfig.entities.length; i++) {
				EntityImportDeclaration entityConfig = worldConfig.entities[i];

				try {
					IEntity entity = createEntity(entityConfig, name);
					world.addEntity(entity);

					entity.getBody().setLocation(entityConfig.location);
					entity.getBody().setDirection(entityConfig.direction);
				} catch (EntityConstructionException e) {
					m_logger.error("Unable to construct entity, assuming it does not exist.", e);
				}

				monitor.statusChanged((worldConfig.artifactImports.length + i) / (float) totalArtifactsToLoad, "Loading Scene Entities.");
			}

			for (ZoneDeclaration z : worldConfig.zones)
				world.addZone(z.name, z.region);

			monitor.statusChanged(1.0F, "Completed");

			return world;
		} catch (ValueSerializationException | ConfigurationConstructionException | URISyntaxException e) {
			throw new WorldConstructionException(name, e);
		}
	}

	public static final class WorldConfiguration implements ISerializable {
		@Nullable
		public String script;

		public String weather;

		public int worldWidth;
		public int worldHeight;

		public float metersPerUnit;
		public float logicPerUnit;
		public float friction;

		public SceneArtifactImportDeclaration[] artifactImports = new SceneArtifactImportDeclaration[0];
		public EntityImportDeclaration[] entities = new EntityImportDeclaration[0];
		public ZoneDeclaration[] zones = new ZoneDeclaration[0];

		public WorldConfiguration() {
		}

		@Override
		public void serialize(IVariable target) throws ValueSerializationException {
			if (this.script != null)
				target.addChild("script").setValue(this.script);

			if (this.weather != null)
				target.addChild("weather").setValue(weather);

			target.addChild("worldWidth").setValue(this.worldWidth);
			target.addChild("worldHeight").setValue(this.worldHeight);

			target.addChild("logicPerUnit").setValue(this.logicPerUnit);
			target.addChild("metersPerUnit").setValue(this.metersPerUnit);
			target.addChild("friction").setValue(this.friction);

			target.addChild("artifactImports").setValue(this.artifactImports);
			target.addChild("entities").setValue(this.entities);
			target.addChild("zones").setValue(this.zones);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException {
			try {
				if (source.childExists("script"))
					this.script = source.getChild("script").getValue(String.class);

				if (source.childExists("weather"))
					this.weather = source.getChild("weather").getValue(String.class);

				this.worldWidth = source.getChild("worldWidth").getValue(Integer.class);
				this.worldHeight = source.getChild("worldHeight").getValue(Integer.class);

				this.logicPerUnit = source.getChild("logicPerUnit").getValue(Double.class).floatValue();
				this.metersPerUnit = source.getChild("metersPerUnit").getValue(Double.class).floatValue();
				this.friction = source.getChild("friction").getValue(Double.class).floatValue();

				this.artifactImports = source.getChild("artifactImports").getValues(SceneArtifactImportDeclaration[].class);
				this.entities = source.getChild("entities").getValues(EntityImportDeclaration[].class);
				this.zones = source.getChild("zones").getValues(ZoneDeclaration[].class);
			} catch (NoSuchChildVariableException e) {
				throw new ValueSerializationException(e);
			}
		}

		public static final class SceneArtifactImportDeclaration implements ISerializable {
			@Nullable
			public String model;

			public boolean isStatic;

			public Direction direction;

			@Nullable
			public boolean isTraversable;

			public Vector3F[] locations = new Vector3F[0];

			public SceneArtifactImportDeclaration() {
			}

			@Override
			public void serialize(IVariable target) throws ValueSerializationException {
				target.addChild("model").setValue(model);
				target.addChild("isStatic").setValue(this.isStatic);
				target.addChild("direction").setValue(direction.ordinal());
				target.addChild("locations").setValue(locations);
				target.addChild("isTraversable").setValue(this.isTraversable);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException {
				try {
					this.model = source.getChild("model").getValue(String.class);
					this.isStatic = source.getChild("isStatic").getValue(Boolean.class);

					if (source.childExists("locations"))
						this.locations = source.getChild("locations").getValues(Vector3F[].class);

					Integer dirBuffer = source.getChild("direction").getValue(Integer.class);

					if (dirBuffer < 0 || dirBuffer >= Direction.values().length)
						throw new ValueSerializationException(new IndexOutOfBoundsException("Direction ordinal outside of bounds."));

					direction = Direction.values()[dirBuffer];

					this.isTraversable = source.getChild("isTraversable").getValue(Boolean.class);
				} catch (NoSuchChildVariableException e) {
					throw new ValueSerializationException(e);
				}
			}
		}

		public static final class EntityImportDeclaration implements ISerializable {
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

			public EntityImportDeclaration() {
			}

			@Override
			public void serialize(IVariable target) throws ValueSerializationException {
				if (this.name != null && type.length() > 0)
					target.addChild("name").setValue(this.name);

				target.addChild("type").setValue(this.type);

				if (this.location != null)
					target.addChild("location").setValue(this.location);

				if (this.direction != null)
					target.addChild("direction").setValue(this.direction.ordinal());

				if (this.config != null && config.length() > 0)
					target.addChild("config").setValue(this.config);

				if (this.auxConfig != null)
					target.addChild("auxConfig").setValue(this.auxConfig);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException {
				try {
					if (source.childExists("name"))
						this.name = source.getChild("name").getValue(String.class);

					type = source.getChild("type").getValue(String.class);

					if (source.childExists("location"))
						this.location = source.getChild("location").getValue(Vector3F.class);

					if (source.childExists("direction"))
						this.direction = Direction.values()[source.getChild("direction").getValue(Integer.class)];

					if (source.childExists("config"))
						this.config = source.getChild("config").getValue(String.class);

					if (source.childExists("auxConfig"))
						this.auxConfig = source.getChild("auxConfig");
				} catch (NoSuchChildVariableException e) {
					throw new ValueSerializationException(e);
				}
			}
		}

		public static final class ZoneDeclaration implements ISerializable {
			@Nullable
			public String name;

			@Nullable
			public Rect3F region;

			public ZoneDeclaration() {
			}

			@Override
			public void serialize(IVariable target) throws ValueSerializationException {
				target.addChild("name").setValue(name);
				target.addChild("region").setValue(region);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException {
				try {
					name = source.getChild("name").getValue(String.class);
					region = source.getChild("region").getValue(Rect3F.class);
				} catch (NoSuchChildVariableException e) {
					throw new ValueSerializationException(e);
				}
			}
		}
	}
}
