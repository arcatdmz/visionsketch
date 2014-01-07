package jp.junkato.vsketch.interpreter.input;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import org.simpleframework.xml.Element;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageFile implements InputSource {
	@Element
	private String filePath;
	private IplImage image;
	private int frameIndex;

	ImageFile() {}

	public ImageFile(String filePath) {
		this.filePath = filePath;
		reload();
	}

	public static String createIdentifier(String filePath) {
		return String.format("%s:%s", ImageFile.class.getSimpleName(), filePath);
	}

	@Override
	public String getIdentifier() {
		return createIdentifier(filePath);
	}

	@Override
	public void reload() {
		dispose();
		image = cvLoadImage(filePath).clone();
	}

	@Override
	public IplImage nextFrame() {
		if (frameIndex >= 1) {
			return null;
		}
		return image;
	}

	@Override
	public boolean setFrameIndex(long frameIndex) {
		if (frameIndex >= 1) {
			frameIndex = 0;
		}
		return true;
	}

	@Override
	public long getFrameIndex() {
		return frameIndex;
	}

	@Override
	public long getFrameCount() {
		return 1;
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.release();
			image = null;
		}
	}

	@Override
	public String toString() {
		return filePath;
	}

}
