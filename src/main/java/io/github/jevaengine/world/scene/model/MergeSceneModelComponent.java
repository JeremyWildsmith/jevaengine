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
package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel.ISceneModelComponent;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public final class MergeSceneModelComponent implements ISceneModelComponent
{
	private final ISceneModelComponent m_components[];
	private final String m_name;
	
	public MergeSceneModelComponent(String name, ISceneModelComponent ...components)
	{
		m_name = name;
		m_components = components;

		Arrays.sort(m_components, new Comparator<ISceneModelComponent>() {
			@Override
			public int compare(ISceneModelComponent o1, ISceneModelComponent o2) {
				String components1[] = o1.getName().split("\\.");
				String components2[] = o2.getName().split("\\.");

				Integer o1int = getInteger(components1[components1.length - 1]);
				Integer o2int = getInteger(components1[components2.length - 1]);
				
				if(o1int == null && o2int == null)
					return 0;
				else if(o1int == null || o2int == null)
					return o1int == null ? -1 : 1;
				else
					return o1int.compareTo(o2int);
			}
		});

	}
	
	private static Integer getInteger(String s)
	{
		try
		{
			return Integer.valueOf(s);
		} catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	public MergeSceneModelComponent(String name, Collection<ISceneModelComponent> components)
	{
		this(name, components.toArray(new ISceneModelComponent[components.size()]));
	}
	
	@Override
	public String getName()
	{
		return m_name;
	}
	
	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		for(ISceneModelComponent c : m_components)
			c.render(g, x, y, scale);
	}

	@Override
	public boolean testPick(int x, int y, float scale)
	{
		for(ISceneModelComponent c : m_components)
			if(c.testPick(x, y, scale))
				return true;
		
		return false;
	}

	@Override
	public Rect3F getBounds()
	{
		Rect3F aabbs[] = new Rect3F[m_components.length];
		
		for(int i = 0; i < aabbs.length; i++)
			aabbs[i] = m_components[i].getBounds();
		
		return Rect3F.getAABB(aabbs);
	}
	
	@Override
	public Vector3F getOrigin()
	{
		return m_components.length == 0 ? new Vector3F() : m_components[0].getOrigin();
	}
}
