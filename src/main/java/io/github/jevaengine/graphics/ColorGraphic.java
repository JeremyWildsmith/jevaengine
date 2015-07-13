package io.github.jevaengine.graphics;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.RGBImageFilter;

public final class ColorGraphic implements IImmutableGraphic
{
	private final Color m_color;
	private final int m_width;
	private final int m_height;
	
	public ColorGraphic(Color color, int width, int height)
	{
		m_color = color;
		m_width = width;
		m_height = height;
	}

	@Override
	public void render(Graphics2D g, int dx, int dy, int dw, int dh, int sx, int sy, int sw, int sh)
	{
		g.setColor(m_color);
		g.fillRect(dx, dy, dw, dh);
	}

	@Override
	public void render(Graphics2D g, int dx, int dy, float scale)
	{
		g.setColor(m_color);
		g.fillRect(dx, dy, (int)(m_width * scale), (int)(m_height * scale));
	}

	@Override
	public IImmutableGraphic filterImage(RGBImageFilter filter)
	{
		Color newColor = new Color(filter.filterRGB(0, 0, m_color.getRGB()));
		
		return new ColorGraphic(newColor, m_width, m_height);
	}

	@Override
	public boolean pickTest(int x, int y)
	{
		return getBounds().contains(new Vector2D(x, y));
	}

	@Override
	public Rect2D getBounds()
	{
		return new Rect2D(m_width, m_height);
	}

	@Override
	public IImmutableGraphic duplicate()
	{
		return new ColorGraphic(m_color, m_width, m_height);
	}
}
