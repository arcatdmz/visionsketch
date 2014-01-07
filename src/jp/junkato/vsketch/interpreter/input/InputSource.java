package jp.junkato.vsketch.interpreter.input;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface InputSource {
	public String getIdentifier();
	public void reload();
	public IplImage nextFrame();
	public boolean setFrameIndex(long frameIndex);
	public long getFrameIndex();
	public long getFrameCount();
	public void dispose();
}
