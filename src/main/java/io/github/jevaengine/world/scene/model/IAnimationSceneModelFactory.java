package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.world.scene.model.sprite.SpriteSceneModelFactory;

import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(SpriteSceneModelFactory.class)
public interface IAnimationSceneModelFactory extends ISceneModelFactory
{
	IAnimationSceneModel create(URI name) throws SceneModelConstructionException;
}
