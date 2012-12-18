/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aviggiano
 */
public class Preprocesser {
    private ArrayList<String> processedMessage;
    private ArrayList<String> rawMessage;
    private ArrayList<Character> separators;
    private String message;
    
    
    public Preprocesser (char separator) {
        processedMessage = new ArrayList<>();
        rawMessage = new ArrayList<>();
        separators = new ArrayList<>();
            separators.add(separator);
    }
    
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
        
    }
    
    public void filterDuplicates() {
        HashSet hashSet = new HashSet();
        hashSet.addAll(processedMessage);
        processedMessage.clear();
        processedMessage.addAll(hashSet);
    }
    
    public ArrayList<String> getProcessedMessage() {
        return processedMessage;
    }

    public boolean hasMessage() {
        return (! processedMessage.isEmpty());
    }
    
    public int processedMessageSize() {
        return processedMessage.size();
    }

    public void add(String message) {
        rawMessage.add(message);
    }

    public void addSeparator(char c) {
        separators.add(c);
    }
}
