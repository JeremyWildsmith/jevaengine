package io.github.jevaengine.graphics;

public interface IImmutableAnimation
{
	String getName();
	int getCurrentFrameIndex();
	int getTotalFrames();
	
	public static final class NullAnimation implements IImmutableAnimation
	{
		@Override
		public String getName()
		{
			return "null";
		}

		@Override
		public int getCurrentFrameIndex()
		{
			return -1;
		}

		@Override
		public int getTotalFrames()
		{
			return 0;
		}
	}
}
