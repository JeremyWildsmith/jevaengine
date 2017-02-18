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
package io.github.jevaengine.graphics;

import io.github.jevaengine.graphics.Animation.IAnimationEventListener;
import io.github.jevaengine.graphics.IImmutableAnimation.NullAnimation;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.util.Nullable;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Sprite implements IRenderable, Cloneable
{
	private HashMap<String, Animation> m_animations = new HashMap<>();

	@Nullable
	private Animation m_currentAnimation;

	private IImmutableGraphic m_srcImage;

	private float m_fNaturalScale;

	public Sprite(Sprite src)
	{
		m_srcImage = src.m_srcImage;
		m_currentAnimation = null;
		m_fNaturalScale = src.m_fNaturalScale;

		for (Entry<String, Animation> entry : src.m_animations.entrySet())
		{
			Animation copyAnimation = new Animation(entry.getValue());
			m_animations.put(entry.getKey(), copyAnimation);
			
			if(src.m_currentAnimation == entry.getValue())
				m_currentAnimation = copyAnimation;
		}
	}

	public Sprite(IImmutableGraphic srcImage, float fNaturalScale)
	{
		m_srcImage = srcImage;
		m_currentAnimation = null;
		m_animations = new HashMap<>();

		m_fNaturalScale = fNaturalScale;
	}

	@Override
	public Sprite clone()
	{
		return new Sprite(this);
	}
	
	public Rect2D getBounds()
	{
		if(m_currentAnimation == null)
			return new Rect2D();
		
		return m_currentAnimation.getCurrentFrame().getSourceRect();
	}

	public Vector2D getOrigin()
	{
		if(m_currentAnimation == null)
			return new Vector2D();
		
		return m_currentAnimation.getCurrentFrame().getOrigin();
	}
	
	public Vector2F getOrigin(float scale)
	{
		Vector2D srcOrigin = getOrigin();
		
		return new Vector2F((float)srcOrigin.x * scale, (float)srcOrigin.y * scale);
	}

	public String[] getAnimations()
	{
		Set<String> keys = m_animations.keySet();
		
		return keys.toArray(new String[keys.size()]);
	}
	
	public boolean hasAnimation(String name)
	{
		return m_animations.containsKey(name);
	}

	public IImmutableAnimation getAnimation(String name)
	{
		IImmutableAnimation animation = m_animations.get(name);
		
		return animation == null ? new NullAnimation() : animation;
	}
	
	@Nullable
	public String getCurrentAnimation()
	{
		if(m_currentAnimation != null)
		{
			for(Map.Entry<String, Animation> e : m_animations.entrySet())
			{
				if(e.getValue() == m_currentAnimation)
					return e.getKey();
			}
			
			assert false: "It is not possible for the current animation to not exist as an entry in the set of animations for this sprite.";
			
			return null;
		}else
			return null;
	}

	public void setAnimation(String animationName, AnimationState state) throws NoSuchSpriteAnimation
	{
		setAnimation(animationName, state, null);
	}
	
	public void setAnimation(String animationName, AnimationState state, @Nullable IAnimationEventListener eventListener)  throws NoSuchSpriteAnimation
	{
		m_currentAnimation = m_animations.get(animationName);

		if (m_currentAnimation == null)
			throw new NoSuchSpriteAnimation(animationName);

		m_currentAnimation.setState(state, eventListener);
	}

	public void addAnimation(String name, Animation anim)
	{
		m_animations.put(name, anim);
		
		if(m_currentAnimation == null)
			m_currentAnimation = anim;
	}

	public void update(int deltaTime)
	{
		if (m_currentAnimation != null)
		{
			m_currentAnimation.update(deltaTime);
		}
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		if (m_currentAnimation == null)
			return;
		
		Frame currentFrame = m_currentAnimation.getCurrentFrame();

		int destX = x - Math.round(currentFrame.getOrigin().x * scale * m_fNaturalScale);
		int destY = y - Math.round(currentFrame.getOrigin().y * scale * m_fNaturalScale);

		Rect2D srcRect = currentFrame.getSourceRect();
	
		m_srcImage.render(g, destX, destY, 
				Math.round((float)srcRect.width * scale * m_fNaturalScale),
				Math.round((float)srcRect.height * scale * m_fNaturalScale),
				srcRect.x, srcRect.y, srcRect.width, srcRect.height);
	}
	
	public boolean testPick(int x, int y, float scale)
	{
		if(m_currentAnimation == null)
			return false;
		
		Rect2D bounds = m_currentAnimation.getCurrentFrame().getSourceRect();
		
		int xTest = Math.round(x * (1.0F / (m_fNaturalScale * scale))) + m_currentAnimation.getCurrentFrame().getOrigin().x;
		int yTest = Math.round(y * (1.0F / (m_fNaturalScale * scale))) + m_currentAnimation.getCurrentFrame().getOrigin().y;
		
		if(xTest < 0 || yTest < 0 || xTest > bounds.width || yTest > bounds.height)
			return false;
		
		xTest += bounds.x;
		yTest += bounds.y;
		
		return  m_srcImage.pickTest(xTest, yTest);
	}
	
	public static final class NoSuchSpriteAnimation extends Exception
	{
		private static final long serialVersionUID = 1L;
	
		private NoSuchSpriteAnimation(String name)
		{
			super("Cannot find sprite animation: " + name);
		}
	}
}
