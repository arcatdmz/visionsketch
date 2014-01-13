package jp.junkato.vsketch;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.BindException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import jp.junkato.vsketch.function.FunctionCompiler;
import jp.junkato.vsketch.interpreter.Code;
import jp.junkato.vsketch.interpreter.Interpreter;
import jp.junkato.vsketch.interpreter.input.Camera;
import jp.junkato.vsketch.server.SimpleHttpServer;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.utils.VsketchUtils;

public class VsketchMain {

	private static final String TITLE = "Visionsketch";

	private static VsketchMain instance;

	private VsketchFrame frame;
	private ConfigFile configFile;
	private FunctionCompiler compiler;
	private Interpreter interpreter;
	private Code code;
	private SimpleHttpServer server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		VsketchMain.getInstance().initialize(args);
	}

	public static VsketchMain getInstance() {
		if (instance == null) {
			instance = new VsketchMain();
		}
		return instance;
	}

	private VsketchMain() {}

	private void initialize(final String args[]) {
		if (VsketchUtils.isMac()) {
			// http://alvinalexander.com/apple/mac/java-mac-native-look/Putting_your_application_na.shtml
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					TITLE);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				initGUI();
				initComponents();
				initServer();
				loadConfig(args);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * Setup GUI components.
	 */
	private void initGUI() {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame = VsketchFrame.getInstance();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				VsketchMain.this.dispose();
			}
		});
		frame.setTitle(TITLE);
		if (VsketchUtils.isMac()) {
			enableFullScreenMode(frame);
		}
	}

	public static void enableFullScreenMode(Window window) {
		String className = "com.apple.eawt.FullScreenUtilities";
		String methodName = "setWindowCanFullScreen";

		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getMethod(methodName, new Class<?>[] {
					Window.class, boolean.class });
			method.invoke(null, window, true);
		} catch (Throwable t) {
			System.err.println("Full screen mode is not supported");
			t.printStackTrace();
		}
	}
   
	/**
	 * Compile predefined image processing components.
	 */
	private void initComponents() {
		compiler = new FunctionCompiler();
	}

	private void initServer() {
		initServer(8080);
	}

	private void initServer(int port) {
		try {
			server = new SimpleHttpServer(port);
			server.start();
		} catch(BindException e) {
			initServer(port + 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load initial configuraton.
	 * @param args
	 */
	private void loadConfig(String[] args) {

		// Set default code object.
		Code code = new Code();
		setCode(code);

		// Load config.
		initConfig();
		configFile.load();
		if (code.getSource() == null) {
			// Use webcam by default.
			code.setSource(Camera.createIdentifier());
		}

		// Handle arguments.
		if (args.length <= 0) {
			return;
		}
		boolean autoPlay = false;
		int wait = -1;
		for (int i = 0; i < args.length; i ++) {
			String arg = args[i];
			if ("-wait".equalsIgnoreCase(arg)
					 && i + 1 < args.length) {
				try {
					arg = args[++ i];
					wait = Integer.valueOf(arg);
				}
				catch (NumberFormatException nfe) {}
				continue;
			}
			if ("-code".equalsIgnoreCase(arg)
					&& i + 1 < args.length) {
				File dir = new File(args[++ i]);
				Code c = Code.load(dir);
				if (c != null) {
					setCode(c);
				}
				continue;
			}
			autoPlay |= "-autoplay".equalsIgnoreCase(arg);
		}
		if (autoPlay) {
			if (wait >= 0) {
				interpreter.play(wait);
			} else {
				interpreter.play();
			}
		}
	}

	/**
	 * Setup ConfigFile object.
	 */
	private void initConfig() {
		String userDir = System.getProperty("user.dir");
		String binPath = File.separatorChar + "bin";
		String filePath;
		if (userDir.endsWith(binPath)) {
			userDir = userDir.substring(0,
					userDir.length() - binPath.length());
		}
		filePath = userDir + File.separator + "vsketch.txt";
		configFile = new ConfigFile(this, filePath);
	}

	public void dispose() {
		if (configFile != null) {
			configFile.save();
		}
		interpreter.stop();
		server.stop();
		code.dispose();
	}

	public Interpreter getInterpreter() {
		return interpreter;
	}

	public FunctionCompiler getCompiler() {
		return compiler;
	}

	public SimpleHttpServer getServer() {
		return server;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {

		if (interpreter != null) {
			interpreter.stop();
			interpreter.removeListener(frame.getPlayerPanel());
		}
		if (this.code != null) {
			this.code.dispose();
		}

		interpreter = new Interpreter();
		interpreter.addListener(frame.getPlayerPanel());
		this.code = code;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.getCodePanel().getCodeSketchPanel().repaint();
			}
		});
	}

}
