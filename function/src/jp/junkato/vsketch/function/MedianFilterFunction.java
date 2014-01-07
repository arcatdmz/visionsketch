package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.function.*;
import jp.junkato.vsketch.interpreter.*;
import jp.junkato.vsketch.shape.*;

public class MedianFilterFunction extends Function {

	private IplImage resultImage;

	public MedianFilterFunction(Stmt stmt, FunctionTemplate template) {
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
		resultImage = IplImage.createIfNotCompatible(resultImage, sourceImage);

		// Do some processing.
		cvSmooth (sourceImage, resultImage, CV_MEDIAN, 5);
		// cvCopy(sourceImage, resultImage);
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
