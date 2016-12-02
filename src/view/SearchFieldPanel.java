package view;

import java.awt.Dimension;
import java.awt.FlowLayout;import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import datastructures.SearchField;

public class SearchFieldPanel extends JPanel{


	private static final long serialVersionUID = 1L;
	private JTextField mSearchField = new JTextField();
	private JComboBox<String> mComboSearchField = new JComboBox<String>();
	private JCheckBox mIsExact = new JCheckBox("Exact words");
	private JCheckBox mIsSentence = new JCheckBox("Exact sentence");
	private boolean mComboPanel = false;

	public SearchFieldPanel(SearchField field, Controller Controller) {

		if(field.equals(SearchField.AGENCY) || field.equals(SearchField.USDEPT) || field.equals(SearchField.USBUREAU)) {
			mComboPanel = true;
		}
		
		// search panel
		this.setName(field.field());

		this.setPreferredSize(new Dimension(750, 50));
		this.setSize(new Dimension(750, 50));
		this.setLayout(new FlowLayout());

		JLabel searchDescription = new JLabel(field.description());
		this.add(searchDescription);
		
		if(mComboPanel) {
			 DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
			 
			 for(String val : Controller.getComboBoxValues(field))
				 model.addElement(val);
			 
			mComboSearchField.setModel(model);
			mComboSearchField.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			this.add(mComboSearchField);
		} else {

			mSearchField.setColumns(15);
			mSearchField.setName("searchField");
			this.add(mSearchField);
			this.add(mIsExact);
			this.add(mIsSentence);
		}


		JButton removebutton = new JButton();
		removebutton.setText("Remove");
		removebutton.setActionCommand("Remove" + field.field());
		removebutton.addActionListener(Controller);
		this.add(removebutton);
	}
	
	public String getSearchValue() {
		if(mComboPanel)
			return (String) mComboSearchField.getSelectedItem();
		
		return mSearchField.getText();
	}
	
	public boolean isExact() {
		if(mComboPanel)
			return false;
		
		if(isSentence())
			return false;
		
		return mIsExact.isSelected();
	}
	
	public boolean isSentence() {
		if(mComboPanel)
			return false;
		return mIsSentence.isSelected();
	}
}
