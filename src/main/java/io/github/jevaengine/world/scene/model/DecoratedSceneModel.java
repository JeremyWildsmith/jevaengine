package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Matrix3X3;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.world.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DecoratedSceneModel implements ISceneModel
{
	private final ISceneModel m_model;

	private final ArrayList<ISceneModelComponent> m_additionalComponents = new ArrayList<>();
	
	public DecoratedSceneModel(ISceneModel model, ISceneModelComponent ... additionalComponents)
	{
		this(model, Arrays.asList(additionalComponents));
	}
	
	public DecoratedSceneModel(ISceneModel model, List<ISceneModelComponent> additionalComponents)
	{
		m_model = model;
		m_additionalComponents.addAll(additionalComponents);
	}
	
	public DecoratedSceneModel(ISceneModel model)
	{
		m_model = model;
	}

	@Override
	public void dispose()
	{
		m_model.dispose();
	}
	
	@Override
	public DecoratedSceneModel clone()
	{
		return new DecoratedSceneModel(m_model, m_additionalComponents);
	}
	
	@Override
	public List<ISceneModelComponent> getComponents(Matrix3X3 projection)
	{
		ArrayList<ISceneModelComponent> components = new ArrayList<>();
		
		components.addAll(m_model.getComponents(projection));
		components.addAll(m_additionalComponents);
		
		return components;
	}

	@Override
	public Rect3F getAABB()
	{
		return m_model.getAABB();
	}

	@Override
	public void update(int deltaTime)
	{
		m_model.update(deltaTime);
	}

	@Override
	public Direction getDirection()
	{
		return m_model.getDirection();
	}

	@Override
	public void setDirection(Direction direction)
	{
		m_model.setDirection(direction);
	}
	
	public void add(ISceneModelComponent component)
	{
		m_additionalComponents.add(component);
	}
	
	public void remove(ISceneModelComponent component)
	{
		m_additionalComponents.remove(component);
	}
}
