package data.preprocessing;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to Read and Write data into a file. In the case of this
 * specific application, the data is a measure of pollution transmitted by a 
 * radio module Xbee. 
 * 
 * At present only the Write functionalities are being used, as there are no
 * specific usage for the Read method (the extraction of the data and its analysis
 * would be made by a third party program, such as Microsoft Excel).
 * 
 * @author aviggiano <aviggiano@centrale-marseille.fr> et al.
 * @version 1.0
 * @since 2012-07
 */

public class FileWriterReader {
    // Class variables

    /**
     * Default filename and extension is message.txt. This can be changed as 
     * required. Default folder is the current one.
     * The message is an ArraList of Strings that stores each line of the file,
     * when reading, and the message that will be written into the file, when 
     * writing.
     */    
    
    public static final String EXTENSION = ".txt";
    public static String FILENAME = "message";
    public static final String FOLDER = System.getProperty("user.dir");
    
    public static final int READ = 0;
    public static final int WRITE = 1;
    
    protected ArrayList<String> message = new ArrayList<>();
  
    // Algorithms
    
    /**
     * Initializes the filename.
     * 
     * User should create an instance of FileWriterReader, pointing out the name
     * of the file they will be reading/writing, and then apply methods such as 
     * read() or write(String message).
     * 
     * @param filename the name of the file to be read/written.
     */
    public FileWriterReader (String filename) {
        FILENAME = filename;
    }
    
    /**
     * Reads a file and store each line as a String on an ArrayList.
     * 
     * This method should be reviewed, because it's been a long time since it is
     * not used.
     * 
     * @return an ArrayList of Strings where each element is a line of the file.
     */
    public ArrayList<String> read() {

        File folder = new File(FOLDER);
        File file = new File(folder, FILENAME + EXTENSION);
        message = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String ligne;
            String aux;
            while ((ligne = bufferedReader.readLine()) != null) {
                aux = ligne;
                message.add(aux);
            }
            fileReader.close();
            bufferedReader.close();
        } catch (IOException e) {
        }
        
        return message;
    }
    
    /**
     * Writes a message on a file. Message is written after all existing lines.
     * To change this functionality (overwrite a file), fileWriter should be 
     * instantiated as a new FileWriter(file, false);
     * 
     * Filename is inferred, as it would have been defined on the object 
     * creation.
     * 
     * @param message the message that will be written on the file.
     * @see java.io.FileWriter
     */
    public void write(String message){
    	this.message.add(message);
        
        File folder = new File(FOLDER);
        File file = new File(folder, FILENAME + EXTENSION);

        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true); 
            PrintWriter printWriter = new PrintWriter(fileWriter);

            String aux;
            for (int i = 0; i < this.message.size(); i++) {
                aux = this.message.get(i);
                printWriter.print(aux);
            }

            printWriter.print('\n');

            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
        }

        this.message.clear();        
    }

    /**
     * Returns the size of the message read/written.
     * 
     * @return the size of the message.
     */
    int messageSize() {
        return message.size();
    }  
}