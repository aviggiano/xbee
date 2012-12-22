package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This is the main pane of the Graphic User Interface.
 * 
 * The upper pane is composed by the combo boxes and buttons, and the bottom 
 * pane is composed by two text panes. The left one shows the entire 
 * transmission flow, and the right one shows only what is being written on the
 * file (after processing the message), that is, the sorted message.
 * 
 * @author aviggiano <aviggiano@centrale-marseille.fr>
 * @version 1.0
 * @since 2012-07
 */
class MainPane extends JPanel {
    // Class variables
    
    private JPanel panelHaut;
    private JPanel panelBas;
    
    private JLabel labelCOMPort;
    private JLabel labelBaudRate;
    private JLabel labelLog;
    protected static JTextPane textPane;
    protected static JTextPane textPaneSorted;
    private Font font;
    private JPanel dividedPane;
    private DefaultCaret caret;
    private DefaultCaret caretSorted;
    
    /**
     * Constructor that does all the work, subdivided in many void methods.
     */
    public MainPane()  {
        super(new GridLayout(1,0));
        
        //creeLesLabels()
        creeLesLabels();
        //cree le panelHaut
        creeLePanelHaut();
        
        //cree le panelBas
        creeLePanelBas();
        
        //met les panel Haut et Bas dans un JSplitPane vertical
        metLesPanelHautEtBasJSplitPaneVertical();
        
        //Add the split pane to this panel.
        add(dividedPane);   
    }

    /**
     * Creates the labels of the upper pane.
     */
    private void creeLesLabels() {
        labelCOMPort = new JLabel ("COM Port");
        labelBaudRate = new JLabel ("Baud Rate");
        labelLog = new JLabel ("Log");
    }

    /**
     * Creates the upper pane.
     */
    private void creeLePanelHaut() {
        panelHaut = new JPanel();
        panelHaut.setLayout (new BoxLayout (panelHaut, BoxLayout.Y_AXIS));
        
        
        JPanel ligne1 = new JPanel();
        JPanel ligne2 = new JPanel();
        
        ligne1.setLayout (new FlowLayout());
        ligne2.setLayout (new FlowLayout());
        
        
        ligne1.add (labelCOMPort);
        ligne1.add (GUI.comboBoxCOMPort);
        ligne1.add (labelBaudRate);
        ligne1.add (GUI.textFieldBaudRate);
        
        
        ligne2.add (GUI.buttonConnect);
        
        ligne2.add (GUI.buttonDisconnect);
        
        /**
         * This could be improved. These glues does not have a very nice 
         * esthetics, but the were a very easy and fast solution to the problem
         * of disposing the components in the pane.
         */
        panelHaut.add(Box.createVerticalGlue());
        panelHaut.add(ligne1);
        panelHaut.add(Box.createVerticalGlue());
        panelHaut.add(ligne2);
        panelHaut.add(Box.createVerticalGlue());
    }

    /**
     * Creates the bottom pane.
     */
    private void creeLePanelBas() {
        //cree la zone de texte
        creeLaZoneDeTexte();    
        caret = (DefaultCaret)textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);   
        
        caretSorted = (DefaultCaret)textPaneSorted.getCaret();
        caretSorted.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        panelBas = new JPanel();
        panelBas.setLayout(new GridLayout(1,2));
        panelBas.add(textPane);
        panelBas.add(textPaneSorted);
    }

    /**
     * Creates the text panes (left and right).
     */
    private void creeLaZoneDeTexte() {
        textPane = new JTextPane();
        textPane.setSize( textPane.getPreferredSize() );
        
        textPaneSorted = new JTextPane();
        textPaneSorted.setSize(textPaneSorted.getPreferredSize());
        
        font = new Font("Arial", Font.PLAIN, 12);        
    }

    /**
     * Wraps everything up in a divided pane.
     */
    private void metLesPanelHautEtBasJSplitPaneVertical() {
          dividedPane = new JPanel();
          dividedPane.setLayout(new GridLayout(0,1));
          dividedPane.add(panelHaut, BorderLayout.NORTH);
          dividedPane.add(panelBas, BorderLayout.SOUTH);
          panelBas.add(new JScrollPane(textPane));
          panelBas.add(new JScrollPane(textPaneSorted));
    }
}
