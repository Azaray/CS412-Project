package view;

/**
 * Created by kiril on 12/11/16.
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class gui {
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */


    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Search...");


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());


        //search panel

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        JLabel searchDescription = new JLabel("Enter keywords..");
        JTextField searchField = new JTextField();
        searchField.setColumns(30);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchDescription);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        contentPane.add(searchPanel, BorderLayout.NORTH);

        JLabel suggestions = new JLabel("Suggestions..");


        JPanel suggestionListPanel = new JPanel();
        suggestionListPanel.setLayout(new BoxLayout(suggestionListPanel, BoxLayout.Y_AXIS));

        ArrayList<JLabel> suggestionList = new ArrayList<>();
        ArrayList<JButton> actionsList = new ArrayList<>();

        for(int i=0; i<10; i++) {
            String iStr = Integer.toString(i+1);
            JLabel suggestion = new JLabel(iStr);
            suggestionList.add(suggestion);
            suggestionList.get(i).setAlignmentX(Component.LEFT_ALIGNMENT);
            suggestionListPanel.add(suggestionList.get(i));

        }

        suggestionListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPane.add(suggestionListPanel, BorderLayout.WEST);

        JPanel navigation = new JPanel();
        navigation.setLayout(new FlowLayout());

        JButton previous = new JButton("Previous");
        JButton next = new JButton("Next");

        navigation.add(previous);
        navigation.add(next);

        contentPane.add(navigation, BorderLayout.SOUTH);

        //Display the window.

        frame.pack();
        //frame.setSize(700, 700);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

