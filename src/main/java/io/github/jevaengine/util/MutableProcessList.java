package io.github.jevaengine.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class MutableProcessList<T> implements List<T> {

	private final List<WeakReference<Queue<T>>> m_processQueues = new LinkedList<>();
	private final List<T> m_workSet = new ArrayList<>();
	
	public MutableProcessList() { }
	
	public MutableProcessList(List<T> workSet)
	{
		m_workSet.addAll(workSet);
	}
	
	private void doProcessQueuesOperation(IProcessQueueOperation<T> operation)
	{
		Iterator<WeakReference<Queue<T>>> it = m_processQueues.iterator();
		
		while(it.hasNext())
		{
			WeakReference<Queue<T>> queue = it.next();
			
			if(queue.get() == null)
				it.remove();
			else
				operation.perform(queue.get());
		}
	}
	@Override
	public Iterator<T> iterator()
	{
		return new IteratorImpl();
	}

	@Override
	public boolean add(final T e)
	{
		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.add(e);
			}
		});
		
		m_workSet.add(e);
		
		return true;
	}

	@Override
	public boolean addAll(final Collection<? extends T> c)
	{
		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.addAll(c);
			}
		});
		
		m_workSet.addAll(c);
		
		return true;
	}

	@Override
	public void clear()
	{

		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.clear();
			}
		});
		
		m_workSet.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return m_workSet.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return m_workSet.containsAll(c);
	}

	@Override
	public boolean isEmpty()
	{
		return m_workSet.isEmpty();
	}

	@Override
	public boolean remove(final Object o)
	{
		m_workSet.remove(o);

		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.remove(o);
			}
		});
		
		return true;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		
		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.removeAll(c);
			}
		});
		
		return m_workSet.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c)
	{

		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.retainAll(c);
			}
		});
		
		return m_workSet.retainAll(c);
	}

	@Override
	public int size()
	{
		return m_workSet.size();
	}

	@Override
	public Object[] toArray()
	{
		return m_workSet.toArray();
	}

	@Override
	public <Y> Y[] toArray(Y[] a)
	{
		return m_workSet.toArray(a);
	}

	@Override
	public void add(int index, T element)
	{
		m_workSet.add(index, element);
	}
	
	@Override
	public boolean addAll(int index, final Collection<? extends T> c) {
		doProcessQueuesOperation(new IProcessQueueOperation<T>() {

			@Override
			public void perform(Queue<T> q)
			{
				q.addAll(c);
			}
		});
		
		return m_workSet.addAll(index, c);
	}
	
	@Override
	public T get(int index)
	{
		return m_workSet.get(index);
	}
	
	@Override
	public int indexOf(Object o)
	{
		return m_workSet.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o)
	{
		return m_workSet.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<T> listIterator()
	{
		return new IteratorImpl();
	}
	
	@Override
	public ListIterator<T> listIterator(int index)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public T remove(int index)
	{
		final T remove = m_workSet.remove(index);
		
		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.remove(remove);
			}
		});
		
		return remove;
	}
	
	@Override
	public T set(int index, final T element)
	{
		final T remove = m_workSet.set(index, element);
		doProcessQueuesOperation(new IProcessQueueOperation<T>() {
			@Override
			public void perform(Queue<T> q) {
				q.add(element);
				q.remove(remove);
			}
		});
		
		return remove;
	}
	
	@Override
	public List<T> subList(int fromIndex, int toIndex) 
	{
		return m_workSet.subList(fromIndex, toIndex);
	}
	
	private final class IteratorImpl implements ListIterator<T>
	{
		private final Queue<T> m_processQueue = new LinkedList<>();
		private T m_last;
		
		public IteratorImpl()
		{
			m_processQueues.add(new WeakReferenceWithEquals<>(m_processQueue));
			m_processQueue.addAll(m_workSet);
		}
		
		@Override
		public boolean hasNext()
		{
			boolean isEmpty = m_processQueue.isEmpty();
			
			if(isEmpty)
			{
				m_processQueues.remove(new Object() {
					@Override
					public int hashCode()
					{
						return m_processQueue.hashCode();
					}
					
					public boolean equals(Object o)
					{
						if(o instanceof WeakReference<?>)
						{
							return m_processQueue == ((WeakReference<?>)o).get();
						}else
							return false;
					}
				});
			}
			
			return !isEmpty;
		}

		@Override
		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();

			m_last = m_processQueue.poll();
			return m_last;
		}

		@Override
		public void remove()
		{
			MutableProcessList.this.remove(m_last);
		}

		@Override
		public void add(T arg0)
		{
			throw new UnsupportedOperationException();	
		}

		@Override
		public boolean hasPrevious()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int nextIndex()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public T previous()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int previousIndex()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(T arg0)
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private interface IProcessQueueOperation<T>
	{
		void perform(Queue<T> q);
	}
	
	private static final class WeakReferenceWithEquals<T> extends WeakReference<T>
	{
		public WeakReferenceWithEquals(T referent)
		{
			super(referent);
		}
	
		@Override
		public int hashCode()
		{
			return get() == null ? 0 : get().hashCode();
		}
		
		@Override
		public boolean equals(Object o)
		{
			return get() == null ? false : get().equals(o);
		}
	}
}
