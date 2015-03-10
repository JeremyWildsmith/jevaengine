/*******************************************************************************
 * Copyright (c) 2013 Jeremy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * If you'd like to obtain a another license to this code, you may contact Jeremy to discuss alternative redistribution options.
 * 
 * Contributors:
 *     Jeremy - initial API and implementation
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.joystick;

public interface IInputSourceProcessor
{
	void mouseClicked(InputMouseEvent e);
	void mouseMoved(InputMouseEvent e);
	void mouseButtonStateChanged(InputMouseEvent e);
	void mouseWheelMoved(InputMouseEvent e);
	void mouseLeft(InputMouseEvent e);
	void mouseEntered(InputMouseEvent e);
	void keyTyped(InputKeyEvent e);
	void keyDown(InputKeyEvent e);
	void keyUp(InputKeyEvent e);
}
