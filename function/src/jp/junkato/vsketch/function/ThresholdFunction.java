package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.function.*;
import jp.junkato.vsketch.interpreter.*;
import jp.junkato.vsketch.shape.*;

public class ThresholdFunction extends Function {

	private IplImage resultImage;
	public int threshold = 60;

	public ThresholdFunction(Stmt stmt, FunctionTemplate template) {
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
				|| resultImage.depth() != sourceImage.depth()
				|| resultImage.nChannels() != sourceImage.nChannels()) {
			if (resultImage != null) {
				resultImage.release();
			}
			resultImage = IplImage.create(
					sourceImage.width(),
					sourceImage.height(),
					sourceImage.depth(),
					1);
		}

		// Do some processing.
		cvCvtColor(sourceImage, resultImage, CV_RGB2GRAY);
		cvThreshold(resultImage, resultImage, threshold, 255, CV_THRESH_BINARY);
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
		super.dispose();
	}

}
