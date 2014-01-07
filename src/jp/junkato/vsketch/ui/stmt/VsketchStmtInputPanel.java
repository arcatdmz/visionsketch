package jp.junkato.vsketch.ui.stmt;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import jp.junkato.vsketch.function.Function;
import jp.junkato.vsketch.function.FunctionTemplate;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.shape.Shape;
import jp.junkato.vsketch.tool.CircleTool;
import jp.junkato.vsketch.tool.LineTool;
import jp.junkato.vsketch.tool.Painter;
import jp.junkato.vsketch.tool.ScrollTool;
import jp.junkato.vsketch.tool.RectangleTool;
import jp.junkato.vsketch.tool.RemoveTool;
import jp.junkato.vsketch.tool.ShapeListener;
import jp.junkato.vsketch.tool.Tool;
import jp.junkato.vsketch.ui.VsketchFrame;

public class VsketchStmtInputPanel extends JPanel implements ShapeListener {
	private static final long serialVersionUID = 6399615540198759780L;
	private VsketchPreviewPane pane;
	private Tool[] tools;
	private Tool tool;
	private Stmt stmt;

	/**
	 * Create the panel.
	 */
	public VsketchStmtInputPanel() {
		tools = new Tool[] {
				new ScrollTool(),
				new CircleTool(),
				new LineTool(),
				new RectangleTool(),
				new RemoveTool()
		};

		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(boxLayout);
		JScrollPane toolPane = new JScrollPane(panel);
		toolPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(toolPane, BorderLayout.NORTH);
		
		pane = new VsketchPreviewPane();
		pane.getPanel().addPainter(new ShapesPainter());
		add(pane);

		for (Tool tool : tools) {
			panel.add(tool.getButton());
			tool.getButton().setMargin(new Insets(10, 5, 10, 5));
			tool.addShapeListener(this);
		}
	}

	public VsketchPreviewPane getPane() {
		return pane;
	}

	void setStmt(Stmt stmt) {
		this.stmt = stmt;
		pane.setStmt(stmt.getParent());
		for (Tool tool : tools) {
			tool.setStmt(stmt);
		}
		updateToolsList();
	}

	public void setTool(Tool tool) {
		if (this.tool != null) {
			pane.getPanel().removeMouseListener(this.tool);
			pane.getPanel().removeMouseMotionListener(this.tool);
		}
		if (tool != null) {
			pane.getPanel().addMouseListener(tool);
			pane.getPanel().addMouseMotionListener(tool);
		}
		this.tool = tool;
	}

	void updateToolsList() {
		Function function = stmt.getFunction();
		FunctionTemplate functionTemplate = function == null ?
				null : stmt.getFunction().getTemplate();
		for (Tool tool : tools) {
			tool.getButton().setVisible(functionTemplate == null ||
					functionTemplate.isCapableOf(tool, stmt));
		}
	}

	@Override
	public void onShapeAdded(Shape shape) {
		stmt.onShapeAdded(shape);
	}

	@Override
	public void onShapeRemoved(Shape shape) {
		stmt.onShapeRemoved(shape);
	}

	@Override
	public void onShapeUpdated(Shape shape) {
		stmt.onShapeUpdated(shape);
	}

	private class ShapesPainter implements Painter {
		public void paint(Graphics g) {
			if (stmt == null) {
				return;
			}
			((Graphics2D)g).setStroke(VsketchFrame.stroke);
			for (Shape shape : stmt.getShapes()) {
				shape.paint(g, pane);
			}
		}
	}

}
