package xbeelistener;

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
 *
 * @author aviggiano <aviggiano@centrale-marseille.fr>
 * @version 1.0
 * @since 2012-07-01
 */
class MainPane extends JPanel {

    private JPanel panelHaut;
    private JPanel panelBas;
    
    private JLabel labelCOMPort;
    private JLabel labelBaudRate;
    private JLabel labelLog;
    protected static JTextPane textPane;
    protected static JTextPane textPaneSorted;
    private Font font;
    private JPanel splitPane; //c'est plus un JSplitPanel. 
    private DefaultCaret caret;
    private DefaultCaret caretSorted;
    
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
        add(splitPane);   
    }

    private void creeLesLabels() {
        labelCOMPort = new JLabel ("COM Port");
        labelBaudRate = new JLabel ("Baud Rate");
        labelLog = new JLabel ("Log");
    }

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
        
        panelHaut.add(Box.createVerticalGlue());
        panelHaut.add(ligne1);
        panelHaut.add(Box.createVerticalGlue());
        panelHaut.add(ligne2);
        panelHaut.add(Box.createVerticalGlue());
        
        
        
    }

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

    private void creeLaZoneDeTexte() {
        textPane = new JTextPane();
        textPane.setSize( textPane.getPreferredSize() );
        
        textPaneSorted = new JTextPane();
        textPaneSorted.setSize(textPaneSorted.getPreferredSize());
        
        font = new Font("Arial", Font.PLAIN, 12);
        
    }

    private void metLesPanelHautEtBasJSplitPaneVertical() {
          splitPane = new JPanel();
          splitPane.setLayout(new GridLayout(0,1));
          splitPane.add(panelHaut, BorderLayout.CENTER);
          splitPane.add(panelBas, BorderLayout.SOUTH);
          panelBas.add(new JScrollPane(textPane));
          panelBas.add(new JScrollPane(textPaneSorted));
    }
    
    
}
