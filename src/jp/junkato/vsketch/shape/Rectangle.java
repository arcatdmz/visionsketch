package jp.junkato.vsketch.shape;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.simpleframework.xml.ElementArray;

import jp.junkato.vsketch.ui.stmt.VsketchPreviewPane;

import static com.googlecode.javacv.cpp.opencv_core.cvFillConvexPoly;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Rectangle implements Shape {
	private static final long serialVersionUID = -7828084175844537493L;
	private int draggingIndex;
	private boolean highlighted;
	@ElementArray
	public Point[] corners = new Point[4];

	public int getDraggingIndex() {
		return draggingIndex;
	}

	public void setDraggingIndex(int draggingIndex) {
		this.draggingIndex = draggingIndex;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	@Override
	public boolean contains(int x, int y) {
		Polygon poly = new Polygon();
		for (Point point : corners) {
			if (point == null) {
				return false;
			}
			poly.addPoint(point.x, point.y);
		}
		return poly.contains(x, y);
	}

	public CvPoint2D32f asCvFloatPoints() {
		CvPoint2D32f cvPoints = new CvPoint2D32f(corners.length);
		for (int i = 0; i < corners.length; i ++) {
			if (corners[i] == null) {
				cvPoints.deallocate();
				return null;
			}
			cvPoints.position(i).x(corners[i].x);
			cvPoints.position(i).y(corners[i].y);
		}
		cvPoints.position(0);
		return cvPoints;
	}

	public CvPoint asCvPoints() {
		CvPoint cvPoints = new CvPoint(corners.length);
		for (int i = 0; i < corners.length; i ++) {
			if (corners[i] == null) {
				cvPoints.deallocate();
				return null;
			}
			cvPoints.position(i).x(corners[i].x);
			cvPoints.position(i).y(corners[i].y);
		}
		cvPoints.position(0);
		return cvPoints;
	}

	@Override
	public void fill(IplImage image) {
		CvPoint points = asCvPoints();
		if (points != null) {
			cvFillConvexPoly(image, points, 4, CvScalar.WHITE, 8, 0);
			points.deallocate();
		}
	}

	@Override
	public String toString() {
		return String.format(
				"rectangle [x:%d, y:%d] [x:%d, y:%d] [x:%d, y:%d] [x:%d, y:%d]",
				getX(corners[0]), getY(corners[0]),
				getX(corners[1]), getY(corners[1]),
				getX(corners[2]), getY(corners[2]),
				getX(corners[3]), getY(corners[3]));
	}

	private int getX(Point p) {
		return p == null ? -1 : p.x;
	}

	private int getY(Point p) {
		return p == null ? -1 : p.y;
	}

	@Override
	public void paint(Graphics g, VsketchPreviewPane pane) {
		for (int i = 0; i < corners.length; i ++) {
			g.setColor(Color.yellow);
			Point sp = corners[i];
			Point ep = corners[(i + 1) % corners.length];
			if (sp != null && ep != null) {
				g.drawLine(
						pane.imageToViewX(sp.x),
						pane.imageToViewY(sp.y),
						pane.imageToViewX(ep.x),
						pane.imageToViewY(ep.y));
			}
			if (i == getDraggingIndex() && isHighlighted()) {
				g.setColor(Color.red);
			}
			if (sp != null) {
				g.drawOval(
						pane.imageToViewX(sp.x) - 5,
						pane.imageToViewY(sp.y) - 5,
						10, 10);
			}
		}
	}

	@Override
	public Rectangle clone() {
		Rectangle rectangle = new Rectangle();
		rectangle.draggingIndex = draggingIndex;
		rectangle.highlighted = highlighted;
		for (int i = 0; i < corners.length; i ++) {
			rectangle.corners[i] = corners[i] == null ?
					null : new Point(corners[i]);
		}
		return rectangle;
	}

}
