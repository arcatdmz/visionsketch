package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.function.*;
import jp.junkato.vsketch.interpreter.*;
import jp.junkato.vsketch.shape.*;

public class DiscChangeCounterFunction extends Function {

	private IplImage resultImage;
	private int count;
	private int a = 700;
	private boolean isChanging;
	private CvFont boldFont;

	public DiscChangeCounterFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
		boldFont = new CvFont(CV_FONT_HERSHEY_SIMPLEX, 1, 2);
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
			resultImage = IplImage.create(
					sourceImage.width(),
					sourceImage.height(),
					sourceImage.depth(),
					3);
		}

		Function f = getParentStmt().getFunction();
		if (f != null) {
			System.out.println(f.getRetValues().get("area"));
			int area = Integer.parseInt(f.getRetValues().get("area").toString());
			if (area > a) {
				cvCircle(resultImage, cvPoint((int)(resultImage.width()/2), (int)(resultImage.height()/2)), 100, CvScalar.GREEN, -1, 8, 0);
				if (!isChanging) {
					count++;
				}
			} else {
				cvCircle(resultImage, cvPoint((int)(resultImage.width()/2), (int)(resultImage.height()/2)), 100, CvScalar.RED, -1, 8, 0);
			}
			isChanging = (area > a);   
			String message = String.format("%d", count);
			cvPutText(resultImage, message, cvPoint(5, resultImage.height()/2), boldFont, CvScalar.WHITE);
			return;
		}

		// Do some processing.
		cvCopy(sourceImage, resultImage);
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
