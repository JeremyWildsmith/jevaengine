package io.github.jevaengine.ui;

import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.util.IObserverRegistry;

public abstract class WindowBehaviourInjector
{
	private Window m_host;
	
	public final void inject(Window host) throws NoSuchControlException
	{
		m_host = host;
		doInject();
	}
	
	protected final IObserverRegistry getObservers()
	{
		return m_host.getObservers();
	}
	
	protected final void addControl(Control control, Vector2D location)
	{
		m_host.addControl(control, location);
	}
	
	protected final void addControl(Control control)
	{
		m_host.addControl(control);
	}
	
	protected final void removeControl(Control control)
	{
		m_host.removeControl(control);
	}
	
	protected final <T extends Control> T getControl(Class<T> controlClass, String name) throws NoSuchControlException
	{
		return m_host.getControl(controlClass, name);
	}
	
	protected abstract void doInject() throws NoSuchControlException;
}
