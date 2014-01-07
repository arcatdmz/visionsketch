package jp.junkato.vsketch.function;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.tool.Tool;
import jp.junkato.vsketch.ui.FunctionButton;

public abstract class FunctionTemplate {
	private FunctionButton button;
	private Map<String, Class<?>> retTypes;

	public FunctionTemplate() {
		button = new FunctionButton(this);
		retTypes = new HashMap<String, Class<?>>();
	}

	public FunctionButton getButton() {
		return button;
	}

	public Map<String, Class<?>> getRetTypes() {
		return retTypes;
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getIconFileName();

	public abstract Function newInstance(Stmt stmt);

	public boolean isCapableOf(Tool tool, Stmt stmt) {
		return true;
	}

	public boolean check(Stmt stmt) {
		return true;
	}

	public String getCodeFile() {
		return getCodeFile(getClass().getName());
	}

	public static String getCodeFile(String className) {
		return FunctionCompiler.getCodeDir()
				+ className.replace('.', File.separatorChar)
				+ ".java";
	}

}
