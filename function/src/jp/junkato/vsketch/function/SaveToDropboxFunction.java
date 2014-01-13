package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.*;
import jp.junkato.vsketch.function.*;
import jp.junkato.vsketch.interpreter.*;
import jp.junkato.vsketch.shape.*;

public class SaveToDropboxFunction extends Function {

	private IplImage resultImage;

	public SaveToDropboxFunction(Stmt stmt, FunctionTemplate template) {
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
			resultImage = IplImage.createCompatible(sourceImage);
		}

		// Do some processing.
		Function f = getParentStmt().getFunction();
		if (f != null && f.getRetValues().containsKey("brightness")) {
			int brightness = Integer.parseInt(f.getRetValues().get("brightness").toString());
			if (brightness > 100) {
				cvSaveImage("/Users/arc/Dropbox/Shared/J.N/whiteboard.jpg", sourceImage); // for Mac OS X
				//cvSaveImage("C:\\Users\\arc\\Shared\\J.N\\whiteboard.jpg", sourceImage); // for Windows
				cvCopy(sourceImage, resultImage);
			}
		}
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
