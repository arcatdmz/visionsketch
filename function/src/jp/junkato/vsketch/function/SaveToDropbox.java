package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.*;
import jp.junkato.vsketch.tool.*;

public class SaveToDropbox extends FunctionTemplate {

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
		return shapes.size() == 0 && parent != null && parent.getFunction() != null && parent.getFunction().getRetValues().containsKey("brightness");
	}

	@Override
	public String getName() {
		return "Dropbox";
	}

	@Override
	public String getDescription() {
		return "Save current image in Dropbox if it's bright enough.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_134_inbox_in.png";
	}

	public SaveToDropboxFunction newInstance(Stmt stmt) {
		return new SaveToDropboxFunction(stmt, this);
	}

}
