package jp.junkato.vsketch.interpreter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;

public class Code {
	@Element
	int currentId;
	@Element
	private Input input;
	private Set<Stmt> stmts;
	private Serializer serializer;

	public Code() {
		stmts = new HashSet<Stmt>();
	}

	public int getCurrentId() {
		return currentId;
	}

	public Set<Stmt> getStmts() {
		return stmts;
	}

	public void remove(Stmt stmt) {
		if (stmt.getParent() != null) {
			stmt.getParent().getChildren().remove(stmt);
		}
		stmts.remove(stmt);
		LinkedList<Stmt> queue = new LinkedList<Stmt>();
		queue.add(stmt);
		while (!queue.isEmpty()) {
			Stmt s = queue.poll();
			queue.addAll(s.getChildren());
			stmts.remove(s);
			s.dispose();
		}
	}

	public Input getSource() {
		return input;
	}

	public void setSource(String identifier) {
		if (input == null) {
			input = Input.newInstance(this, identifier);
			input.setX(10 + Stmt.BORDER_WIDTH);
			input.setY(10 + Stmt.BORDER_WIDTH);
			stmts.add(input);
		} else {
			input.setIdentifier(identifier);
		}
	}

	public void paint(Graphics g, Stmt activeStmt, int mx, int my) {
		g.setColor(Color.black);
		for (Stmt stmt : stmts) {
			if (stmt.getParent() != null) {
				g.drawLine(
						stmt.getX() + Stmt.THUMBNAIL_WIDTH / 2,
						stmt.getY() + Stmt.THUMBNAIL_HEIGHT / 2,
						stmt.getParent().getX() + Stmt.THUMBNAIL_WIDTH / 2,
						stmt.getParent().getY() + Stmt.THUMBNAIL_HEIGHT / 2);
				int x = (stmt.getX() + stmt.getParent().getX() + Stmt.THUMBNAIL_WIDTH) / 2;
				int y = (stmt.getY() + stmt.getParent().getY() + Stmt.THUMBNAIL_HEIGHT) / 2;
				double theta = Math.atan2(
						stmt.getY() - stmt.getParent().getY(),
						stmt.getX() - stmt.getParent().getX());
				g.drawLine(x, y,
						x + (int)(Math.cos(theta + 2.5) * 15),
						y + (int)(Math.sin(theta + 2.5) * 15));
				g.drawLine(x, y,
						x + (int)(Math.cos(theta - 2.5) * 15),
						y + (int)(Math.sin(theta - 2.5) * 15));
			}
		}
		for (Stmt stmt : stmts) {
			stmt.paintInCodeView(g, stmt == activeStmt, mx, my);
		}
	}

	public void dispose() {
		for (Stmt stmt : stmts) {
			stmt.dispose();
		}
	}

	Serializer getSerializer() {
		if (serializer != null) {
			return serializer;
		}
		final Transform<Point> transform = new Transform<Point>() {
			@Override
			public Point read(String text) throws Exception {
				String xs = text.substring(0, text.indexOf(',')).trim();
				String ys = text.substring(text.indexOf(',')+1).trim();
				return new Point(Integer.valueOf(xs), Integer.valueOf(ys));
			}
			@Override
			public String write(Point p) throws Exception {
				return p.x + ", " + p.y;
			}
		};
		serializer = new Persister(new Matcher() {
			@SuppressWarnings("rawtypes")
			@Override
			public Transform match(Class cls) throws Exception {
				if (cls == Point.class) {
					return transform;
				}
				return null;
			}
		});
		return serializer;
	}

	public boolean save(File dir) {
		if (dir.exists()) {
			return false;
		}
		dir.mkdir();

		// Save tree and UI info
		Serializer serializer = getSerializer();
		File file = new File(dir, "tree.xml");
		try {
			serializer.write(this, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Save function and parameters info
		LinkedList<Stmt> stmts = new LinkedList<Stmt>(input.getChildren());
		while (!stmts.isEmpty()) {
			Stmt stmt = stmts.poll();
			stmt.saveFunction(dir);
			stmts.addAll(stmt.getChildren());
		}
		return true;
	}

	public static Code load(File dir) {
		Serializer serializer = new Persister();
		File file = new File(dir, "tree.xml");
		try {
			Code code = serializer.read(Code.class, file);
			code.input.setCode(code);
			code.input.reload();
			code.stmts.add(code.input);
			Queue<Stmt> queue = new LinkedList<Stmt>();
			queue.add(code.input);
			while (!queue.isEmpty()) {
				Stmt parent = queue.poll();
				for (Stmt stmt : parent.getChildren()) {
					stmt.setCode(code);
					stmt.setParent(parent);
					stmt.loadFunction(dir);
					code.stmts.add(stmt);
					queue.add(stmt);
				}
			}
			return code;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
