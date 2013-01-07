package gui;

import data.processing.FileWriterReader;
import data.recovery.CommunicationPort;
import gnu.io.CommPortIdentifier;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * The Graphic User Interface class. 
 * 
 * @author aviggiano <aviggiano@centrale-marseille.fr>
 * @version 1.0
 * @since 2012-07
 */

public class GUI extends JFrame implements ActionListener, MouseListener{
    // Class variables
    
    CommunicationPort communicationPort;
    public static FileWriterReader rw;    
    
    private static DateFormat dateFormat;
    private static Date date;    
    private InetAddress addr;
    private byte[] ipAddr;
    private String hostname;    
    
    public static final int MSG = 0;
    public static final int INFO = 1;
    public static final int ERROR = 2;
    
    public static final int LEFT_PANE = 0;
    public static final int RIGHT_PANE = 1;
    
    private String[] lookAndFeel = {"Windows", "Nimbus", "Motif", "Ocean"}; // look and feel "Windows". essayez "Nimbus", "Steel", "Ocean", etc.
    private JFileChooser fileChooser;
    
    // the resolution of the window
    private double resWidth = 3;
    private double resHeight = 2;
    
    private JMenuItem MIAbout;
    
    private JMenuBar menuBar;
  
    private JMenu menuHelp;
    
    private JPopupMenu popUpMenu; // not used, but can be implemented in the future.
    protected JTextArea textArea;
    protected JScrollPane scrollPane;
    protected Toolkit toolkit = Toolkit.getDefaultToolkit();
    protected Dimension screenSize = toolkit.getScreenSize();
    protected Dimension frameSize;
    
    private JMenu menuFile;
    private JMenuItem MISave;
    private JMenuItem MISaveAs;
    private JMenuItem MIClose;    
    
    private JMenu menuEdit;
    
    private JMenu menuLAF;
    private JRadioButtonMenuItem MIWindowsLAF;
    private JRadioButtonMenuItem MINimbusLAF;
    private JRadioButtonMenuItem MIMetalLAF;
    private ButtonGroup buttonGroupLAF;
    
    private JMenu menuAbout;
    
    static protected JComboBox comboBoxCOMPort;
    static protected JTextField textFieldBaudRate;
    
    static protected JButton buttonConnect;
    static protected JButton buttonDisconnect;
    private MainPane mainPane;
    private boolean savedForTheFirstTime = true;
    private String savedAsFileName = "";
    
