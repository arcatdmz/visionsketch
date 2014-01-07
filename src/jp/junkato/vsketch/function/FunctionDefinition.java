package jp.junkato.vsketch.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import jp.junkato.vsketch.ui.VsketchFrame;

public class FunctionDefinition {
	private static String template;
	private static String functionTemplate;
	public final static String defaultIconFileName = "glyphicons_320_filter.png";
	@Element
	private String className;
	@Element(required = false)
	private String stmtCheckCode;
	@Element(required = false)
	private String toolCheckCode;
	@Element
	private String name;
	@Element(required = false)
	private String description;
	@Element
	private String iconFileName;
	@ElementMap(name="retTypes", key="key", keyType=String.class,valueType=Class.class,attribute=true,inline=false)
	private Map<String, Class<?>> retTypes;
	private String functionCode;
	private Class<FunctionTemplate> templateClass;

	public FunctionDefinition() {
		retTypes = new HashMap<String, Class<?>>();
		name = "";
		description = "";
		iconFileName = FunctionDefinition.defaultIconFileName;
		stmtCheckCode = "true";
		toolCheckCode = "true";
		functionCode = "";
	}

	public void save() {
		Serializer serializer = new Persister();
		File file = new File(getFileName());
		try {
			serializer.write(this, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void export() {
		File file;
		BufferedWriter bw = null;
		try {
			file = new File(getFunctionCodeFile());
			bw = new BufferedWriter(new FileWriter(file, false));
			bw.write(getFunctionCode());
			bw.close();
			file = new File(getTemplateCodeFile());
			bw = new BufferedWriter(new FileWriter(file, false));
			bw.write(getTemplateCode());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
			} catch (IOException e) {}
		}
	}

	public static FunctionDefinition load(String className) {
		Serializer serializer = new Persister();
		File file = new File(String.format("%s%s.xml", 
				FunctionCompiler.getDefDir(), className));
		try {
			// Deserialize the definition file.
			FunctionDefinition def = serializer.read(FunctionDefinition.class, file);

			// Load the code file.
			file = new File(def.getFunctionCodeFile());
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(file));
			int ch;
			while ((ch = br.read()) >= 0) sb.append((char) ch);
			br.close();
			def.setFunctionCode(sb.toString());
			return def;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public FunctionTemplate newTemplateInstance() {
		try {
			return templateClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getFileName() {
		return FunctionCompiler.getDefDir() + className + ".xml";
	}

	public String getClassName() {
		return className;
	}

	public String getFunctionClassName() {
		return className + "Function";
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Class<FunctionTemplate> getTemplateClass() {
		return templateClass;
	}

	void setTemplateClass(Class<FunctionTemplate> templateClass) {
		this.templateClass = templateClass;
	}

	public Map<String, Class<?>> getRetTypes() {
		return retTypes;
	}

	public Class<?> getRetType(String key) {
		return retTypes.get(key);
	}

	public void addRetType(String key, Class<?> value) {
		retTypes.put(key, value);
	}

	public boolean removeRetType(String key) {
		return retTypes.remove(key) != null;
	}

	public String getStmtCheckCode() {
		return stmtCheckCode;
	}

	public void setStmtCheckCode(String stmtCheckCode) {
		this.stmtCheckCode = stmtCheckCode;
	}

	public String getToolCheckCode() {
		return toolCheckCode;
	}

	public void setToolCheckCode(String toolCheckCode) {
		this.toolCheckCode = toolCheckCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconFileName() {
		return iconFileName;
	}

	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}

	public String getFunctionCodeFile() {
		return FunctionCompiler.getDefDir() + getClassName() + "Function.java";
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public String getDefaultFunctionCode() {
		if (functionTemplate == null) {
			functionTemplate = read("/functionTemplate.txt");
		}
		return String.format(functionTemplate, getFunctionClassName(), getFunctionClassName());
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getTemplateCodeFile() {
		return FunctionCompiler.getDefDir() + getClassName() + ".java";
	}

	public String getTemplateCode() {
		return getTemplateCode(
				className,
				retTypes,
				toolCheckCode,
				stmtCheckCode,
				name,
				description,
				iconFileName);
	}

	public static String getTemplateCode(String className,
			Map<String, Class<?>> retTypes,
			String toolCheckCode,
			String stmtCheckCode,
			String name,
			String description,
			String iconFileName) {
		String ls = System.getProperty("line.separator");

		if (template == null) {
			template = read("/template.txt");
		}

		String functionClassName = className + "Function";

		String constructor = "";
		if (retTypes.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(ls);
			sb.append("\tpublic ");
			sb.append(className);
			sb.append("() {");
			sb.append(ls);
			for (Entry<String, Class<?>> entry : retTypes.entrySet()) {
				sb.append("\t\tgetRetTypes().put(\"");
				sb.append(entry.getKey().replaceAll("\"", "\\\\\""));
				sb.append("\", ");
				sb.append(entry.getValue().getName());
				sb.append(".class);");
				sb.append(ls);
			}
			sb.append("\t}");
			sb.append(ls);
			constructor = sb.toString();
		}

		return String.format(template,
				className,
				constructor,
				toolCheckCode,
				stmtCheckCode,
				name,
				description,
				iconFileName,
				functionClassName,
				functionClassName);
	}

	private static String read(String resourceName) {
		String ls = System.getProperty("line.separator");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						VsketchFrame.class.getResourceAsStream(resourceName)));
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(ls);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
