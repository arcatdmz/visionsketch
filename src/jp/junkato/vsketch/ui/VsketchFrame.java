package jp.junkato.vsketch.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.CardLayout;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.junkato.vsketch.ui.code.VsketchCodePanel;
import jp.junkato.vsketch.ui.stmt.VsketchStmtPanel;

public class VsketchFrame extends JFrame {
	private static final long serialVersionUID = -8146867638970911579L;
	private static final int canvasWidth = 840;
	private static final int canvasHeight = 600;

	public static final String ICON_PREFIX = "/icons/";
	public static final Font headerFont = new Font("Meiryo UI", Font.BOLD, 24);
	public static final Font defaultFont = new Font("Meiryo UI", Font.PLAIN, 16);
	public static final Font smallFont = new Font("Meiryo UI", Font.PLAIN, 11);
	public static Color blue = new Color(9, 33, 64);
	public static Color green = new Color(74, 176, 100);
	public static Color lightGreen = new Color(120, 211, 134);
	public static BasicStroke stroke = new BasicStroke(3);
	public static BasicStroke boldStroke = new BasicStroke(18);

	private static VsketchFrame instance;

	private JPanel contentPane;
	private JPanel cardPanel;
	private AnimatedGlassPane glassPane;
	private VsketchCodePanel codePanel;
	private VsketchStmtPanel stmtPanel;
	private PlaybackControllerPanel playerPanel;

	private static Pattern iconNamePattern = Pattern.compile("icons/(.+?\\.png)");
	private static List<Icon> icons;

	public static VsketchFrame getInstance() {
		if (instance == null) {
			instance = new VsketchFrame();
		}
		return instance;
	}

	/**
	 * Create the frame.
	 */
	public VsketchFrame() {

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		contentPane.add(getCardPanel(), BorderLayout.CENTER);	
		contentPane.add(getPlayerPanel(), BorderLayout.SOUTH);

		glassPane = new AnimatedGlassPane();
		setGlassPane(glassPane);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	public void showCodePanel() {
		CardLayout cl = (CardLayout)cardPanel.getLayout();
		cl.show(cardPanel, getCodePanel().getName());
	}

	public void showStmtPanel() {
		CardLayout cl = (CardLayout)cardPanel.getLayout();
		cl.show(cardPanel, getStmtPanel().getName());
	}

	private JPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel();
			cardPanel.setLayout(new CardLayout(0, 0));
			cardPanel.add(getCodePanel(), getCodePanel().getName());
			cardPanel.add(getStmtPanel(), getStmtPanel().getName());
		}
		return cardPanel;
	}

	public VsketchCodePanel getCodePanel() {
		if (codePanel == null) {
			codePanel = new VsketchCodePanel(canvasWidth, canvasHeight);
		}
		return codePanel;
	}

	public VsketchStmtPanel getStmtPanel() {
		if (stmtPanel == null) {
			stmtPanel = new VsketchStmtPanel();
		}
		return stmtPanel;
	}

	public PlaybackControllerPanel getPlayerPanel() {
		if (playerPanel == null) {
			playerPanel = new PlaybackControllerPanel();
		}
		return playerPanel;
	}

	public AnimatedGlassPane getVsketchGlassPane() {
		return glassPane;
	}

	public static List<Icon> getIcons() {
		if (icons != null) {
			return icons;
		}
		icons = new ArrayList<Icon>();
		URL dir = VsketchFrame.class.getClassLoader().getResource("icons");
		try {
			JarURLConnection con = (JarURLConnection) dir.openConnection();
			JarFile jar = con.getJarFile();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				Matcher m = iconNamePattern.matcher(name);
				if (m.matches()) {
					Icon icon = new Icon();
					icon.name = m.group(1);
					icon.image = new ImageIcon(
							VsketchFrame.class.getClassLoader().getResource(name));
					icons.add(icon);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return icons;
	}

	public static Icon getIcon(String name) {
		for (Icon icon : getIcons()) {
			if (icon.name.equals(name)) {
				return icon;
			}
		}
		return null;
	}

	public static ImageIcon getImageIcon(String name) {
		Icon icon = getIcon(name);
		return icon == null ? null : icon.image;
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
}
