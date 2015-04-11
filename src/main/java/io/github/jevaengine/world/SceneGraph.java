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

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Rect2F;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.MutableProcessList;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.EffectMap.TileEffects;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntity.IEntityBodyObserver;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.IPhysicsBodyContactObserver;
import io.github.jevaengine.world.physics.IPhysicsBodyOrientationObserver;
import io.github.jevaengine.world.physics.IPhysicsWorld;
import io.github.jevaengine.world.physics.PhysicsBodyDescription;
import io.github.jevaengine.world.physics.PhysicsBodyDescription.PhysicsBodyShape;
import io.github.jevaengine.world.physics.PhysicsBodyDescription.PhysicsBodyType;
import io.github.jevaengine.world.scene.ISceneBuffer;
import io.github.jevaengine.world.search.ISearchFilter;
import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class SceneGraph implements IDisposable
{
	private final List<EntitySector> m_sectors = new ArrayList<>();
	private final List<EntityEntry> m_entities = new ArrayList<>();
	private final List<EntityEntry> m_dynamicEntities = new MutableProcessList<>();

	private final Observers m_observers = new Observers();
	
	private final IPhysicsWorld m_hostWorld;
	
	public SceneGraph(IPhysicsWorld hostWorld)
	{
		m_hostWorld = hostWorld;
	}
	
	@Override
	public void dispose()
	{
		for (EntityEntry e : new MutableProcessList<>(m_entities))
		{
			IEntity entity = e.getSubject();
			e.dispose();
			entity.dispose();
		}
		for(EntitySector s : m_sectors)
			s.dispose();
		
		m_sectors.clear();
		m_entities.clear();
	}

	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	private EntitySector getSector(Vector2F location)
	{
		LayerSectorCoordinate sectorCoordinates = new LayerSectorCoordinate(location);
		
		if (!m_sectors.contains(sectorCoordinates))
			m_sectors.add(new EntitySector(location));
		
		int sec = m_sectors.indexOf(sectorCoordinates);

		EntitySector sector = m_sectors.get(sec);
		
		return sector;
	}
	
	@Nullable
	private EntityEntry getEntityEntry(IEntity entity)
	{
		for(EntityEntry entry : m_entities)
		{
			if(entry.getSubject().equals(entity))
				return entry;
		}
		
		return null;
	}
	
	public EntitySet getEntities(@Nullable Rect2D region)
	{
		return new EntitySet(region);
	}
	
	public EntitySet getEntities()
	{
		return getEntities(null);
	}
	
	public void add(IEntity entity)
	{
		EntityEntry entry = new EntityEntry(entity);
		
		m_entities.add(entry);
		
		if(!entity.isStatic())
			m_dynamicEntities.add(entry);
		
		m_observers.raise(ISceneGraphObserver.class).addedEntity(entity);
	}
	
	public void remove(IEntity entity)
	{
		EntityEntry entry = getEntityEntry(entity);
		
		if(entry != null)
		{
			m_entities.remove(entry);
			m_dynamicEntities.remove(entry);
			entry.dispose();

			m_observers.raise(ISceneGraphObserver.class).removedEntity(entity);
		}
	}

	public TileEffects getTileEffects(Vector2D location)
	{
		int index = m_sectors.indexOf(new LayerSectorCoordinate(location));

		if (index < 0)
			return new TileEffects();

		EntitySector sector = m_sectors.get(m_sectors.indexOf(new LayerSectorCoordinate(location)));

		return sector.getTileEffects(location);
	}

	public void update(int delta)
	{
		for (EntitySector sector : m_sectors)
			sector.update(delta);
		
		for(EntityEntry e : m_dynamicEntities)
			e.getSubject().update(delta);
	}

	void enqueueRender(ISceneBuffer targetScene, Rect2F renderBounds)
	{
		HashSet<Integer> renderSectors = new HashSet<>();

		int sectorX = (int)Math.floor((float)renderBounds.x / EntitySector.SECTOR_DIMENSIONS);
		int sectorY = (int)Math.floor((float)renderBounds.y / EntitySector.SECTOR_DIMENSIONS);
		int sectorWidth = (int)Math.ceil((float)renderBounds.width / (float)EntitySector.SECTOR_DIMENSIONS);
		int sectorHeight = (int)Math.ceil((float)renderBounds.height / (float)EntitySector.SECTOR_DIMENSIONS);

		for (int y = sectorY; y <= sectorY + sectorHeight; y++)
		{
			for (int x = sectorX; x <= sectorX + sectorWidth; x++)
			{
				renderSectors.add(m_sectors.indexOf(new LayerSectorCoordinate(x, y, true)));
			}
		}

		HashSet<IEntity> renderEntities = new HashSet<>();
		for (Integer i : renderSectors)
		{
			if (i >= 0)
				m_sectors.get(i).enqueueRender(renderEntities, renderBounds);
		}
		
		for(IEntity e : renderEntities)
			targetScene.addModel(e.getModel(), e, e.getBody().getLocation());
	}
	
	public interface ISceneGraphObserver
	{
		void addedEntity(IEntity e);
		void removedEntity(IEntity e);
	}
	
	private class EntityEntry implements IDisposable
	{
		private final IEntity m_subject;
		private final LocationObserver m_observer = new LocationObserver();
		
		private final ArrayList<EntitySector> m_containingSectors = new ArrayList<>();
		
		public EntityEntry(IEntity subject)
		{
			m_subject = subject;
			subject.getObservers().add(m_observer);
			subject.getBody().getObservers().add(m_observer);
			place();
		}
		
		@Override
		public void dispose()
		{
			remove();
			m_subject.getBody().getObservers().remove(m_observer);
			m_subject.getObservers().remove(m_observer);
		}
		
		private IEntity getSubject()
		{
			return m_subject;
		}
		
		private void place()
		{
			Rect3F aabb = m_subject.getBody().getAABB();
			Vector2D min = aabb.min().getXy().floor();
			Vector2D max = aabb.max().getXy().ceil();
			
			for(int x = min.x; x <= max.x; x++)
			{
				for(int y = min.y; y <= max.y; y++)
				{
					EntitySector s = getSector(new Vector2F(x, y));
					s.addEntity(m_subject);
					m_containingSectors.add(s);
				}
			}
		}
		
		private void remove()
		{
			for(EntitySector s : m_containingSectors)
				s.removeEntity(m_subject);
			
			m_containingSectors.clear();
		}
		
		public void refresh()
		{
			remove();
			place();
		}
		
		private class LocationObserver implements IPhysicsBodyOrientationObserver, IEntityBodyObserver
		{
			@Override
			public void locationSet()
			{
				refresh();
			}
		
			@Override
			public void bodyChanged(IPhysicsBody oldBody, IPhysicsBody newBody)
			{
				oldBody.getObservers().remove(m_observer);
				newBody.getObservers().add(m_observer);
				refresh();
			}
			
			@Override
			public void directionSet() { }
		}
	}
	
	private static class LayerSectorCoordinate
	{
		int x;
		int y;
		boolean isSectorScale;

		public LayerSectorCoordinate(int _x, int _y, boolean _isSectorScale)
		{
			x = _x;
			y = _y;
			isSectorScale = _isSectorScale;
		}
		
		public LayerSectorCoordinate(Vector2F location)
		{
			x = location.round().x;
			y = location.round().y;
		}

		public LayerSectorCoordinate(Vector2D location)
		{
			x = location.x;
			y = location.y;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
				return true;
			else if (o == null)
				return false;
			else if (o instanceof LayerSectorCoordinate)
			{
				LayerSectorCoordinate coord = (LayerSectorCoordinate) o;

				return coord.x == x && coord.y == y && coord.isSectorScale == isSectorScale;
			}
			else if (o instanceof EntitySector)
			{
				EntitySector sector = (EntitySector) o;
				
				if(isSectorScale)
					return sector.m_location.equals(new Vector2D(x, y));
				else
					return new Rect2D(sector.m_location.x * EntitySector.SECTOR_DIMENSIONS,
							sector.m_location.y * EntitySector.SECTOR_DIMENSIONS,
							EntitySector.SECTOR_DIMENSIONS,
							EntitySector.SECTOR_DIMENSIONS)
							.contains(new Vector2D(x, y));
			} else
				return false;
		}
	}

	private final class EntitySector implements IDisposable
	{
		protected static final int SECTOR_DIMENSIONS = 60;

		private final ArrayList<IEntity> m_dynamic =  new ArrayList<>();
		private final ArrayList<IEntity> m_static = new ArrayList<>();

		private final EffectMap m_staticEffectMap = new EffectMap();
		private final EffectMap m_dynamicEffectMap = new EffectMap();

		private final Vector2D m_location;
		private boolean m_isDirty = false;
		
		private final IPhysicsBody m_regionSensorBody;

		public EntitySector(Vector2F containingLocation)
		{
			m_location = containingLocation.divide(SECTOR_DIMENSIONS).floor();
			m_regionSensorBody = m_hostWorld.createBody(new PhysicsBodyDescription(PhysicsBodyType.Static, PhysicsBodyShape.Box, new Rect3F(SECTOR_DIMENSIONS, SECTOR_DIMENSIONS, SECTOR_DIMENSIONS), 1.0F, true, true, 0.0F));
			m_regionSensorBody.setLocation(new Vector3F(m_location.multiply(SECTOR_DIMENSIONS).add(new Vector2D(SECTOR_DIMENSIONS / 2, SECTOR_DIMENSIONS / 2)), 0));
			m_regionSensorBody.getObservers().add(new RegionSensorObserver());
		}

		@Override
		public void dispose()
		{
			m_regionSensorBody.destory();
		}
		
		public void addEntity(IEntity entity)
		{
			if(m_dynamic.contains(entity) || m_static.contains(entity))
				return;
			
			if(entity.isStatic())
			{
				m_static.add(entity);
				m_isDirty = true;
			} else
				m_dynamic.add(entity);
		}
		
		public void removeEntity(IEntity entity)
		{
			m_dynamic.remove(entity);
			
			if(m_static.contains(entity))
			{
				m_static.remove(entity);
				m_isDirty = true;
			}
		}

		public List<IEntity> getEntities()
		{
			ArrayList<IEntity> all = new ArrayList<>(m_dynamic);
			all.addAll(m_static);
			
			return all;
		}
		
		public TileEffects getTileEffects(Vector2D location)
		{
			return m_staticEffectMap.getTileEffects(location).overlay(m_dynamicEffectMap.getTileEffects(location));
		}

		private void blendEffectMap(EffectMap map, IEntity entity)
		{
			IPhysicsBody body = entity.getBody();
			Rect2F bounds = body.getAABB().getXy();
			
			if(body.isCollidable() && bounds.hasArea())
			{
				int x = (int)Math.floor(bounds.x);
				int y = (int)Math.floor(bounds.y);
				int right = x + (int)Math.ceil(bounds.width);
				int bottom = y + (int)Math.ceil(bounds.height);
				
				for(int cx = x; cx < right; cx++)
				{
					for(int cy = y; cy < bottom; cy++)
						map.applyOverlayEffects(new Vector2D(cx, cy), new TileEffects(entity));
				}
			}
		}
		
		public void update(int deltaTime)
		{
			m_dynamicEffectMap.clear();

			for (IEntity e : m_dynamic)
				blendEffectMap(m_dynamicEffectMap, e);
			
			if (m_isDirty)
			{
				m_staticEffectMap.clear();

				for (IEntity e : m_static)
					blendEffectMap(m_staticEffectMap, e);
				
				m_isDirty = false;
			}
		}

		public void enqueueRender(HashSet<IEntity> renderList, Rect2F renderBounds)
		{
			for (IEntity e : m_static)
			{
				Vector2F location = e.getBody().getLocation().getXy();
				
				if (renderBounds.intersects(e.getModel().getAABB().getXy().add(location)))
					renderList.add(e);
			}
			
			for (IEntity e : m_dynamic)
			{
				Vector2D location = e.getBody().getLocation().getXy().round();

				if (renderBounds.intersects(e.getModel().getAABB().getXy().add(location)))
					renderList.add(e);
			}
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
				return true;
			else if (o == null)
				return false;
			else if (o instanceof Vector2D || o instanceof Vector2F)
			{
				Vector2D src = (o instanceof Vector2F) ? ((Vector2F) o).floor() : ((Vector2D) o);
				return new Rect2D(m_location.x * SECTOR_DIMENSIONS, m_location.y * SECTOR_DIMENSIONS, SECTOR_DIMENSIONS, SECTOR_DIMENSIONS).contains(src);
			} else
				return false;
		}
		
		public class RegionSensorObserver implements IPhysicsBodyContactObserver
		{
			@Override
			public void onBeginContact(IImmutablePhysicsBody other)
			{
				if(!other.hasOwner())
					return;
				
				EntityEntry e = getEntityEntry(other.getOwner());
				
				if(e != null)
					e.refresh();
			}

			@Override
			public void onEndContact(IImmutablePhysicsBody other)
			{
				if(!other.hasOwner())
					return;
				
				EntityEntry e = getEntityEntry(other.getOwner());
				
				if(e != null)
					e.refresh();
			}
		}
	}
	
	public final class EntitySet
	{
		@Nullable
		private Rect2D m_region;
		
		private EntitySet(Rect2D region)
		{
			m_region = region;
		}
		
		private EntitySet()
		{
			this(null);
		}
		
		@SuppressWarnings("unchecked")
		@Nullable
		public <T extends IEntity> T getByName(Class<T> clazz, String name)
		{
			if(m_region != null)
			{
				for(T e : getContainedEntities(clazz, m_region))
					return e;
			} else
			{
				for(EntityEntry entry : m_entities)
				{
					IEntity e = entry.getSubject();
					
					if(e.getInstanceName().equals(name) && clazz.isAssignableFrom(e.getClass()))
						return (T)e;
				}
			}
			
			return null;
		}
		
		private <T extends IEntity> T[] getContainedEntities(Class<T> clazz, Rect2D region)
		{
			//Used to prevent entry duplication for Entities that are contained by multiple sectors.
			HashSet<T> entities = new HashSet<>();
			
			final int startX = (int)Math.floor(region.x / (float)EntitySector.SECTOR_DIMENSIONS) * EntitySector.SECTOR_DIMENSIONS;
			final int startY = (int)Math.floor(region.y / (float)EntitySector.SECTOR_DIMENSIONS) * EntitySector.SECTOR_DIMENSIONS;
			
			for(int x = startX; x <= region.x + region.width; x+= EntitySector.SECTOR_DIMENSIONS)
			{
				  for(int y = startY; y < region.y + region.height; y+=EntitySector.SECTOR_DIMENSIONS)
				  {
						for(IEntity e : getSector(new Vector2F(x + 1, y + 1)).getEntities())
						{
							if(clazz.isAssignableFrom(e.getClass()))
								entities.add((T)e);
						}
					}
			}
			
			return entities.toArray((T[])Array.newInstance(clazz, entities.size()));
		}
		
		public <T extends IEntity> T[] search(Class<T> clazz, ISearchFilter<T> filter)
		{
			//if m_region == null, use filter's search bounds,
			//otherwise use intersecting area.
			
			Rect2D searchBounds = m_region == null ? filter.getSearchBounds() : m_region.getOverlapping(filter.getSearchBounds());
			
			ArrayList<T> found = new ArrayList<>();

			for (T entity : getContainedEntities(clazz, searchBounds))
			{
				if (filter.shouldInclude(entity.getBody().getLocation().getXy()))
					found.add(entity);
			}

			return found.toArray((T[])Array.newInstance(clazz, found.size()));
		}
		
		public IEntity[] all()
		{
			if(m_region != null)
				return getContainedEntities(IEntity.class, m_region);
			
			ArrayList<IEntity> entities = new ArrayList<>();
			
			for(EntityEntry e : m_entities)
				entities.add(e.getSubject());
			
			return entities.toArray(new IEntity[m_entities.size()]);
		}
	}
}
