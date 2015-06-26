package io.github.jevaengine.util;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class ObserversTest
{
	private Observers m_observers;
	
	private int m_a;
	private int m_b;
	
	@Before
	public void startup()
	{
		m_observers = new Observers();
		m_a = 0;
		m_b = 0;
	}
	
	@Test
	public void invokeProperObserversTest()
	{
		AObserver a = new AObserver();
		BObserver b = new BObserver();
		ABObserver ab = new ABObserver();
		
		m_observers.add(a);
		m_observers.add(b);
		m_observers.add(ab);
		m_observers.add(new WrongObserver());
		m_observers.raise(IAB.class).a();

		assertEquals(2, m_a);
		assertEquals(0, m_b);
		
		m_observers.raise(IA.class).a();
		
		assertEquals(4, m_a);
		assertEquals(0, m_b);
		
		m_observers.remove(a);
		
		m_observers.raise(IA.class).a();
		
		assertEquals(5, m_a);
		assertEquals(0, m_b);
		
		m_observers.raise(IAB.class).b();
		
		assertEquals(5, m_a);
		assertEquals(2, m_b);
		
		m_observers.remove(a);
		m_observers.remove(b);
		m_observers.remove(ab);
		
		m_observers.raise(IAB.class).a();
		
		assertEquals(5, m_a);
		assertEquals(2, m_b);
	}
	
	@Test
	public void recognizeObserversChangeInTimeTest()
	{
		m_observers.add(new IA(){

			@Override
			public void a()
			{
				m_observers.add(new AObserver());
				m_observers.remove(this);
			}
		});
		
		m_observers.raise(IA.class).a();
		assertEquals(1, m_a);
		
		m_observers.raise(IA.class).a();
		assertEquals(2, m_a);
	}
	
	interface IA
	{
		void a();
	}
	
	interface IB
	{
		void b();
	}
	
	interface IAC extends IA
	{
		void c();
	}
	
	interface IAB extends IA, IB
	{
		
	}
	
	private class AObserver implements IA
	{

		@Override
		public void a()
		{
			m_a++;
		}
	}
	
	private class BObserver implements IB
	{
		@Override
		public void b()
		{
			m_b++;
		}
	}
	
	private class ABObserver implements IA, IB
	{
		@Override
		public void b() {
			m_b++;
		}

		@Override
		public void a()
		{
			m_a++;
		}
	}
	
	//If there is an error in terms of the Observers utilization of
	//reflection and in recognizing between interface IA/IB implementations and similar 
	//method names than this will be shown using this class.		
	private class WrongObserver
	{
		@SuppressWarnings("unused")
		public void a()
		{
			m_a++;
		}
		
		@SuppressWarnings("unused")
		public void b()
		{
			m_b++;
		}
	}
}
