/*******************************************************************************
 * Copyright (c) 2013 Jeremy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * If you'd like to obtain a another license to this code, you may contact Jeremy to discuss alternative redistribution options.
 * 
 * Contributors:
 *     Jeremy - initial API and implementation
 ******************************************************************************/
package io.github.jevaengine.world.search;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Rect2F;

import io.github.jevaengine.math.Vector2F;

public class RectangleSearchFilter<T> implements ISearchFilter<T>
{

	private Rect2F m_srcRect;

	public RectangleSearchFilter(Rect2F rect)
	{
		m_srcRect = rect;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#getSearchBounds()
	 */
	@Override
	public final Rect2D getSearchBounds()
	{
		return m_srcRect.round();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#shouldInclude(jeva.math.Vector2F)
	 */
	@Override
	public boolean shouldInclude(Vector2F location)
	{
		return m_srcRect.contains(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#filter(java.lang.Object)
	 */
	@Override
	public T filter(T o)
	{
		return o;
	}
}
