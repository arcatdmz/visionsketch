package jp.junkato.vsketch.ui.code;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.action.LoadCodeAction;
import jp.junkato.vsketch.ui.action.SaveCodeAction;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;

public class VsketchCodePanel extends JPanel {
	private static final long serialVersionUID = 6153428836900512177L;
	private transient VsketchCodeSketchPanel codeSketchPanel;

	/**
	 * Create the panel.
	 */
	public VsketchCodePanel(int width, int height) {
		setName("Canvas");
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridBagLayout());
		
				JLabel lblCodePanel = new JLabel("Canvas");
				lblCodePanel.setFont(VsketchFrame.headerFont);
				GridBagConstraints gbc_lblCodePanel = new GridBagConstraints();
				gbc_lblCodePanel.insets = new Insets(5, 5, 5, 0);
				gbc_lblCodePanel.weightx = 1.0;
				gbc_lblCodePanel.fill = GridBagConstraints.BOTH;
				gbc_lblCodePanel.anchor = GridBagConstraints.NORTHWEST;
				gbc_lblCodePanel.gridx = 0;
				gbc_lblCodePanel.gridy = 0;
				panel.add(lblCodePanel, gbc_lblCodePanel);
				
				JButton btnSaveCode = new JButton(new SaveCodeAction());
				btnSaveCode.setFont(VsketchFrame.defaultFont);
				GridBagConstraints gbc_btnSaveCode = new GridBagConstraints();
				gbc_btnSaveCode.insets = new Insets(0, 5, 0, 0);
				gbc_btnSaveCode.gridx = 1;
				gbc_btnSaveCode.gridy = 0;
				panel.add(btnSaveCode, gbc_btnSaveCode);

				JButton btnLoadCode = new JButton(new LoadCodeAction());
				btnLoadCode.setFont(VsketchFrame.defaultFont);
				GridBagConstraints gbc_btnLoadCode = new GridBagConstraints();
				gbc_btnLoadCode.insets = new Insets(0, 5, 0, 0);
				gbc_btnLoadCode.gridx = 2;
				gbc_btnLoadCode.gridy = 0;
				panel.add(btnLoadCode, gbc_btnLoadCode);

//				JButton btnSetting = new JButton(new ShowSettingAction());
//				btnSetting.setFont(VsketchFrame.defaultFont);
//				GridBagConstraints gbc_btnSetting = new GridBagConstraints();
//				gbc_btnSetting.insets = new Insets(0, 5, 0, 0);
//				gbc_btnSetting.gridx = 3;
//				gbc_btnSetting.gridy = 0;
//				panel.add(btnSetting, gbc_btnSetting);
		
		codeSketchPanel = new VsketchCodeSketchPanel(width, height);
		JScrollPane scrollPane = new JScrollPane(getCodeSketchPanel());
		add(scrollPane);
	}

	public VsketchCodeSketchPanel getCodeSketchPanel() {
		return codeSketchPanel;
	}

	public void repaintView() {
		codeSketchPanel.repaint();
	}

}
