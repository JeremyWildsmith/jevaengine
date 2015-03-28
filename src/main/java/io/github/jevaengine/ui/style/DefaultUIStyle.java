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
package io.github.jevaengine.ui.style;

import java.util.Map;
import java.util.NoSuchElementException;

public final class DefaultUIStyle implements IUIStyle
{
	private String m_defaultStyle;
	private Map<String, ComponentStyle> m_componentStyles;

	public DefaultUIStyle(String defaultStyle, Map<String, ComponentStyle> componentStyles)
	{
		m_defaultStyle = defaultStyle;
		m_componentStyles = componentStyles;
	}
	
	@Override
	public void dispose()
	{
		for(ComponentStyle style : m_componentStyles.values())
			style.dispose();
	}

	public ComponentStyle getComponentStyle(String componentName)
	{
		ComponentStyle style = m_componentStyles.get(componentName);
		
		if(style == null)
			style = m_componentStyles.get(m_defaultStyle);
		
		if(style == null)
			throw new NoSuchElementException();
		
		return style;
	}
}
