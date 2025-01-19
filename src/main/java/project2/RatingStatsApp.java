package project2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


/**
 * DO NOT MODIFY: Exploratory Data Analysis of Amazon Product Reviews
 * @author tesic
 * @author toufik
 * @version 2.0
 */
public class RatingStatsApp{

    // Used to read from System's standard input
    // Private objects to be accessed between functions

    private static JFrame mainMenu = new JFrame("Main Menu");		// Frame 1
    private static JFrame statsMenu = new JFrame("Computed Stats");  // StatsFrame
    private static JFrame dataIDFrame = new JFrame();					    // Frame 2
    private static JFrame frame3 = new JFrame("Computed Report");	// Frame 3
    private static DatasetHandler dh;

    public static void main(final String[] args) {
		mainMenu.setLocationRelativeTo(null);
		
		dataIDFrame.setLocationRelativeTo(null);
		frame3.setLocationRelativeTo(null);
        panelOne();
    }

    /**
     * First Panel in Gui, Serves as Main Menu, shows two options "Display Computed Stats..." or "Add New Collection.."
     * Displays current Datasets that have computed stats
     * Calls next two panels accordingly
     * @throws IOException
     */
    public static void panelOne(){
        try{
            dh = new DatasetHandler();
            int dbSize = dh.getDataSets();
            mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            StringBuilder startUpLabel = new StringBuilder();

            startUpLabel.append("<html>Loading the datasets from:"  + dh.getFolderPath() + "<BR>");
            startUpLabel.append("     " + dbSize + " datasets available" + "<BR></html>");
            JOptionPane.showMessageDialog(mainMenu, startUpLabel.toString(), "Message", JOptionPane.PLAIN_MESSAGE);

            JButton  statsButton = new JButton("Display Computed Stats for specific DataID");
            statsButton.setFont(new Font("Serif", Font.BOLD, 15));
            statsButton.setBackground(new Color(255,0,213));
            statsButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    mainMenu.setVisible(false);
                    if (dbSize < 1){
                        JOptionPane noDataBase = new JOptionPane();
                        noDataBase.showMessageDialog(mainMenu, "<html>There is no data to select from<BR> Please select another option</html>", "Empty Database", JOptionPane.ERROR_MESSAGE);
                        mainMenu.setVisible(true);
                    }
                    else {
                        JPanel statsPanel = new JPanel();
                        statsMenu.setLayout(new BorderLayout());
                        String[] firstRow = new String[4]; //Header Row in Table
                        Object[][] content = new String[dbSize][4]; //Body of Table

                        firstRow[0] = "DataID";
                        firstRow[1] = "NUMBER";
                        firstRow[2] = "RATINGS";
                        firstRow[3] = "STATS";
                    
                        int i = 0;
                        Scanner in = new Scanner(dh.printDB());
                        in.nextLine();
                        while (in.hasNext()){ //Creates the table of an existing dataset
                            String[] curLine = in.nextLine().split(",");
                            content[i][0] = curLine[0];
                            content[i][1] = curLine[1];
                            content[i][2] = curLine[2];
                            content[i][3] = curLine[3];
                            ++i;
                        }
                        
                        //Text Field for User to Input
                        JTextField exDataID = new JTextField("Enter Data ID");
                        
                        JLabel label = new JLabel();
                        label.setText("Please Enter Existing DataID from Dataset");
                        DefaultTableModel model = new DefaultTableModel(content, firstRow);
                        JTable dataset = new JTable();
                        dataset.setModel(model);
                        dataset.setBackground(new Color(249,156,237)); // Rose Color
                        JScrollPane tableViewer = new JScrollPane();
                        tableViewer.setViewportView(dataset);

                        dataset.setDefaultEditor(Object.class, null);
                        dataset.getColumnModel().getColumn(0).setPreferredWidth(50);
                        dataset.getColumnModel().getColumn(1).setPreferredWidth(50);
                        dataset.getColumnModel().getColumn(2).setPreferredWidth(50);
                        dataset.getColumnModel().getColumn(3).setPreferredWidth(200);
                        dataset.setPreferredSize(dataset.getPreferredScrollableViewportSize());
                        JPanel statsTable = new JPanel();

                        statsTable.add(tableViewer, "Center");
                        statsTable.setPreferredSize(dataset.getPreferredSize());
                        
                        statsPanel.add(exDataID, "East");
                        statsPanel.add(label, "West");

                        //submits the users input and takes them to Panel3
                        JButton submitButton = new JButton("Submit");
                        submitButton.setFont(new Font("Serif", Font.BOLD, 15));
                        submitButton.setBackground(new Color(255,0,213));
                        submitButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e){
                                final String userDataID = exDataID.getText();
                                if (dh.checkID(userDataID)){
                                    statsMenu.setVisible(false);

                                    try{
                                        var d = dh.populateCollection(userDataID);
                                            if (d.hasStats()){
                                                String[] buttons = {"Use Existing Data", "Create New Data", "Cancel"};
                                                int option = JOptionPane.showOptionDialog(submitButton, "Choose One of the Following", "What Do", JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[3]);
                                                if(option == 3){
                                                    statsMenu.setVisible(true);
                                                }
                                                else if(option == 2){
                                                    d.computeStats();
                                                    dh.saveStatsToFile(userDataID);
                                                } 
                                                dh.saveStatsToFile(userDataID);
                                            }
                                            else{
                                                d.computeStats();
                                                dh.saveStatsToFile(userDataID);                                                
                                            }
                                                dh.saveReportToFile(userDataID, 20);
                                                dh.writeDBToFile();                                            
                                    } catch (IOException exe ){
                                        System.out.println(exe.getMessage() + " Error Here " + exe.getStackTrace());
                                    }
                                    panelThree(userDataID);
                                } else{
                                    JOptionPane.showMessageDialog(submitButton, "Unique DataID: " + userDataID + " Does not exist in the Dataset", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });

                        //takes the user to the main menu
                        JButton firstMenu = new JButton("Main Menu");
                        firstMenu.setFont(new Font("Serif", Font.BOLD, 15));
                        firstMenu.setBackground(new Color(255,0,213));
                        firstMenu.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e){
                                statsMenu.setVisible(false);
                                mainMenu.setVisible(true);
                            }
                        });

