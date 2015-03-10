package io.github.jevaengine.ui;

import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultWindowFactory.class)
public interface IWindowFactory
{
	Window create(URI name, WindowBehaviourInjector behaviourInject) throws WindowConstructionException;
	Window create(URI name) throws WindowConstructionException;
	
	public static final class WindowConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public WindowConstructionException(URI assetName, Exception cause) {
			super("Error constructing window " + assetName.toString(), cause);
		}
	}
}
