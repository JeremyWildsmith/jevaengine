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

import io.github.jevaengine.joystick.InputMouseEvent.MouseButton;
import io.github.jevaengine.math.Vector2D;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.SwingUtilities;

public class FrameInputSource implements IInputSource, MouseMotionListener, MouseListener, KeyListener, MouseWheelListener
{
	private final ConcurrentLinkedQueue<IInputEvent> m_events = new ConcurrentLinkedQueue<IInputEvent>();

	private boolean m_isDragging = false;

	private int m_lastKeyCode = 0;
	
	public static FrameInputSource create(final Component target)
	{
		final FrameInputSource manager = new FrameInputSource();
		
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					target.addKeyListener(manager);
					target.addMouseMotionListener(manager);
					target.addMouseListener(manager);
					target.addMouseWheelListener(manager);
				}
			});
		} catch (InvocationTargetException | InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		return manager;
	}

	@Override
	public void process(IInputSourceProcessor processor)
	{
		while (!m_events.isEmpty())
		{
			m_events.remove().relay(processor);
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		m_isDragging = false;
		m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseMoved, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), false, false));
	}

	public void mouseClicked(MouseEvent e)
	{
		m_isDragging = false;
		m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseClicked, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), false, false));
	}

	public void keyTyped(KeyEvent e)
	{
		m_events.add(new InputKeyEvent(InputKeyEvent.KeyEventType.KeyTyped, m_lastKeyCode, e.getKeyChar()));
	}

	public void mouseDragged(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (!m_isDragging)
				m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseClicked, new Vector2D(e.getX(), e.getY()), MouseButton.Left, false, false));

			m_isDragging = true;
			m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseMoved, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), false, true));
		} else
		{
			if (!m_isDragging)
				m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseClicked, new Vector2D(e.getX(), e.getY()), MouseButton.Right, false, false));

			m_isDragging = true;
			m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseMoved, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), false, false));
		}
	}

	public void mousePressed(MouseEvent e)
	{
		m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MousePressed, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), true, false));
	}

	public void mouseReleased(MouseEvent e)
	{
		m_isDragging = false;
		m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseReleased, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), false, false));
	}

	public void mouseEntered(MouseEvent e)
	{
		m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseEntered, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), false, false));
	}

	public void mouseExited(MouseEvent e)
	{
		m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseLeft, new Vector2D(e.getX(), e.getY()), MouseButton.fromButton(e.getButton()), false, false));
	}

	public void keyPressed(KeyEvent e)
	{
		m_lastKeyCode = e.getKeyCode();
		m_events.add(new InputKeyEvent(InputKeyEvent.KeyEventType.KeyDown, e.getKeyCode(), e.getKeyChar()));
	}

	public void keyReleased(KeyEvent e)
	{
		m_events.add(new InputKeyEvent(InputKeyEvent.KeyEventType.KeyUp, e.getKeyCode(), e.getKeyChar()));
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		m_events.add(new InputMouseEvent(InputMouseEvent.MouseEventType.MouseWheelMoved, new Vector2D(e.getX(), e.getY()), MouseButton.Left, false, false, e.getUnitsToScroll()));
	}
}
