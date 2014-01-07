package jp.junkato.vsketch.shape;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.simpleframework.xml.Element;

import jp.junkato.vsketch.ui.stmt.VsketchPreviewPane;

import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Circle implements Shape {
	private static final long serialVersionUID = 59492586164638488L;
	private boolean highlighted;
	@Element
	public Point center = new Point();
	@Element
	public float radius;

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean contains(int x, int y) {
		float rSq = radius * radius;
		return center.distanceSq(x, y) < rSq;
	}

	@Override
	public void fill(IplImage image) {
		cvCircle(image, cvPoint(center.x, center.y), (int)radius, CvScalar.WHITE, 0, 8, 0);
	}

	@Override
	public String toString() {
		return String.format(
				"circle [x:%d, y:%d, r:%.0f]",
				center.x, center.y, radius);
	}

	@Override
	public void paint(Graphics g, VsketchPreviewPane pane) {
		if (highlighted) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.yellow);
		}
		g.drawOval(
				pane.imageToViewX(
						(int) (center.x - radius)),
				pane.imageToViewY(
						(int) (center.y - radius)),
				pane.imageToViewX(
						(int) (radius * 2)),
				pane.imageToViewY(
						(int) (radius * 2)));
	}

	@Override
	public Circle clone() {
		Circle circle = new Circle();
		circle.highlighted = highlighted;
		circle.center = new Point(center);
		circle.radius = radius;
		return circle;
	}
}
