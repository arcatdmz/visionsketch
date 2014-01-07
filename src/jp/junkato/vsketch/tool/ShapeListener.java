package jp.junkato.vsketch.tool;

import jp.junkato.vsketch.shape.Shape;

public interface ShapeListener {
	public void onShapeAdded(Shape shape);
	public void onShapeRemoved(Shape shape);
	public void onShapeUpdated(Shape shape);
}
