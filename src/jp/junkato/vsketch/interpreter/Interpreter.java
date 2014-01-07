package jp.junkato.vsketch.interpreter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.function.Function;
import jp.junkato.vsketch.function.FunctionDefinition;
import jp.junkato.vsketch.function.FunctionParameter;
import jp.junkato.vsketch.function.FunctionTemplate;
import jp.junkato.vsketch.ui.VsketchFrame;

public class Interpreter {
	private ScheduledExecutorService ses;
	private ScheduledFuture<?> future;
	private Set<InterpreterListener> listeners;

	public Interpreter() {
		ses = Executors.newSingleThreadScheduledExecutor();;
		listeners = new HashSet<InterpreterListener>();
	}

	private Input getSource() {
		return VsketchMain.getInstance().getCode().getSource();
	}

	public void reload(FunctionDefinition def) {
		System.out.println("--- Clearing references to " + def.getClassName());
		stop();

		Stmt stmt = VsketchFrame.getInstance().getStmtPanel().getStmt();
		Function currentFunction = stmt.getFunction();
		boolean restoreCurrentFunction = currentFunction != null
				&& currentFunction.getTemplate().getClass().getSimpleName().equals(def.getClassName());
		FunctionTemplate template = VsketchMain.getInstance().getCompiler().getTemplate(def);

		// Remove the button and its correspondent template instance.
		boolean newFunction = template == null;
		if (!newFunction) {
			VsketchMain.getInstance().getCompiler().removeTemplate(template);

			// Clear function preview.
			VsketchFrame.getInstance().getStmtPanel().removeFunction();
	
			// Replace existing functions with dummy instances.
			if (restoreCurrentFunction) {
				// Restore stmt.function
				stmt.setFunction(currentFunction);
				// Clear reference
				currentFunction = null;
			}
			clearFunctionRefs(template);
	
			// Clear the last reference.
			System.out.print("original class hash: ");
			System.out.println(template.getClass().hashCode());
			template = null;
		}

		// Reload the class.
		System.out.println("--- Compiling " + def.getClassName());
		template = VsketchMain.getInstance().getCompiler().reloadTemplateClass(def);

		// Restore function instances.
		System.out.println("--- Restoring references to " + def.getClassName());
		if (restoreCurrentFunction) {
			restoreFunctionRefs(template);
		}
		if (restoreCurrentFunction || newFunction) {
			VsketchFrame.getInstance().getStmtPanel().setFunction(stmt.getFunction());
			stmt.nextFrame();
		}
		System.out.print("new class hash: ");
		System.out.println(template.getClass().hashCode());
	}

	private void clearFunctionRefs(FunctionTemplate templateToBeCleared) {
		LinkedList<Stmt> stmts = new LinkedList<Stmt>(getSource().getChildren());
		while (!stmts.isEmpty()) {
			Stmt stmt = stmts.poll();
			Function f = stmt.getFunction();

			if (f != null && f.getTemplate() == templateToBeCleared) {
				DummyFunction dummy = new DummyFunction(stmt, f.getParameter() == null ?
						null : f.getParameter().clone());
				stmt.setFunction(dummy);
			}

			stmts.addAll(stmt.getChildren());
		}
	}

	private Stmt restoreFunctionRefs(FunctionTemplate template) {
		LinkedList<Stmt> stmts = new LinkedList<Stmt>(getSource().getChildren());
		Stmt root = null;
		while (!stmts.isEmpty()) {
			Stmt stmt = stmts.poll();
			Function f = stmt.getFunction();

			if (f instanceof DummyFunction) {
				Function function = template.newInstance(stmt);
				FunctionParameter parameter = ((DummyFunction) f).parameter;
				if (parameter != null) {
					function.setParameter(parameter);
					function.parameterize(parameter);
				}
				stmt.setFunction(function);
				if (root == null) {
					root = stmt;
				}
			}

			stmts.addAll(stmt.getChildren());
		}
		return root;
	}

	private class DummyFunction extends Function {
		private FunctionParameter parameter;
		public DummyFunction(Stmt stmt, FunctionParameter parameter) {
			super(stmt, null);
			this.parameter = parameter;
		}
		@Override
		public IplImage getImage() {
			return null;
		}
	}

	public void play() {
		play(67);
	}

	public void play(int wait) {
		if (future == null) {
			future = ses.scheduleAtFixedRate(
					new Player(), wait, wait, TimeUnit.MILLISECONDS);
			for (InterpreterListener listener : listeners) {
				listener.onStarted();
			}
		}
	}

	public void stop() {
		if (future != null) {
			future.cancel(true);
			future = null;
			for (InterpreterListener listener : listeners) {
				listener.onStopped();
			}
		}
	}

	public boolean isPlaying() {
		return future != null;
	}

	private class Player implements Runnable {
		public void run() {
			try {
				next();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void next() {
		if (getSource().nextFrame()
				&& VsketchFrame.getInstance().getStmtPanel().isShowing()) {
			VsketchFrame.getInstance().getStmtPanel().repaintView();
		}
		if (getDuration() >= 0 &&
				getCurrentPosition() >= getDuration() - 1) {
			stop();
		}
	}

	public void seek(long frameIndex) {
		getSource().setFrameIndex(frameIndex);
		if (future == null) {
			next();
		}
	}

	public long getCurrentPosition() {
		return getSource().getFrameIndex();
	}

	public long getDuration() {
		return getSource().getFrameCount();
	}

	public void addListener(InterpreterListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener(InterpreterListener listener) {
		return listeners.remove(listener);
	}

}
