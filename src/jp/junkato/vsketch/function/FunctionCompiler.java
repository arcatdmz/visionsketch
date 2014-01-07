package jp.junkato.vsketch.function;

import java.awt.Insets;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import jp.junkato.vsketch.ui.VsketchFrame;

import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

public class FunctionCompiler {

	/**
	 * jp.junkato.vsketch.function
	 */
	public static final String packageName = Function.class.getPackage().getName();

	private ArrayList<FunctionDefinition> definitions;

	public FunctionCompiler() {
		definitions = new ArrayList<FunctionDefinition>();

		System.out.println("--- Compiling all image processing components.");
		loadDefinitions();
		compileDefinitions();

		loadTemplateClasses();
		System.out.println("--- Loaded compiled classes.");
	}

	private void loadDefinitions() {
		String dir = getDefDir();
		File[] files = new File(dir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		definitions = new ArrayList<FunctionDefinition>();
		for (File file : files) {
			String name = file.getName();
			String className = name.substring(0, name.length() - ".xml".length());
			FunctionDefinition def = FunctionDefinition.load(className);
			definitions.add(def);
		}
	}

	private void compileDefinitions() {
		for (FunctionDefinition def : definitions) {
			def.export();
		}
		compile(getCodeDir());
	}

	public FunctionDefinition getDefinition(String className) {
		for (FunctionDefinition def : definitions) {
			if (def.getClassName().equals(className)) {
				return def;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void loadTemplateClasses() {
		for (FunctionDefinition def : definitions) {
			System.out.println(def.getClassName());
			Class<?> templateClass = loadClass(packageName + "." + def.getClassName());
			def.setTemplateClass((Class<FunctionTemplate>) templateClass);
		}
	}

	@SuppressWarnings("unchecked")
	public FunctionTemplate reloadTemplateClass(FunctionDefinition def) {

		// Unload the class.
		if (def == null) {
			return null;
		}
		def.setTemplateClass(null);
		System.gc();

		// Compile.
		compile(
				def.getTemplateCodeFile(),
				def.getFunctionCodeFile());

		// Reload.
		Class<FunctionTemplate> templateClass = (Class<FunctionTemplate>) loadClass(
				packageName + "." + def.getClassName());
		if (FunctionTemplate.class.isAssignableFrom(templateClass)) {
			def.setTemplateClass((Class<FunctionTemplate>) templateClass);
		}
		if (!definitions.contains(def)) {
			definitions.add(def);
		}

		// Create an instance.
		FunctionTemplate functionTemplate = def.newTemplateInstance();
		addTemplate(functionTemplate);
		return functionTemplate;
	}

	/**
	 * @param className
	 * @see <a href="http://www.nminoru.jp/~nminoru/java/class_unloading.html">Java のクラスアンロード (Class Unloading)</a>
	 */
	private Class<?> loadClass(String className) {
		URLClassLoader loader = null;
		try {
			URL classPath = new File(getOutputClassDir()).toURI().toURL();
			loader = new URLClassLoader(new URL[]{classPath});
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(loader);
			Class<?> cls = loader.loadClass(className);

			// This loads dependent classes, such as function implementation.
			cls.newInstance();

			Thread.currentThread().setContextClassLoader(cl);
			return cls;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Do not load more classes with this loader.
			if (loader != null)
				try {
					loader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	/**
	 * @param file
	 * @return
	 * @see <a href="http://help.eclipse.org/indigo/?topic=/org.eclipse.jdt.doc.isv/guide/jdt_api_compile.htm">Eclipse documentation / Compiling Java code</a>
	 */
	public boolean compile(String... files) {
		final StringBuffer errorBuffer = new StringBuffer();
		Writer internalWriter = new Writer() {
			public void write(char[] buf, int off, int len) {
				errorBuffer.append(buf, off, len);
			}

			public void flush() {
			}

			public void close() {
			}
		};
		PrintWriter writer = new PrintWriter(internalWriter);
		PrintWriter outWriter = new PrintWriter(System.out);

		String[] args = new String[6 + files.length];
		args[0] = "-classpath"; args[1] = getClassPath();
		args[2] = "-1.6";
		args[3] = "-nowarn";
		args[4] = "-d"; args[5] = getOutputClassDir();
		for (int i = 0; i < files.length; i ++) args[6 + i] = files[i];
		boolean result = BatchCompiler.compile(args, outWriter, writer, null);

		writer.flush();
		writer.close();

		if (!result) {
			JOptionPane.showMessageDialog(null, errorBuffer.toString());
		}
		System.err.println(errorBuffer.toString());
		return result;
	}

	public static String getDataDir() {
		return System.getProperty("user.dir") + File.separator
				+ "data" + File.separator;
	}

	public static String getCodeDir() {
		return System.getProperty("user.dir") + File.separator
				+ "function" + File.separator + "src" + File.separator;
	}

	public static String getDefDir() {
		return getCodeDir() + packageName.replace('.', File.separatorChar) + File.separator;
	}

	public static String getClassPath() {
		String libDir = getLibraryDir();
		String s = File.separator;
		StringBuilder sb = new StringBuilder();
		sb.append(getIdeClassDir());
		sb.append(File.pathSeparator);
		sb.append(libDir);
		sb.append("javacv");
		sb.append(s);
		sb.append("javacv.jar");
		return sb.toString();
	}

	public static String getLibraryDir() {
		return System.getProperty("user.dir") + File.separator + "lib"
				+ File.separator;
	}

	public static String getIdeClassDir() {
		return System.getProperty("user.dir") + File.separator + "bin"
				+ File.separator;
	}

	public static String getOutputClassDir() {
		return System.getProperty("user.dir") + File.separator
				+ "function" + File.separator + "bin" + File.separator;
	}

	List<FunctionTemplate> templates;

	public List<FunctionTemplate> getTemplates() {
		if (templates == null) {
			reloadTemplates();
		}
		return templates;
	}

	public void reloadTemplates() {
		clearTemplates();
		for (FunctionDefinition def : definitions) {
			addTemplate(def.newTemplateInstance());
		}
	}

	private void clearTemplates() {
		templates = new ArrayList<FunctionTemplate>();
	}

	public void addTemplate(FunctionTemplate functionTemplate) {
		functionTemplate.getButton().setMargin(new Insets(10, 5, 10, 5));
		templates.add(functionTemplate);
		VsketchFrame.getInstance().getStmtPanel().getOutputPanel().addFunctionToList(functionTemplate);
	}

	public void removeTemplate(FunctionTemplate functionTemplate) {
		templates.remove(functionTemplate);
		VsketchFrame.getInstance().getStmtPanel().getOutputPanel().removeFunctionFromList(functionTemplate);
	}

	public FunctionTemplate getTemplate(FunctionDefinition def) {
		for (FunctionTemplate functionTemplate : getTemplates()) {
			if (functionTemplate.getClass().getSimpleName().equals(def.getClassName())) {
				return functionTemplate;
			}
		}
		return null;
	}

	public FunctionTemplate getTemplate(String className) {
		FunctionDefinition def = getDefinition(className);
		if (def == null) return null;
		return getTemplate(def);
	}

}
