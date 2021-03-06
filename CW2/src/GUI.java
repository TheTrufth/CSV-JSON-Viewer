import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

import static java.util.Collections.max;


public class GUI extends JFrame implements ItemListener {
    /* JFrames - Secondary windows */
    private JFrame sumOfFrame;
    private JFrame barChartFrame;

    /* Model */
    private Model myModel;

    /* CheckBoxList using the custom ChkBox class that I implemented */
    private final ArrayList<ChkBox> checkBoxList = new ArrayList<>();

    /* Panels */
    private JPanel topMainPanel;
    private JPanel middleMainPanel;

    /* Menu Bar */
    JMenuBar menuBar;
    JMenu Load;
    JMenu Export;
    JMenu Stats;
    JMenu About;
    JMenuItem loadFile;
    JMenuItem exportFile;
    JMenuItem viewStats;
    JMenuItem sumOf;

    /* Top Main Panels */
    private JTextField searchText;
    private JPanel topMainButtonPanel;
    private JList<String> list;
    private JButton removeFromList;
    private JButton clearFilterButton;
    private DefaultListModel <String> listModel;
    private JButton goButton;
    private JComboBox<String> typeFieldsCombo;

    /* Middle Main Panel */
    private JPanel middleTopPanel;
    private JPanel leftMiddleMainPanel;
    private JPanel rightMiddleMainPanel;

    /* Left Middle Main Panel */
    JLabel totalNumberOfMatchingRecordsLabel;
    StringBuffer listChoices;
    JCheckBox chkBox;

    /* Right middle Main Panel */
    JTable table;
    ArrayList<RowFilter<Object, Object>> filterTable;
    TableRowSorter<DefaultTableModel> sorter;

