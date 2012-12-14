package data.preprocessing;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author aviggiano <aviggiano@centrale-marseille.fr> et al.
 * @version 1.0
 * @since 2012-07-01
 */

public class FileWriterReader 
{
    	
    public static final String EXTENSION = ".txt";
    public static String FILENAME = "message";
    public static final String REPERTOIRE = System.getProperty("user.dir");
    public static final int MAX_DISTINCT_HASH_CODES = 100;
    
    public static final int READ = 0;
    public static final int WRITE = 1;
    
    protected ArrayList<String> message = new ArrayList<>();
    
    public long[] distinctHashCodes = new long[MAX_DISTINCT_HASH_CODES];
    public int distinctHashCodesLast = 0;
        
  
    public void read() 
    {
        File repertoire = new File(REPERTOIRE); // creation du repertoire
        File fichier = new File(repertoire, FILENAME + EXTENSION); //creation du fichier
        message = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(fichier);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String ligne;
            while ((ligne = bufferedReader.readLine()) != null) //tant que la ligne est pas nulle
            {
                String aux = ligne;
                message.add(aux);
                aux = null;
            }
            fileReader.close();
            bufferedReader.close();
		}
		
		catch (IOException e) {}
	}
    
    public void write(String message){
    	this.message.add(message);
    	write();
        // this.message = new ArrayList<String>(); ?
    }
    
    public void write(String message, String filename){
        FILENAME = filename;
        write(message);
    }
    
    
    	public void write()
	{
		File repertoire = new File(REPERTOIRE); // creation du repertoire
		File fichier = new File(repertoire,FILENAME+EXTENSION); //creation du fichier
		
		try
		{
			fichier.createNewFile(); // creation du fichier dans le dossier
			FileWriter fileWriter = new FileWriter(fichier, true); // false = souscrire si fichier existant
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			for (int i=0;i<message.size() && isNewMessage(message.get(i));i++) //parcourir le Arraylist
			{
				String aux = message.get(i);
				printWriter.print(aux); // imprime une ligne dans le fichier
			}
			
			printWriter.print('\n');
			
			// quando terminar:
			printWriter.flush(); // permet l ecriture dans le fichier
			printWriter.close(); // ferme le fichier
		}
		
		catch (IOException e) {}
                
		this.message.clear();
	}
        
        public FileWriterReader(String filename, int type, String message)
        {
        	FILENAME = filename;
        	this.message.add(message);
        	
        	if (type == READ) {
        		read();         		
        	}
        	if (type == WRITE){
        		write(); 
        	} 
        }

    int size() {
        return message.size();
    }  

    private boolean isNewMessage(String string) {
        long stringHash = hash (string);
        
        for (int i = 0; i < distinctHashCodesLast; i++) {
            if (stringHash == distinctHashCodes[i]) return false;
        }
        
        /* a partir de la, c'est une nouvelle message */
        
        // On n'a pas atteint la fin du vecteur
        if (distinctHashCodesLast != MAX_DISTINCT_HASH_CODES-1) {
            distinctHashCodes[distinctHashCodesLast] = stringHash;
            
            distinctHashCodesLast++;
        }
        
        // On a atteint la fin du vecteur : on cree un nouveau vecteur pour ne pas prendre beaucoup de place dans la memoire
        else {
            // on cree un nouveau vecteur
            distinctHashCodes = new long[MAX_DISTINCT_HASH_CODES];
            
            distinctHashCodesLast = 0;
            distinctHashCodes[distinctHashCodesLast] = stringHash;
        }
        return true;
    }
    
    long hash(String str) {
        long hash = 5381;

        for (int c = 0; c < str.length(); c++ )
            hash = ((hash << 5) + hash) + str.charAt(c); /* hash * 33 + c */

        //System.out.println("[" + str + "]" + " -> hash = " + hash);
        return hash;
    }    
}