/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jevaengine.world.pathfinding;

import io.github.jevaengine.math.Vector2F;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Jeremy
 */
public class RouteReduceTest
{
	@Test
	public void reduceRoute()
	{
		final Vector2F initialRoute[] = new Vector2F[] {
			new Vector2F(0, 0),
			new Vector2F(1, 0),
			new Vector2F(2, 0),
			new Vector2F(2, 1),
			new Vector2F(2, 2),
			new Vector2F(2, 3),
			new Vector2F(2, 0),
		};
		
		final Vector2F expectedRoute[] = new Vector2F[] {
			new Vector2F(0, 0),
			new Vector2F(2, 0),
			new Vector2F(2, 3),
			new Vector2F(2, 0),
		};
		
		Route r = new Route(initialRoute).reduce();
		
		assertTrue(r.length() == expectedRoute.length);
		
		for(int i = 0; i < expectedRoute.length; i++)
		{
			assertTrue(r.getCurrentTarget().equals(expectedRoute[i]));
			r.nextTarget();
		}
	}
}