    /**
     * Constructor that does all the work, subdivided in many void methods.
     */
    public GUI() {
        //demarre la COM Port
        demarrer(); 
        //set title, size and location
        setTitleSizeAndLocation();       
        //set look and feel
        setLookAndFeel(lookAndFeel[0]);        
        //prend le container du JFrame
        Container container = this.getContentPane();        
       
        //on cree les differentes parties du conteneur
            //partie NORTH
        creePartieNORTH();
            //partie CENTER
        creePartieCENTER();

        //met tout ca dans le conteneur avec un BorderLayout
        metToutCaDansLeConteneurAvecUnBorderLayout(container);        
        
        //show
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand(); // this should be evited, because if we change the name of the ActionCommand, the command will change as well.
        Object source = ae.getSource();
        
        if (source.equals(MIWindowsLAF)) {
            setLookAndFeel(lookAndFeel[0]);
            SwingUtilities.updateComponentTreeUI(this); 
        }
        
        if (source.equals(MIClose)){
            System.exit(0);            
        }
        
        if (source.equals(MISave)){
            saveData();
        }
        
        if (source.equals(MISaveAs)){
            saveDataAs();
        }
        
        if (source.equals(MIAbout)){
            about();
        }        

        if (command.equals("Nimbus Look & Feel")){
            setLookAndFeel(lookAndFeel[1]);
            SwingUtilities.updateComponentTreeUI(this); 
        }

        if (command.equals("Metal Look & Feel")){
            setLookAndFeel("Metal");
            SwingUtilities.updateComponentTreeUI(this);
        }
                
        if (source.equals(buttonConnect)) {
            communicationPort = new CommunicationPort(comboBoxCOMPort.getSelectedItem().toString(), Integer.parseInt(textFieldBaudRate.getText()));
            
            try {
                communicationPort.connect(comboBoxCOMPort.getSelectedItem().toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        
        if (source.equals(buttonDisconnect)){
            communicationPort.closePort();
        }
                
                
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mousePressed(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * References the instance of the class CommunicationPort.
     */
    private void demarrer() {
         communicationPort = new CommunicationPort();
         setHostValues();
         setFileWriterReader();
    }

    /**
     * Set the title, size and location of the window. 
     * 
     * At the version 1.0, it's title is 'Xbee Listener', the size is defined by
     * the resolution (see Class variables), and the location is centered in the
     * screen.
     */
    private void setTitleSizeAndLocation() {
        setTitle("Xbee Listener");
        frameSize = new Dimension ((int)(screenSize.width/resWidth), (int)(screenSize.height/resHeight));
        
        setSize(frameSize); 
        setLocation((int)((screenSize.width - frameSize.width)/2), (int)((screenSize.height - frameSize.height)/2));
        
        /**
         * Exit on close. If we want to add another behavior to the closing 
         * action, it should be included here.
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });        
    }

    /**
     * Sets the look and feel of the window. 
     * 
     * @param string the name of the look and feel.
     */
    private void setLookAndFeel(String string) {
                try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (string.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Creates the NORTH part of the window, with the menu bar, the combo boxes 
     * and the buttons.
     */
    private void creePartieNORTH() {
        //cree le menu avec les icones
        creeMenuAvecIcones();
        //cree les boutons avec icones
        creeBoutonsAvecIcones();
        //cree les combo box
        creeLesComboBox();
        //met les menus dans leur place
        metLesMenusDansLeurPlace();
        //met les listeners pour les boutons et les menus
        metLesListenersPourLesBoutonsEtLesMenus();
        //met une barre d'outils
        //metUneBarreDOutils();        
    }

    /**
     * Creates the CENTER part of the window -- everything but the menu bar.
     */
    private void creePartieCENTER() {
        //cree le pane ou toutes les actions auront lieu
        creeLePaneOuToutesLesActionsAurontLieu();
    }

    /**
     * Creates the menu items. 
     */
    private void creeMenuAvecIcones() {
        //on doit mettre a chaque fois getClass().getRessource(URL) pour construire le JAR
        //si on ne met que ImageIcon(URL) ca ne marche pas
        MISave = new JMenuItem("Enregistrer", new ImageIcon(this.getClass().getResource("images/save16.gif")));
        MISave.setToolTipText("Enregistre les bases de donnees");
        MISave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        MISaveAs = new JMenuItem("Enregistrer sous...", new ImageIcon(this.getClass().getResource("images/saveAs16.gif")));
        MISaveAs.setToolTipText("Enregistre les bases de donnees avec un nom quelconque");

        MIAbout = new JMenuItem("A propos",new ImageIcon(this.getClass().getResource("images/help16.png")));
        MIClose = new JMenuItem("Fermer");        
     
        //pour le LookAndFeel
        MIWindowsLAF = new JRadioButtonMenuItem("Windows Look & Feel");
            MIWindowsLAF.setSelected(true); //cette methode peut etre ameilleure (si on initialize le lookAndFeel comme qqch d'autre que le [0], ca va cocher toujours le Windows LAF
        MINimbusLAF = new JRadioButtonMenuItem("Nimbus Look & Feel");
        MIMetalLAF = new JRadioButtonMenuItem("Metal Look & Feel");
            //on les met dans un ButtonGroup (on ne peut choisir qu'un LAF a la fois)
            buttonGroupLAF = new ButtonGroup();
            buttonGroupLAF.add(MIWindowsLAF);
            buttonGroupLAF.add(MINimbusLAF);
            buttonGroupLAF.add(MIMetalLAF);    
            
       creeLesMenus();
    }

    /**
     * Creates the menus.
     */
    private void creeLesMenus() {
        //menus
        menuBar = new JMenuBar();
        menuFile = new JMenu("Fichier");
        
        menuLAF = new JMenu ("Look & Feel");
        menuHelp = new JMenu("Aide");        
    }

    /**
     * Creates the buttons.
     */
    private void creeBoutonsAvecIcones() {
        buttonConnect = new JButton("Connect");
        buttonDisconnect = new JButton("Disconnect");
    }

    /**
     * Inserts the menu items into their respective menus.
     */
    private void metLesMenusDansLeurPlace() {
        //menu File
        menuFile.add(MISave);
        menuFile.add(MISaveAs);
        // menuFile.add(MIOpen); // not used
        menuFile.add(MIClose);  
        
//        //menu Edit
//        menuEdit.add(MIImport);
//        menuEdit.add(MIDelete);
//        menuEdit.add(new JSeparator());
//        menuEdit.add(MIFind);   
        
        //menu Look And Feel
        menuLAF.add(MIWindowsLAF);
        menuLAF.add(MINimbusLAF);
        menuLAF.add(MIMetalLAF);           
        
        //menu Help
        menuHelp.add(MIAbout);      
        
        //barre de menus
        menuBar.add(menuFile);
//        barreDeMenus.add(menuEdit);
        menuBar.add(menuLAF);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);        
    }

    /**
     * Adds the ActionListeners to buttons and menus.
     */
    private void metLesListenersPourLesBoutonsEtLesMenus() {
        // les menu itens
        MISave.addActionListener(this);
        MISaveAs.addActionListener(this);
//        MIOpen.addActionListener(this);
        MIClose.addActionListener(this);
        MIAbout.addActionListener(this);

//        MIImport.addActionListener(this);
//        MIDelete.addActionListener(this);
//        MINew.addActionListener(this);
//        MIFind.addActionListener(this);
        
        MIWindowsLAF.addActionListener(this);
        MINimbusLAF.addActionListener(this);
        MIMetalLAF.addActionListener(this);
        // les boutons
        buttonConnect.addActionListener(this);
        buttonDisconnect.addActionListener(this);
        // set enabled pour les menus
        MISave.setEnabled(true);
        MISaveAs.setEnabled(true);   
    }

    /**
     * Creates an instance of the MainPane.
     * 
     * @see MainPane.
     */
    private void creeLePaneOuToutesLesActionsAurontLieu() {
        mainPane = new MainPane();
    }

    /**
     * Wraps the MainPane into the container and sets it to the CENTER of a BorderLayout.
     * 
     * @param container the container of the Frame.
     */
    private void metToutCaDansLeConteneurAvecUnBorderLayout(Container container) {
        container.add(mainPane, BorderLayout.CENTER);        
    }

    /**
     * Creates the combo boxes.
     */
    private void creeLesComboBox() {
        comboBoxCOMPort = cb1();
        
        textFieldBaudRate = new JTextField ("9600");
        textFieldBaudRate.setColumns(8);
    }
    
    /**
     * Creates the combo box with the available COM Ports.
     * 
     * @return a combo box with the available COM Ports.
     */
    private JComboBox cb1() {
        ArrayList<String> ports = new ArrayList<String>();

        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                ports.add(portId.getName());
            }
        }

        return new JComboBox(ports.toArray((new String[ports.size()])));
    }

    /**
     * Appends a text message of type MSG into the desired text pane.
     * 
     * @param text the text message.
     * @param pane the pane (right or left).
     */
    public static void append (String text, int pane){	
        append(text, MSG, pane); 
    }    
    
    /**
     * Appends a text message of a specific type (MSG, INFO, ERROR, ...) into the desired text pane.
     * 
     * @param text the text message.
     * @param messageType the type of the message.
     * @param pane the pane (right or left).
     */
    public static void append(String text, int messageType, int pane) {
        StyledDocument doc;

        if (pane == RIGHT_PANE)
            doc = MainPane.textPaneSorted.getStyledDocument();
        else
            doc = MainPane.textPane.getStyledDocument();
        
        Color color;
        
        if (messageType == ERROR) color = Color.RED;
        else if (messageType == INFO) color = Color.BLUE;
        else if (messageType == MSG) color = Color.BLACK;
        else color = Color.BLACK;
        
        // Define a keyword attribute
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, color);

        // Add the text to the pane
        try{
            doc.insertString(0, text, null );
            doc.insertString(doc.getLength(), text, keyWord );
        }
        catch(Exception e) { System.out.println(e); }
    }        

    /**
     * Save the data to file.
     */
    private void saveData() {
        String filename = (!savedAsFileName.equals("")) ? savedAsFileName : (hostname + "_donnees");
        
        if (savedForTheFirstTime) {
             rw = new FileWriterReader(filename);
             //rw.write(dateFormat.format(date) + " @ " + hostname + "\n");
             
             savedForTheFirstTime = false;
        }
        else {
            rw = new FileWriterReader(filename);
        }
        
        GUI.append("\nDonnees enregistrees avec succès dans le fichier ''" + filename + rw.EXTENSION + "''.", INFO, LEFT_PANE);
    }
    
    /**
     * Save the data to a file of specific name.
     */
    private void saveDataAs() {
        fileChooser = new JFileChooser(".");
        fileChooser.setSelectedFile(new File("donnees"));
            try{
                int returnValue = fileChooser.showSaveDialog(this);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    
                    File file = fileChooser.getSelectedFile();
                    savedAsFileName = file.getName();
                    
                    rw  = new FileWriterReader(savedAsFileName);
                    //rw.write(dateFormat.format(date) + " @ " + hostname + "\n");

                    this.append("\nDonnees enregistrees avec succès dans le fichier ''" + savedAsFileName + rw.EXTENSION + "''.", INFO);
                }
                else {
                    this.append("\nEnregistrement annulé.", LEFT_PANE); 
                }                            
            }
            catch (Exception e){
                System.out.println(e);
            }    
        
    }
    
    /**
     * Set host values (name and IP address).
     */
    private void setHostValues(){
        
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = new Date();
                  
        try {
                    addr = InetAddress.getLocalHost();

                    // Get IP Address
                    ipAddr = addr.getAddress();

                    // Get hostname
                    hostname = addr.getHostName();
                } catch (UnknownHostException e) {
                    
        }        
    }

    /**
     * Sets up the FileWriterReader.
     * 
     * There are too many redundant methods. This should be revised/cleaned up.
     */
    private void setFileWriterReader() {
        String filename = hostname + "_donnees";
        rw  = new FileWriterReader(filename);
    }

    /**
     * Sets up the About section.
     */
    private void about() {
        JPanel pane = new JPanel();
        JLabel presentation = new JLabel ("Programme de récupératin de donnees Xbee");
        JLabel blank = new JLabel(" ");
        JLabel version = new JLabel("Version 1.0");
        
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
        pane.add(presentation);
        pane.add(blank);
        pane.add(version);
        
        JOptionPane.showConfirmDialog(this, pane , "Projet Transverse NEZ", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }
}
