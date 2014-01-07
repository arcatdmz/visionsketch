package jp.junkato.vsketch.ui.stmt;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Map.Entry;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.function.Function;
import jp.junkato.vsketch.function.FunctionTemplate;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.tool.ScrollTool;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.action.CreateNewFunctionAction;
import jp.junkato.vsketch.ui.action.RemoveFunctionAction;
import jp.junkato.vsketch.utils.VsketchUtils;

public class VsketchStmtOutputPanel extends JPanel {
	private static final long serialVersionUID = 6399615540198759780L;
	private JPanel panel;
	private VsketchPreviewPane pane;
	private JPanel retValuesPanel;
	private JButton newButton;
	private JButton removeButton;
	private Function function;
	private Stmt stmt;

	/**
	 * Create the panel.
	 */
	public VsketchStmtOutputPanel() {
		setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(boxLayout);
		JScrollPane functionPane = new JScrollPane(panel);
		functionPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(functionPane, BorderLayout.NORTH);
		
		pane = new VsketchPreviewPane();
		ScrollTool scrollTool = new ScrollTool();
		pane.getPanel().addMouseListener(scrollTool);
		pane.getPanel().addMouseMotionListener(scrollTool);
		add(pane);

		retValuesPanel = new JPanel();
		BoxLayout boxLayout2 = new BoxLayout(retValuesPanel, BoxLayout.X_AXIS);
		retValuesPanel.setLayout(boxLayout2);
		add(retValuesPanel, BorderLayout.SOUTH);
	}

	public VsketchPreviewPane getPane() {
		return pane;
	}

	void setStmt(Stmt stmt) {
		this.stmt = stmt;
		pane.setStmt(stmt);
		updateFunctionsList();
		setFunction(stmt.getFunction());
	}

	void setFunction(Function function) {

		// Remove listeners of previous (in most cases, parent statement's) function instance.
		if (this.function != null) {
			pane.getPanel().removeMouseListener(this.function);
			pane.getPanel().removeMouseMotionListener(this.function);
			pane.getPanel().removeMouseWheelListener(this.function);
		}
		this.function = function;

		// Register listeners of new function instance.
		if (function != null) {
			pane.getPanel().addMouseListener(function);
			pane.getPanel().addMouseMotionListener(function);
			pane.getPanel().addMouseWheelListener(function);
		}

		// Add optional GUI components.
		retValuesPanel.removeAll();
		if (function != null) {
			FunctionTemplate template = function.getTemplate();
			for (Entry<String, Class<?>> entry :
					template.getRetTypes().entrySet()) {
				retValuesPanel.add(new RetValuePanel(entry.getKey(), entry.getValue()));
			}
		}
		pane.updatePanelSize();
		validate();
	}

	public void addFunctionToList(FunctionTemplate functionTemplate) {
		panel.remove(getNewButton());
		panel.remove(getRemoveButton());

		// Add a button.
		panel.add(functionTemplate.getButton());

		panel.add(getNewButton());
		panel.add(getRemoveButton());

		// Show relevant buttons only.
		if (stmt != null) {
			functionTemplate.getButton().setVisible(functionTemplate.check(stmt));
		}
	}

	public void removeFunctionFromList(FunctionTemplate functionTemplate) {
		panel.remove(functionTemplate.getButton());
	}

	private JButton getNewButton() {
		if (newButton == null) {
			newButton = new JButton(new CreateNewFunctionAction());
			newButton.setFont(VsketchFrame.defaultFont);
			newButton.setMargin(new Insets(10, 5, 10, 5));
			if (VsketchUtils.isMac()) {
				newButton.putClientProperty("JButton.buttonType", "square");
			}
		}
		return newButton;
	}

	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton(new RemoveFunctionAction());
			removeButton.setFont(VsketchFrame.defaultFont);
			removeButton.setMargin(new Insets(10, 5, 10, 5));
			if (VsketchUtils.isMac()) {
				removeButton.putClientProperty("JButton.buttonType", "square");
			}
		}
		return removeButton;
	}

	public void updateFunctionsList() {
		for (FunctionTemplate functionTemplate :
				VsketchMain.getInstance().getCompiler().getTemplates()) {
			functionTemplate.getButton().setVisible(functionTemplate.check(stmt));
		}
	}

	public class RetValuePanel extends JPanel {
		private static final long serialVersionUID = 3375782675756968224L;
		private String key;
		private Class<?> type;
		private JComponent comp;

		public RetValuePanel(String key, Class<?> type) {
			setLayout(new BorderLayout());

			this.key = key;
			JLabel keyLabel = new JLabel(String.format("%s:", key));
			keyLabel.setFont(VsketchFrame.defaultFont);
			keyLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			add(keyLabel, BorderLayout.NORTH);

			this.type = type;
			if (type == Integer.class) {
				this.comp = new JLabel();
			}
			if (this.comp != null) {
				comp.setFont(VsketchFrame.headerFont);
				comp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				add(this.comp, BorderLayout.CENTER);
			}

		}

		public void paintComponent(Graphics g) {
			Object value = function.getRetValues().get(key);
			if (type == Integer.class) {
				((JLabel)comp).setText(String.valueOf(value));
			}
			super.paintComponent(g);
		}
	}

}
