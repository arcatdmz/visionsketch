package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.*;
import jp.junkato.vsketch.tool.*;

public class CalcBrightnessAvg extends FunctionTemplate {

	public CalcBrightnessAvg() {
		getRetTypes().put("brightness", java.lang.Integer.class);
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
		return shapes.size() == 0;
	}

	@Override
	public String getName() {
		return "Get brightness";
	}

	@Override
	public String getDescription() {
		return "Get average of brightness. (0-255)";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_320_filter.png";
	}

	public CalcBrightnessAvgFunction newInstance(Stmt stmt) {
		return new CalcBrightnessAvgFunction(stmt, this);
	}

}
