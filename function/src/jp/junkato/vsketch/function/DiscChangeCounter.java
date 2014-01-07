package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.*;
import jp.junkato.vsketch.tool.*;

public class DiscChangeCounter extends FunctionTemplate {

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
		return shapes.size() == 0 && parent != null && parent.getFunction() != null && parent.getFunction().getRetValues().containsKey("area") && parent.getFunction().getRetValues().containsKey("count");
	}

	@Override
	public String getName() {
		return "Count discs";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_320_filter.png";
	}

	public DiscChangeCounterFunction newInstance(Stmt stmt) {
		return new DiscChangeCounterFunction(stmt, this);
	}

}
