package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import jp.junkato.vsketch.function.*;
import jp.junkato.vsketch.interpreter.*;
import jp.junkato.vsketch.shape.*;

//import javax.swing.JFrame;

public class BackgroundSubtractionFunction extends Function {

	private IplImage backgroundImage;
	private IplImage resultImage;
//	private JFrame frame;

	public BackgroundSubtractionFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
//		frame = new JFrame();
	}

	// Executed when shapes are updated.
	public void parameterize(FunctionParameter parameter) {
		if (backgroundImage != null) {
			backgroundImage.release();
			backgroundImage = null;
		}
		backgroundImage = parameter.getImage().clone();
//		frame.setVisible(true);
	}

	// The main function code for this image processing component.
	public void calculate(IplImage sourceImage) {

		// Initialize resultImage.
		resultImage = IplImage.createIfNotCompatible(resultImage, sourceImage);

		// Do some processing.
		cvAbsDiff(sourceImage, backgroundImage, resultImage);
	}

	// Return the result image to be passed to the connected component.
	public IplImage getImage() {
		return resultImage;
	}

	// Clear memory used by this image processing component.
	public void dispose() {
		if (backgroundImage != null) {
			backgroundImage.release();
			backgroundImage = null;
		}
		if (resultImage != null) {
			resultImage.release();
			resultImage = null;
		}
//		frame.dispose();
//		frame = null;
		super.dispose();
	}

}
