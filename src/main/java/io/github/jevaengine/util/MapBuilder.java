package io.github.jevaengine.util;

import java.util.HashMap;
import java.util.Map;

public final class MapBuilder<X, Y>
{
	private Map<X, Y> m_map;
	
	public MapBuilder()
	{
		m_map = new HashMap<X, Y>();
	}
	
	public MapBuilder(Map<X, Y> map)
	{
		m_map = map;
	}
	
	public MapBuilder<X, Y> a(X x, Y y)
	{
		m_map = new HashMap<>(m_map);
		m_map.put(x, y);
		
		return new MapBuilder<>(m_map);
	}
	
	public Map<X, Y> get()
	{
		return m_map;
	}
}
