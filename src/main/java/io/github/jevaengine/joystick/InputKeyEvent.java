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

public class InputKeyEvent implements IInputEvent
{

	public enum KeyEventType
	{
		KeyTyped,
		KeyDown,
		KeyUp,
	}

	public InputKeyEvent.KeyEventType type;
	public int keyCode;
	public char keyChar;

	protected InputKeyEvent(InputKeyEvent.KeyEventType _type, int _keyCode, char _keyChar)
	{
		type = _type;
		keyCode = _keyCode;
		keyChar = _keyChar;
	}

	public void relay(IInputSourceProcessor handler)
	{
		switch (type)
		{
			case KeyTyped:
				handler.keyTyped(this);
				break;
			case KeyUp:
				handler.keyUp(this);
				break;
			case KeyDown:
				handler.keyDown(this);
				break;
		}
	}
}