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
package io.github.jevaengine.world.entity;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.script.*;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.World.WorldBridge;
import io.github.jevaengine.world.entity.tasks.IdleTask;
import io.github.jevaengine.world.entity.tasks.InvokeScriptFunctionTask;
import io.github.jevaengine.world.entity.tasks.InvokeScriptTimeoutFunctionTask;
import io.github.jevaengine.world.entity.tasks.SynchronousOneShotTask;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.IPhysicsBodyOrientationObserver;
import io.github.jevaengine.world.physics.NullPhysicsBody;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;
import io.github.jevaengine.world.scene.model.NullSceneModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface IEntity extends IDisposable {
	/*
	 * World association methods.
	 */
	@Nullable
	World getWorld();

	void associate(World world);

	void disassociate();

	/*
	 * Entity property methods
	 */
	String getInstanceName();

	Map<String, Integer> getFlags();

	boolean isStatic();

	/*
	 * Returns the entity's scene model.
	 */
	IImmutableSceneModel getModel();

	/*
	 * Physics body accessor
	 */
	IPhysicsBody getBody();

	IEntityTaskModel getTaskModel();

	/*
	 * Observer methods
	 */
	IObserverRegistry getObservers();

	/*
	 * Entity bridge, used as interface to entity via scripts.
	 */
	EntityBridge getBridge();

	/*
	 * Logic routine.
	 */
	void update(int delta);

	public interface IEntityWorldObserver {
		void enterWorld();

		void leaveWorld();
	}

	public interface IEntityFlagObserver {
		void flagSet(String name, int value);

		void flagCleared(String name);
	}

	public interface IEntityBodyObserver {
		void bodyChanged(IPhysicsBody oldBody, IPhysicsBody newBody);
	}

	public static class EntityBridge {
		public final ScriptEvent onEnter;
		public final ScriptEvent onLeave;
		public final ScriptEvent onFlagSet;
		public final ScriptEvent onFlagCleared;
		public final ScriptEvent onLocationSet;
		protected final URI m_context;
		private final Logger m_logger = LoggerFactory.getLogger(EntityBridge.class);
		private final IEntity m_host;
		private final IFunctionFactory m_functionFactory;
		private final HashMap<String, IFunction> m_interfaceMapping = new HashMap<>();
		private final EntityBridgeNotifier m_notifier = new EntityBridgeNotifier();

		public EntityBridge(IEntity host, IFunctionFactory functionFactory, URI context) {
			m_host = host;
			m_context = context;
			m_functionFactory = functionFactory;

			onEnter = new ScriptEvent(functionFactory);
			onLeave = new ScriptEvent(functionFactory);
			onFlagSet = new ScriptEvent(functionFactory);
			onFlagCleared = new ScriptEvent(functionFactory);
			onLocationSet = new ScriptEvent(functionFactory);

			host.getObservers().add(m_notifier);
			host.getBody().getObservers().add(m_notifier);
		}

		public EntityBridge(IEntity host, IFunctionFactory functionFactory) {
			this(host, functionFactory, URI.create(""));
		}

		public EntityBridge(IEntity host) {
			this(host, new NullFunctionFactory());
		}

		@ScriptHiddenMember
		public final IEntity getEntity() {
			return m_host;
		}

		public final WorldBridge getWorld() {
			World world = m_host.getWorld();

			return world == null ? null : world.getBridge();
		}

		public final String resolve(String path) {
			try {
				return m_context.resolve(new URI(path)).toString();
			} catch (URISyntaxException e) {
				m_logger.error("Unable to resolve path. Assuming no resolution has taken place.", e);
				return path;
			}
		}

		public final String getUri() {
			return m_context.toString();
		}

		public final void log(String message) {
			m_logger.info("Log from " + m_context + ":" + m_host.getInstanceName() + ", " + message);
		}

		public final String getInstanceName() {
			return m_host.getInstanceName();
		}

		public final Vector3F getLocation() {
			return getEntity().getBody().getLocation();
		}

		public final void setLocation(Vector3F location) {
			getEntity().getBody().setLocation(location);
		}

		public final void setLocation(float x, float y, float z) {
			getEntity().getBody().setLocation(new Vector3F(x, y, z));
		}

		public final void cancelTasks() {
			getEntity().getTaskModel().cancelTasks();
		}

		public final void invoke(Object target, Object... parameters) {
			try {
				getEntity().getTaskModel().addTask(new InvokeScriptFunctionTask(m_functionFactory.wrap(target), parameters));
			} catch (UnrecognizedFunctionException e) {
				m_logger.error("Error wrapping invoke target on entity " + getEntity().getInstanceName(), e);
			}
		}

		public final void invokeTimeout(int timeout, Object target, Object... parameters) {
			try {
				getEntity().getTaskModel().addTask(new InvokeScriptTimeoutFunctionTask(timeout, m_functionFactory.wrap(target), parameters));
			} catch (UnrecognizedFunctionException e) {
				m_logger.error("Error wrapping invoke target on entity " + getEntity().getInstanceName(), e);
			}
		}

		public final void idle(int length) {
			getEntity().getTaskModel().addTask(new IdleTask(length));
		}

		public final void leave() {
			getEntity().getTaskModel().addTask(new SynchronousOneShotTask() {
				@Override
				public void run(IEntity entity) {
					getEntity().getWorld().removeEntity(entity);
					entity.dispose();
				}
			});
		}

		public final boolean testFlag(String name, int value) {
			Integer i = getEntity().getFlags().get(name);

			return i != null && i.equals(value);
		}

		public final int getFlag(String name) {
			Integer i = getEntity().getFlags().get(name);

			return i == null ? 0 : i;
		}

		public final boolean isFlagSet(String name) {
			return getEntity().getFlags().get(name) != null;
		}

		public final void mapInterface(String interfaceName, Object rawFunction) {
			try {
				m_interfaceMapping.put(interfaceName, m_functionFactory.wrap(rawFunction));

			} catch (UnrecognizedFunctionException e) {
				m_logger.error("Unable to map interface " + interfaceName + " on entity" + getInstanceName(), e);
			}
		}

		public final Object invokeInterface(String interfaceName, Object... arguments) throws NoSuchInterfaceException, InterfaceExecuteException {
			IFunction function = m_interfaceMapping.get(interfaceName);

			if (function == null)
				throw new NoSuchInterfaceException(interfaceName);

			try {
				return function.call(arguments);
			} catch (ScriptExecuteException e) {
				throw new InterfaceExecuteException(interfaceName, e);
			}
		}

		public static final class InterfaceExecuteException extends Exception {
			private static final long serialVersionUID = 1L;

			public InterfaceExecuteException(String name, Exception cause) {
				super("Error executing interface: " + name, cause);
			}
		}

		public static final class NoSuchInterfaceException extends Exception {
			private static final long serialVersionUID = 1L;

			public NoSuchInterfaceException(String name) {
				super("No such interface: " + name);
			}
		}

		private class EntityBridgeNotifier implements IEntityWorldObserver, IEntityFlagObserver, IEntityBodyObserver, IPhysicsBodyOrientationObserver {
			@Override
			public void enterWorld() {
				try {
					onEnter.fire();
				} catch (ScriptExecuteException e) {
					m_logger.error("onEnter delegate failed on entity " + m_host.getInstanceName(), e);
				}
			}

			@Override
			public void leaveWorld() {
				try {
					onLeave.fire();
				} catch (ScriptExecuteException e) {
					m_logger.error("onLeave delegate failed on entity " + m_host.getInstanceName(), e);
				}
			}

			@Override
			public void flagSet(String name, int value) {
				try {
					onFlagSet.fire(name, value);
				} catch (ScriptExecuteException e) {
					m_logger.error("onFlagSet delegate failed on entity " + m_host.getInstanceName(), e);
				}
			}

			@Override
			public void flagCleared(String name) {
				try {
					onFlagCleared.fire(name);
				} catch (ScriptExecuteException e) {
					m_logger.error("onFlagCleared delegate failed on entity " + m_host.getInstanceName(), e);
				}
			}

			@Override
			public void locationSet() {
				try {
					onLocationSet.fire();
					;
				} catch (ScriptExecuteException e) {
					m_logger.error("onLocationSet delegate failed on entity " + m_host.getInstanceName(), e);
				}
			}

			@Override
			public void directionSet() {
			}

			@Override
			public void bodyChanged(IPhysicsBody oldBody, IPhysicsBody newBody) {
				oldBody.getObservers().remove(m_notifier);
				newBody.getObservers().add(m_notifier);
			}
		}
	}

	public static final class NullEntity implements IEntity {
		private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

		private final String m_name = getClass().getName() + INSTANCE_COUNT.getAndIncrement();
		private final EntityBridge m_bridge;
		private World m_world;

		public NullEntity() {
			m_bridge = new EntityBridge(this);
		}

		@Override
		public void dispose() {
			if (m_world != null)
				disassociate();
		}

		@Override
		@Nullable
		public World getWorld() {
			return m_world;
		}

		@Override
		public void associate(World world) {
			if (m_world != null)
				throw new WorldAssociationException("Entity already associated to world.");

			m_world = world;

		}

		@Override
		public void disassociate() {
			if (m_world == null)
				throw new WorldAssociationException("Entity has not been associated to a world.");

			m_world = null;
		}

		@Override
		public String getInstanceName() {
			return m_name;
		}

		@Override
		public Map<String, Integer> getFlags() {
			return new HashMap<>();
		}

		@Override
		public boolean isStatic() {
			return true;
		}

		@Override
		public IImmutableSceneModel getModel() {
			return new NullSceneModel();
		}

		@Override
		public IPhysicsBody getBody() {
			return new NullPhysicsBody();
		}

		@Override
		public IEntityTaskModel getTaskModel() {
			return new NullEntityTaskModel();
		}

		@Override
		public IObserverRegistry getObservers() {
			return new NullObservers();
		}

		@Override
		public EntityBridge getBridge() {
			return m_bridge;
		}

		@Override
		public void update(int delta) {
		}
	}
}
