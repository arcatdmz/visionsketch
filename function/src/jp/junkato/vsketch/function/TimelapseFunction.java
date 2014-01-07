package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.function.FunctionParameter;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.Line;
import jp.junkato.vsketch.shape.Shape;
import jp.junkato.vsketch.utils.VsketchUtils;

public class TimelapseFunction extends Function {

	private CvPoint2D32f srcPoints, dstPoints;
	private CvMat mapMatrix;
	private int width = 640; // Initial buffer holds 640 frames.
	private int height;
	private IplImage warpedImage;
	private IplImage temporaryImage;
	private IplImage resultImage;
	private int currentX = 0;

	public TimelapseFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
	}

	/**
	 * Calculate the map matrix from the selected rectangle of the input image.
	 */
	@Override
	public void parameterize(FunctionParameter parameter) {
		Shape shape = parameter.getShapes().iterator().next();
		Line line = (Line) shape;
		srcPoints = line.asCvFloatPoints();

		// Calculate height of the target image.
		height = (int) VsketchUtils.distance(
				srcPoints.position(1).x(),
				srcPoints.position(1).y(),
				srcPoints.position(2).x(),
				srcPoints.position(2).y());
		srcPoints.position(0);

		// Prepare intermediate result holders.
		IplImage currentImage = parameter.getImage();
		warpedImage = IplImage.create(
				cvSize(1, height),
				currentImage.depth(),
				currentImage.nChannels());
		resultImage = IplImage.create(
				cvSize(width, height),
				currentImage.depth(),
				currentImage.nChannels());
		cvZero(resultImage);
		temporaryImage = resultImage.clone();

		// Prepare info for warp matrix.
		dstPoints = new CvPoint2D32f(4);
		for (int j = 0; j < 4; j ++) {
			dstPoints.position(j).x(j / 2 != j % 2 ? 0 : 1);
			dstPoints.position(j).y(j < 2 ? 0 : height);
		}
		dstPoints.position(0);

		// Calculate mapping matrix.
		mapMatrix = cvCreateMat(3, 3, CV_32FC1);
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
			parameterize();
		}
		cvWarpPerspective(sourceImage, warpedImage, mapMatrix);
		if (currentX >= width) {

			// Backup copy
			cvSetImageROI(resultImage, cvRect(1, 0, width - 1, height));
			cvSetImageROI(temporaryImage, cvRect(0, 0, width - 1, height));
			cvCopy(resultImage, temporaryImage);
			cvResetImageROI(resultImage);

			// Perspective warp
			cvSetImageROI(temporaryImage, cvRect(width - 1, 0, 1, height));
			cvCopy(warpedImage, temporaryImage);
			cvResetImageROI(temporaryImage);

			// Copy back
			cvCopy(temporaryImage, resultImage);
		} else {
			cvSetImageROI(resultImage, cvRect(currentX, 0, 1, height));
			cvCopy(warpedImage, resultImage);
			cvResetImageROI(resultImage);
			currentX ++;
		}
	}

	@Override
	protected void calculateThumbnail(IplImage image, IplImage thumbnail) {
		int x = Math.max(currentX - getStmt().getThumbnailWidth(), 0);
		cvZero(thumbnail);
		cvSetImageROI(image, cvRect(x, 0, getStmt().getThumbnailWidth(), height));
		cvResize(image, thumbnail);
		cvResetImageROI(image);
	}

	@Override
	public IplImage getImage() {
		return resultImage;
	}

	@Override
	public void dispose() {
		if (srcPoints != null) {
			srcPoints.deallocate();
			srcPoints = null;
		}
		if (dstPoints != null) {
			dstPoints.deallocate();
			dstPoints = null;
		}
//		if (mapMatrix != null) {
//			mapMatrix.deallocate();
//			mapMatrix = null;
//		}
		if (warpedImage != null) {
			warpedImage.release();
			warpedImage = null;
		}
		if (temporaryImage != null) {
			temporaryImage.release();
			temporaryImage = null;
		}
		if (resultImage != null) {
			resultImage.release();
			resultImage = null;
		}
		super.dispose();
	}

}
