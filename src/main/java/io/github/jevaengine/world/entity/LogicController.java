/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.world.entity;

import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.audio.NullAudioClipFactory;
import io.github.jevaengine.script.IFunctionFactory;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.NullFunctionFactory;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.ISceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jeremy
 */
public class LogicController implements IEntity
{
	private static AtomicInteger m_unnamedCount = new AtomicInteger(0);

	private final Logger m_logger = LoggerFactory.getLogger(DefaultEntity.class);
	
	private final String m_name;

	private World m_world;
	
	private HashMap<String, Integer> m_flags = new HashMap<>();
	
	private LogicControllerBridge m_bridge;
	private final IEntityTaskModel m_taskModel;
	
	private final ISceneModel m_model = new NullSceneModel();
	private final IPhysicsBody m_body = new NullPhysicsBody();
	
	private final Observers m_observers = new Observers();
	
	public LogicController(IEntityTaskModelFactory taskModelFactory)
	{
		this(taskModelFactory, null);
	}

	public LogicController(IEntityTaskModelFactory taskModelFactory, @Nullable String name)
	{
		m_name = (name == null ? this.getClass().getName() + m_unnamedCount.getAndIncrement() : name);	
		m_bridge = new LogicControllerBridge(new NullAudioClipFactory(), new NullFunctionFactory(), URI.create(""));

		m_taskModel = taskModelFactory.create(this);
	}
	
	public LogicController(IEntityTaskModelFactory taskModelFactory, IScriptBuilder behavior, IAudioClipFactory audioClipFactory, @Nullable String name)
	{
		m_name = (name == null ? this.getClass().getName() + m_unnamedCount.getAndIncrement() : name);	
		m_taskModel = taskModelFactory.create(this);
	
		m_bridge = new LogicControllerBridge(audioClipFactory, behavior.getFunctionFactory(), behavior.getUri());
		
		try
		{
			behavior.create(m_bridge);
		} catch (IScriptBuilder.ScriptConstructionException e)
		{
			m_logger.error("Unable to instantiate behavior for entity " + m_name + ". Assuming null behavior", e);
		}
	}
	
	@Override
	public void dispose()
	{
		m_observers.clear();
		
		if(m_world != null)
			m_world.removeEntity(this);
	}
	
	@Override
	public IObserverRegistry getObservers()
	{
		return m_observers;
	}
	
	@Override
	public final IPhysicsBody getBody()
	{
		return m_body;
	}
	
	/*
	 * Primitive entity property accessors.
	 */
	
	@Override
	public final String getInstanceName()
	{
		return m_name;
	}
	
	@Override
	public boolean isStatic()
	{
		return false;
	}
	
	@Override
	public IEntityTaskModel getTaskModel()
	{
		return m_taskModel;
	}
	
	/*
	 * Flag operations.
	 */
	public final void setFlag(String name, int value)
	{
		m_flags.put(name, value);
		m_observers.raise(IEntityFlagObserver.class).flagSet(name, value);
	}
		
	public final void setFlag(String name)
	{
		setFlag(name, 0);
	}
		
	public final void clearFlag(String name)
	{
		m_flags.remove(name);
		m_observers.raise(IEntityFlagObserver.class).flagCleared(name);
	}

	public final void clearFlags()
	{
		m_flags.clear();
	}
	
	@Override
	public final Map<String, Integer> getFlags()
	{
		return Collections.unmodifiableMap(m_flags);
	}

	/*
	 * World association routines.
	 */
	@Override
	public final World getWorld()
	{
		return m_world;
	}

	@Override
	public final void associate(World world)
	{
		if (m_world != null)
			throw new WorldAssociationException("Already associated with world");

		m_world = world;

		m_observers.raise(IEntityWorldObserver.class).enterWorld();
	}

	@Override
	public final void disassociate()
	{
		if (m_world == null)
			throw new WorldAssociationException("Not associated with world");

		m_taskModel.cancelTasks();

		m_observers.raise(IEntityWorldObserver.class).leaveWorld();

		m_world = null;
	}
	
	/*
	 * Scene model methods...
	 */
	@Override
	public final IImmutableSceneModel getModel()
	{
		return m_model;
	}
	
	@Override
	public final LogicControllerBridge getBridge()
	{
		return m_bridge;
	}

	@Override
	public final void update(int deltaTime)
	{
		if (m_world == null)
			throw new WorldAssociationException("Entity is unassociated with a world and thus cannot process logic.");

		m_taskModel.update(deltaTime);
		m_model.update(deltaTime);
		doLogic(deltaTime);
	}
	
	protected void doLogic(int deltaTime) { }
	
	public final class LogicControllerBridge extends EntityBridge
	{	
		private LogicControllerBridge(IAudioClipFactory audioClipFactory, IFunctionFactory functionFactory, URI scriptUri)
		{
			super(LogicController.this, audioClipFactory, functionFactory, scriptUri);
		}
		
		public final void setFlag(String name, int value)
		{
			LogicController.this.setFlag(name, value);
		}
		
		public final void clearFlag(String name)
		{
			LogicController.this.clearFlag(name);
		}
		
		public final void clearFlags()
		{
			LogicController.this.clearFlags();
		}
	}
}

