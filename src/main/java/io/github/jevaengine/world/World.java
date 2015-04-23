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

import io.github.jevaengine.FutureResult;
import io.github.jevaengine.IDisposable;
import io.github.jevaengine.IInitializationMonitor;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.NullVariable;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Rect2F;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.script.IFunction;
import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilder.ScriptConstructionException;
import io.github.jevaengine.script.NullFunctionFactory;
import io.github.jevaengine.script.ScriptEvent;
import io.github.jevaengine.script.ScriptExecuteException;
import io.github.jevaengine.script.ScriptHiddenMember;
import io.github.jevaengine.script.UnrecognizedFunctionException;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.util.SynchronousExecutor;
import io.github.jevaengine.util.SynchronousExecutor.ISynchronousTask;
import io.github.jevaengine.world.IWeatherFactory.IWeather;
import io.github.jevaengine.world.SceneGraph.EntitySet;
import io.github.jevaengine.world.SceneGraph.ISceneGraphObserver;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntity.EntityBridge;
import io.github.jevaengine.world.entity.IEntityFactory.EntityConstructionException;
import io.github.jevaengine.world.entity.IParallelEntityFactory;
import io.github.jevaengine.world.physics.IPhysicsWorld;
import io.github.jevaengine.world.physics.IPhysicsWorldFactory;
import io.github.jevaengine.world.physics.ScaledPhysicsWorld;
import io.github.jevaengine.world.scene.ISceneBuffer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class World implements IDisposable
{
	private final Logger m_logger = LoggerFactory.getLogger(World.class);
	private final Observers m_observers = new Observers();
	
	private final Map<String, Rect3F> m_zones = new HashMap<>();
	
	private SceneGraph m_sceneGraph;
	private Rect2D m_worldBounds;
	private final float m_metersPerUnit;
	private final float m_logicPerUnit;
	
	private WorldBridgeNotifier m_script;

	private SynchronousExecutor m_syncExecuter = new SynchronousExecutor();

	private final IPhysicsWorld m_physicsWorld;
	private final IParallelEntityFactory m_entityFactory;
	
	private IWeather m_weather;
	
	public World(int worldWidth, int worldHeight, float friction, float metersPerUnit, float logicPerUnit, IWeather weather, IPhysicsWorldFactory physicsWorldFactory, IEffectMapFactory effectMapFactory, IParallelEntityFactory entityFactory, @Nullable IScriptBuilder scriptFactory)
	{
		m_weather = weather;
		m_physicsWorld = new ScaledPhysicsWorld(physicsWorldFactory.create(friction), metersPerUnit);
		m_logicPerUnit = logicPerUnit;
		m_metersPerUnit = metersPerUnit;
		m_entityFactory = entityFactory;
		m_worldBounds = new Rect2D(worldWidth, worldHeight);
		
		m_sceneGraph = new SceneGraph(m_physicsWorld, new ScaledEffectMapFactory(effectMapFactory, m_logicPerUnit));
		m_sceneGraph.getObservers().add(new WorldEntityObserver());
		
		if (scriptFactory != null)
			m_script = new WorldBridgeNotifier(scriptFactory);
		else
			m_script = new WorldBridgeNotifier();
	}
	
	@Override
	public void dispose()
	{
		m_weather.dispose();
		m_sceneGraph.dispose();
	}

	public IWeather getWeather()
	{
		return m_weather;
	}
	
	public void setWeather(IWeather weather)
	{
		if(m_weather != null)
			m_weather.dispose();
		
		m_weather = weather;
	}
	
	public IPhysicsWorld getPhysicsWorld()
	{
		return m_physicsWorld;
	}
	
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	public Rect2D getBounds()
	{
		return new Rect2D(m_worldBounds);
	}
	
	public float getLogicTilePerUnit()
	{
		return m_logicPerUnit;
	}
	
	public float getMetersPerUnit()
	{
		return m_metersPerUnit;
	}

	public IImmutableEffectMap getEffectMap()
	{
		return m_sceneGraph.getEffectMap();
	}
	
	public void addZone(String name, Rect3F zone)
	{
		m_zones.put(name, zone);
	}
	
	public Map<String, Rect3F> getZones()
	{
		return Collections.unmodifiableMap(m_zones);
	}
	
	public Map<String, Rect3F> getContainingZones(Vector3F location)
	{
		Map<String, Rect3F> containingZones = new HashMap<>();
		for(Map.Entry<String, Rect3F> z : m_zones.entrySet())
		{
			if(z.getValue().contains(location))
				containingZones.put(z.getKey(), new Rect3F(z.getValue()));
		}
		
		return containingZones;
	}
	
	public void addEntity(IEntity entity)
	{
		entity.associate(this);
		m_sceneGraph.add(entity);
	}

	public void removeEntity(IEntity entity)
	{
		if(entity.getWorld() == this)
			entity.disassociate();
		
		m_sceneGraph.remove(entity);
	}
	
	public EntitySet getEntities()
	{
		return m_sceneGraph.getEntities();
	}
	
	public WorldBridge getBridge()
	{
		return m_script.getScriptBridge();
	}

	public void update(int delta)
	{
		m_syncExecuter.execute();
		m_sceneGraph.update(delta);
		
		//It is important that the physics world be updated after the entities have been updated.
		//The forces to be applied this cycle may be relative to the delta time elapsed since last cycle.
		m_physicsWorld.update(delta);
		m_weather.update(delta);
	}
	
	public void fillScene(ISceneBuffer sceneBuffer, Rect2F region)
	{
		m_sceneGraph.enqueueRender(sceneBuffer, region);
		sceneBuffer.addEffect(m_weather);
	}
	
	private class WorldEntityObserver implements ISceneGraphObserver
	{
		@Override
		public void addedEntity(IEntity e)
		{
			m_observers.raise(IWorldObserver.class).addedEntity(e);
		}

		@Override
		public void removedEntity(IEntity e)
		{
			m_observers.raise(IWorldObserver.class).removedEntity(e);
		}
	}
	
	private class WorldBridgeNotifier implements IWorldObserver
	{		
		private WorldBridge m_bridge;
		
		public WorldBridgeNotifier()
		{
			m_bridge = new WorldBridge(new NullFunctionFactory());
		}
		
		public WorldBridge getScriptBridge()
		{
			return m_bridge;
		}

		public WorldBridgeNotifier(IScriptBuilder scriptFactory)
		{
			m_bridge = new WorldBridge(scriptFactory.getFunctionFactory());
			
			try
			{
				scriptFactory.create(m_bridge);
			} catch(ScriptConstructionException e)
			{
				m_logger.error("Error instantiating world script", e);
			}
		}

		@Override
		public void addedEntity(IEntity subject)
		{
			try
			{
				m_bridge.onEntityEnter.fire(subject.getBody());
			} catch (ScriptExecuteException e)
			{
				m_logger.error("Unable to completely invoke onEntityEnter script event", e);
			}
		}

		@Override
		public void removedEntity(IEntity subject)
		{
			try
			{
				m_bridge.onEntityLeave.fire(subject.getBody());
			} catch (ScriptExecuteException e)
			{
				m_logger.error("Unable to completely invoke onEntityEnter script event", e);
			}
		}
	}

	public interface IWorldObserver
	{
		void addedEntity(IEntity e);
		void removedEntity(IEntity e);
	}
	
	public final class WorldBridge
	{
		private final IFunctionFactory m_functionFactory;
		
		public final ScriptEvent onTick;
		public final ScriptEvent onEntityEnter;
		public final ScriptEvent onEntityLeave;
		
		private final Logger m_logger = LoggerFactory.getLogger(WorldBridge.class);
		
		private final URI m_context = URI.create("");
		
		private WorldBridge(IFunctionFactory functionFactory)
		{
			onTick = new ScriptEvent(functionFactory);
			onEntityEnter = new ScriptEvent(functionFactory);
			onEntityLeave = new ScriptEvent(functionFactory);
			
			m_functionFactory = functionFactory;
		}
			
		@ScriptHiddenMember
		public World getWorld()
		{
			return World.this;
		}
		
		@Nullable
		private IFunction wrapFunction(Object function)
		{
			if(function == null)
				return null;
			
			try
			{
				return m_functionFactory.wrap(function);
			} catch (UnrecognizedFunctionException e) {
				m_logger.error("Could not wrap function, replacing with null behavior.");
				return null;
			}
		}
		
		public void createEntity(String name, String entityTypeName, String config, IImmutableVariable auxConfig, @Nullable final Object rawSuccessCallback)
		{
			final IFunction successCallback = wrapFunction(rawSuccessCallback);
			
			try
			{
				m_entityFactory.create(entityTypeName, name, config == null ? m_context : new URI(config), auxConfig, new IInitializationMonitor<IEntity, EntityConstructionException>() {

					@Override
					public void statusChanged(float progress, String status) { }

					@Override
					public void completed(final FutureResult<IEntity, EntityConstructionException> item)
					{
						m_syncExecuter.enqueue(new ISynchronousTask() {
							@Override
							public boolean run()
							{
								try
								{
									final IEntity entity = item.get();
									World.this.addEntity(entity);
									
									if(successCallback != null)
										successCallback.call(entity.getBridge());
								}catch(EntityConstructionException e)
								{
									m_logger.error("Unable to construct entity requested by script:", e);
								} catch (ScriptExecuteException e)
								{
									m_logger.error("Error invoking entity construction success callback", e);
								}
								
								return true;
							}
						});
					}
				});
			} catch (URISyntaxException e) {
				m_logger.error("Unable to construct entity requested by script:", e);
			}
		}
		
		public void createEntity(String entityTypeName, String config, IImmutableVariable auxConfig, @Nullable final Object rawSuccessCallback)
		{
			createEntity(null, entityTypeName, config, auxConfig, rawSuccessCallback);
		}
		
		public void createEntity(String entityTypeName, IImmutableVariable auxConfig, @Nullable final Object rawSuccessCallback)
		{
			createEntity(entityTypeName, null, auxConfig, rawSuccessCallback);
		}
		
		public void createEntity(String entityTypeName, String config, @Nullable Object rawSuccessCallback)
		{
			createEntity(entityTypeName, config, new NullVariable(), rawSuccessCallback);
		}
		
		public void createEntity(String entityTypeName, String config)
		{
			createEntity(entityTypeName, config, new NullVariable(), null);
		}
		
		public void createEntity(String entityTypeName, @Nullable final Object rawSuccessCallback)
		{
			createEntity(null, entityTypeName, null, rawSuccessCallback);
		}
		
		public EntityBridge getEntity(String name)
		{
			IEntity e = World.this.getEntities().getByName(IEntity.class, name);
			
			return e == null ? null : e.getBridge();
		}

		public void addEntity(EntityBridge entity)
		{
			World.this.addEntity(entity.getEntity());
		}
		
		public void removeEntity(EntityBridge entity)
		{
			World.this.removeEntity(entity.getEntity());
		}
		
		public Rect3F[] getContainingZones(Vector3F location)
		{
			Collection<Rect3F> zones = World.this.getContainingZones(location).values();
			return zones.toArray(new Rect3F[zones.size()]);
		}
	}
}