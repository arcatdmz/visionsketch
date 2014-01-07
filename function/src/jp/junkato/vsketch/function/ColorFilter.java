package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.*;
import jp.junkato.vsketch.tool.*;

public class ColorFilter extends FunctionTemplate {

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
		return shapes.size() > 0 && image != null && image.nChannels() >= 3;
	}

	@Override
	public String getName() {
		return "Color filter";
	}

	@Override
	public String getDescription() {
		return "Extract regions with similar colors.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_320_filter.png";
	}

	public ColorFilterFunction newInstance(Stmt stmt) {
		return new ColorFilterFunction(stmt, this);
	}

}