    /* Globals <Important!> */
    String[] typeFields;
    Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW};
    Random rng = new Random();
    JComboBox<? extends String> filterType = new JComboBox<>(new String[]{"AND", "OR"});

    // This creates the initialises the GUI
    public GUI(){
        super("Patients Loader by Dinesh Anantharaja");
        loadFilePopup();
        createGUI();
        setJMenuBar(menuBar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    // This method creates the panels
    private void createGUI(){
        createMenuBar();
        createTopMainPanel();
        createMiddleMainPanel();
        add(topMainPanel, BorderLayout.NORTH);
        add(middleMainPanel, BorderLayout.CENTER);
    }

    // This method creates the menubar that is located at the top
    private void createMenuBar(){
        menuBar = new JMenuBar();
        Load = new JMenu("Load");
        Export = new JMenu("Export");
        Stats = new JMenu("Stats");
        About = new JMenu("About");

        // Create menu items
        loadFile = new JMenuItem("Load file");
        exportFile = new JMenuItem("Export to .json file");
        viewStats = new JMenuItem("View Statistics");
        sumOf = new JMenuItem("Sum of column");

        // Add action listeners
        loadFile.addActionListener((ActionEvent e) -> loadNewFile());
        exportFile.addActionListener((ActionEvent e) -> exportFileAsJSON());
        viewStats.addActionListener((ActionEvent e) -> viewStatsPopup());
        sumOf.addActionListener((ActionEvent e) -> sumOfPopup());

        Load.add(loadFile);
        Export.add(exportFile);
        Stats.add(viewStats);
        Stats.add(sumOf);

        menuBar.add(Load);
        menuBar.add(Export);
        menuBar.add(Stats);
        menuBar.add(About);
    }

    // This method calculates the total sum of a column - only works if every data is either int/double/float
    // It will calculate the sum of the current view column (i.e. filter dependent)
    private Double calculateSumOf(int colIndex){
        int rowCount = sorter.getViewRowCount();
        double sum = 0.0;
        for (int i = 0; i < rowCount; i++){
            try {
                sum += Double.parseDouble((String) table.getValueAt(i, colIndex));
            }
            catch (Exception e){
                errorMessage("These values are not int|double|float!");
                return 0.0;
            }
        }
        return sum;
    }

    // The secondary window used to calculate the sum
    private void sumOfPopup(){
        sumOfFrame = new JFrame("SUM of ");
        JPanel sumOfPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel t = new JLabel("Choose your field!: ");

        JComboBox<String> cb = new JComboBox<>(typeFields) ;
        JButton sumOfButton = new JButton("Calculate");
        JLabel s = new JLabel("Sum of ");

        sumOfButton.addActionListener((ActionEvent e) -> {

            String cs = (String) cb.getSelectedItem();
            try {
                int colIndex = table.getColumnModel().getColumnIndex(cs);
                Double sum = calculateSumOf(colIndex);
                s.setText(" Sum of " + cb.getSelectedItem() + " = " +sum);
                gbc.gridx = 1;
                gbc.gridy = 1;
                sumOfPanel.add(s, gbc);
                sumOfFrame.pack();
                sumOfFrame.repaint();
            }
            catch (Exception ee){
                errorMessage("This column does not exist in table. \n Maybe it is hidden?");
            }

        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        sumOfPanel.add(t, gbc);
        gbc.gridx++;
        sumOfPanel.add(cb, gbc);
        gbc.gridx++;
        sumOfPanel.add(sumOfButton, gbc);

        sumOfFrame.add(sumOfPanel);
        sumOfFrame.pack();
        sumOfFrame.setVisible(true);
    }

    // This method is used to build the BarChart
    // It adds a column to the barChart.
    private JLabel addColumn(String value, Color color, int width, int height, int count){
        Icon i = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                // Can do width - {int} to get bar spacing.
                g.fillRect(x, y, width, height);
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
        JLabel l = new JLabel(value + " - " + count);
        if (value.equals("")){
            l.setText("None - " + count);
        }
        l.setHorizontalTextPosition(JLabel.CENTER);
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setVerticalTextPosition(JLabel.TOP);
        l.setVerticalAlignment(JLabel.BOTTOM);
        l.setIcon(i);
        return l;
    }

    // The secondary window that is used to display the BarChart (filter dependent).
    // Age is located as the last comboBox. (Will only work for patient files) (filter dependent)
    private void viewStatsPopup(){
        GridBagConstraints gbc = new GridBagConstraints();
        barChartFrame = new JFrame("Barchart ");
        barChartFrame.setLayout(new GridBagLayout());
        JLabel t = new JLabel("Choose your field!: ");

        JComboBox<String> fields = new JComboBox<>(typeFields) ;
        fields.addItem("AGE");
        JButton displayBarChart = new JButton("Display Bar Chart");

        JPanel barChartPanel = new JPanel(new GridLayout(1, 0, 5, 0));
        displayBarChart.addActionListener((ActionEvent e) -> {
            String cs = (String) fields.getSelectedItem();
            assert cs != null;
            try {
                barChartPanel.removeAll();
                barChartPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
                barChartPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " BarChart of " + fields.getSelectedItem() + " where headings are of the format " + fields.getSelectedItem() + ":FREQUENCY"));
                HashMap<String, Integer> count = new HashMap<>();
                // dinesh = list of most left column e.g. list of ID's people belonging to the bar
                HashMap<String, String> dinesh = new HashMap<>();
                // If its age distribution
                if (cs.equals("AGE")){
                    // Calculate the indexes of the data. Used when table is filtered.
                    int[] rowIndexes = new int[sorter.getViewRowCount()];
                    for (int index=0; index<sorter.getViewRowCount(); index++){
                        rowIndexes[index] = sorter.convertRowIndexToModel(index);
                    }
                    String[] ages = myModel.getAges(rowIndexes);
                    for (String s : ages) {
                        if (count.containsKey(s)) {
                            count.put(s, count.get(s) + 1);
                        } else {
                            count.put(s, 1);
                        }
                    }
                }
                else {
                    int colIndex = table.getColumnModel().getColumnIndex(cs);
                    for (int i = 0; i < sorter.getViewRowCount(); i++) {
                        String s = (String) table.getValueAt(i, colIndex);
                        if (count.containsKey(s)) {
                            count.put(s, count.get(s) + 1);
                            dinesh.put(s, dinesh.get(s) + "<br>" + table.getValueAt(i, 0) + "</br>");
                        } else {
                            count.put(s, 1);
                            dinesh.put(s, table.getValueAt(i, 0) + "\n");
                        }
                    }
                }

                int maxVal = max(count.values());
                for (String s : count.keySet()) {
                    Color myColour = colors[rng.nextInt(colors.length)];
                    JLabel l = addColumn(s, myColour, 50, 250 * count.get(s) / maxVal, count.get(s));
                    l.setToolTipText("<html>" + dinesh.get(s) + "</html>");
                    barChartPanel.add(l);
                }

                barChartFrame.pack();
                barChartFrame.repaint();
            }
            catch (Exception ee){
                errorMessage("This column does not exist in table. \n Maybe it is hidden?");
            }

        });
        gbc.gridx = gbc.gridy = 0;
        barChartFrame.add(t, gbc);
        gbc.gridx++;
        barChartFrame.add(fields, gbc);
        gbc.gridx++;
        barChartFrame.add(displayBarChart, gbc);
        JScrollPane sc = new JScrollPane(barChartPanel);
        sc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        sc.setPreferredSize(new Dimension(1000, 500));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        barChartFrame.add(sc, gbc);
        barChartFrame.pack();
        barChartFrame.setVisible(true);
    }

    // Load a new file into the GUI
    // Does not ask for confirmation, so make sure you export before loading new file
    private void loadNewFile(){
        loadFilePopup();
        try {
            sumOfFrame.setVisible(false);
            sumOfFrame.dispose();
        }
        catch (Exception ignored){}
        try {
            barChartFrame.setVisible(false);
            barChartFrame.dispose();
        }
        catch (Exception ignored){}
        remove(topMainPanel);
        remove(middleMainPanel);
        createTopMainPanel();
        createMiddleMainPanel();
        add(topMainPanel, BorderLayout.NORTH);
        add(middleMainPanel, BorderLayout.CENTER);
        pack();
        repaint();
        setVisible(true);
    }

    // Can only import files ending in .csv or .json
    private void loadFilePopup(){
        // Code goes here
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            setTitle(selectedFile.getName() + " - CSV/JSON Viewer by Dinesh Anantharaja");
            if (selectedFile.toString().endsWith(".json")){
                try {
                    myModel = new Model(selectedFile.getAbsolutePath(), "JSON");
                }
                catch(Exception e){
                    errorMessage("The JSON file is invalid/empty");
                    System.exit(-1);
                }
            }
            if (selectedFile.toString().endsWith(".csv")){
                try {
                    myModel = new Model(selectedFile.getAbsolutePath(), "CSV");
                }
                catch(Exception e){
                    errorMessage("The CSV file is invalid/empty");
                    System.exit(-1);
                }
            }
            typeFields = myModel.getColNames();
        }

        else if (myModel == null) {
            System.exit(0);
        }

    }

    // Export current view table as JSON
    // If a file already exists it will not ask for confirmation and just rewrites.
    private void exportFileAsJSON(){
        // Code goes here
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION){
            File whereToSave = fileChooser.getSelectedFile();
            myModel.writeToFile(whereToSave.getAbsolutePath() + ".json", listChoices.toString(), getCurrentTableData());
        }
    }

    // This method responsible for checking if a valid search field is clicked in the search list
    // ... inorder to remove it.
    private void listClicked() {
        int n = list.getSelectedIndex();
        if (!(n < 0) || (n > listModel.size())) {
            removeFromList.setEnabled(true);
        }
    }

    // Creates the top buttons needed for searching/filtering the table.
    private void createTopMainButtons(){
        topMainButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        listModel = new DefaultListModel<>();
        list = new JList<>();
        list.addMouseListener(
                new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent e)
                    {
                        listClicked();
                    }
                });

        JButton addToList = new JButton("Add");
        addToList.addActionListener((ActionEvent e) -> addFieldToListButtonClicked());
        removeFromList = new JButton("Remove");
        removeFromList.setEnabled(false);
        removeFromList.addActionListener((ActionEvent e) -> removeFromListButtonClicked());

        list.setModel(listModel);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setVisible(true);

        goButton = new JButton("GO");
        goButton.setEnabled(false);
        goButton.addActionListener((ActionEvent e) -> goButtonClicked());

        clearFilterButton = new JButton("Clear search");
        clearFilterButton.setEnabled(false);
        clearFilterButton.addActionListener((ActionEvent e) -> clearFilter());

        searchText = new JTextField(10);
        searchText.addActionListener((ActionEvent e) -> clearSearchText());


        //topMainPanel.add(addedFieldsList);
        typeFieldsCombo = new JComboBox<>(typeFields);

        JLabel l = new JLabel("Select type field: ");
        list.setVisibleRowCount(5);

        JLabel filterL = new JLabel("Select filter type: ");


        topMainButtonPanel.add(removeFromList, gbc);
        gbc.gridx++;
        topMainButtonPanel.add(filterL, gbc);
        gbc.gridx++;
        topMainButtonPanel.add(filterType, gbc);
        gbc.gridx -= 3;
        gbc.gridy++;
        topMainButtonPanel.add(clearFilterButton, gbc);
        gbc.gridy++;
        topMainButtonPanel.add(goButton, gbc);
        gbc.gridx++;
        gbc.gridy--;
        topMainPanel.add(listScroller);
        gbc.gridx++;
        topMainButtonPanel.add(l, gbc);
        gbc.gridx++;
        topMainButtonPanel.add(typeFieldsCombo, gbc);
        gbc.gridx++;
        topMainButtonPanel.add(searchText, gbc);
        gbc.gridx++;
        topMainButtonPanel.add(addToList, gbc);


    }

    // Remove the filters
    // Does not remove CheckBox filters
    private void clearFilter(){
        String [][] data = myModel.getData(listChoices.toString());
        String[] headings = myModel.getTableHeadings(listChoices.toString());
        listModel.clear();
        DefaultTableModel newModel = new DefaultTableModel(data, headings);
        table.setModel(newModel);
        sorter.setRowFilter(null);
        goButton.setEnabled(false);
        clearFilterButton.setEnabled(false);
        totalNumberOfMatchingRecordsLabel.setText("Total records in table: " + sorter.getViewRowCount());
    }

    private void clearSearchText(){
        searchText.setText("");
    }

    private void createTopMainPanel(){
        topMainPanel = new JPanel(new BorderLayout());
        topMainPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        topMainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Search table"));
        createTopMainButtons();
        topMainPanel.add(topMainButtonPanel, BorderLayout.EAST);
    }

    private void addFieldToListButtonClicked(){
        /* Code goes here */
        String type = (String) typeFieldsCombo.getSelectedItem();
        String s = searchText.getText();
        if (s.length() > 0) {
            String ss = type + "=" + s;
            listModel.addElement(ss);
            goButton.setEnabled(true);
        }
        searchText.setText("");
    }

    private void removeFromListButtonClicked(){
        int i = list.getSelectedIndex();
        if (!(i < 0) || (i > listModel.size())) {
            listModel.remove(i);
        }
        if (listModel.size() == 0){
            goButton.setEnabled(false);
            clearFilter();
        }
        removeFromList.setEnabled(false);
    }

    private void goButtonClicked(){
         table.setRowSorter(sorter);
         List<String> ArrListTypeField = Arrays.asList(typeFields);

         filterTable = new ArrayList<>(ArrListTypeField.size());
         RowFilter<TableModel, Object> rf;

         for (int i=0; i < listModel.getSize(); i++){
             String s = listModel.get(i);
             String[] sArr = s.split("=");
             filterTable.add(RowFilter.regexFilter("(?i)" + sArr[1], ArrListTypeField.indexOf(sArr[0])));
         }
         if (filterType.getSelectedItem() == "AND"){
             rf = RowFilter.andFilter(filterTable);
             sorter.setRowFilter(rf);
         }
         else if (filterType.getSelectedItem() == "OR"){
             rf = RowFilter.orFilter(filterTable);
             sorter.setRowFilter(rf);
         }
         totalNumberOfMatchingRecordsLabel.setText("Total records in table: " + sorter.getViewRowCount());
         clearFilterButton.setEnabled(true);
    }

    // Used when exporting the table to JSON.
    private String[][] getCurrentTableData(){
        TableModel m = table.getModel();
        int numberOfCols = m.getColumnCount();
        int numberOfRows = sorter.getViewRowCount();
        String[][] myData = new String[numberOfRows][numberOfCols];
        for (int y=0; y < numberOfCols; y++){
            for (int i=0; i < numberOfRows; i++) {
                myData[i][y] = (String) m.getValueAt(sorter.convertRowIndexToModel(i), y);
            }
        }
        return myData;
    }

    private void createMiddleMainPanel(){
        middleMainPanel = new JPanel(new BorderLayout());
        middleMainPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        middleMainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "View Data"));
        createLeftMiddleMainPanel();
        createRightMiddleMainPanel(listChoices.toString());
        createMiddleTopPanel();
        middleMainPanel.add(middleTopPanel, BorderLayout.NORTH);
        middleMainPanel.add(leftMiddleMainPanel, BorderLayout.WEST);
        middleMainPanel.add(rightMiddleMainPanel, BorderLayout.CENTER);
    }

    private void createMiddleTopPanel() {
        middleTopPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        middleTopPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        middleTopPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Table Stats"));
        JLabel totalNumberOfRecordsLabel = new JLabel("Total number of records: " + table.getRowCount());
        totalNumberOfMatchingRecordsLabel = new JLabel("Total records in table: " + table.getRowCount());
        middleTopPanel.add(totalNumberOfRecordsLabel, gbc);
        gbc.gridy += 5;
        middleTopPanel.add(totalNumberOfMatchingRecordsLabel, gbc);
    }

    private void createLeftMiddleMainPanel(){
        leftMiddleMainPanel = new JPanel(new GridLayout(0, 1));
        leftMiddleMainPanel.setBorder(BorderFactory.createEmptyBorder());
        leftMiddleMainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filter"));

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz0123456789!@??$%^&*(),./;'\\[]<>?:|{}`~".toCharArray();

        int i = 0;
        for (String s: typeFields){
            chkBox = new JCheckBox(s);
            chkBox.setMnemonic(alphabet[i]);
            chkBox.setSelected(true);
            chkBox.addItemListener(this);
            leftMiddleMainPanel.add(chkBox);
            ChkBox myChkBox = new ChkBox(alphabet[i], i, chkBox);
            checkBoxList.add(myChkBox);
            i++;
        }
        listChoices = new StringBuffer(myModel.getFullOptionString());
    }

    public void itemStateChanged(ItemEvent e){
        int index = 0;
        char c = '-';
        ItemSelectable source = e.getItemSelectable();

        for (ChkBox chkbox: checkBoxList){
            if (source == chkbox.getChkbox()){
                index = chkbox.getIndex();
                c = chkbox.getC();
            }
        }

        if (e.getStateChange() == ItemEvent.DESELECTED){
            c = '-';
        }
        listChoices.setCharAt(index, c);
        updateTable();

    }

    private void updateTable(){
        try {
            String[][] data = myModel.getData(listChoices.toString());
            String[] headings = myModel.getTableHeadings(listChoices.toString());
            DefaultTableModel newModel = new DefaultTableModel(data, headings);
            table.setModel(newModel);
            goButtonClicked();
        }
        catch (Exception e){
            errorMessage("You cannot hide every column. \n Please leave at least 1 column unhidden!");
        }
    }

    private void createRightMiddleMainPanel(String listChoices){
        rightMiddleMainPanel = new JPanel(new GridLayout(0, 1));
        rightMiddleMainPanel.setBorder(BorderFactory.createEmptyBorder());
        rightMiddleMainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Table"));
        rightMiddleMainPanel.setPreferredSize(new Dimension(1000, 500));
        String [][] data = myModel.getData(listChoices);
        String[] headings = myModel.getTableHeadings(listChoices);
        DefaultTableModel model = new DefaultTableModel(data, headings);
        sorter = new TableRowSorter<>(model);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setBounds(10, 0, 457, 103);
        JScrollPane jp = new JScrollPane(table);
        jp.setBounds(10,0, 457, 103);
        jp.setVisible(true);
        add(jp);
        rightMiddleMainPanel.add(jp);

        this.table = table;
    }

    // Error Popups
    private void errorMessage(String message){
        JOptionPane.showMessageDialog(new JFrame(), message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}