package jp.junkato.vsketch.interpreter;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.function.Function;
import jp.junkato.vsketch.function.FunctionParameter;
import jp.junkato.vsketch.function.FunctionTemplate;
import jp.junkato.vsketch.shape.Shape;
import jp.junkato.vsketch.tool.ShapeListener;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.AnimatedGlassPane;
import jp.junkato.vsketch.ui.code.VsketchCodePanel;
import jp.junkato.vsketch.ui.code.VsketchCodeSketchPanel;
import jp.junkato.vsketch.ui.stmt.VsketchPreviewPane;
import jp.junkato.vsketch.ui.stmt.VsketchPreviewPanel;
import jp.junkato.vsketch.ui.stmt.VsketchStmtOutputPanel;
import jp.junkato.vsketch.ui.stmt.VsketchStmtPanel;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Stmt implements ShapeListener {
	static final int BORDER_WIDTH = 30;
	public static final int THUMBNAIL_WIDTH = 160;
	public static final int THUMBNAIL_HEIGHT = 120;
	@Element
	private int id;
	private Code code;
	private Stmt parent;
	//@Element(required=false)
	private Function function;
	private Set<Shape> shapes;
	@ElementList(empty=true)
	private Set<Stmt> children;
	@Element
	private int x;
	@Element
	private int y;
	private BufferedImage javaImage;
	private BufferedImage javaThumbnail;
	private VsketchPreviewPane pane;

	Stmt() {
		setShapes(new HashSet<Shape>());
	}

	public Stmt(Code code, Stmt parentStmt) {
 		this();
		this.id = code.currentId ++;
		setCode(code);
		setParent(parentStmt);
		setChildren(new HashSet<Stmt>());
	}

	public int getId() {
		return id;
	}

	public Code getCode() {
		return code;
	}

	void setCode(Code code) {
		this.code = code;
	}

// Code graph-related methods

	public Stmt getParent() {
		return parent;
	}

	void setParent(Stmt parent) {
		this.parent = parent;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		if (this.function != null) {
			try {
				this.function.dispose();
			} catch (Exception e) {
				e.printStackTrace();
				// Ignore the exception.
			}
		}
		this.function = function;
	}

	public Set<Stmt> getChildren() {
		return children;
	}

	private void setChildren(Set<Stmt> children) {
		this.children = children;
	}

	public void setPreviewPane(VsketchPreviewPane pane) {
		this.pane = pane;
	}

	public void edit(int offsetX, int offsetY) {
		VsketchFrame frame = VsketchFrame.getInstance();

		// Set nodes to edit.
		VsketchStmtPanel stmtPanel = frame.getStmtPanel();
		stmtPanel.setStmt(this);

		// Convert the coordinates from the sketch view to the glass pane.
		AnimatedGlassPane glassPane = frame.getVsketchGlassPane();
		VsketchCodePanel codePanel = frame.getCodePanel();
		VsketchCodeSketchPanel sketchPanel = codePanel.getCodeSketchPanel();
		Point center = SwingUtilities.convertPoint(sketchPanel,
				x + THUMBNAIL_WIDTH / 2, y + THUMBNAIL_HEIGHT / 2, glassPane);

		// Start animation.
		glassPane.startAnimation(center, true);
	}

	public void newChild(int x, int y) {
		VsketchFrame frame = VsketchFrame.getInstance();
		Stmt child = new Stmt(code, this);
		child.setX(x - THUMBNAIL_WIDTH / 2);
		child.setY(y - THUMBNAIL_HEIGHT / 2);
		children.add(child);
		code.getStmts().add(child);

		// Set the statement to edit.
		VsketchStmtPanel stmtPanel = frame.getStmtPanel();
		stmtPanel.setStmt(child);

		// Convert the coordinates from the sketch view to the glass pane.
		AnimatedGlassPane glassPane = frame.getVsketchGlassPane();
		VsketchCodePanel codePanel = frame.getCodePanel();
		VsketchCodeSketchPanel sketchPanel = codePanel.getCodeSketchPanel();
		Point center = SwingUtilities.convertPoint(sketchPanel,
				x, y, glassPane);

		// Start animation.
		glassPane.startAnimation(center, true);
	}

// Code view-related methods

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isInside(int x, int y) {
		return x >= this.x && x < this.x + THUMBNAIL_WIDTH &&
				y >= this.y && y < this.y + THUMBNAIL_HEIGHT;
	}

	public boolean isInBorder(int x, int y) {
		return x >= this.x - BORDER_WIDTH && x < this.x + THUMBNAIL_WIDTH + BORDER_WIDTH &&
				y >= this.y - BORDER_WIDTH && y < this.y + THUMBNAIL_HEIGHT + BORDER_WIDTH;
	}

	public boolean isOnBorder(int x, int y) {
		return !isInside(x, y) && isInBorder(x, y);
	}

	public IplImage getRawThumbnail() {
		return function == null ? null : function.getThumbnail();
	}

	public int getThumbnailWidth() {
		return THUMBNAIL_WIDTH;
	}

	public int getThumbnailHeight() {
		return THUMBNAIL_HEIGHT;
	}

	public BufferedImage getThumbnail() {
		IplImage thumbnail = getRawThumbnail();
		if (thumbnail == null) {
			return null;
		}
		javaThumbnail = checkImageCompatibility(thumbnail, javaThumbnail);
		thumbnail.copyTo(javaThumbnail);
		return javaThumbnail;
	}

	public void paintInCodeView(Graphics g, boolean isActive, int mx, int my) {
		g.setColor(Color.black);
		BufferedImage thumbnail = getThumbnail();
		g.drawRect(
				x - BORDER_WIDTH, y - BORDER_WIDTH,
				Stmt.THUMBNAIL_WIDTH + BORDER_WIDTH * 2 - 1,
				Stmt.THUMBNAIL_HEIGHT + BORDER_WIDTH * 2 - 1);
		g.drawRect(
				x - 1, y - 1,
				Stmt.THUMBNAIL_WIDTH + 1,
				Stmt.THUMBNAIL_HEIGHT + 1);
		if (thumbnail != null) {
			g.drawImage(thumbnail, x, y, null);
		}
		if (function != null) {
			g.setFont(VsketchFrame.defaultFont);
			g.drawString(function.getTemplate().getName(), x, y + Stmt.THUMBNAIL_HEIGHT + BORDER_WIDTH / 2 + 5);
		}
	}

// Statement view-related methods

	public Set<Shape> getShapes() {
		return shapes;
	}

	public void setShapes(Set<Shape> shapes) {
		this.shapes = shapes;
	}

	@Override
	public void onShapeAdded(Shape shape) {
		shapes.add(shape);
		updateFunctionsList();
		updateParameter();
	}

	@Override
	public void onShapeRemoved(Shape shape) {
		shapes.remove(shape);
		updateFunctionsList();
		updateParameter();
	}

	@Override
	public void onShapeUpdated(Shape shape) {
		updateParameter();
	}

	private void updateFunctionsList() {
		VsketchStmtOutputPanel panel = VsketchFrame.getInstance().getStmtPanel().getOutputPanel();
		panel.updateFunctionsList();
	}

	private void updateParameter() {
		if (function != null && function.getTemplate().check(this)) {
			function.parameterize();
			function.calculate();
			VsketchStmtOutputPanel panel = VsketchFrame.getInstance().getStmtPanel().getOutputPanel();
			panel.getPane().getPanel().repaint();
			// TODO Prevent frequent repaint for performance tuning?
		}
	}

	public int getWidth() {
		return getRawOutput() == null ? 0 : getRawOutput().width();
	}

	public int getHeight() {
		return getRawOutput() == null ? 0 : getRawOutput().height();
	}

	public IplImage getRawOutput() {
		return function == null ? null : function.getImage();
	}

	public BufferedImage getOutput() {
		IplImage image = getRawOutput();
		if (image == null || image.isNull()) {
			return null;
		}
		javaImage = checkImageCompatibility(image, javaImage);
		image.copyTo(javaImage);
		return javaImage;
	}

	public void paintInStmtView(Graphics g) {
		VsketchPreviewPanel panel = pane.getPanel();
		int width = panel.getWidth(), height = panel.getHeight();

		switch (panel.getFitMode()) {
		case ORIGINAL:
		default:
			g.drawImage(getOutput(), 0, 0, null);
			break;
		case FIT_HORIZONTAL:
			g.drawImage(getOutput(), 0, 0,
					width, getOutput().getHeight() * width / getOutput().getWidth(), null);
			break;
		case FIT_VERTICAL:
			g.drawImage(getOutput(), 0, 0,
					getOutput().getWidth() * height / getOutput().getHeight(), height, null);
			break;
		case FIT_BOTH:
			g.drawImage(getOutput(), 0, 0,
					width, height, null);
			break;
		}
	}

	public void paintShapesInStmtView(Graphics g) {
		((Graphics2D)g).setStroke(VsketchFrame.stroke);
		for (Shape shape : shapes) {
			shape.paint(g, pane);
		}
	}

	/**
	 * Normal statement passes the current result image to the next statement.
	 * @return 
	 */
	public boolean nextFrame() {
		if (function != null) {
			function.calculate();
		}
		for (Stmt child : children) {
			child.nextFrame();
		}
		return true;
	}

	public void dispose() {
		code = null;
		if (function != null) {
			function.dispose();
		}
		shapes.clear();
		children.clear();
	}

	void saveFunction(File dir) {
		if (function == null) {
			return;
		}
		FunctionParameter p = function.getParameter();
		if (p == null) {
			p = new FunctionParameter();
			p.setShapes(new HashSet<Shape>());
		}

		// Serialize shapes.
		Serializer serializer = code.getSerializer();
		File s = new File(dir, String.format("%d-%s.xml",
				id,
				function.getTemplate().getClass().getSimpleName()));
		try {
			serializer.write(p, s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Serialize image.
		if (p != null && p.getImage() != null) {
			cvSaveImage(new File(dir, String.format("%d-%s-image.png",
					id,
					function.getTemplate().getClass().getSimpleName())).getAbsolutePath(),
					p.getImage());
		}
	}

	boolean loadFunction(File dir) {

		// Look for the XML file storing function parameters.
		final Pattern pattern = Pattern.compile(String.format("%d-(.+)(\\.xml|-image\\.png)", id));
		String[] fileNames = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return pattern.matcher(name).matches();
			}
		});
		if (fileNames.length <= 0) {
			return false;
		}
		String fileName = fileNames[0];
		Matcher matcher = pattern.matcher(fileName);
		matcher.matches();
		String className = matcher.group(1);

		// Look for the function template and instantiate the function object.
		FunctionTemplate template = VsketchMain.getInstance().getCompiler().getTemplate(className);
		setFunction(template.newInstance(this));

		// Load the parameter
		FunctionParameter parameter = null;
		try {
			parameter = code.getSerializer().read(FunctionParameter.class, new File(dir,
					String.format("%d-%s.xml", id, className)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		File imageFile = new File(dir,
				String.format("%d-%s-image.png", id, className));
		if (imageFile.exists()) {
			if (parameter == null) {
				parameter = new FunctionParameter();
			}
			parameter.setImage(cvLoadImage(imageFile.getAbsolutePath()));
		}
		if (parameter != null) {
			getFunction().setParameter(parameter);
			getFunction().parameterize(parameter);
			for (Shape shape : parameter.getShapes()) {
				shapes.add(shape.clone());
			}
		}
		getFunction().calculate();
		return true;
	}

	public static BufferedImage checkImageCompatibility(IplImage image, BufferedImage javaImage) {
		if (javaImage == null
				|| javaImage.getWidth() != image.width()
				|| javaImage.getHeight() != image.height()
				|| javaImage.getColorModel().getPixelSize() != image.nChannels() * 8) {
			if (image.nChannels() == 4) {
				javaImage = new BufferedImage(
						image.width(), image.height(), BufferedImage.TYPE_INT_ARGB);
			} else if (image.nChannels() == 3) {
				javaImage = new BufferedImage(
						image.width(), image.height(), BufferedImage.TYPE_3BYTE_BGR);
			} else if (image.nChannels() == 1) {
				javaImage = new BufferedImage(
						image.width(), image.height(), BufferedImage.TYPE_BYTE_GRAY);
			}
		}
		return javaImage;
	}

}