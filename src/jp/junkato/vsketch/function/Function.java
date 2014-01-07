package jp.junkato.vsketch.function;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.junkato.vsketch.interpreter.Stmt;

public abstract class Function extends MouseAdapter {
	private transient FunctionTemplate template;
	private transient Stmt stmt;
	private transient FunctionParameter parameter;
	private IplImage thumbnail;
	private boolean thumbnailUpToDate;
	private Map<String, Object> retValues;
	private static CvFont font, boldFont;

	public Function(Stmt stmt, FunctionTemplate template) {
		this.stmt = stmt;
		this.template = template;
		retValues = new HashMap<String, Object>();
		font = new CvFont(CV_FONT_HERSHEY_SIMPLEX, 0.5, 1);
		boldFont = new CvFont(CV_FONT_HERSHEY_SIMPLEX, 0.5, 2);
	}

	protected Stmt getStmt() {
		return stmt;
	}

	protected Stmt getParentStmt() {
		return stmt.getParent();
	}

	public Map<String, Object> getRetValues() {
		return retValues;
	}

	public FunctionParameter getParameter() {
		return parameter;
	}

	public void setParameter(FunctionParameter parameter) {
		this.parameter = parameter;
	}

	public final void parameterize() {
		if (parameter != null) parameter.dispose();
		parameter = new FunctionParameter();
		parameter.setImage(getParentStmt().getRawOutput());
		parameter.setShapes(getStmt().getShapes());
		parameterize(parameter);
	}

	public synchronized void parameterize(FunctionParameter parameter) {
	}

	public final void calculate() {
		IplImage sourceImage = getParentStmt().getRawOutput();
		if (sourceImage == null || !checkTemplate()) {
			return;
		}
		calculate(sourceImage);
		thumbnailUpToDate = false;
	}

	public synchronized void calculate(IplImage sourceImage) {
	}

	public abstract IplImage getImage();

	public IplImage getThumbnail() {

		// Check if there is no need for updating the thumbnail.
		if (thumbnailUpToDate) {
			return thumbnail;
		}
		thumbnailUpToDate = true;

		// Check if the current image is null.
		IplImage image = getImage();
		if (image == null) {
			return thumbnail;
		}

		// Check if we need to create a new image object for the thumbnail.
		if (thumbnail == null ||
				thumbnail.depth() != image.depth() ||
				thumbnail.nChannels() != image.nChannels()) {
			thumbnail = IplImage.create(
					Stmt.THUMBNAIL_WIDTH, Stmt.THUMBNAIL_HEIGHT,
					image.depth(), image.nChannels());
		}

		// Calculate the thumbnail.
		calculateThumbnail(image, thumbnail);
		return thumbnail;
	}

	protected void calculateThumbnail(IplImage image, IplImage thumbnail) {
		calculateDefaultThumbnail(image, thumbnail, retValues);
	}

	public static void calculateDefaultThumbnail(IplImage image, IplImage thumbnail, Map<String, Object> retValues) {

		// e.g. 16/9 = 1.777 > 4/3 = 1.333 ... 16*3 > 9*4
		boolean fitX = image.width() * Stmt.THUMBNAIL_HEIGHT > image.height() * Stmt.THUMBNAIL_WIDTH;
		float scale = fitX ?
				Stmt.THUMBNAIL_WIDTH * 1f / image.width() :
					Stmt.THUMBNAIL_HEIGHT * 1f / image.height();

		int x = (int) ((Stmt.THUMBNAIL_WIDTH - image.width() * scale) / 2);
		int y = (int) ((Stmt.THUMBNAIL_HEIGHT - image.height() * scale) / 2);
		cvZero(thumbnail);
		cvSetImageROI(thumbnail, cvRect(x, y, Stmt.THUMBNAIL_WIDTH - x * 2, Stmt.THUMBNAIL_HEIGHT - y * 2));
		cvResize(image, thumbnail);
		cvResetImageROI(thumbnail);

		if (retValues != null) {
			y = 5;
			for (Entry<String, Object> entry : retValues.entrySet()) {
				String message = String.format("%s: %s", entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
				cvPutText(thumbnail, message, cvPoint(5, 10 + y), boldFont, CvScalar.WHITE);
				cvPutText(thumbnail, message, cvPoint(5, 10 + y), font, CvScalar.BLACK);
				y += 20;
			}
		}
	}

	public void dispose() {
		if (parameter != null) {
			parameter.dispose();
			parameter = null;
		}
	}

	public FunctionTemplate getTemplate() {
		return template;
	}

	protected boolean checkTemplate() {
		return stmt == null ? false : template.check(stmt);
	}

	public static String getDataDir() {
		return System.getProperty("user.dir") + File.separator
				+ "function" + File.separator + "data" + File.separator;
	}

	public String getDataFile() {
		return getDataDir()
				+ getClass().getSimpleName() + "." + getStmt().getId();
	}

	public String getCodeFile() {
		return FunctionCompiler.getCodeDir()
				+ getClass().getName().replace('.', File.separatorChar)
				+ ".java";
	}

	public String getCode() {
		String filePath = getCodeFile();
		StringBuilder sb = new StringBuilder();
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return sb.toString();
	}

}
