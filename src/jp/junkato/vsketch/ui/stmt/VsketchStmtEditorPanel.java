package jp.junkato.vsketch.ui.stmt;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;

import jp.junkato.vsketch.function.FunctionDefinition;
import jp.junkato.vsketch.ui.Icon;
import jp.junkato.vsketch.ui.IconComboBox;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.action.UpdateFunctionAction;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import javax.swing.JButton;

public class VsketchStmtEditorPanel extends JPanel {
	private static final long serialVersionUID = -7566947509495247925L;
	private JTextField txtFunctionName;
	private JTextField txtDescription;
	private IconComboBox iconComboBox;
	private RSyntaxTextArea txtrCode;
	private RSyntaxTextArea txtrStmtCheck;
	private RSyntaxTextArea txtrToolCheck;

	/**
	 * Create the panel.
	 */
	public VsketchStmtEditorPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0, 1, 0, 0};
		gridBagLayout.rowWeights = new double[]{0, 0, 0, 0, 0, 1, 0};
		setLayout(gridBagLayout);
		
		JLabel lblFunctionName = new JLabel("Function name:");
		lblFunctionName.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_lblFunctionName = new GridBagConstraints();
		gbc_lblFunctionName.fill = GridBagConstraints.VERTICAL;
		gbc_lblFunctionName.insets = new Insets(0, 0, 5, 5);
		gbc_lblFunctionName.anchor = GridBagConstraints.WEST;
		gbc_lblFunctionName.gridx = 0;
		gbc_lblFunctionName.gridy = 0;
		add(lblFunctionName, gbc_lblFunctionName);
		
		txtFunctionName = new JTextField();
		txtFunctionName.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_txtFunctionName = new GridBagConstraints();
		gbc_txtFunctionName.fill = GridBagConstraints.BOTH;
		gbc_txtFunctionName.insets = new Insets(0, 0, 5, 5);
		gbc_txtFunctionName.gridx = 1;
		gbc_txtFunctionName.gridy = 0;
		add(txtFunctionName, gbc_txtFunctionName);
		txtFunctionName.setColumns(10);
		
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.fill = GridBagConstraints.VERTICAL;
		gbc_lblDescription.anchor = GridBagConstraints.WEST;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 1;
		add(lblDescription, gbc_lblDescription);
		
		txtDescription = new JTextField();
		txtDescription.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_txtDescription = new GridBagConstraints();
		gbc_txtDescription.gridwidth = 3;
		gbc_txtDescription.insets = new Insets(0, 0, 5, 0);
		gbc_txtDescription.fill = GridBagConstraints.BOTH;
		gbc_txtDescription.gridx = 1;
		gbc_txtDescription.gridy = 1;
		add(txtDescription, gbc_txtDescription);
		txtDescription.setColumns(10);
		
		JLabel lblIcon = new JLabel("Icon:");
		lblIcon.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_lblIcon = new GridBagConstraints();
		gbc_lblIcon.anchor = GridBagConstraints.WEST;
		gbc_lblIcon.insets = new Insets(0, 5, 5, 5);
		gbc_lblIcon.gridx = 2;
		gbc_lblIcon.gridy = 0;
		add(lblIcon, gbc_lblIcon);
		
		iconComboBox = new IconComboBox();
		GridBagConstraints gbc_iconComboBox = new GridBagConstraints();
		gbc_iconComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_iconComboBox.fill = GridBagConstraints.BOTH;
		gbc_iconComboBox.gridx = 3;
		gbc_iconComboBox.gridy = 0;
		add(iconComboBox, gbc_iconComboBox);

		JLabel lblStmtCheck = new JLabel("Acceptable input:");
		lblStmtCheck.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_lblStmtCheck = new GridBagConstraints();
		gbc_lblStmtCheck.fill = GridBagConstraints.VERTICAL;
		gbc_lblStmtCheck.anchor = GridBagConstraints.WEST;
		gbc_lblStmtCheck.insets = new Insets(0, 0, 5, 5);
		gbc_lblStmtCheck.gridx = 0;
		gbc_lblStmtCheck.gridy = 2;
		add(lblStmtCheck, gbc_lblStmtCheck);
		
		txtrStmtCheck = new RSyntaxTextArea(2, 20);
		txtrStmtCheck.setFont(VsketchFrame.defaultFont);
		txtrStmtCheck.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		txtrStmtCheck.setCodeFoldingEnabled(true);
		txtrStmtCheck.setAntiAliasingEnabled(true);
		txtrStmtCheck.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_txtrStmtCheck = new GridBagConstraints();
		gbc_txtrStmtCheck.gridwidth = 3;
		gbc_txtrStmtCheck.insets = new Insets(0, 0, 5, 0);
		gbc_txtrStmtCheck.fill = GridBagConstraints.BOTH;
		gbc_txtrStmtCheck.gridx = 1;
		gbc_txtrStmtCheck.gridy = 2;
		add(txtrStmtCheck, gbc_txtrStmtCheck);

		JLabel lblToolCheck = new JLabel("Available tools:");
		lblToolCheck.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_lblToolCheck = new GridBagConstraints();
		gbc_lblToolCheck.fill = GridBagConstraints.VERTICAL;
		gbc_lblToolCheck.anchor = GridBagConstraints.WEST;
		gbc_lblToolCheck.insets = new Insets(0, 0, 5, 5);
		gbc_lblToolCheck.gridx = 0;
		gbc_lblToolCheck.gridy = 3;
		add(lblToolCheck, gbc_lblToolCheck);
		
		txtrToolCheck = new RSyntaxTextArea(2, 20);
		txtrToolCheck.setFont(VsketchFrame.defaultFont);
		txtrToolCheck.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		txtrToolCheck.setCodeFoldingEnabled(true);
		txtrToolCheck.setAntiAliasingEnabled(true);
		txtrToolCheck.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_txtrToolCheck = new GridBagConstraints();
		gbc_txtrToolCheck.gridwidth = 3;
		gbc_txtrToolCheck.insets = new Insets(0, 0, 5, 0);
		gbc_txtrToolCheck.fill = GridBagConstraints.BOTH;
		gbc_txtrToolCheck.gridx = 1;
		gbc_txtrToolCheck.gridy = 3;
		add(txtrToolCheck, gbc_txtrToolCheck);
		
		JLabel lblCode = new JLabel("Code:");
		lblCode.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_lblCode = new GridBagConstraints();
		gbc_lblCode.fill = GridBagConstraints.VERTICAL;
		gbc_lblCode.anchor = GridBagConstraints.WEST;
		gbc_lblCode.insets = new Insets(0, 0, 5, 5);
		gbc_lblCode.gridx = 0;
		gbc_lblCode.gridy = 4;
		add(lblCode, gbc_lblCode);

		txtrCode = new RSyntaxTextArea();
		txtrCode.setFont(VsketchFrame.defaultFont);
		txtrCode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		txtrCode.setCodeFoldingEnabled(true);
		txtrCode.setAntiAliasingEnabled(true);
		
		RTextScrollPane scrollPane = new RTextScrollPane(txtrCode);
		scrollPane.setLineNumbersEnabled(true);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 4;
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 5;
		add(scrollPane, gbc_textArea);

		JButton btnUpdate = new JButton(new UpdateFunctionAction());
		btnUpdate.setFont(VsketchFrame.defaultFont);
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.anchor = GridBagConstraints.EAST;
		gbc_btnUpdate.gridwidth = 4;
		gbc_btnUpdate.insets = new Insets(5, 0, 0, 0);
		gbc_btnUpdate.gridx = 0;
		gbc_btnUpdate.gridy = 6;
		add(btnUpdate, gbc_btnUpdate);
	}

	private FunctionDefinition def;
	public FunctionDefinition getFunctionDefinition() {
		return def;
	}

	void open(FunctionDefinition def, boolean newFunc) {
		this.def = def;
		iconComboBox.setSelectedItem(VsketchFrame.getIcon(def.getIconFileName()));
		txtDescription.setText(def.getDescription());
		txtrStmtCheck.setText(def.getStmtCheckCode());
		txtrToolCheck.setText(def.getToolCheckCode());
		if (newFunc) {
			txtFunctionName.setText(def.getClassName());
			txtrCode.setText(def.getDefaultFunctionCode());
		} else {
			txtFunctionName.setText(def.getName());
			txtrCode.setText(def.getFunctionCode());
		}
	}

	public void save() {
//		Function function = VsketchFrame.getInstance().getStmtPanel()
//				.getStmt().getFunction();
//		FunctionTemplate template = function.getTemplate();
//		FunctionDefinition def = VsketchFrame.getInstance().getCompiler()
//				.getDefinition(template.getClass().getSimpleName());
		def.setName(txtFunctionName.getText());
		def.setIconFileName(iconComboBox.getSelectedIndex() >= 0 ?
				((Icon)iconComboBox.getSelectedItem()).name
				: FunctionDefinition.defaultIconFileName);
		def.setDescription(txtDescription.getText());
		def.setStmtCheckCode(txtrStmtCheck.getText());
		def.setToolCheckCode(txtrToolCheck.getText());
		def.setFunctionCode(txtrCode.getText());
		def.save();
		def.export();
	}

}
