package io.github.jevaengine.ui.style;

import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultUIStyleFactory.class)
public interface IUIStyleFactory
{
	IUIStyle create(URI name) throws UIStyleConstructionException;
	
	public static final class UIStyleConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public UIStyleConstructionException(URI assetName, Exception cause) {
			super("Error constructing UI Style " + assetName.toString(), cause);
		}
	}
}
