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
package io.github.jevaengine.config.json;

import io.github.jevaengine.IAssetStreamFactory;
import io.github.jevaengine.IAssetStreamFactory.AssetStreamConstructionException;
import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.util.ThreadSafe;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;

public final class JsonConfigurationFactory implements IConfigurationFactory {
	private final IAssetStreamFactory m_assetFactory;

	@Inject
	public JsonConfigurationFactory(IAssetStreamFactory assetFactory) {
		m_assetFactory = assetFactory;
	}

	@Override
	@ThreadSafe
	public IImmutableVariable create(URI name) throws ConfigurationConstructionException {
		return createMutable(name);
	}

	@Override
	@ThreadSafe
	public IVariable createMutable(URI name) throws ConfigurationConstructionException {
		try {
			return JsonVariable.create(m_assetFactory.create(name));
		} catch (IOException | ValueSerializationException | AssetStreamConstructionException e) {
			throw new ConfigurationConstructionException(name, e);
		}
	}
}
