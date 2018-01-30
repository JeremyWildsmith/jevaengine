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
package io.github.jevaengine.world.scene.model.sprite;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.audio.IAudioClip;
import io.github.jevaengine.audio.IAudioClipFactory.AudioClipConstructionException;
import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;
import io.github.jevaengine.world.physics.PhysicsBodyShape.PhysicsBodyShapeType;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.sprite.SpriteSceneModelComponent.IDefaultSceneModelComponentObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class SpriteSceneModel implements IAnimationSceneModel {
	private final LinkedHashMap<String, SpriteSceneModelAnimation> m_animations = new LinkedHashMap<>();

	private SpriteSceneModelAnimation m_currentAnimation = null;
	private Direction m_direction = Direction.XYPlus;

	private PhysicsBodyShape m_bodyShape;

	SpriteSceneModel(PhysicsBodyShape bodyShape) {
		m_bodyShape = new PhysicsBodyShape(bodyShape);
	}

	SpriteSceneModel() {
	}

	SpriteSceneModel(SpriteSceneModel source) {
		for (Map.Entry<String, SpriteSceneModelAnimation> a : source.m_animations.entrySet()) {
			SpriteSceneModelAnimation coppiedAnimation = new SpriteSceneModelAnimation(a.getValue());
			m_animations.put(a.getKey(), coppiedAnimation);

			if (source.m_currentAnimation == a.getValue())
				m_currentAnimation = coppiedAnimation;
		}

		m_direction = source.m_direction;

		if (source.m_bodyShape != null)
			m_bodyShape = new PhysicsBodyShape(source.m_bodyShape);
	}

	@Override
	public SpriteSceneModel clone() {
		return new SpriteSceneModel(this);
	}

	@Override
	public void dispose() {
		for (SpriteSceneModelAnimation a : m_animations.values())
			a.dispose();

		m_animations.clear();
	}

	void addAnimation(String name, SpriteSceneModelAnimation animation) {
		m_animations.put(name, animation);
		refreshCurrentAnimation();
	}

	@Override
	public List<ISceneModelComponent> getComponents(Matrix3X3 projection) {
		if (m_currentAnimation == null)
			return new ArrayList<>();

		return m_currentAnimation.getComponents();
	}

	@Override
	public Rect3F getAABB() {
		if (m_animations.size() == 0)
			return new Rect3F();

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minZ = Float.MAX_VALUE;

		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		float maxZ = Float.MIN_VALUE;

		for (SpriteSceneModelAnimation a : m_animations.values()) {
			Rect3F aabb = a.getAABB();
			minX = Math.min(minX, aabb.x);
			minY = Math.min(minY, aabb.y);
			minZ = Math.min(minZ, aabb.z);

			maxX = Math.max(maxX, aabb.x + aabb.width);
			maxY = Math.max(maxY, aabb.y + aabb.height);
			maxZ = Math.max(maxZ, aabb.z + aabb.depth);
		}

		return new Rect3F(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
	}

	@Override
	public PhysicsBodyShape getBodyShape() {
		if (m_bodyShape == null)
			m_bodyShape = new PhysicsBodyShape(PhysicsBodyShapeType.Box, getAABB());

		return new PhysicsBodyShape(m_bodyShape);
	}

	@Override
	public IAnimationSceneModelAnimation getAnimation(String name) {
		SpriteSceneModelAnimation animation = m_animations.get(name);

		if (animation == null)
			return new NullAnimationSceneModelAnimation();

		return animation;
	}

	@Override
	public boolean hasAnimation(String name) {
		return m_animations.containsKey(name);
	}

	private void refreshCurrentAnimation() {
		refreshCurrentAnimation(null);
	}

	private void refreshCurrentAnimation(@Nullable SpriteSceneModelAnimation lastActive) {
		SpriteSceneModelAnimation previous = m_currentAnimation;

		if (m_currentAnimation != null && m_currentAnimation.getState() == AnimationSceneModelAnimationState.Stop)
			m_currentAnimation = null;

		//If we are refreshing due to a state change in a active animation (ie, the one interfaced with causing the
		//activation of this subroutine.) then we will give it priority in executing. Otherwise, iterate over animations until
		//an active one is located.
		if (lastActive != null && lastActive.getInternalState() != AnimationSceneModelAnimationState.Stop)
			m_currentAnimation = lastActive;

		if (previous != m_currentAnimation) {
			if (previous != null)
				previous.m_observers.raise(IAnimationSceneModelAnimationObserver.class).stateChanged(previous.getState());

			if (m_currentAnimation != null)
				m_currentAnimation.m_observers.raise(IAnimationSceneModelAnimationObserver.class).stateChanged(m_currentAnimation.getState());
		}

		//If we've resulted in a null animation, we'll default back to previous animation.
		//This prevents short clips where no animation is active (resulting in a blank flicker in-between animation selection.)
		if (m_currentAnimation == null)
			m_currentAnimation = previous;
	}

	@Override
	public void update(int delta) {
		refreshCurrentAnimation();

		if (m_currentAnimation != null)
			m_currentAnimation.update(delta);

	}

	@Override
	public Direction getDirection() {
		return m_direction;
	}

	@Override
	public void setDirection(Direction direction) {
		m_direction = direction;
		refreshCurrentAnimation();
	}

	public final class SpriteSceneModelAnimation implements IAnimationSceneModelAnimation, IDisposable {
		private final Logger m_logger = LoggerFactory.getLogger(SpriteSceneModelAnimation.class);

		private final Map<Direction, List<SpriteSceneModelComponent>> m_components = new HashMap<>();
		private final Map<Direction, Rect3F> m_aabbs = new HashMap<>();
		private final Map<String, IAudioClip> m_eventAudio = new HashMap<>();

		private final Observers m_observers = new Observers();

		SpriteSceneModelAnimation() {
		}

		SpriteSceneModelAnimation(SpriteSceneModelAnimation source) {
			for (Map.Entry<Direction, List<SpriteSceneModelComponent>> e : source.m_components.entrySet()) {
				for (SpriteSceneModelComponent c : e.getValue())
					addComponent(e.getKey(), new SpriteSceneModelComponent(c));
			}

			for (Map.Entry<String, IAudioClip> a : source.m_eventAudio.entrySet()) {
				try {
					m_eventAudio.put(a.getKey(), a.getValue().create());
				} catch (AudioClipConstructionException e) {
					m_logger.error("Error occured attempting to duplicate event audio on model", e);
				}
			}
		}

		@Override
		public void dispose() {
			for (IAudioClip c : m_eventAudio.values())
				c.dispose();

			m_eventAudio.clear();
		}

		private void mergeAABB(Direction direction, Rect3F aabb) {
			if (!m_aabbs.containsKey(direction))
				m_aabbs.put(direction, new Rect3F(aabb));

			Rect3F destAabb = m_aabbs.get(direction);

			float maxX = Math.max(destAabb.x + destAabb.width, aabb.x + aabb.width);
			float maxY = Math.max(destAabb.y + destAabb.height, aabb.y + aabb.height);
			float maxZ = Math.max(destAabb.z + destAabb.depth, aabb.z + aabb.depth);

			destAabb.x = Math.min(aabb.x, destAabb.x);
			destAabb.y = Math.min(aabb.y, destAabb.y);
			destAabb.z = Math.min(aabb.z, destAabb.z);
			destAabb.width = maxX - destAabb.x;
			destAabb.height = maxY - destAabb.y;
			destAabb.depth = maxZ - destAabb.z;
		}

		private Rect3F getAABB() {
			Rect3F aabb = m_aabbs.get(m_direction);

			return aabb == null ? new Rect3F() : new Rect3F(aabb);
		}

		private List<ISceneModelComponent> getComponents() {
			List<SpriteSceneModelComponent> components = m_components.get(m_direction);

			return components == null ? new ArrayList<ISceneModelComponent>() : new ArrayList<ISceneModelComponent>(components);
		}

		void addComponent(final Direction direction, SpriteSceneModelComponent component) {
			List<SpriteSceneModelComponent> components = m_components.get(direction);

			if (components == null) {
				components = new ArrayList<>();
				m_components.put(direction, components);
			}

			components.add(component);
			mergeAABB(direction, component.getBounds());

			component.getObservers().add(new IDefaultSceneModelComponentObserver() {
				@Override
				public void onFrameEvent(String name) {
					if (direction != m_direction)
						return;

					if (m_eventAudio.containsKey(name))
						m_eventAudio.get(name).play();

					m_observers.raise(IAnimationSceneModelAnimationObserver.class).event(name);
				}
			});
		}

		void addEventAudio(String event, IAudioClip clip) {
			if (m_eventAudio.containsKey(event))
				m_eventAudio.get(event).dispose();

			m_eventAudio.put(event, clip);
		}

		@Override
		public AnimationSceneModelAnimationState getState() {
			if (m_currentAnimation != this)
				return AnimationSceneModelAnimationState.Stop;
			else
				return getInternalState();
		}

		@Override
		public void setState(AnimationSceneModelAnimationState state) {
			AnimationSceneModelAnimationState currentState = getState();

			for (List<SpriteSceneModelComponent> l : m_components.values()) {
				for (SpriteSceneModelComponent c : l)
					c.setState(state);
			}

			refreshCurrentAnimation(this);

			if (getState() != currentState)
				m_observers.raise(IAnimationSceneModelAnimationObserver.class).stateChanged(getState());
		}

		protected AnimationSceneModelAnimationState getInternalState() {
			List<SpriteSceneModelComponent> components = m_components.get(m_direction);

			if (components == null || components.isEmpty())
				return AnimationSceneModelAnimationState.Stop;

			for (SpriteSceneModelComponent c : components) {
				if (c.getState() != AnimationSceneModelAnimationState.Stop)
					return AnimationSceneModelAnimationState.Play;
			}

			return AnimationSceneModelAnimationState.Stop;
		}

		private void update(int delta) {
			Set<SpriteSceneModelComponent> allComponents = new HashSet<>();

			for (List<SpriteSceneModelComponent> components : m_components.values())
				allComponents.addAll(components);

			for (SpriteSceneModelComponent c : allComponents)
				c.update(delta);
		}

		@Override
		public IObserverRegistry getObservers() {
			return m_observers;
		}
	}
}
