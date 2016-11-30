package controller;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by kiril on 29/11/16.
 */
public class SuggestionsExpansion implements MouseListener {
    private JList suggestionsList;
    private Controller controller;

    public SuggestionsExpansion(JList suggestionsList, Controller controller) {
        this.suggestionsList = suggestionsList;
        this.controller = controller;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        suggestionsList.getSelectedValue();
        controller.Go();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
