package jp.junkato.vsketch.tool;

import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import jp.junkato.vsketch.shape.Shape;
import jp.junkato.vsketch.interpreter.Stmt;

public class RemoveTool extends Tool {
	private Stmt stmt;
	private Set<Shape> toBeRemoved;

	public RemoveTool() {
		toBeRemoved = new HashSet<Shape>();
	}

	@Override
	public String getName() {
		return "Remove";
	}

	@Override
	public String getDescription() {
		return "Remove the clicked shape.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_016_bin.png";
	}

	@Override
	public void setStmt(Stmt stmt) {
		this.stmt = stmt;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		for (Shape shape : stmt.getShapes()) {
			if (shape.contains(x, y)) {
				toBeRemoved.add(shape);
			}
		}
		for (Shape shape : toBeRemoved) {
			removeShape(shape);
		}
		toBeRemoved.clear();
		repaintView();
	}

}
