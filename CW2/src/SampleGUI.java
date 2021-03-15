import java.awt.*;

import javax.swing.*;

public class SampleGUI extends JFrame
{
    public SampleGUI()
    {
        init();
    }

    private void init()
    {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Grid Bag Layout Example");

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(label, gbc);

        JTextField firstNameTextField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        detailPanel.add(firstNameTextField, gbc);

        label = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;
        detailPanel.add(label, gbc);

        JTextField lastNameTextField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        detailPanel.add(lastNameTextField, gbc);

        label = new JLabel("Telephone:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;
        detailPanel.add(label, gbc);

        JTextField telephoneTextField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        detailPanel.add(telephoneTextField, gbc);

        label = new JLabel("Priority Name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;
        detailPanel.add(label, gbc);

        String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        JComboBox priorityComboBox = new JComboBox(data);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        detailPanel.add(priorityComboBox, gbc);

        label = new JLabel("Call Description:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;
        detailPanel.add(label, gbc);

        JTextArea descriptionTextArea = new JTextArea();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        detailPanel.add(descriptionTextArea, gbc);


        getContentPane().add(detailPanel);
    }


    public static void main(String[] args)
    {
        SampleGUI gui = new SampleGUI();

        gui.setSize(250, 400);
        gui.setVisible(true);
    }
}
