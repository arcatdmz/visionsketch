package jp.junkato.vsketch.shape;

import static com.googlecode.javacv.cpp.opencv_core.cvFillConvexPoly;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.simpleframework.xml.Element;

import jp.junkato.vsketch.ui.stmt.VsketchPreviewPane;
import jp.junkato.vsketch.utils.VsketchUtils;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Line implements Shape {
	private static final long serialVersionUID = -5278113392343360501L;
	private boolean highlighted;
	@Element
	public int sx, sy, ex, ey;
	public float width = 10;

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean contains(int x, int y) {
		Polygon poly = new Polygon();
		double rotation = Math.atan2(ey - sy, ex - sx);
		double width = this.width;
		double height = VsketchUtils.distance(sx, sy, ex, ey);
		double hx = (float) (Math.cos(rotation) * height);
		double hy = (float) (Math.sin(rotation) * height);
		double wx = (float) (Math.sin(rotation) * width / 2);
		double wy = (float) (Math.cos(rotation) * width / 2);
		poly.addPoint((int) (sx - wx), (int) (sy + wy));
		poly.addPoint((int) (sx + wx), (int) (sy - wy));
		poly.addPoint((int) (sx + wx + hx), (int) (sy - wy + hy));
		poly.addPoint((int) (sx - wx + hx), (int) (sy + wy + hy));
		return poly.contains(x, y);
	}

	public CvPoint2D32f asCvFloatPoints() {
		double rotation = Math.atan2(ey - sy, ex - sx);
		float height = (float) VsketchUtils.distance(sx, sy, ex, ey);
		float hx = (float) (Math.cos(rotation) * height);
		float hy = (float) (Math.sin(rotation) * height);
		float wx = (float) (Math.sin(rotation) * width / 2);
		float wy = (float) (Math.cos(rotation) * width / 2);
		CvPoint2D32f srcPoints = new CvPoint2D32f(4);
		srcPoints.position(0).x(sx - wx);
		srcPoints.position(0).y(sy + wy);
		srcPoints.position(1).x(sx + wx);
		srcPoints.position(1).y(sy - wy);
		srcPoints.position(2).x(sx + wx + hx);
		srcPoints.position(2).y(sy - wy + hy);
		srcPoints.position(3).x(sx - wx + hx);
		srcPoints.position(3).y(sy + wy + hy);
		srcPoints.position(0);
		return srcPoints;
	}

	public CvPoint asCvPoints() {
		double rotation = Math.atan2(ey - sy, ex - sx);
		float height = (float) VsketchUtils.distance(sx, sy, ex, ey);
		float hx = (float) (Math.cos(rotation) * height);
		float hy = (float) (Math.sin(rotation) * height);
		float wx = (float) (Math.sin(rotation) * width / 2);
		float wy = (float) (Math.cos(rotation) * width / 2);
		CvPoint srcPoints = new CvPoint(4);
		srcPoints.position(0).x((int)(sx - wx));
		srcPoints.position(0).y((int)(sy + wy));
		srcPoints.position(1).x((int)(sx + wx));
		srcPoints.position(1).y((int)(sy - wy));
		srcPoints.position(2).x((int)(sx + wx + hx));
		srcPoints.position(2).y((int)(sy - wy + hy));
		srcPoints.position(3).x((int)(sx - wx + hx));
		srcPoints.position(3).y((int)(sy + wy + hy));
		srcPoints.position(0);
		return srcPoints;
	}

	@Override
	public void fill(IplImage image) {
		CvPoint points = asCvPoints();
		cvFillConvexPoly(image, points, 4, CvScalar.WHITE, 8, 0);
		points.deallocate();
	}

	@Override
	public String toString() {
		return String.format(
				"line [x:%d, y:%d] [x:%d, y:%d]",
				sx, sy,
				ex, ey);
	}

	@Override
	public void paint(Graphics g, VsketchPreviewPane pane) {
		g.setColor(highlighted ? Color.red : Color.yellow);

		double rotation = Math.atan2(ey - sy, ex - sx);
		double height = VsketchUtils.distance(sx, sy, ex, ey);
		double hx = (float) (Math.cos(rotation) * height);
		double hy = (float) (Math.sin(rotation) * height);
		double wx = (float) (Math.sin(rotation) * width / 2);
		double wy = (float) (Math.cos(rotation) * width / 2);

		Point[] points = new Point[] {
				new Point((int) (sx - wx), (int) (sy + wy)),
				new Point((int) (sx + wx), (int) (sy - wy)),
				new Point((int) (sx + wx + hx), (int) (sy - wy + hy)),
				new Point((int) (sx - wx + hx), (int) (sy + wy + hy))
		};
		for (int i = 0; i < points.length; i ++) {
			Point sp = points[i], ep = points[(i + 1) % points.length];
			g.drawLine(
					pane.imageToViewX(sp.x),
					pane.imageToViewY(sp.y),
					pane.imageToViewX(ep.x),
					pane.imageToViewY(ep.y));
		}
	}

	@Override
	public Line clone() {
		Line line = new Line();
		line.highlighted = highlighted;
		line.sx = sx;
		line.sy = sy;
		line.ex = ex;
		line.ey = ey;
		line.width = width;
		return line;
	}

}
