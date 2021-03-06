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

import io.github.jevaengine.graphics.Animation.IAnimationEventListener;
import io.github.jevaengine.graphics.AnimationState;
import io.github.jevaengine.graphics.Sprite;
import io.github.jevaengine.graphics.Sprite.NoSuchSpriteAnimation;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel.AnimationSceneModelAnimationState;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel.ISceneModelComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public final class SpriteSceneModelComponent implements ISceneModelComponent {
	private final Logger m_logger = LoggerFactory.getLogger(SpriteSceneModelComponent.class);
	private final Sprite m_sprite;
	private final String m_animation;
	private final Rect3F m_bounds;
	private final Vector3F m_origin;
	private final String m_name;
	private final Observers m_observers = new Observers();
	private AnimationSceneModelAnimationState m_state = AnimationSceneModelAnimationState.Stop;

	SpriteSceneModelComponent(SpriteSceneModelComponent source) {
		m_sprite = new Sprite(source.m_sprite);
		m_animation = source.m_animation;
		m_bounds = new Rect3F(source.m_bounds);
		m_state = source.m_state;
		m_name = source.m_name;
		m_origin = new Vector3F(source.m_origin);
	}

	SpriteSceneModelComponent(String name, Sprite sprite, String animation, Rect3F bounds, Vector3F origin) {
		m_sprite = sprite;
		m_animation = animation;
		m_bounds = bounds;
		m_origin = new Vector3F(origin);
		m_name = name;
	}

	@Override
	public String getName() {
		return m_name;
	}

	void update(int delta) {
		m_sprite.update(delta);
	}

	AnimationSceneModelAnimationState getState() {
		return m_state;
	}

	void setState(AnimationSceneModelAnimationState state) {
		try {
			m_state = state;

			switch (state) {
				case Play:
					m_sprite.setAnimation(m_animation, AnimationState.Play, new IAnimationEventListener() {
						@Override
						public void onFrameEvent(String name) {
							m_observers.raise(IDefaultSceneModelComponentObserver.class).onFrameEvent(name);
						}

						@Override
						public void onStateEvent() {
						}
					});

					break;
				case PlayToEnd:
					m_sprite.setAnimation(m_animation, AnimationState.PlayToEnd, new IAnimationEventListener() {
						@Override
						public void onFrameEvent(String name) {
							m_observers.raise(IDefaultSceneModelComponentObserver.class).onFrameEvent(name);
						}

						@Override
						public void onStateEvent() {
							m_state = AnimationSceneModelAnimationState.Stop;
						}
					});

					break;
				case PlayWrap:
					m_sprite.setAnimation(m_animation, AnimationState.PlayWrap, new IAnimationEventListener() {
						@Override
						public void onFrameEvent(String name) {
							m_observers.raise(IDefaultSceneModelComponentObserver.class).onFrameEvent(name);
						}

						@Override
						public void onStateEvent() {
						}
					});
					break;
				case Stop:
					m_sprite.setAnimation(m_animation, AnimationState.Stop);
					break;
				default:
					assert false : "Unexpected SceneModelAnimationState";
					break;
			}
		} catch (NoSuchSpriteAnimation e) {
			m_logger.error("Unable to set model component animation as it does not exist for given direction.", e);
		}
	}

	IObserverRegistry getObservers() {
		return m_observers;
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale) {
		m_sprite.render(g, x, y, scale);
	}

	@Override
	public boolean testPick(int x, int y, float scale) {
		return m_sprite.testPick(x, y, scale);
	}

	@Override
	public Rect3F getBounds() {
		return new Rect3F(m_bounds);
	}

	@Override
	public Vector3F getOrigin() {
		return m_origin;
	}

	interface IDefaultSceneModelComponentObserver {
		void onFrameEvent(String name);
	}
}
