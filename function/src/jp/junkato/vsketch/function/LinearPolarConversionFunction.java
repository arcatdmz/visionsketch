package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.function.FunctionParameter;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.Circle;
import jp.junkato.vsketch.shape.Shape;

public class LinearPolarConversionFunction extends Function {

	// Used to create parameters.
	private CvPoint2D32f center;
	private double radius;
	private int width = 640, height = 320;

	// Used to calculate next images.
	private IplImage resultImage;

	public LinearPolarConversionFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
	}

	@Override
	public void parameterize(FunctionParameter parameter) {
		Shape shape = parameter.getShapes().iterator().next();
		Circle circle = (Circle) shape;

		// Setup parameters.
		if (center != null) {
			center.deallocate();
		}
		center = new CvPoint2D32f(circle.center.x, circle.center.y);
		radius = circle.radius;
	}

	/**
	 * Apply linear polar conversion to the specified circle area.
	 */
	@Override
	public void calculate(IplImage sourceImage) {
		if (resultImage == null
				|| resultImage.depth() != sourceImage.depth()
				|| resultImage.nChannels() != sourceImage.nChannels()) {
			if (resultImage != null) {
				resultImage.release();
			}
			resultImage = IplImage.create(
					width, height, sourceImage.depth(), sourceImage.nChannels());
		}
		cvLinearPolar(
				sourceImage,
				resultImage,
				center,
				radius,
				CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS);
	}

	@Override
	public IplImage getImage() {
		return resultImage;
	}

	@Override
	public void dispose() {
		if (resultImage != null) {
			resultImage.release();
			resultImage = null;
		}
		if (center != null) {
			center.deallocate();
			center = null;
		}
		super.dispose();
	}

}