package io.github.jevaengine.util;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MutableProcessListTest {
	private List<Integer> m_mutableProcessList;

	@Before
	public void startup() {
		m_mutableProcessList = new MutableProcessList<>();
	}

	@Test
	public void mutateAndParse() {
		int factorial = 1;

		m_mutableProcessList.add(10);

		for (Integer i : m_mutableProcessList) {
			factorial *= i;
			if (i > 1)
				m_mutableProcessList.add(i - 1);
		}

		assertEquals(3628800, factorial);
	}

}
