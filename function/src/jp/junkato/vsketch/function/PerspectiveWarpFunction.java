package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.function.FunctionParameter;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.Rectangle;
import jp.junkato.vsketch.shape.Shape;

public class PerspectiveWarpFunction extends Function {

	private CvPoint2D32f srcPoints, dstPoints;
	private CvMat mapMatrix;
	private int width = 640, height = 800;
	private IplImage resultImage;

	public PerspectiveWarpFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
	}

	/**
	 * Calculate the map matrix from the selected rectangle of the input image.
	 */
	@Override
	public void parameterize(FunctionParameter parameter) {
		Shape shape = parameter.getShapes().iterator().next();
//		if (!(shape instanceof Rectangle)) {
//			err("shapes other than a rectangle not supported.");
//			return false;
//		}
		final Rectangle rectangle = (Rectangle) shape;
		final CvPoint2D32f srcPoints = rectangle.asCvFloatPoints();
		if (srcPoints == null) {
			return;
		}

		// Setup destination positions.
		dstPoints = new CvPoint2D32f(4);
		for (int j = 0; j < 4; j ++) {
			dstPoints.position(j).x(j / 2 == j % 2 ? 0 : width);
			dstPoints.position(j).y(j < 2 ? 0 : height);
		}
		dstPoints.position(0);

		// Calculate mapping matrix.
		// TODO We might allow the user to specify additional points to
		// calculate homography using cvFindHomography
		if (mapMatrix == null) {
			mapMatrix = cvCreateMat(3, 3, CV_32FC1);
		}
		cvGetPerspectiveTransform(srcPoints, dstPoints, mapMatrix);
	}

	/**
	 * Apply perspective warp to the input image.
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
		if (mapMatrix == null) {
			return;
		}
		//cvSet(resultImage, cvScalar(255, 255, 255, 0));
		cvWarpPerspective(sourceImage, resultImage, mapMatrix);
	}

	@Override
	public IplImage getImage() {
		return resultImage;
	}

	@Override
	public void dispose() {
		if (srcPoints != null) {
			//srcPoints.deallocate();
			srcPoints = null;
		}
		if (dstPoints != null) {
			//dstPoints.deallocate();
			dstPoints = null;
		}
		if (mapMatrix != null) {
			//mapMatrix.deallocate();
			mapMatrix = null;
		}
		if (resultImage != null) {
			resultImage.release();
			resultImage = null;
		}
		super.dispose();
	}

}
