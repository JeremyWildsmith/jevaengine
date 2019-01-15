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

import io.github.jevaengine.config.*;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.ui.DefaultWindowFactory.WindowLayoutDeclaration.ControlLayoutDeclaration;
import io.github.jevaengine.ui.IControlFactory.ControlConstructionException;
import io.github.jevaengine.ui.style.IUIStyleFactory;
import io.github.jevaengine.ui.style.IUIStyleFactory.UIStyleConstructionException;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

public final class DefaultWindowFactory implements IWindowFactory {
	private final IControlFactory m_controlFactory;
	private final IConfigurationFactory m_configurationFactory;
	private final IUIStyleFactory m_styleFactory;

	@Inject
	public DefaultWindowFactory(IControlFactory controlFactory, IConfigurationFactory configurationFactory, IUIStyleFactory styleFactory) {
		m_controlFactory = controlFactory;
		m_configurationFactory = configurationFactory;
		m_styleFactory = styleFactory;
	}

	@Override
	public Window create(URI name, WindowBehaviourInjector behaviourInject) throws WindowConstructionException {
		try {
			Window window = create(name);
			behaviourInject.inject(window);
			return window;
		} catch (NoSuchControlException e) {
			throw new WindowConstructionException(name, e);
		}
	}

	@Override
	public Window create(URI name) throws WindowConstructionException {
		try {
			WindowLayoutDeclaration decl = m_configurationFactory.create(name).getValue(WindowLayoutDeclaration.class);

			Window window = new Window(decl.bounds.width, decl.bounds.height);

			if(decl.style != null)
				window.setStyle(m_styleFactory.create(name.resolve(new URI(decl.style))));

			for (ControlLayoutDeclaration ctrlDecl : decl.controls) {

				Control control = m_controlFactory.create(ctrlDecl.type, ctrlDecl.name, name, ctrlDecl.config == null ? new NullVariable() : ctrlDecl.config);
				control.setLocation(ctrlDecl.location);
				window.addControl(control);

				if (ctrlDecl.style != null)
					control.setStyle(m_styleFactory.create(name.resolve(new URI(ctrlDecl.style))));
			}

			return window;
		} catch (ValueSerializationException | ConfigurationConstructionException | ControlConstructionException | UIStyleConstructionException | URISyntaxException e) {
			throw new WindowConstructionException(name, e);
		}
	}

	public static final class WindowLayoutDeclaration implements ISerializable {
		public String style;
		public Rect2D bounds;
		public ControlLayoutDeclaration[] controls;

		@Override
		public void serialize(IVariable target) throws ValueSerializationException {
			target.addChild("style").setValue(style);
			target.addChild("bounds").setValue(bounds);
			target.addChild("controls").setValue(controls);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException {
			try {
				if(source.childExists("style"))
					style = source.getChild("style").getValue(String.class);

				bounds = source.getChild("bounds").getValue(Rect2D.class);
				controls = source.getChild("controls").getValues(ControlLayoutDeclaration[].class);
			} catch (NoSuchChildVariableException e) {
				throw new ValueSerializationException(e);
			}
		}

		public static final class ControlLayoutDeclaration implements ISerializable {
			public String type;
			public String name;
			public String style;
			public IImmutableVariable config;

			public Vector2D location;

			@Override
			public void serialize(IVariable target) throws ValueSerializationException {
				target.addChild("type").setValue(type);
				target.addChild("location").setValue(location);

				if (name != null)
					target.addChild("name").setValue(name);

				if (style != null)
					target.addChild("style").setValue(style);

				if (config != null)
					target.addChild("config").setValue(config);
			}

			@Override
			public void deserialize(IImmutableVariable source) throws ValueSerializationException {
				try {
					type = source.getChild("type").getValue(String.class);
					location = source.getChild("location").getValue(Vector2D.class);

					if (source.childExists("name"))
						name = source.getChild("name").getValue(String.class);

					if (source.childExists("style"))
						style = source.getChild("style").getValue(String.class);

					if (source.childExists("config"))
						config = source.getChild("config");
				} catch (NoSuchChildVariableException e) {
					throw new ValueSerializationException(e);
				}
			}
		}
	}

}
