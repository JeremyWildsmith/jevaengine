package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.PhysicsBodyShape;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TranslatedAnimationSceneModel implements IAnimationSceneModel {
	private final IAnimationSceneModel m_model;
	private final Vector3F m_translation;

	public TranslatedAnimationSceneModel(IAnimationSceneModel model, Vector3F translation) {
		m_model = model;
		m_translation = new Vector3F(translation);
	}

	@Override
	public IAnimationSceneModel clone() {
		return new TranslatedAnimationSceneModel(m_model, m_translation);
	}

	@Override
	public IAnimationSceneModelAnimation getAnimation(String name) {
		return m_model.getAnimation(name);
	}

	@Override
	public boolean hasAnimation(String name) {
		return m_model.hasAnimation(name);
	}

	@Override
	public void update(int deltaTime) {
		m_model.update(deltaTime);
	}

	@Override
	public IObserverRegistry getObservers() {
		return m_model.getObservers();
	}

	@Override
	public Collection<ISceneModelComponent> getComponents(Matrix3X3 projection) {
		List<ISceneModelComponent> components = new ArrayList<>();
		for (final ISceneModelComponent c : m_model.getComponents(projection)) {
			components.add(new ISceneModelComponent() {

				@Override
				public String getName() {
					return c.getName();
				}

				@Override
				public boolean testPick(int x, int y, float scale) {
					return c.testPick(x, y, scale);
				}

				@Override
				public Rect3F getBounds() {
					return c.getBounds();
				}

				@Override
				public Vector3F getOrigin() {
					return c.getOrigin().add(m_translation);
				}

				@Override
				public void render(Graphics2D g, int x, int y, float scale) {
					c.render(g, x, y, scale);
				}
			});
		}

		return components;
	}

	@Override
	public Rect3F getAABB() {
		return m_model.getAABB().add(m_translation);
	}

	@Override
	public Direction getDirection() {
		return m_model.getDirection();
	}

	@Override
	public void setDirection(Direction direction) {
		m_model.setDirection(direction);
	}

	@Override
	public PhysicsBodyShape getBodyShape() {
		return m_model.getBodyShape();
	}

	@Override
	public void dispose() {
		m_model.dispose();
	}

	@Override
	public String[] getAnimations() {
		return m_model.getAnimations();
	}
}
