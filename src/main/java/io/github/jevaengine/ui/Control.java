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

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.ui.style.ComponentStyle;
import io.github.jevaengine.ui.style.IUIStyle;
import io.github.jevaengine.ui.style.NullUIStyle;
import io.github.jevaengine.util.Nullable;

public abstract class Control implements IImmutableControl, IDisposable {
	private final String m_componentName;

	private final String m_instanceName;

	private Control m_parent;
	private IUIStyle m_style = new NullUIStyle();

	private Vector2D m_location = new Vector2D();

	private boolean m_isVisible = true;
	private boolean m_hasFocus = false;

	public Control(String componentName) {
		this(componentName, null);
	}

	public Control(String componentName, @Nullable String instanceName) {
		m_componentName = componentName;
		m_instanceName = instanceName == null ? "__CONTROL_UNAMED" : instanceName;
	}

	@Override
	public void dispose() {
		m_style.dispose();
	}

	public final boolean hasFocus() {
		return m_hasFocus;
	}

	final void setFocus() {
		m_hasFocus = true;
		onFocusChanged();
	}

	final void clearFocus() {
		m_hasFocus = false;
		onFocusChanged();
	}

	@Override
	public final String getInstanceName() {
		return m_instanceName;
	}

	public final Vector2D getLocation() {
		return new Vector2D(m_location);
	}

	public final void setLocation(Vector2D location) {
		m_location = new Vector2D(location);
	}

	public final Vector2D getAbsoluteLocation() {
		if (m_parent != null)
			return m_location.add(m_parent.getAbsoluteLocation());
		else
			return getLocation();
	}

	public final ComponentStyle getComponentStyle() {
		return m_style.getComponentStyle(m_componentName);
	}

	public final IUIStyle getStyle() {
		return m_style;
	}

	public final void setStyle(IUIStyle style) {
		m_style.dispose();

		m_style = style;
		onStyleChanged();
	}

	public final Control getParent() {
		return m_parent;
	}

	public final void setParent(Control parent) {
		m_parent = parent;

		if (m_parent != null)
			setStyle(m_parent.m_style);
	}

	public final boolean isVisible() {
		if (m_parent != null)
			return m_parent.isVisible() && m_isVisible;

		return m_isVisible;
	}

	public final void setVisible(boolean isVisible) {
		if (m_parent != null && isVisible)
			m_parent.setVisible(isVisible);

		m_isVisible = isVisible;
	}

	protected void onStyleChanged() {
	}

	protected void onEnter() {
	}

	protected void onLeave() {
	}

	protected void onFocusChanged() {
	}
}
