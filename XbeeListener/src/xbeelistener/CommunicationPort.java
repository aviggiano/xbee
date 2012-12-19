package xbeelistener;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.preprocessing.Preprocesser;

/**
 *
 * @author aviggiano <aviggiano@centrale-marseille.fr>
 * @version 1.0
 * @since 2012-07-01
 */
	public class CommunicationPort {       
            
		private int rate; 
		private static String COMPortNumber;
		
		static DateFormat dateFormat;
		static Date date;
		
		private static String filename;
	
		protected static String log;
		
		CommPortIdentifier commPortID;
		private InputStream in;
		private OutputStream out;
		private CommPort commPort;

		
		private static boolean stop = false;

	    public CommunicationPort(){
	        super();
	    }
	    
	    public CommunicationPort(String COMPortNumber, int rate){
	    	super();
	    	this.rate = rate;
	    	this.COMPortNumber = COMPortNumber;
	    }
 
	    void connect ( String portName ) throws Exception{
	    	stop = false;
	    	
	    	GUI.append("Getting port identifiers...\n", GUI.INFO, GUI.LEFT_PANE );
	        commPortID = CommPortIdentifier.getPortIdentifier(portName);
	        GUI.append("Succes.\n", GUI.INFO, GUI.LEFT_PANE);
	        
	        if ( commPortID.isCurrentlyOwned() ){
                    String message2 = "Error: Port is currently in use. Please try again.\n";
	            System.out.println(message2);
	            GUI.append(message2, GUI.ERROR, GUI.LEFT_PANE);
	        }
	        else{
                    String message = "Connecting to port...\n\n";
	            System.out.println(message);
	            GUI.append(message, GUI.INFO, GUI.LEFT_PANE);
	            commPort = commPortID.open(this.getClass().getName(),2000);
	            if ( commPort instanceof SerialPort )
	            {
	            	GUI.append("Starting serial reader and writer...\n", GUI.INFO, GUI.LEFT_PANE);
	            	//si le port est present mais pas connecte
	                SerialPort serialPort = (SerialPort) commPort;
	                serialPort.setSerialPortParams(rate,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
 
	                in = serialPort.getInputStream();
	                out = serialPort.getOutputStream();
 
	                (new Thread(new SerialReader(in))).start();
	                (new Thread(new SerialWriter(out))).start();
 
	            }
                    else { // si le port n'est pas present
	            	String message1 = "Error: Only serial ports are handled by this example.\n";
	                System.out.println(message1);
	                GUI.append(message1, GUI.ERROR, GUI.LEFT_PANE);
	            }
	        }   
	    }
	    
	    public void disconnect() {
	    	String message = "Closing port : " + commPortID.getName() + "\n";
	    	System.out.println(message);
	    	GUI.append(message, GUI.INFO,GUI.LEFT_PANE);

	    	try {
                    commPort.getInputStream().close();
		} catch (IOException e) {}
	    	
                stop = true;
	    }
	    

	    class CloseThread extends Thread {
		    public void run()
		    {
		    	try {
                            Thread.sleep(4000);
			} catch (InterruptedException e) {}

                        ((SerialPort) commPort).removeEventListener();
                        commPort.close();    
		    }
	    }

	    public void closePort() {
		    try {
		    	if (commPort != null) {
		    		stop = true; 
			    	commPort.getInputStream().close();
			    	commPort.getOutputStream().close();
		
			    	new CloseThread().start();

			    	String message = "\nClosing port...\n\n";
			    	System.out.println(message);
			    	GUI.append(message, GUI.INFO, GUI.LEFT_PANE);			
                        }
		    }
		    catch (Exception e) {
                        GUI.append(e.getMessage(), GUI.ERROR, GUI.LEFT_PANE);
                    }
		    
	    }	    

 
	    public static class SerialReader implements Runnable {
	        InputStream in; 
 
	        public SerialReader ( InputStream in ) {
	            this.in = in;
	        }
 
                @Override
	        public void run () {
	            byte[] buffer = new byte[1024];
	            int len = -1;
                    Preprocesser processer = new Preprocesser ('\n');
                    processer.addSeparator('\r');
                    int count = 0;
                    int processedMessageOldSize = 0;
                    
	            try {
	                while ( !stop && ( (len = this.in.read(buffer)) > -1) ) { 
	                    // log
                            log = new String(buffer,0,len);
                            processer.add(log);
                            
                            
	                    System.out.print(log);
	                    GUI.append(log, GUI.MSG, GUI.LEFT_PANE);
                            GUI.rw.write(log);
                            
                            count++;
                            if (count == 50) {
                                processer.process();
                                processer.filterDuplicates();    
                        
                                for (int i = processedMessageOldSize; processer.hasMessage() && i < processer.processedMessageSize(); i++) {
                                    GUI.append(processer.getProcessedMessage().get(i) + "\n", GUI.MSG, GUI.RIGHT_PANE);
                                    System.out.println("=[" + processer.getProcessedMessage().get(i) + "]=");
                                }
                                processedMessageOldSize = processer.processedMessageSize();
                                count = 0;
                            }
                        }
                        


	            }
	            catch ( IOException e ){}
	        }               
            
            /*
             * ORIGINAL METHOD -- IT WORKS!
             */
//	            try {
//	                while ( !stop && ( (len = this.in.read(buffer)) > -1) ) { // !stop 
//	                    log = new String(buffer,0,len);
//                            
//                            //System.out.println("len = " + len + " BUFFER = [" +  log + "]");
//	                    System.out.print(log);
//	                    GUI.append(log);
//                            GUI.rw.write(log);
//	                }
//	            }
//	            catch ( IOException e ){}
//	        }                
	    
	    }
 
	    public static class SerialWriter implements Runnable {
	        OutputStream out;
 
	        public SerialWriter ( OutputStream out ) {
	            this.out = out;
	        }
 
                @Override
	        public void run () {
	            try
	            {                
	                int c = 0;
	                while ( ( c = System.in.read()) > -1 )
	                {
	                    this.out.write(c);
	                }                
	            }
	            catch ( IOException e ){}
	        }
	    }
	}