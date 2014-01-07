package jp.junkato.vsketch.tool;

import java.awt.Point;
import java.awt.event.MouseEvent;

import jp.junkato.vsketch.shape.Rectangle;
import jp.junkato.vsketch.shape.Shape;

public class RectangleTool extends Tool {
	private Rectangle currentRectangle;

	@Override
	public String getName() {
		return "Rectangle";
	}

	@Override
	public String getDescription() {
		return "Select a rectangle.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_094_vector_path_square.png";
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		int draggingIndex = currentRectangle.getDraggingIndex();
		currentRectangle.corners[draggingIndex].setLocation(x, y);
		repaintView();
		updateShape(currentRectangle);
		if (draggingIndex + 1 >= currentRectangle.corners.length ||
				currentRectangle.corners[draggingIndex + 1] != null) {
			currentRectangle.setDraggingIndex(-1);
			currentRectangle = null;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		int draggingIndex = -1;

		if (currentRectangle == null) {

			// Find the closest corner of existing rectangles.
			int distanceSq = 30 * 30;
			for (Shape s : getStmt().getShapes()) {
				if (!(s instanceof Rectangle)) {
					continue;
				}
				Rectangle r = (Rectangle) s;
				for (int i = 0; i < r.corners.length; i ++) {
					Point p = r.corners[i];
					if (p != null) {
						int dSq;
						dSq = (x - p.x) * (x - p.x);
						dSq += (y - p.y) * (y - p.y);
						if (dSq < distanceSq) {
							currentRectangle = r;
							draggingIndex = i;
							distanceSq = dSq;
						}
					}
				}
			}

			// Create a new rectangle.
			if (currentRectangle == null) {
				currentRectangle = new Rectangle();
				draggingIndex = 0;
				addShape(currentRectangle);
			}

		} else {

			// Define a new corner in a new rectangle.
			for (int i = 0; i < currentRectangle.corners.length; i ++) {
				Point p = currentRectangle.corners[i];
				if (p == null) {
					draggingIndex = i;
					break;
				}
			}
		}

		// Set a corner position of the rectangle.
		currentRectangle.setDraggingIndex(draggingIndex);
		if (draggingIndex >= 0 || draggingIndex < 4) {
			if (currentRectangle.corners[draggingIndex] == null) {
				currentRectangle.corners[draggingIndex] = new Point();
			}
			currentRectangle.corners[draggingIndex].setLocation(x, y);
		}
		updateShape(currentRectangle);
		currentRectangle.setHighlighted(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int index = currentRectangle.getDraggingIndex();
		if (index >= 0 || index < 4) {
			int x = viewToImageX(e.getX());
			int y = viewToImageY(e.getY());
			currentRectangle.corners[index].setLocation(x, y);
		}
		repaintView();
	}

}
