package jp.junkato.vsketch.interpreter.input;

import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateCameraCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvQueryFrame;
import static com.googlecode.javacv.cpp.opencv_highgui.cvReleaseCapture;

import org.simpleframework.xml.Element;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

public class Camera implements InputSource {
	private CvCapture capture;
	private IplImage image;
	@Element
	private int deviceIndex = -1;
	private long frameIndex;

	public Camera() {
		this(-1);
	}

	public Camera(int index) {
		this.deviceIndex = index;
		reload();
	}

	public static String createIdentifier() {
		return createIdentifier(-1);
	}

	public static String createIdentifier(int deviceIndex) {
		return String.format("%s:%d", Camera.class.getSimpleName(), deviceIndex);
	}

	@Override
	public String getIdentifier() {
		return createIdentifier(deviceIndex);
	}

	@Override
	public void reload() {
		dispose();
		capture = cvCreateCameraCapture(deviceIndex);
	}

	@Override
	public synchronized IplImage nextFrame() {

		// This image object cannot be released by the user code.
		// Cf. http://stackoverflow.com/questions/10951016/unhandled-exception-opencv-cvreleasecapture-and-cvreleaseimage-c
		IplImage nextFrame = cvQueryFrame(capture);

		// No frame found. The end of this capture object.
		if (nextFrame == null) {
			return null;
		}

		// Update image object.
		if (image == null) {
			// This image object can be released by the user code.
			image = nextFrame.clone();
		} else {
			cvCopy(nextFrame, image);
		}
		frameIndex ++;
		return image;
	}

	@Override
	public boolean setFrameIndex(long frameIndex) {
		return false;
	}

	@Override
	public long getFrameIndex() {
		return frameIndex;
	}

	@Override
	public long getFrameCount() {
		return -1;
	}

	@Override
	public synchronized void dispose() {
		if (capture != null) {
			cvReleaseCapture(capture);
			capture = null;
		}
		if (image != null) {
			image.release();
			image = null;
		}
	}

	@Override
	public String toString() {
		return "";
	}

}
