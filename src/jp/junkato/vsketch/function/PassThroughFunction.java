package jp.junkato.vsketch.function;

import jp.junkato.vsketch.interpreter.Stmt;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class PassThroughFunction extends Function {

	public PassThroughFunction(Stmt stmt, FunctionTemplate template) {
		super(stmt, template);
	}

	@Override
	public IplImage getImage() {
		return getParentStmt().getRawOutput();
	}

}
