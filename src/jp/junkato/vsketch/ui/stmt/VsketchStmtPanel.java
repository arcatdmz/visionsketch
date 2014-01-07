package jp.junkato.vsketch.ui.stmt;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.function.Function;
import jp.junkato.vsketch.function.FunctionDefinition;
import jp.junkato.vsketch.function.FunctionTemplate;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.action.ShowCodeAction;
import jp.junkato.vsketch.utils.VsketchUtils;

public class VsketchStmtPanel extends JPanel {
	private static final long serialVersionUID = 536600327442731271L;
	private JTabbedPane tabbedPane;
	private JSplitPane splitPane;
	private VsketchStmtInputPanel inputPanel;
	private VsketchStmtOutputPanel outputPanel;
	private VsketchStmtEditorPanel editorPanel;
	private transient Stmt stmt;

	/**
	 * Create the panel.
	 */
	public VsketchStmtPanel() {
		setName("statement panel");
		setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		if (VsketchUtils.isMac()) {
			tabbedPane.setFont(VsketchFrame.defaultFont);
		} else {
			tabbedPane.setFont(VsketchFrame.headerFont);
		}
		add(tabbedPane);

		editorPanel = new VsketchStmtEditorPanel();
		editorPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		splitPane = new JSplitPane();
		if (VsketchUtils.isMac()) {
			final Color c = new Color(230, 230, 230);

			// EmptyBorder looks a bit brighter...
			splitPane.setBorder(new LineBorder(c, 5));
			splitPane.setUI(new BasicSplitPaneUI() {
	            public BasicSplitPaneDivider createDefaultDivider() {
	            return new BasicSplitPaneDivider(this) {
					private static final long serialVersionUID = 2317812363162522913L;
					public void setBorder(Border b) {
	                }
	                @Override
	                    public void paint(Graphics g) {
	                    g.setColor(c);
	                    g.fillRect(0, 0, getSize().width, getSize().height);
	                    super.paint(g);
	                }
	            };
	            }
			});
			BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
			divider.setBackground(c);
			divider.setBorder(null);
		}
		tabbedPane.addTab("Visual", splitPane);
		tabbedPane.addTab("Code", editorPanel);
		
		inputPanel = new VsketchStmtInputPanel();
		splitPane.setLeftComponent(inputPanel);

		outputPanel = new VsketchStmtOutputPanel();
		splitPane.setRightComponent(outputPanel);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridBagLayout());
		
			JLabel lblStmtPanel = new JLabel("Editor");
			lblStmtPanel.setFont(VsketchFrame.headerFont);
			GridBagConstraints gbc_lblStmtPanel = new GridBagConstraints();
			gbc_lblStmtPanel.insets = new Insets(5, 5, 5, 5);
			gbc_lblStmtPanel.weightx = 1.0;
			gbc_lblStmtPanel.fill = GridBagConstraints.BOTH;
			gbc_lblStmtPanel.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblStmtPanel.gridx = 0;
			gbc_lblStmtPanel.gridy = 0;
			panel.add(lblStmtPanel, gbc_lblStmtPanel);
		
			JButton btnShowCode = new JButton(new ShowCodeAction());
			btnShowCode.setFont(VsketchFrame.defaultFont);
			GridBagConstraints gbc_btnShowCode = new GridBagConstraints();
			gbc_btnShowCode.insets = new Insets(5, 5, 5, 5);
			gbc_btnShowCode.weightx = 0;
			gbc_btnShowCode.fill = GridBagConstraints.BOTH;
			gbc_btnShowCode.anchor = GridBagConstraints.NORTHWEST;
			gbc_btnShowCode.gridx = 1;
			gbc_btnShowCode.gridy = 0;
			panel.add(btnShowCode, gbc_btnShowCode);

		splitPane.setDividerLocation(.5);
		splitPane.setDividerSize(30);
	}

	public void setStmt(Stmt stmt) {
		this.stmt = stmt;
		inputPanel.setStmt(stmt);
		outputPanel.setStmt(stmt);
		if (stmt.getFunction() != null) {
			Function function = stmt.getFunction();
			FunctionDefinition def = VsketchMain.getInstance().getCompiler()
					.getDefinition(function.getTemplate().getClass().getSimpleName());
			editorPanel.open(def, false);
		}
		splitPane.setDividerLocation(.5);
	}

	public Stmt getStmt() {
		return stmt;
	}

	public void removeFunction() {
		setNewFunction(null);
	}

	public void setNewFunction(FunctionTemplate functionTemplate) {
		if (functionTemplate != null && !functionTemplate.check(stmt)) {
			return;
		}

		// Set function instance.
		Function function;
		if (functionTemplate == null) {
			function = null;
			setFunction(null);
		} else {
			function = functionTemplate.newInstance(stmt);
			function.parameterize();
			function.calculate();
			setFunction(function);
			repaintView();
		}
	}

	public void setFunction(Function function) {
		stmt.setFunction(function);

		//
		outputPanel.setFunction(function);

		// Update the list of available tools
		inputPanel.updateToolsList();

		// Update the text-based code editor
		if (function != null) {
			FunctionDefinition def = VsketchMain.getInstance().getCompiler()
					.getDefinition(function.getTemplate().getClass().getSimpleName());
			editorPanel.open(def, false);
		}
	}

	public void openEditor(FunctionDefinition def) {
		editorPanel.open(def, true);
		tabbedPane.setSelectedIndex(1);
	}

	public void repaintView() {
		inputPanel.getPane().getPanel().repaint();
		outputPanel.getPane().getPanel().repaint();
	}

	public VsketchStmtInputPanel getInputPanel() {
		return inputPanel;
	}

	public VsketchStmtOutputPanel getOutputPanel() {
		return outputPanel;
	}

	public VsketchStmtEditorPanel getEditorPanel() {
		return editorPanel;
	}
}
