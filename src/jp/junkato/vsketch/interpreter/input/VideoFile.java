package jp.junkato.vsketch.interpreter.input;

import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_CAP_PROP_FRAME_COUNT;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_CAP_PROP_POS_FRAMES;
import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateFileCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvGetCaptureProperty;
import static com.googlecode.javacv.cpp.opencv_highgui.cvQueryFrame;
import static com.googlecode.javacv.cpp.opencv_highgui.cvReleaseCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSetCaptureProperty;

import org.simpleframework.xml.Element;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

public class VideoFile implements InputSource {
	@Element
	private String filePath;
	private CvCapture capture;
	private IplImage image;
	private long frameIndex;
	private long frameCount;

	VideoFile() {}

	public VideoFile(String filePath) {
		this.filePath = filePath;
		reload();
	}

	public static String createIdentifier(String filePath) {
		return String.format("%s:%s", VideoFile.class.getSimpleName(), filePath);
	}

	@Override
	public String getIdentifier() {
		return createIdentifier(filePath);
	}

	@Override
	public void reload() {
		dispose();
		capture = cvCreateFileCapture(filePath);
		frameCount = (long) cvGetCaptureProperty(
				capture, CV_CAP_PROP_FRAME_COUNT);
	}

	@Override
	public synchronized IplImage nextFrame() {

		// This image object cannot be released by the user code.
		// Cf. http://stackoverflow.com/questions/10951016/unhandled-exception-opencv-cvreleasecapture-and-cvreleaseimage-c
		IplImage nextFrame = cvQueryFrame(capture);

		// No frame found. The end of this capture object.
		if (nextFrame == null) {
			frameCount = frameIndex + 1;
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
	public synchronized boolean setFrameIndex(long frameIndex) {
		try {
			cvSetCaptureProperty(
					capture, CV_CAP_PROP_POS_FRAMES, frameIndex);
			this.frameIndex = frameIndex;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public long getFrameIndex() {
		return frameIndex;
	}

	@Override
	public long getFrameCount() {
		return frameCount;
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
		return filePath;
	}

}
