package jp.junkato.vsketch.function;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.Color;
import java.util.Random;

import jp.junkato.vsketch.interpreter.Stmt;

public class ContourCounterFunction extends Function {

	// Used to calculate next images.
	private IplImage resultImage;
	private IplImage intermediateImage;
	private boolean flip;

	public ContourCounterFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
	}

	/**
	 * Count contours.
	 */
	@Override
	public void calculate(IplImage sourceImage) {
		if (resultImage == null
				|| resultImage.width() != sourceImage.width()
				|| resultImage.height() != sourceImage.height()) {
			if (resultImage != null) {
				resultImage.release();
			}
			intermediateImage = IplImage.createCompatible(sourceImage);
			resultImage = IplImage.create(
					sourceImage.width(),
					sourceImage.height(),
					sourceImage.depth(),
					3);
		}

		// Contour detection
		CvSeq contours = new CvSeq();
		CvSeq ptr;
		CvMemStorage mem = cvCreateMemStorage(0);
		// if (flip) {
		//cvNot(sourceImage, intermediateImage);
		// } else {
		cvCopy(sourceImage, intermediateImage);
		// } 
		int count = cvFindContours(
				intermediateImage, mem, contours, sizeof(CvContour.class),
				CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE,
				cvPoint(0, 0));
		int area = 0;

		cvCvtColor(sourceImage, resultImage, CV_GRAY2BGR);
		if (count > 0) {
			Random rand = new Random();
			for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
				area += cvContourArea(ptr, CV_WHOLE_SEQ, 0);
				Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
				CvScalar color = CV_RGB(randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue());
				cvDrawContours(resultImage, ptr, color, CV_RGB(0, 0, 0), -1, CV_FILLED, 8, cvPoint(0, 0));
				// CvRect rect = cvBoundingRect(ptr, 0);
				// cvRectangle(resultImage, cvPoint(rect.x(), rect.y()), cvPoint(rect.x() + rect.width(), rect.y() + rect.height()), CV_RGB(230, 230, 255), 1, 8, 0);
				cvClearSeq(ptr);
			}
			rand = null;
		}

		cvClearMemStorage(mem);
		cvReleaseMemStorage(mem);

		getRetValues().put("count", count);
		getRetValues().put("area", area);
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
		super.dispose();
	}
}