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
package io.github.jevaengine.world.scene.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class ExtentionMuxedAnimationSceneModelFactory implements IAnimationSceneModelFactory {
	private final Map<String, IAnimationSceneModelFactory> m_factories = new HashMap<>();
	private final IAnimationSceneModelFactory m_defaultFactory;

	public ExtentionMuxedAnimationSceneModelFactory(IAnimationSceneModelFactory defaultFactory) {
		m_defaultFactory = defaultFactory;
	}

	public void put(String extention, IAnimationSceneModelFactory factory) {
		String ext = extention.startsWith(".") ? extention : "." + extention;
		m_factories.put(ext, factory);
	}

	@Override
	public IAnimationSceneModel create(URI name) throws SceneModelConstructionException {
		for (Map.Entry<String, IAnimationSceneModelFactory> e : m_factories.entrySet()) {
			if (name.getPath().endsWith(e.getKey()))
				return e.getValue().create(name);
		}

		return m_defaultFactory.create(name);
	}
}
