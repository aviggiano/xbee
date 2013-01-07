package data.recovery;

import data.processing.CSVParser;
import data.processing.Preprocesser;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import gui.GUI;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * This file contains three classes : the CommunicationPort, which opens the 
 * serial communication with the COM Port, and the Serial Writer and Reader, 
 * two static classes that manipulates it. 
 * 
 * This java file contains several details that could be improved : 
 * - it has three non anonymous classes in a single file, that could be 
 *   separated in the future (for the moment this was not done because it wasn't
 *   necessary);
 * - it invokes several times GUI methods and variables, while a priori the 
 *   serial communication should be independent of a Graphic User Interface : 
 *   one may want to use only the terminal to show the data transmission, and a 
 *   high influence of GUI specificities could warm this application. A solution 
 *   consists in creating another class only to manage these interactions. 
 * - it has a poor threading management. The two instances of SerialWriter and 
 *   SerialReader created in the connect method have the Runnable interface, but
 *   no threading control is done to prevent unexpected behavior. As the code
 *   was simply copied from an online example and not revised at first, many 
 *   modifications were done only to adjusts bugs and errors, instead of 
 *   creating a solid and perennial solution. For instance, the weird behavior
 *   of the read method of the SerialReader class, which returns a 'randomly' 
 *   broken message, {"Hel", "lo ", "W", "or", "ld"} instead of {"Hello World"},
 *   was not resolved by this time. We believe this is caused by the Xbee buffer
 *   filling, which sends only a piece of message at a time. A better study of
 *   this case could reveal their real causes, and this could be done revising
 *   this code in terms of threading management.
 * 
 * @author aviggiano <aviggiano@centrale-marseille.fr>
 * @version 1.0
 * @since 2012-07
 */
public class CommunicationPort {
    // Class variables
    
    private int baud;
    private static String COMPortNumber;
    static DateFormat dateFormat;
    static Date date;
    private static String filename;
    protected static String log;
    CommPortIdentifier commPortID;
    private InputStream in;
    private OutputStream out;
    private CommPort commPort;
    private static boolean canStop = false;

    /**
     * Constructor of the Object class.
     */
    public CommunicationPort() {
        super();
    }
    
    /**
     * Constructor that initializes the main variables.
     * 
     * @param COMPortNumber the number of the COM Port where the Xbee is connected to.
     * @param baud the baud rate (usually 9600, but it depends on the transmission).
     */
    public CommunicationPort(String COMPortNumber, int baud) {
        this.baud = baud;
        this.COMPortNumber = COMPortNumber;
    }

