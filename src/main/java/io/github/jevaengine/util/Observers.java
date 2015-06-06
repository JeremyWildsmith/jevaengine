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
package io.github.jevaengine.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Observers implements IObserverRegistry
{
	private final Collection<Object> m_observers = new MutableProcessList<>();

	private final Map<Class<?>, Object> m_broadcasters = new HashMap<>();

	private static void getInterfaces(Set<Class<?>> list, Class<?> clazz)
	{
		for(Class<?> c : clazz.getInterfaces())
		{
			list.add(c);
			getInterfaces(list, c);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T createBroadcaster(Class<T> clazz)
	{
		final Set<Class<?>> baseInterfaces = new HashSet<>();
		
		baseInterfaces.add(clazz);
		getInterfaces(baseInterfaces, clazz);
	
		return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				method.setAccessible(true);
				traverseObservers:
				for(Object o : m_observers)
				{
					for(Class<?> c : baseInterfaces)
					{
						if(!method.getDeclaringClass().equals(c))
							continue;
						
						if(c.isAssignableFrom(o.getClass()))
						{
							method.invoke(o, args);
							continue traverseObservers;
						}
					}
				}
				
				return null;
			}
		});
	}
	
	@Override
	public void add(Object o)
	{
		m_observers.add(o);
	}

	@Override
	public void remove(Object o)
	{
		m_observers.remove(o);
	}

	@SuppressWarnings("unchecked")
	public <T> T raise(Class<T> clazz)
	{
		if(!m_broadcasters.containsKey(clazz))
			m_broadcasters.put(clazz, createBroadcaster(clazz));
		
		return (T)m_broadcasters.get(clazz);
	}
	
	public void clear()
	{
		m_observers.clear();
	}
}
