package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.world.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MergeSceneModel implements ISceneModel
{
	private final List<ISceneModel> m_models = new ArrayList<>();;
	
	public MergeSceneModel(ISceneModel ... models)
	{
		m_models.addAll(Arrays.asList(models));
	}

	public MergeSceneModel() { }
	
	@Override
	public void dispose()
	{
		for(ISceneModel m : m_models)
			m.dispose();
		
		m_models.clear();
	}
	
	public void add(ISceneModel model)
	{
		model.setDirection(getDirection());
		m_models.add(model);
	}
	
	public void remove(ISceneModel model)
	{
		m_models.remove(model);
	}
	
	@Override
	public ISceneModel clone()
	{
		ISceneModel coppies[] = new ISceneModel[m_models.size()];
		
		for(int i = 0; i < m_models.size(); i++)
			coppies[i] = m_models.get(i).clone();
		
		return new MergeSceneModel(coppies);
	}

	@Override
	public List<ISceneModelComponent> getComponents()
	{
		ArrayList<ISceneModelComponent> componentBuffer = new ArrayList<>();
		
		for(ISceneModel m : m_models)
			componentBuffer.addAll(m.getComponents());
		
		return componentBuffer;
	}

	@Override
	public Rect3F getAABB()
	{
		if(m_models.size() == 0)
			return new Rect3F();
		
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minZ = Float.MAX_VALUE;
		
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		float maxZ = Float.MIN_VALUE;
		
		for(ISceneModel m : m_models)
		{
			Rect3F aabb = m.getAABB();
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
	public void update(int deltaTime)
	{
		for(ISceneModel m : m_models)
			m.update(deltaTime);
	}

	@Override
	public Direction getDirection()
	{
		Direction d = null;
		
		for(ISceneModel m : m_models)
		{
			if(d == null)
				d = m.getDirection();
			else if(m.getDirection() != d)
				return Direction.Zero;
		}
		
		return d;
	}

	@Override
	public void setDirection(Direction direction)
	{
		for(ISceneModel m : m_models)
			m.setDirection(direction);
	}

}
