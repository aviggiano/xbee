/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.preprocessing;

import java.util.ArrayList;

/**
 * This class parses a String into the CSV format, according to the data 
 * analysis format. At the version 1.0, the data received by the Xbee is in 
 * the format NXYYYYMMDDhhmmssTTTd.dd (for example, N20197001010002273.14), which 
 * stands for data transmitted by the NEZ X, at the date YYYY-MM-DD, at the time
 * hh:mm:ss, of the sensor type TTT and measure d.dd.
 * 
 * If these fields are changed, this class should reflect the changes in the 
 * transmission to parse the data correctly.
 * 
 * @author aviggiano
 * @version 1.0
 * @since 2012-12
 */
public class CSVParser {
    // Class variables
    
    private int sizeOfMessage;
    private ArrayList<String> CSVParsedListOfMessages;
    
    /**
     * Initializes the class variables.
     * 
     * @param sizeOfMessage the size of each message to be parsed.
     */
    public CSVParser(int sizeOfMessage){
        this.sizeOfMessage = sizeOfMessage;
        CSVParsedListOfMessages = new ArrayList<>();
    }
    
    /**
     * Parses the list of message to a CSV format. 
     * 
     * This method can be improved to be more general. Passing an explicit 
     * argument to the substring function can lead to a 
     * StringIndexOutOfBoundsException if the size of the message is not well
     * defined.
     * 
     * @param listOfMessages the list of the messages.
     * @throws StringIndexOutOfBoundsException if the size of the message is not 
     *         well defined.
     */
    public void parseToCSV (ArrayList<String> listOfMessages){
        String parsedMessage = "";
        for (int i = 0; i < listOfMessages.size(); i++){
            if (listOfMessages.get(i).length() == sizeOfMessage && listOfMessages.get(i).charAt(0) == 'N'){
                parsedMessage += listOfMessages.get(i).substring(1, 2)   + ",";       // NEZ X
                parsedMessage += listOfMessages.get(i).substring(2, 6)   + "-" +      // Date YYYY-MM-DD
                                 listOfMessages.get(i).substring(6, 8)   + "-" +
                                 listOfMessages.get(i).substring(8, 10)  + ",";
                parsedMessage += listOfMessages.get(i).substring(10, 12) + ":" +      // Time hh:mm:ss
                                 listOfMessages.get(i).substring(12, 14) + ":" +
                                 listOfMessages.get(i).substring(14, 16) + ",";       
                parsedMessage += listOfMessages.get(i).substring(16, 19) + ",";       // Sensor type TTT
                parsedMessage += listOfMessages.get(i).substring(19, 23) ;            // Measure d.dd
                CSVParsedListOfMessages.add(parsedMessage);
            }
        }
    }
    
    public String parseToCSV (String message){
        String parsedMessage = "";
        
        if (message.length() == sizeOfMessage && message.charAt(0) == 'N'){
            parsedMessage += message.substring(1, 2)   + ",";       // NEZ X
            parsedMessage += message.substring(2, 6)   + "-" +      // Date YYYY-MM-DD
                             message.substring(6, 8)   + "-" +
                             message.substring(8, 10)  + ",";
            parsedMessage += message.substring(10, 12) + ":" +      // Time hh:mm:ss
                             message.substring(12, 14) + ":" +
                             message.substring(14, 16) + ",";       
            parsedMessage += message.substring(16, 19) + ",";       // Sensor type TTT
            parsedMessage += message.substring(19, 23) ;            // Measure d.dd
        }
        
        return parsedMessage;
    }    
    
    /**
     * Get a CSV format String message.
     * 
     * @param i the index of the CSV parsed list of messages.
     * @return the string in CSV format.
     */
    public String get (int i) {
        return CSVParsedListOfMessages.get(i);
    }

    public boolean hasMessage() {
        return !CSVParsedListOfMessages.isEmpty();
    }
    
    public int size () {
        return CSVParsedListOfMessages.size();
    }
}
