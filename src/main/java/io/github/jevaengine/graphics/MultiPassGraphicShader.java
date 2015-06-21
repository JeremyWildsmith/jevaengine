package io.github.jevaengine.graphics;

public class MultiPassGraphicShader implements IGraphicShader
{
	private final IGraphicShader[] m_shaders;
	
	public MultiPassGraphicShader(IGraphicShader ... shaders)
	{
		m_shaders = shaders;
	}
	
	@Override
	public IImmutableGraphic shade(IImmutableGraphic source)
	{
		IImmutableGraphic shaded = source;
		
		for(IGraphicShader s : m_shaders)
			shaded = s.shade(shaded);
		
		return shaded;
	}
	
}