    /**
     * Connects the communication port with the radio module.
     * 
     * @param portName the name of the port to which the Xbee is connected.
     * @throws NoSuchPortException if the portName isn't a valid port.
     * @throws PortInUseException if one tries to open an opened port.
     * @throws UnsupportedCommOperationException if one tries to set invalid parameters to a port.
     * @throws IOException if one tries to get an IOStream of an invalid port.
     */
    public void connect(String portName) throws Exception {
        canStop = false;

        GUI.append("Getting port identifiers... ", GUI.INFO, GUI.LEFT_PANE);
        commPortID = CommPortIdentifier.getPortIdentifier(portName);
        GUI.append("Succes.\n", GUI.INFO, GUI.LEFT_PANE);

        if (commPortID.isCurrentlyOwned()) {
            String message2 = "Error: Port is currently in use. Please try again in a moment.\n";
            System.out.println(message2);
            GUI.append(message2, GUI.ERROR, GUI.LEFT_PANE);
        } else {
            String message = "Connecting to port...\n\n";
            System.out.println(message);
            GUI.append(message, GUI.INFO, GUI.LEFT_PANE);
            commPort = commPortID.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                GUI.append("Starting serial reader and writer...\n", GUI.INFO, GUI.LEFT_PANE);
                //si le port est present mais pas connecte
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();

                // starts the Threads for Writing and Reading a COM Port.
                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            } else { // si le port n'est pas present
                String message1 = "Error: Only serial ports are handled by this appliction.\n";
                System.out.println(message1);
                GUI.append(message1, GUI.ERROR, GUI.LEFT_PANE);
            }
        }
    }

    /**
     * Disconnects the communication port in use.
     */
    public void disconnect() {
        String message = "Closing port : " + commPortID.getName() + "\n";
        System.out.println(message);
        GUI.append(message, GUI.INFO, GUI.LEFT_PANE);

        try {
            commPort.getInputStream().close();
        } catch (IOException e) {
        }

        canStop = true;
    }

    /**
     * This class is used to close a Thread run by the SerialWriter/Reader 
     * classes. The goal is to close the COM Port, but it is necessary (?) to 
     * wait for the other threads to settle down before doing so.
     * 
     * This constraint -- Thread.sleep(4000) -- should be verified and, if 
     * possible, removed.
     */
    class CloseThread extends Thread {

        /**
         * Closes the COM Port in use.
         */
        @Override
        public void run() {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }

            ((SerialPort) commPort).removeEventListener();
            commPort.close();
        }
    }

    /**
     * This class reads the data transmitted by the radio module. 
     */
    public static class SerialReader implements Runnable {
        // Class variables
        
        InputStream in;
        private static final int PROCESSED_MESSAGE_LIMIT_SIZE = 100;

        /**
         * Initializes the InputStream.
         * 
         * @param in the InputStream of the class.
         */
        public SerialReader(InputStream in) {
            this.in = in;
        }

        /**
         * The method that does the actual reading and processing of the 
         * transmitted data. 
         * 
         * The processing is done by an instance of the Preprocesser class.
         * As this operation must be done regularly, a counter is introduced to 
         * tell when the processing step should occur. Other methods could be 
         * used to trigger this functionality, but this is the easiest one to 
         * implement. Further 
         */
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int len = -1;
            Preprocesser processer = new Preprocesser('\n');
            CSVParser csvParser = new CSVParser(messageSize());
            processer.addSeparator('\r');
            int countDoTheProcessing = 0;
            int processerMessageOldSize = 0;

            try {
                while (!canStop && ((len = this.in.read(buffer)) > -1)) {
                    // log
                    log = new String(buffer, 0, len);
                    processer.add(log);

                    System.out.print(log);
                    GUI.append(log, GUI.MSG, GUI.LEFT_PANE);

                    countDoTheProcessing++;
                    if (countDoTheProcessing == 50) {
                        processer.process();
                        processer.filterDuplicates();

                        for (int i = processerMessageOldSize; processer.hasMessage() &&
                                                        i < processer.processedMessageSize(); i++) {
                            String csvMessage = csvParser.parseToCSV(processer.get(i));
                            if (csvMessage.length() != 0 ){
                                GUI.append(csvMessage + "\n", GUI.MSG, GUI.RIGHT_PANE);
                                GUI.rw.write(csvParser.parseToCSV(processer.get(i)) + "\n");
                            }
                            System.out.println("[" + csvParser.parseToCSV(processer.get(i))+ "]");
                        }
                        processerMessageOldSize = processer.processedMessageSize();
                        countDoTheProcessing = 0;
                    }
                    
                    if (processer.processedMessageSize() == PROCESSED_MESSAGE_LIMIT_SIZE) {
                        processer.clear();
                        System.out.println(processer.processedMessageSize());
                        countDoTheProcessing = 0;
                    }
                }
            } catch (IOException e) {
            }
        }

        private int messageSize() {
            int N = 1;      // NEZ
            int X = 1;      // numero du NEZ
            int YYYY = 4;   // annee
            int MM = 2;     // mois
            int DD = 2;     // jour
            int hh = 2;     // heure
            int mm = 2;     // minute
            int ss = 2;     // seconde
            int TTT = 3;    // type du capteur
            int dpdd = 4;   // mesure d.dd
            
            int size = N+X+YYYY+MM+DD+hh+mm+ss+TTT+dpdd;
            
            return size;
        }
    }

    /**
     * This class writes the output on a stream. 
     * 
     * For the moment, this class is not directly used, so it should be revised,
     * removed or deprecated.
     */
    public static class SerialWriter implements Runnable {
        // Class variables
        
        OutputStream out;

        /**
         * Constructor that initializes the main parameter.
         * 
         * @param out the OutputStream in which the data will be written.
         */
        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        /**
         * The actual writing.
         */
        @Override
        public void run() {
            try {
                int c = 0;
                while ((c = System.in.read()) > -1) {
                    this.out.write(c);
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * Method responsible for closing the port in use.
     */
    public void closePort() {
        try {
            if (commPort != null) {
                canStop = true;
                commPort.getInputStream().close();
                commPort.getOutputStream().close();

                new CloseThread().start();

                String message = "\nClosing port...\n\n";
                System.out.println(message);
                GUI.append(message, GUI.INFO, GUI.LEFT_PANE);
            }
        } catch (Exception e) {
        }

    }    
}