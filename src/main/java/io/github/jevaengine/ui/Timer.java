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
package io.github.jevaengine.ui;

import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;

import java.awt.*;

public class Timer extends Control {
	private static final String COMPONENT_NAME = "timer";

	private final Observers m_observers = new Observers();

	public Timer(String instanceName) {
		super(COMPONENT_NAME, instanceName);
	}

	public Timer() {
		this(null);
	}

	public IObserverRegistry getObservers() {
		return m_observers;
	}

	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent) {
		return false;
	}

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent) {
		return false;
	}

	@Override
	public Rect2D getBounds() {
		return new Rect2D();
	}

	@Override
	public void update(int deltaTime) {
		m_observers.raise(ITimerObserver.class).update(deltaTime);
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale) {
	}

	public interface ITimerObserver {
		void update(int deltaTime);
	}
}
