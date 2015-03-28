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
package io.github.jevaengine.joystick;

import io.github.jevaengine.math.Vector2D;

import java.awt.event.MouseEvent;

public class InputMouseEvent implements IInputEvent
{

	public enum MouseEventType
	{
		MousePressed,
		MouseReleased,
		MouseMoved,
		MouseClicked,
		MouseWheelMoved,
		MouseLeft,
		MouseEntered,
	}

	public enum MouseButton
	{
		Unknown,
		Left,
		Middle,
		Right;

		public static InputMouseEvent.MouseButton fromButton(int button)
		{
			switch (button)
			{
				case MouseEvent.BUTTON1:
					return Left;
				case MouseEvent.BUTTON2:
					return Middle;
				case MouseEvent.BUTTON3:
					return Right;
				default:
					return Unknown;
			}
		}
	}

	private static Vector2D lastLocation = new Vector2D();

	public InputMouseEvent.MouseEventType type;

	public Vector2D location;

	public Vector2D delta;

	public InputMouseEvent.MouseButton mouseButton;

	public boolean mouseButtonState;

	public int deltaMouseWheel;

	public boolean isDragging;

	public InputMouseEvent(InputMouseEvent event)
	{
		type = event.type;
		location = new Vector2D(event.location);
		mouseButton = event.mouseButton;
		mouseButtonState = event.mouseButtonState;
		deltaMouseWheel = 0;
		isDragging = event.isDragging;

		delta = new Vector2D(event.location.difference(lastLocation));
	}

	protected InputMouseEvent(InputMouseEvent.MouseEventType _type, Vector2D _location, InputMouseEvent.MouseButton _mouseButton, boolean _mouseButtonState, boolean _isDragging)
	{
		type = _type;
		location = _location;
		mouseButton = _mouseButton;
		mouseButtonState = _mouseButtonState;
		deltaMouseWheel = 0;
		isDragging = _isDragging;

		delta = _location.difference(lastLocation);

		lastLocation = _location;
	}

	protected InputMouseEvent(InputMouseEvent.MouseEventType _type, Vector2D _location, InputMouseEvent.MouseButton _mouseButton, boolean _mouseButtonState, boolean _isDragging, int _iDeltaMouseWheel)
	{
		type = _type;
		location = _location;
		mouseButton = _mouseButton;
		mouseButtonState = _mouseButtonState;
		deltaMouseWheel = _iDeltaMouseWheel;
		lastLocation = _location;
		isDragging = _isDragging;

		delta = _location.difference(lastLocation);

		lastLocation = _location;
	}

	public void relay(IInputSourceProcessor handler)
	{
		switch (type)
		{
			case MousePressed:
			case MouseReleased:
				handler.mouseButtonStateChanged(this);
				break;
			case MouseMoved:
				handler.mouseMoved(this);
				break;
			case MouseClicked:
				handler.mouseClicked(this);
				break;
			case MouseWheelMoved:
				handler.mouseWheelMoved(this);
			case MouseLeft:
				handler.mouseLeft(this);
				break;
			case MouseEntered:
				handler.mouseEntered(this);
				break;
		}
	}
}