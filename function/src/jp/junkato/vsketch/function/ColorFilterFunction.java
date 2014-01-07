package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.Set;

import jp.junkato.vsketch.function.FunctionParameter;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.Shape;

public class ColorFilterFunction extends Function {

	// Used to create parameters.
	private static final int numberOfBins = 16;
	private IplImage histImage;
	private IplImage histMask;
	private CvHistogram hist;

	// Used to calculate next images.
	private IplImage yuvImage;
	private IplImage[] splitImages;
	private IplImage resultImage;

	public ColorFilterFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
	}

	/**
	 * Calculate the histogram from the selected area of the input image.
	 */
	@Override
	public void parameterize(FunctionParameter parameter) {

		if (histImage != null) {
			histImage.release();
		}
		histImage = parameter.getImage().clone();

		if (histMask == null
				|| histMask.width() != histImage.width()
				|| histMask.height() != histImage.height()) {
			histMask = IplImage.create(
					histImage.width(),
					histImage.height(),
					8,
					1);
		}
		createMask(histMask, parameter.getShapes());

		if (hist != null) {
			cvReleaseHist(hist);
		}
		hist = calcHistogram(histImage, histMask);
	}

	/**
	 * Apply color filter to the input image.
	 */
	@Override
	public void calculate(IplImage sourceImage) {
		if (resultImage == null
				|| resultImage.width() != sourceImage.width()
				|| resultImage.height() != sourceImage.height()
				|| resultImage.depth() != sourceImage.depth()) {
			if (yuvImage != null) {
				yuvImage.release();
			}
			yuvImage = IplImage.create(
					sourceImage.width(),
					sourceImage.height(),
					sourceImage.depth(),
					3);
			if (resultImage != null) {
				resultImage.release();
			}
			resultImage = IplImage.create(
					sourceImage.width(),
					sourceImage.height(),
					sourceImage.depth(),
					1);
		}
		if (splitImages == null
				|| splitImages[0].width() != sourceImage.width()
				|| splitImages[0].height() != sourceImage.height()) {
			splitImages = new IplImage[2];
			for (int i = 0; i < splitImages.length; i ++) {
				splitImages[i] = IplImage.create(
						sourceImage.width(),
						sourceImage.height(),
						sourceImage.depth(),
						1);
			}
		}

		if (sourceImage.nChannels() == 4) {
			cvCvtColor(sourceImage, yuvImage, CV_RGBA2RGB);
			cvCvtColor(yuvImage, yuvImage, CV_RGB2YUV);
		} else if (sourceImage.nChannels() == 3) {
			cvCvtColor(sourceImage, yuvImage, CV_BGR2YUV);
		} else return;

		cvSplit(yuvImage, null, splitImages[0], splitImages[1], null);
		cvCalcBackProject(splitImages, resultImage, hist);
	}

	@Override
	public IplImage getImage() {
		return resultImage;
	}

	@Override
	public void dispose() {
		if (hist != null) {
			cvReleaseHist(hist);
			hist = null;
		}
		if (yuvImage != null) {
			yuvImage.release();
			yuvImage = null;
		}
		if (splitImages != null) {
			for (IplImage splitImage : splitImages) {
				splitImage.release();
			}
			splitImages = null;
		}
		if (resultImage != null) {
			resultImage.release();
			resultImage = null;
		}
		if (histImage != null) {
			histImage.release();
			histImage = null;
		}
		super.dispose();
	}

	private void createMask(IplImage histMask, Set<Shape> shapes) {
		cvSetZero(histMask);
		for (Shape shape : shapes) {
			shape.fill(histMask);
		}
	}

	private CvHistogram calcHistogram(IplImage currentImage, IplImage histMask) {

		IplImage yuvImage = IplImage.create(
				currentImage.width(),
				currentImage.height(),
				currentImage.depth(),
				3);
		IplImage[] splitImages = new IplImage[2];
		for (int i = 0; i < splitImages.length; i ++) {
			splitImages[i] = IplImage.create(
					currentImage.width(),
					currentImage.height(),
					yuvImage.depth(),
					1);
		}

		if (currentImage.nChannels() == 4) {
			cvCvtColor(currentImage, yuvImage, CV_RGBA2RGB);
			cvCvtColor(yuvImage, yuvImage, CV_RGB2YUV);
		} else if (currentImage.nChannels() == 3) {
			cvCvtColor(currentImage, yuvImage, CV_BGR2YUV);
		} else return null;

		cvSplit(yuvImage, null, splitImages[0], splitImages[1], null);

		CvHistogram hist = createHist();
		cvCalcHist(splitImages, hist, 0, histMask);

		for (int i = 0; i < splitImages.length; i ++) {
			splitImages[i].release();
		}
		return hist;
	}

	public static CvHistogram createHist() {
		return cvCreateHist(
				2,
				new int[] {
						numberOfBins,
						numberOfBins
				},
				CV_HIST_ARRAY,
				new float[][] {
						new float[] { 0f, 255f },
						new float[] { 0f, 255f }
				},
				1);
	}

	public static CvHistogram copyHist(CvHistogram hist) {
		CvHistogram copy = createHist();
		cvCopyHist(hist, copy);
		return copy;
	}

}