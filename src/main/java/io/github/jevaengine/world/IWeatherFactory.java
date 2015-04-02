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
package io.github.jevaengine.world;

import com.google.inject.ImplementedBy;
import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.world.scene.ISceneBuffer;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEffect;
import java.awt.Graphics2D;
import java.net.URI;
import java.util.Collection;


@ImplementedBy(DefaultWeatherFactory.class)
public interface IWeatherFactory
{
	IWeather create(URI name) throws WeatherConstructionException;
	
	interface IWeather extends ISceneBufferEffect
	{
		void update(int deltaTime);
		void dispose();
	}

	public static final class NullWeather implements IWeather
	{

		@Override
		public void update(int deltaTime) { }

		@Override
		public void dispose() { }

		@Override
		public IRenderable getUnderlay(Rect2D bounds)
		{
			return new NullGraphic();
		}

		@Override
		public IRenderable getOverlay(Rect2D bounds)
		{
			return new NullGraphic();
		}

		@Override
		public void preRenderComponent(Graphics2D g, int offsetX, int offsetY, float scale, ISceneBuffer.ISceneBufferEntry subject, Collection<ISceneBuffer.ISceneBufferEntry> beneath) { }

		@Override
		public void postRenderComponent() { }
	}
	
	public static final class NullWeatherFactory implements IWeatherFactory
	{
		@Override
		public IWeather create(URI name) throws WeatherConstructionException
		{
			return new NullWeather();
		}
	}
	
	public final class WeatherConstructionException extends Exception
	{
		public WeatherConstructionException(Exception e)
		{
			super(e);
		}
	}
}