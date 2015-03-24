package io.github.jevaengine.graphics;

import java.awt.Color;
import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultFontFactory.class)
public interface IFontFactory
{
	IFont create(URI name, Color color) throws FontConstructionException;
	IFont create(URI name) throws FontConstructionException;
	
	public static final class FontConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public FontConstructionException(URI assetName, Exception cause) {
			super("Error constructing font " + assetName.toString(), cause);
		}
	}
	
	public final class NullFontFactory implements IFontFactory
	{
		@Override
		public IFont create(URI name, Color color)
		{
			return new NullFont();
		}
		
		@Override
		public IFont create(URI name)
		{
			return new NullFont();
		}
	}
}
