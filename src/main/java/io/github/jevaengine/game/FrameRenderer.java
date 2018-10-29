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
package io.github.jevaengine.game;

import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.math.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

public final class FrameRenderer implements IRenderer {
	private static final int PREFERRED_BIT_DEPTH = 16;
	private static final int MIN_BIT_DEPTH = 16;
	private final Logger m_logger = LoggerFactory.getLogger(FrameRenderer.class);
	private final int m_canvasRenderWidth;
	private final int m_canvasRenderHeight;

	private final JFrame m_renderTarget;

	private final RenderFitMode m_renderFitMode;

	private BufferStrategy m_bufferStrategy;

	public FrameRenderer(final JFrame target, boolean isFullscreen, RenderFitMode renderFitMode) {
		m_renderTarget = target;
		m_renderFitMode = renderFitMode;

		m_renderTarget.setSize(target.getSize());

		m_canvasRenderWidth = target.getWidth();
		m_canvasRenderHeight = target.getHeight();

		target.setIgnoreRepaint(true);
		target.createBufferStrategy(2);
		m_bufferStrategy = target.getBufferStrategy();

		BufferCapabilities cap = m_bufferStrategy.getCapabilities();

		String openGlEnabled = System.getProperty("sun.java2d.opengl");
		if(openGlEnabled == null || openGlEnabled.toLowerCase().compareTo("true") != 0)
			m_logger.error("OpenGL Hardware Acceleration is not enabled. This will result in a significant performance loss. Please enable the property 'sun.java2d.opengl'");

		if (!cap.getBackBufferCapabilities().isAccelerated())
			m_logger.warn("Backbuffer is not accelerated, may result in poorer render performance.");

		if (!cap.isPageFlipping())
			m_logger.warn("Page flipping is not supported by this device. This may result in increased image flicker or tearing.");

		if (!cap.isMultiBufferAvailable())
			m_logger.warn("Multiple buffering for page flipping is not supported by this device. This may result in poorer performance.");

		if (cap.isFullScreenRequired() && !isFullscreen)
			m_logger.warn("Full screen required for hardware acceleration - not running in fullscreen may cause performance issues.");

		if (isFullscreen) {
			enterFullscreen();

			target.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						GraphicsDevice device = target.getGraphicsConfiguration().getDevice();

						Window fullscreen = device.getFullScreenWindow();

						if (fullscreen != target)
							fullscreen = null;

						if (fullscreen == null)
							enterFullscreen();
						else
							exitFullscreen();
					}
				}
			});
		}
	}

	private void setTargetUnDecorated(boolean isUndecorated) {
		m_renderTarget.setVisible(false);
		m_renderTarget.dispose();
		m_renderTarget.setUndecorated(isUndecorated);
		m_renderTarget.setVisible(true);


		m_renderTarget.setIgnoreRepaint(true);
		m_renderTarget.createBufferStrategy(2);
		m_bufferStrategy = m_renderTarget.getBufferStrategy();
	}

	private void enterFullscreen() {
		setTargetUnDecorated(true);
		GraphicsDevice device = m_renderTarget.getGraphicsConfiguration().getDevice();

		if (!device.isFullScreenSupported())
			m_logger.error("Cannot enter full-screen. Device does not support full-screen mode");
		else {
			device.setFullScreenWindow(m_renderTarget);

			DisplayMode best = device.getDisplayMode();

			if (!device.isDisplayChangeSupported())
				m_logger.error("Device does not support change of display modes. Using default display mode.");
			else {
				for (DisplayMode d : device.getDisplayModes()) {
					int dDeltaWidth = d.getWidth() - m_canvasRenderWidth;
					int dDeltaHeight = d.getHeight() - m_canvasRenderHeight;
					int dDeltaBitDepth = d.getBitDepth() - PREFERRED_BIT_DEPTH;

					int bestDeltaWidth = best.getWidth() - m_canvasRenderWidth;
					int bestDeltaHeight = best.getHeight() - m_canvasRenderHeight;
					int bestDeltaBitDepth = best.getBitDepth() - PREFERRED_BIT_DEPTH;

					if (dDeltaWidth == bestDeltaWidth && dDeltaHeight == bestDeltaHeight) {
						if (d.getBitDepth() > MIN_BIT_DEPTH && (Math.abs(dDeltaBitDepth) < Math.abs(bestDeltaBitDepth)))
							best = d;
					} else if (dDeltaWidth == 0 || (dDeltaWidth > 0 && dDeltaWidth < bestDeltaWidth) &&
							dDeltaHeight == 0 || (dDeltaHeight > 0 && dDeltaHeight < bestDeltaWidth)) {
						best = d;
					}
				}
				device.setDisplayMode(best);
			}
			DisplayMode mode = device.getDisplayMode();

			m_renderTarget.setBounds(new Rectangle(m_renderTarget.getLocation(), new Dimension(mode.getWidth(), mode.getHeight())));
		}
	}

	private void exitFullscreen() {
		GraphicsDevice device = m_renderTarget.getGraphicsConfiguration().getDevice();

		Window fullscrenWindow = device.getFullScreenWindow();

		if (fullscrenWindow == m_renderTarget) {
			device.setFullScreenWindow(null);
			m_renderTarget.setBounds(new Rectangle(m_renderTarget.getLocation(), new Dimension(m_canvasRenderWidth, m_canvasRenderHeight)));
		}

		setTargetUnDecorated(false);
	}

	@Override
	public Vector2D getResolution() {
		return new Vector2D(m_canvasRenderWidth, m_canvasRenderHeight);
	}

	@Override
	public GraphicsConfiguration getGraphicsConfiguration() {
		return m_renderTarget.getGraphicsConfiguration();
	}

	@Override
	public void render(IRenderable frame) {
		if (m_renderTarget == null)
			return;

		Graphics2D g = (Graphics2D) m_bufferStrategy.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		Rectangle bounds = m_renderTarget.getBounds();
		g.setColor(Color.black);
		g.fillRect(0, 0, bounds.width, bounds.height);

		switch (m_renderFitMode) {
			case Frame:
				frame.render(g, (bounds.width - m_canvasRenderWidth) / 2, (bounds.height - m_canvasRenderHeight) / 2, 1.0F);
				break;
			case Stretch:
				Graphics2D g2 = (Graphics2D) g.create();

				AffineTransform transform = g2.getTransform();
				transform.scale((float) bounds.width / m_canvasRenderWidth, (float) bounds.height / m_canvasRenderHeight);
				g2.setTransform(transform);

				frame.render(g2, 0, 0, 1.0F);

				g2.dispose();
				break;
			default:
				throw new UnrecognizedRenderFitModeException();
		}
		g.dispose();

		m_bufferStrategy.show();
	}

	public enum RenderFitMode {
		Stretch,
		Frame,
	}

	public class InvalidRenderTargetException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private InvalidRenderTargetException() {
		}
	}

	public class UnrecognizedRenderFitModeException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private UnrecognizedRenderFitModeException() {
		}
	}
}
