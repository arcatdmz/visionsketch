package jp.junkato.vsketch.tool;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.shape.Shape;
import jp.junkato.vsketch.ui.ToolButton;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.stmt.VsketchPreviewPane;
import jp.junkato.vsketch.ui.stmt.VsketchStmtInputPanel;
import jp.junkato.vsketch.interpreter.Stmt;

public abstract class Tool extends MouseAdapter {
	private VsketchStmtInputPanel inputPanel;
	private VsketchPreviewPane previewPane;
	private ToolButton button;
	private List<ShapeListener> listeners;
	private Stmt stmt;

	public Tool() {
		button = new ToolButton(this);
	}

	public ToolButton getButton() {
		return button;
	}

	protected Stmt getStmt() {
		return stmt;
	}

	public void setStmt(Stmt stmt) {
		this.stmt = stmt;
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getIconFileName();

	public void dispose() {
	}

	private VsketchStmtInputPanel getInputPanel() {
		if (inputPanel == null) {
			inputPanel = VsketchFrame.getInstance().getStmtPanel().getInputPanel();
		}
		return inputPanel;
	}

	private VsketchPreviewPane getPreviewPane() {
		if (previewPane == null) {
			previewPane = getInputPanel().getPane();
		}
		return previewPane;
	}

	protected int imageToViewX(int x) {
		return getPreviewPane().imageToViewX(x);
	}

	protected int imageToViewY(int y) {
		return getPreviewPane().imageToViewY(y);
	}

	protected int viewToImageX(int x) {
		return getPreviewPane().viewToImageX(x);
	}

	protected int viewToImageY(int y) {
		return getPreviewPane().viewToImageY(y);
	}

	protected void repaintView() {
		if (!VsketchMain.getInstance().getInterpreter().isPlaying()) {
			getPreviewPane().getPanel().repaint();
		}
	}

	public void addShapeListener(ShapeListener callback) {
		if (listeners == null) {
			listeners = new ArrayList<ShapeListener>();
		}
		listeners.add(callback);
	}

	public boolean removeShapeListener(ShapeListener callback) {
		return listeners != null
				&& listeners.remove(callback);
	}

	protected void addShape(Shape shape) {
		if (listeners == null) return;
		for (ShapeListener listener : listeners) {
			listener.onShapeAdded(shape);
		}
	}

	protected void removeShape(Shape shape) {
		if (listeners == null) return;
		for (ShapeListener listener : listeners) {
			listener.onShapeRemoved(shape);
		}
	}

	protected void updateShape(Shape shape) {
		if (listeners == null) return;
		for (ShapeListener listener : listeners) {
			listener.onShapeUpdated(shape);
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}
