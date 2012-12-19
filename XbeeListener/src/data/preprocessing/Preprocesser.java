package data.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class processes an ArrayList of Strings into another one, according to 
 * specific rules. At the version 1.0, a message is contained in the ArrayList 
 * rawMessage, divided 'randomly' in strings of variable length. For instance, 
 * a rawMessage containing {"Hel", "lo ", "W", "or", "ld!\nI love", "you.\n"}
 * would be processed to {"Hello World!", "I love you."}, if the 'rule' is that
 * each element should be separated by '\n'.
 * 
 * @author aviggiano
 * @version 1.0
 * @since 2012-12
 */
public class Preprocesser {
    // Class variables
    
    /**
     * An instance of a Preprocesser contains ArrayLists that are 
     * the rawMessage, the processedMessage and the rawMessage's separators.
     */
    private ArrayList<String> processedMessage;
    private ArrayList<String> rawMessage;
    private ArrayList<Character> separators;
    
    /**
     * Creates an instance of a Preprocesser.
     * 
     * @param separator the default separator of the message.
     */
    public Preprocesser (char separator) {
        processedMessage = new ArrayList<>();
        rawMessage = new ArrayList<>();
        separators = new ArrayList<>();
            separators.add(separator);
    }
    
    /**
     * Processes the rawMessage and adds each processed element into the 
     * processedMessage ArrayList.
     */
    
    public void process() {
        String rawString;
        String processedString;
        String shrunkRawMessage = "";
        for (int i = 0; i < rawMessage.size(); i++) {
            rawString = rawMessage.get(i);
            shrunkRawMessage += rawString;
        }
        
        System.out.println("Processing : [" + shrunkRawMessage + "]");
        
        processedString = "";
        for (int j = 0; j < shrunkRawMessage.length(); j++) {
            if (! separators.contains(shrunkRawMessage.charAt(j) )) {
                processedString += shrunkRawMessage.charAt(j);
            }
            else if (processedString.length() > 0) {
                System.out.println("After process : " + processedString);
                processedMessage.add(processedString);  
                processedString = "";
            }
        }
        rawMessage = new ArrayList<>();
    }
    
    /**
     * Filter the duplicates of a processed message.
     * 
     * This method can be improved. Creating a set, adding all elements into it
     * reinserting them again into the ArrayList may not be very efficient.
     */
    public void filterDuplicates() {
        HashSet hashSet = new HashSet();
        hashSet.addAll(processedMessage);
        processedMessage.clear();
        processedMessage.addAll(hashSet);
    }
    
    /**
     * @return the processedMessage
     */
    public ArrayList<String> getProcessedMessage() {
        return processedMessage;
    }
    
    /**
     * @return true if the Preprocesser has a message, and false otherwise.
     */
    public boolean hasMessage() {
        return (! processedMessage.isEmpty());
    }
    
    /**
     * @return the preprocessedMessage size
     */
    public int processedMessageSize() {
        return processedMessage.size();
    }

    /**
     * Adds a separator to the ArrayList, so each message will be separated by 
     * any one of the separators in the separators ArrayList.
     * 
     * @param c the character that separates two messages.
     */
    public void addSeparator(char c) {
        separators.add(c);
    }
    
    /**
     * Adds a message into the rawMessage ArrayList.
     * 
     * @param message the message that will be added into the rawMessage 
     */
    public void add(String message){
        rawMessage.add(message);
    }
}
