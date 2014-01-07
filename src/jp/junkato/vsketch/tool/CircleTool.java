package jp.junkato.vsketch.tool;

import java.awt.event.MouseEvent;

import jp.junkato.vsketch.shape.Circle;
import jp.junkato.vsketch.shape.Shape;

public class CircleTool extends Tool {
	private Circle currentCircle;

	@Override
	public String getName() {
		return "Circle";
	}

	@Override
	public String getDescription() {
		return "Draw a circle.";
	}

	@Override
	public String getIconFileName() {
		return "glyphicons_095_vector_path_circle.png";
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		currentCircle.radius = (currentCircle.center.x - x) * (currentCircle.center.x - x);
		currentCircle.radius += (currentCircle.center.y - y) * (currentCircle.center.y - y);
		currentCircle.radius = (float) Math.sqrt(currentCircle.radius);
		repaintView();
		updateShape(currentCircle);
		currentCircle.setHighlighted(false);
		currentCircle = null;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		currentCircle = null;
		for (Shape s : getStmt().getShapes()) {
			if (!(s instanceof Circle)) {
				continue;
			}
			Circle c  = (Circle) s;
			if (c.center.distance(x, y) < c.radius) {
				currentCircle = c;
			}
		}
		if (currentCircle == null) {
			currentCircle = new Circle();
			addShape(currentCircle);
		}
		currentCircle.center.setLocation(x, y);
		currentCircle.radius = 0.1f;
		updateShape(currentCircle);
		currentCircle.setHighlighted(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		int x = viewToImageX(e.getX());
		int y = viewToImageY(e.getY());
		int radiusSq = (currentCircle.center.x - x) * (currentCircle.center.x - x);
		radiusSq += (currentCircle.center.y - y) * (currentCircle.center.y - y);
		currentCircle.radius = (float) Math.sqrt(radiusSq);
		repaintView();
	}

}
