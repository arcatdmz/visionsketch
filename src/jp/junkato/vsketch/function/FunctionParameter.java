package jp.junkato.vsketch.function;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.xml.ElementList;

import jp.junkato.vsketch.shape.Shape;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FunctionParameter implements Cloneable {
	private IplImage image;
	@ElementList(required=false)
	private Set<Shape> shapes;
	public FunctionParameter() {
	}
	public IplImage getImage() {
		return image;
	}
	public void setImage(IplImage image) {
		this.image = image == null ? null : image.clone();
	}
	public Set<Shape> getShapes() {
		return shapes;
	}
	public void setShapes(Set<Shape> shapes) {
		if (shapes == null) {
			return;
		}
		this.shapes = new HashSet<Shape>();
		for (Shape shape : shapes) {
			this.shapes.add(shape.clone());
		}
	}
	public void dispose() {
		if (image != null) {
			image.release();
			image = null;
		}
		if (shapes != null) {
			shapes.clear();
			shapes = null;
		}
	}
	public FunctionParameter clone() {
		FunctionParameter parameter = new FunctionParameter();
		parameter.image = this.image == null ? null : this.image.clone();
		if (this.shapes == null) {
			parameter.shapes = null;
		} else {
			parameter.shapes = new HashSet<Shape>();
			for (Shape shape : this.shapes) {
				parameter.shapes.add(shape.clone());
			}
		}
		return parameter;
	}
	@Override
	public String toString() {
		return (image == null ? "" : "image:" + image + ", ") + "shapes:" + shapes;
	}
}
