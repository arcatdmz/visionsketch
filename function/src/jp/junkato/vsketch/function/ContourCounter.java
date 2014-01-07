package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.*;
import jp.junkato.vsketch.tool.*;

public class ContourCounter extends FunctionTemplate {

	public ContourCounter() {
		getRetTypes().put("area", java.lang.Integer.class);
		getRetTypes().put("count", java.lang.Integer.class);
	}

	@Override
	public boolean isCapableOf(Tool tool, Stmt stmt) {
		Stmt parent = stmt.getParent();
		Set<Shape> shapes = stmt.getShapes();
		Shape shape = shapes.isEmpty() ? null : shapes.iterator().next();
		return (true) || tool instanceof ScrollTool;
	}

	@Override
	public boolean check(Stmt stmt) {
		Stmt parent = stmt.getParent();
		Set<Shape> shapes = stmt.getShapes();
		Shape shape = shapes.isEmpty() ? null : shapes.iterator().next();
		IplImage image = stmt.getParent() == null ? null : stmt.getParent().getRawOutput();
		return shapes.size() == 0 && image != null && image.nChannels() == 1;
	}

	@Override
	public String getName() {
		return "Contour counter";
	}

	@Override
	public String getDescription() {
		return "Count up contours.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_320_filter.png";
	}

	public ContourCounterFunction newInstance(Stmt stmt) {
		return new ContourCounterFunction(stmt, this);
	}

}
