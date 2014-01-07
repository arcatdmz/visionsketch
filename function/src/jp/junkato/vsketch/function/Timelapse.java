package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.*;
import jp.junkato.vsketch.tool.*;

public class Timelapse extends FunctionTemplate {

	@Override
	public boolean isCapableOf(Tool tool, Stmt stmt) {
		Stmt parent = stmt.getParent();
		Set<Shape> shapes = stmt.getShapes();
		Shape shape = shapes.isEmpty() ? null : shapes.iterator().next();
		return (shapes.size() == 0 || tool instanceof RemoveTool ||  tool instanceof LineTool) || tool instanceof ScrollTool;
	}

	@Override
	public boolean check(Stmt stmt) {
		Stmt parent = stmt.getParent();
		Set<Shape> shapes = stmt.getShapes();
		Shape shape = shapes.isEmpty() ? null : shapes.iterator().next();
		IplImage image = stmt.getParent() == null ? null : stmt.getParent().getRawOutput();
		return shapes.size() == 1 && shape instanceof Line;
	}

	@Override
	public String getName() {
		return "Timelapse";
	}

	@Override
	public String getDescription() {
		return "Make a timelapse image.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_097_vector_path_line.png";
	}

	public TimelapseFunction newInstance(Stmt stmt) {
		return new TimelapseFunction(stmt, this);
	}

}
