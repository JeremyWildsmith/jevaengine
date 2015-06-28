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
package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3D;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel.ISceneModelComponent;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class TopologicalOrthographicProjectionSceneBuffer implements ISceneBuffer
{
	private static final int NUM_CONCURRENT_SORTS = Runtime.getRuntime().availableProcessors();
	
	private static final ExecutorService m_exector = Executors.newFixedThreadPool(NUM_CONCURRENT_SORTS, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
	});
		
	private boolean m_isTopologicalSortDirty = false;
	private Matrix3X3 m_worldToScreenMatrix;
	
	private Vector2D m_translation = new Vector2D();

	private final LinkedList<Vertex> m_unsortedVertices = new LinkedList<>();
	private final LinkedList<Vertex> m_sortedVertices = new LinkedList<>();
	private final List<ISceneBufferEffect> m_effects = new ArrayList<>();
	
	private final ConcurrentLinkedQueue<Vertex> m_dependencyMappingWorkQueue = new ConcurrentLinkedQueue<>();
	
	private final List<DependencyConstructRoutine> m_dependenyConstructRoutines = new ArrayList<>();
	
	public TopologicalOrthographicProjectionSceneBuffer(Matrix3X3 projection)
	{
		m_worldToScreenMatrix = new Matrix3X3(projection);
		
		for(int i = 0; i < NUM_CONCURRENT_SORTS; i++)
			m_dependenyConstructRoutines.add(new DependencyConstructRoutine(m_dependencyMappingWorkQueue, m_unsortedVertices));
	}
	
	@Override
	public void translate(Vector2D translation)
	{
		m_translation = m_translation.add(translation);
	}
	
	private void visit(Vertex v)
	{
		if(v.wasVisited())
			return;
		
		v.markVisited();
		
		while(v.hasIns())
			visit(v.removeIn());
		
		m_sortedVertices.add(v);
	}
	
	private void sort()
	{
		if(!m_isTopologicalSortDirty)
			return;
		
		//Move all sorted entries back into unsorted queue since they must be resorted with the new entries.
		m_unsortedVertices.addAll(m_sortedVertices);
		m_sortedVertices.clear();
		
		m_dependencyMappingWorkQueue.clear();
		m_dependencyMappingWorkQueue.addAll(m_unsortedVertices);
		
		try
		{
			m_exector.invokeAll(m_dependenyConstructRoutines);
		} catch (InterruptedException e) {
			//We've been interrupted. Cancel the sort and return early. This would result in
			//the scene being empty for the present operation that required the scene be sorted.
			//Not a big deal considering how frequently frames are generated.
			Thread.currentThread().interrupt();
			return;
		}
		
		for(Vertex v; (v = m_unsortedVertices.poll()) != null;)
		{
			visit(v);
			v.clearIns();
		}
		
		m_unsortedVertices.clear();
		m_isTopologicalSortDirty = false;
	}
	
	@Override
	public void addModel(IImmutableSceneModel model, @Nullable IEntity dispatcher, Vector3F location)
	{
		m_isTopologicalSortDirty = true;
		
		for(ISceneModelComponent c : model.getComponents(new Matrix3X3(m_worldToScreenMatrix)))
			m_unsortedVertices.add(new Vertex(new SceneGraphicEntry(dispatcher, c, location.add(c.getOrigin()), m_worldToScreenMatrix)));
	}

	@Override
	public void addModel(IImmutableSceneModel model, Vector3F location)
	{
		addModel(model, null, location);
	}
	
	@Override
	public void addEffect(ISceneBufferEffect effect)
	{
		m_effects.add(effect);
	}

	@Override
	public void reset()
	{
		m_isTopologicalSortDirty = false;
		m_unsortedVertices.clear();
		m_sortedVertices.clear();
		m_effects.clear();
		m_translation = new Vector2D();
	}
	
	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation, float scale)
	{
		return m_worldToScreenMatrix.scale(scale).inverse().dot(screenLocation.difference(new Vector3F(m_translation, 0))).getXy();
	}
	
	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation)
	{
		return translateScreenToWorld(screenLocation, 1.0F);
	}
	
	@Override
	public Vector2D translateWorldToScreen(Vector3F location, float fScale)
	{
		Vector3D translation = m_worldToScreenMatrix.scale(fScale).dot(location).add(new Vector3F(m_translation, 0)).round();
		return new Vector2D(translation.x, translation.y);
	}
	
	@Override
	public Vector2D translateWorldToScreen(Vector3F location)
	{
		return translateWorldToScreen(location, 1.0F);
	}

	private List<Queue<ISceneComponentEffect>> createComponentRenderEffects(Graphics2D g, int offsetX, int offsetY, float scale, Vertex subject)
	{
		List<ISceneBufferEntry> beneath = new ArrayList<>();
		
		for(Vertex v : subject.getPersistentIns())
			beneath.add(v.m_entry);
	
		List<Queue<ISceneComponentEffect>> effects = new ArrayList<>();
		for(ISceneBufferEffect e : m_effects)
			effects.add(new LinkedList<>(Arrays.asList(e.getComponentEffect(g, offsetX, offsetY, scale, new Matrix3X3(m_worldToScreenMatrix), subject.m_entry, beneath))));

		return effects;
	}
	
	@Override
	public void render(Graphics2D g, int offsetX, int offsetY, float scale, Rect2D bounds)
	{
		for(ISceneBufferEffect e : m_effects)
			e.getUnderlay(bounds, new Matrix3X3(m_worldToScreenMatrix)).render(g, offsetX, offsetY, scale);
		
		sort();
		for (Vertex v : m_sortedVertices)
		{
			Vector2D renderLocation = translateWorldToScreen(v.m_entry.location, scale);
			
			List<Queue<ISceneComponentEffect>> effects = createComponentRenderEffects(g, offsetX, offsetY, scale, v);			
			
			do
			{
				List<ISceneComponentEffect> passEffects = new ArrayList<>();
				
				Iterator<Queue<ISceneComponentEffect>> it = effects.iterator();
				
				while(it.hasNext())
				{
					Queue<ISceneComponentEffect> c = it.next();
					ISceneComponentEffect effect = c.poll();
					
					if(c.isEmpty())
						it.remove();
					
					if(effect != null)
						passEffects.add(effect);
				}
				
				for(ISceneComponentEffect e : passEffects)
					e.prerender();
				
				v.m_entry.component.render(g, renderLocation.x + offsetX, renderLocation.y + offsetY, scale);

				for(ISceneComponentEffect e : passEffects)
					e.postrender();
				
			}while(!effects.isEmpty());
		}
		
		for(ISceneBufferEffect e : m_effects)
			e.getOverlay(bounds, new Matrix3X3(m_worldToScreenMatrix)).render(g, offsetX, offsetY, scale);
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T pick(Class<T> clazz, int x, int y, float scale)
	{
		sort();
		
		if(m_sortedVertices.isEmpty())
			return null;
		
		ListIterator<Vertex> it = m_sortedVertices.listIterator(m_sortedVertices.size());
		
		while(it.hasPrevious())
		{
			SceneGraphicEntry entry = it.previous().m_entry;
			
			Vector2D renderLocation = translateWorldToScreen(entry.location, scale);
			Vector2D relativePick = new Vector2D(x - renderLocation.x, y - renderLocation.y);
			
			IEntity dispatcher = entry.dispatcher;
			
			if(dispatcher != null &&
				clazz.isAssignableFrom(dispatcher.getClass()) &&
				entry.component.testPick(relativePick.x, relativePick.y, scale))
				return (T)dispatcher;
		}
		
		return null;
	}
	
	private static final class DependencyConstructRoutine implements Callable<Void>
	{
		private final Queue<Vertex> m_subjectQueue;
		private final List<Vertex> m_otherSet;
		
		public DependencyConstructRoutine(Queue<Vertex> subjectQueue, List<Vertex> otherSet)
		{
			m_subjectQueue = subjectQueue;
			m_otherSet = otherSet;
		}
		
		private static boolean isBehind(Rect3F a, Rect3F b)
		{
			Vector3F bMax = b.max().add(new Vector3F());
			Vector3F aMin = a.min();
			
			return (aMin.x - bMax.x < 0 && aMin.y - bMax.y < 0 && aMin.z - bMax.z < 0);
		}
		
		@Override
		public Void call() throws Exception
		{
			for(Vertex subject; (subject = m_subjectQueue.poll()) != null;)
			{
				subject.clearVisit();
				
				for(Vertex other : m_otherSet)
				{
					if(!subject.m_entry.projectedAABB.intersects(other.m_entry.projectedAABB) || subject == other)
						continue;
				
					//If other is behind me, that I be be reached to from it.
					if(isBehind(other.m_entry.bounds, subject.m_entry.bounds))
						subject.inFrom(other);
				}
			}
			
			return null;
		}
	}
	
	private final class SceneGraphicEntry implements ISceneBufferEntry
	{
		private ISceneModelComponent component;
		
		@Nullable
		private IEntity dispatcher;
		
		private Rect3F bounds;
		private Rect2D projectedAABB;
		
		private Vector3F location;
		
		public SceneGraphicEntry(IEntity _dispatcher, ISceneModelComponent _graphic, Vector3F _location, Matrix3X3 projectionMatrix)
		{
			component = _graphic;
			dispatcher = _dispatcher;
			bounds = new Rect3F(_graphic.getBounds()).add(_location);
			projectedAABB = calculateProjectedAABB(bounds, projectionMatrix).add(m_translation);
			
			location = new Vector3F(_location);
		}
		
		private Rect2D calculateProjectedAABB(Rect3F a, Matrix3X3 projectionMatrix)
		{
			Rect2D aAABB = new Rect2D();
			
			aAABB.x = (int)projectionMatrix.dot(a.getPoint(0, 1.0F, 0)).x;
			aAABB.y = (int)projectionMatrix.dot(a.getPoint(0, 0, 1)).y;
			aAABB.width = (int)projectionMatrix.dot(a.getPoint(1, 0, 1)).x - aAABB.x;
			aAABB.height = (int)projectionMatrix.dot(a.getPoint(1, 1, 0)).y - aAABB.y;
			
			return aAABB;
		}

		@Override
		public IEntity getDispatcher()
		{
			return dispatcher;
		}

		@Override
		public ISceneModelComponent getComponent()
		{
			return component;
		}

		@Override
		public Rect2D getProjectedAABB()
		{
			return projectedAABB;
		}
	}
	
	private static final class Vertex
	{
		private SceneGraphicEntry m_entry;
		private boolean m_wasVisited = false;
		private final ArrayList<Vertex> m_ins = new ArrayList<>();
		private final ArrayList<Vertex> m_persistentIns = new ArrayList<>();
		
		public Vertex(SceneGraphicEntry e)
		{
			m_entry = e;
		}
		
		public void inFrom(Vertex src)
		{
			m_ins.add(src);
			m_persistentIns.add(src);
		}
	
		public void clearIns()
		{
			m_ins.clear();
		}
		
		public void clearVisit()
		{
			m_wasVisited = false;
		}
		
		public void markVisited()
		{
			m_wasVisited = true;
		}
		
		public boolean wasVisited()
		{
			return m_wasVisited;
		}
		
		public boolean hasIns()
		{
			return !m_ins.isEmpty();
		}
		
		@Nullable
		public Vertex removeIn()
		{
			if(m_ins.isEmpty())
				return null;
			
			Vertex v = m_ins.get(0);
			m_ins.remove(0);
			
			return v;
		}
		
		@Nullable
		public Vertex[] getPersistentIns()
		{
			return m_persistentIns.toArray(new Vertex[m_persistentIns.size()]);
		}
	}
}
