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
package io.github.jevaengine.audio;

import io.github.jevaengine.IDisposable;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class AudioClipCache implements IDisposable {
	private final ArrayList<SoftReference<Clip>> m_clips = new ArrayList<>();

	private final ArrayList<Clip> m_busyClips = new ArrayList<>();

	private final ByteBufferAdapter m_clipStream;

	private final URI m_clipName;

	private final ReferenceQueue<Clip> m_clipCleanupQueue;

	public AudioClipCache(URI name, byte[] source, ReferenceQueue<Clip> clipCleanupQueue) {
		m_clipName = name;
		m_clipCleanupQueue = clipCleanupQueue;

		m_clipStream = new ByteBufferAdapter(ByteBuffer.wrap(source));
	}

	@Override
	public void dispose() {
		try {
			m_clipStream.close();
		} catch (IOException e) {
			// This should never happen
			throw new RuntimeException(e);
		}
	}

	public synchronized Clip getClip() throws IOException, UnsupportedAudioFileException, UnsupportedAudioFormatException, LineUnavailableException {
		Clip clip = null;

		ArrayList<SoftReference<Clip>> garbageClips = new ArrayList<>();

		for (SoftReference<Clip> entry : m_clips) {
			if (entry.get() == null)
				garbageClips.add(entry);
			else if (!m_busyClips.contains(entry.get()))
				clip = entry.get();
		}

		m_clips.removeAll(garbageClips);

		if (clip == null) {
			m_clipStream.reset();
			try (AudioInputStream ais = AudioSystem.getAudioInputStream(m_clipStream)) {
				AudioFormat baseFormat = ais.getFormat();

				AudioFormat[] supportedTargets = AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, baseFormat);

				if (supportedTargets.length == 0)
					throw new UnsupportedAudioFormatException(baseFormat.toString());

				clip = AudioSystem.getClip();
				try (AudioInputStream targetAis = AudioSystem.getAudioInputStream(supportedTargets[0], ais)) {
					clip.open(targetAis);
				}
			}

			m_clips.add(new SoftReference<>(clip, m_clipCleanupQueue));
		}

		m_busyClips.add(clip);

		return clip;
	}

	public synchronized void freeClip(Clip clip) {
		clip.stop();
		m_busyClips.remove(clip);
	}

	public synchronized void cleanupCache() {
		for (SoftReference<Clip> clip : m_clips) {
			if (clip.get() == null)
				m_clips.remove(clip);
		}
	}

	public URI getName() {
		return m_clipName;
	}

	public synchronized boolean isEmpty() {
		return m_clips.isEmpty();
	}

	private static class ByteBufferAdapter extends InputStream {
		private final ByteBuffer m_buffer;

		public ByteBufferAdapter(ByteBuffer buffer) {
			m_buffer = buffer;
		}

		@Override
		public synchronized void reset() {
			m_buffer.rewind();
		}

		@Override
		public int read() throws IOException {
			if (!m_buffer.hasRemaining())
				return -1;

			// And with 0xFF to mask just one byte of data from the read
			// operation.
			return m_buffer.get() & 0xFF;
		}

		@Override
		public int read(byte[] bytes, int off, int len) throws IOException {
			if (!m_buffer.hasRemaining())
				return -1;

			len = Math.min(len, m_buffer.remaining());
			m_buffer.get(bytes, off, len);
			return len;
		}
	}
}
