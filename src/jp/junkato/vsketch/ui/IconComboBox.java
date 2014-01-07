package jp.junkato.vsketch.ui;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class IconComboBox extends JComboBox<Icon> {
	private static final long serialVersionUID = -3896121568404966553L;

	public IconComboBox() {
		super(new DefaultComboBoxModel<Icon>());
		setRenderer(new IconCellRenderer());
		for (Icon icon : VsketchFrame.getIcons()) {
			addItem(icon);
		}
	}

	public static class IconCellRenderer extends JLabel implements ListCellRenderer<Icon> {
		private static final long serialVersionUID = -5403865861399330199L;

		@Override
		public Component getListCellRendererComponent(
				JList<? extends Icon> list, Icon value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (value != null) setIcon(value.image);
			return this;
		}
	}
}