                        firstMenu.setMaximumSize(new Dimension(200,80));

                        JPanel buttonPanel = new JPanel();
                        buttonPanel.add(submitButton);
                        buttonPanel.add(firstMenu);
                        
                        statsMenu.add(buttonPanel, "South"); 

                        statsMenu.add(statsPanel, "Center");
                        statsMenu.add(statsTable, "North");

                        statsMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        statsMenu.setSize(600,550);
                        statsMenu.setVisible(true);
                    }
                } //end of statsMenu
            });

            JButton newStats = new JButton("Add new collection and compute stats");
            newStats.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    mainMenu.setVisible(false);
                    panelTwo();
                }
            });
            newStats.setFont(new Font("Serif", Font.BOLD, 15));
            newStats.setBackground(new Color(255,0,213));

            JPanel mainPanel = new JPanel();
            JLabel label = new JLabel();
            label.setText("Choose one of the following functions:");

            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(label, "North");
            mainPanel.add(statsButton, "West");
            mainPanel.add(newStats, "East");
        
            mainMenu.getContentPane().add(mainPanel);
            mainMenu.add(mainPanel);
            mainMenu.pack();
            mainMenu.setVisible(true);
        }
        catch (IOException e){
            System.out.println(e.getMessage() + " " + e.getStackTrace());
        }
    } //end of PanelOne

    /**
    * Second Panel in Gui, called when second option is selected in main menu (panelOne)
    * Allows User to enter new unique DataID and name of .csv file
    * Shows error message dialog if unique DataID already exists, and success message dialog if 
    * new DataID is added successfully
    * @throws IOException
    */
    public static void panelTwo(){
         // Select dataset

        //Create pannel for user to specify file name and source file name
        dataIDFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //dataIDFrame.getContentPane().setLayout(new BoxLayout(dataIDFrame.getContentPane(), BoxLayout.Y_AXIS));
        
        JPanel DataIDPanel = new JPanel(); 
        DataIDPanel.setLayout(new BoxLayout(DataIDPanel, BoxLayout.Y_AXIS));

        //label stating purpose of frame
        JLabel DataIDLabel = new JLabel("Please enter a Unique DataID and File Name (.csv)");
                
        //first field 
        JTextField testName = new JTextField("DataID");
        
                
        //second field 
        JTextField testFile = new JTextField("File Name");
        
        
        int j = 20;
        boolean found = false;
        //submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Serif", Font.ITALIC, 15));
        submitButton.setBackground(new Color(255, 0, 213));

        //takes the user to the main menu
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Serif", Font.ITALIC, 15));
        mainMenuButton.setBackground(new Color(255, 0, 213)); 
        
        mainMenuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                dataIDFrame.setVisible(false);
                mainMenu.setVisible(true);
            }
        });

        DataIDPanel.add(DataIDLabel);
        DataIDPanel.add(testName);
        DataIDPanel.add(testFile);
        DataIDPanel.add(submitButton);
        DataIDPanel.add(mainMenuButton);

        submitButton.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                final String enteredDataID = testName.getText();
                final String input = testFile.getText();

                if (!(dh.checkID(enteredDataID))) {
                    
                    boolean check = dh.addCollection(enteredDataID, input);
                    
                    if(check){
                        //found = true;
                        JOptionPane newCollection = new JOptionPane();
                        newCollection.showMessageDialog(dataIDFrame, "Collection " + enteredDataID + " added", "Successfully Added Collection", JOptionPane.INFORMATION_MESSAGE);
                        
                        try{
                            
                            var d = dh.populateCollection(enteredDataID);
                            if(found){
                                d.computeStats();
                                dh.saveStatsToFile(enteredDataID);
                                dh.saveReportToFile(enteredDataID, j);
                            }
                            
                            dataIDFrame.setVisible(false);
                            panelThree(enteredDataID); //dhruves panel 
                        }
                        catch (IOException exe ){
                            System.out.println(exe.getMessage() + " Error Here " + exe.getStackTrace());
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(dataIDFrame, "File not Found, Try again" + "/n", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else{
                        
                    JOptionPane.showMessageDialog(dataIDFrame, "DataID is Already in the Database", "DataID Check", JOptionPane.ERROR_MESSAGE);
                    
                }
                
            }
        });

        
        dataIDFrame.add(DataIDPanel);
        dataIDFrame.pack();
        dataIDFrame.getContentPane().add(DataIDPanel);
        dataIDFrame.setVisible(true);
    
    }
	/**
	 * Loads the computed report into frame
	 * Allows user to exit or restart program entirely
	 * @param userInput	unique dataset indentifier
	 * 
	 */
    public static void panelThree(String userInput){ // Create report
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        final int buttonHeight = 100;
        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new GridLayout(1, 2));
        firstPanel.setMaximumSize(new Dimension(800, buttonHeight + 20));
        // Buttons
        JButton exit = new JButton("Exit");
        JButton restart = new JButton("Start Again");       

        exit.setPreferredSize(new Dimension(50, buttonHeight));
        restart.setPreferredSize(new Dimension(50, buttonHeight));
        firstPanel.add(exit);
        firstPanel.add(restart);
        buttons.add(firstPanel);
        frame3.setResizable(true);

        exit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //(parentComponent, message, title, messageType)
                JOptionPane.showMessageDialog(frame3, "Goodbye!", "Exiting...", JOptionPane.CLOSED_OPTION);
                System.exit(0);
            }
        });

        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                frame3.setVisible(false);
                panelOne();
            }
        });
		// Set the fonts of buttons
		exit.setFont(new Font("Serif", Font.ITALIC ,20));
		restart.setFont(new Font("Serif", Font.ITALIC, 20));
		// Sets background for the buttons
		exit.setBackground(new Color(255, 0, 213));
		restart.setBackground(new Color(255, 0, 213));
		// Calls createReport inside saveReportToFile
		// Located inside DatasetHandler
		
        StringBuilder report = new StringBuilder(DataAnalysis.createReport(dh.getCollection(userInput).getRatingStat(),20));
        String [] firstSplit = report.toString().split(DataAnalysis.LINE_SEP);
        int listSize = firstSplit.length;
		String[][] filteredReport = new String[listSize - 1][4];

        String[] header = DataAnalysis.SUMMARY_HEADER.split(",");

		int j = 1;
		for (int i = 0; j < listSize; ++i){
            if(firstSplit[j].contains(",")){
				String[] dataList = firstSplit[j].split(",");
				filteredReport[i][0] = dataList[0];
                filteredReport[i][1] = dataList[1];
                filteredReport[i][2] = dataList[2];
                filteredReport[i][3] = dataList[3];
			} else{
                filteredReport[i][0] = firstSplit[j];
                filteredReport[i][1] = null;
                filteredReport[i][2] = null;
                filteredReport[i][3] = null;
            }
            ++j;
        }
		DefaultTableModel model = new DefaultTableModel(filteredReport, header);
        JTable table = new JTable();
        table.setModel(model);
        JScrollPane tableViewer = new JScrollPane();
        tableViewer.setViewportView(table);
        table.setDefaultEditor(Object.class, null);
		table.getColumnModel().getColumn(0).setPreferredWidth(500);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
		// secondPanel deals with the JScrollPane
		JPanel secondPanel = new JPanel();
		secondPanel.add(tableViewer);
        JScrollPane scrPane = new JScrollPane(secondPanel);
        scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        secondPanel.setMinimumSize(new Dimension(900,600));
        table.setMinimumSize(new Dimension(900,600));
        scrPane.setMinimumSize(new Dimension(900, 600));

        frame3.setContentPane(buttons);
        frame3.getContentPane().add(scrPane);

        frame3.setSize(520, 600);
        frame3.setMinimumSize(new Dimension(520,600));
        frame3.setLocationRelativeTo(null);
		frame3.pack();
        frame3.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame3.setVisible(true);
		
    }

}// end class