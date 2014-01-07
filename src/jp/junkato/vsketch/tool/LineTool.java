package jp.junkato.vsketch.tool;

import java.awt.event.MouseEvent;
import jp.junkato.vsketch.shape.Line;
import jp.junkato.vsketch.shape.Shape;

public class LineTool extends Tool {
	private Line currentLine;
	private boolean isDraggingStartPoint;

	@Override
	public String getName() {
		return "Line";
	}

	@Override
	public String getDescription() {
		return "Select long and narrow area in the video.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_097_vector_path_line.png";
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		if (isDraggingStartPoint) {
			currentLine.sx = x;
			currentLine.sy = y;
		} else {
			currentLine.ex = x;
			currentLine.ey = y;
		}
		repaintView();
		if (currentLine.sx == currentLine.ex && currentLine.sy == currentLine.ey) {
			removeShape(currentLine);
		} else {
			updateShape(currentLine);
		}
		currentLine.setHighlighted(false);
		currentLine = null;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		currentLine = null;
		int distanceSq = 20 * 20;
		for (Shape s : getStmt().getShapes()) {
			if (!(s instanceof Line)) {
				continue;
			}
			Line l = (Line) s;
			int dSq;
			dSq = (x - l.sx) * (x - l.sx);
			dSq += (y - l.sy) * (y - l.sy);
			if (dSq < distanceSq) {
				isDraggingStartPoint = true;
				currentLine = l;
				distanceSq = dSq;
			}
			dSq = (x - l.ex) * (x - l.ex);
			dSq += (y - l.ey) * (y - l.ey);
			if (dSq < distanceSq) {
				isDraggingStartPoint = false;
				currentLine = l;
				distanceSq = dSq;
			}
		}
		if (currentLine == null) {
			currentLine = new Line();
			currentLine.sx = currentLine.ex = x;
			currentLine.sy = currentLine.ey = y;
			addShape(currentLine);
			isDraggingStartPoint = false;
		} else {
			if (isDraggingStartPoint) {
				currentLine.sx = x;
				currentLine.sy = y;
			} else {
				currentLine.ex = x;
				currentLine.ey = y;
			}
		}
		currentLine.setHighlighted(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		if (isDraggingStartPoint) {
			currentLine.sx = x;
			currentLine.sy = y;
		} else {
			currentLine.ex = x;
			currentLine.ey = y;
		}
		repaintView();
	}

}
