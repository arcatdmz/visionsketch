package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.*;
import jp.junkato.vsketch.function.*;
import jp.junkato.vsketch.interpreter.*;
import jp.junkato.vsketch.shape.*;

public class CalcBrightnessAvgFunction extends Function {

	private IplImage resultImage, a, b;

	public CalcBrightnessAvgFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
	}

	// Executed when shapes are updated.
	public void parameterize(FunctionParameter parameter) {
		// parameter.getImage(): IplImage representing the current image of the parent component
		// parameter.getShapes(): Set<Shape> representing the shapes drawn on the parent component
	}

	// The main function code for this image processing component.
	public void calculate(IplImage sourceImage) {

		// Initialize resultImage.
		if (resultImage == null
				|| resultImage.width() != sourceImage.width()
				|| resultImage.height() != sourceImage.height()
				|| resultImage.depth() != sourceImage.depth()) {
			if (resultImage != null) {
				resultImage.release();
			}
			if (a != null) {
				a.release();
			}
			if (b != null) {
				b.release();
			}
			resultImage = IplImage.createCompatible(sourceImage);
			a = IplImage.create(sourceImage.width(), 1, sourceImage.depth(), sourceImage.nChannels());
			b = IplImage.create(1, 1, sourceImage.depth(), sourceImage.nChannels());
		}

		// Do some processing.
		cvCopy(sourceImage, resultImage);
		cvReduce(sourceImage, a, 0, CV_REDUCE_AVG);
		cvReduce(a, b, 1, CV_REDUCE_AVG);
		CvScalar s = cvGet2D(b, 0, 0);
		int brightness = 0, channels = sourceImage.nChannels();
		for (int i = 0; i < channels; i ++) {
			brightness += (int) s.val(i);
		}
		brightness /= channels;
		getRetValues().put("brightness", brightness);
	}

	// Return the result image to be passed to the connected component.
	public IplImage getImage() {
		return resultImage;
	}

	// Clear memory used by this image processing component.
	public void dispose() {
		if (resultImage != null) {
			resultImage.release();
			resultImage = null;
		}
		if (a != null) {
			a.release();
			a = null;
		}
		if (b != null) {
			b.release();
			b = null;
		}
		super.dispose();
	}

}
