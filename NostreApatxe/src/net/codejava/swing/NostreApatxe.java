package net.codejava.swing;



import java.io.*;
import java.net.*;
//import java.util.*;

import javax.swing.JTextArea;


public class NostreApatxe implements Runnable{
	
	private String ruta;
	private int port;
	private String errorFile;
	private Socket SocketConnexio = null;
	private ServerSocket SocketAcollida = null;
	private JTextArea log = null;
	
	//log es donde se retornara los mensajes que se mostraran por pantalla
	public NostreApatxe(String ruta, int port, String errorFile, JTextArea log) {
		this.ruta = ruta;
		this.port = port;
		this.errorFile = errorFile;
		this.log = log;
		//log.append("Dentro del server\n");
	}
	
	protected void checkErrorFile(){
		
		BufferedWriter writer = null;
		

		try {
			File file = new File(this.ruta + this.errorFile);
			
			if(!file.isFile()){
				
				//a�adimos a la variable los datos que se insertaran en el archivo
				String content = "<html><head><title>Error al acceder al fichero</title></head>";
				content += "<body><h1>El fichero al que intenta acceder no existe</h1></body></html>";
				
				//procedemos a crear el archivo
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
	            writer.write(content);
	            writer.close();
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	}
	
	@Override
	public synchronized void run() {
		
		String NomFitxer;
		DataOutputStream SortidaClient = null;
        BufferedReader EntradaDesdeClient = null;
       
        
        
        try{
        	this.SocketAcollida = new ServerSocket(this.port);
                        
    		while(!Thread.currentThread().isInterrupted()){
    			try{
    				//System.out.println("\nEsperant conexio...");
    				log.setText("Esperando conexion\n" + log.getText());
    				//log.addElement("Esperando conexion");
    				
    				 this.SocketConnexio = this.SocketAcollida.accept();// Servidor esperant conexio
    				
    				SortidaClient = new DataOutputStream(this.SocketConnexio.getOutputStream());
    				EntradaDesdeClient = new BufferedReader(new InputStreamReader(this.SocketConnexio.getInputStream()));
    				
    				//System.out.println("Conexio acceptada" + this.SocketConnexio.toString());
    				log.setText("Conexion aceptada "+ this.SocketConnexio.toString() +"\n" + log.getText());
    				//log.addElement("Conexion aceptada "+ this.SocketConnexio.toString());
    				
    				NomFitxer = EntradaDesdeClient.readLine();
    				
    				//verifiquem que no es null, null el gastarem quan vullgam tancar el servidor
    				if(!NomFitxer.equals("")){
    				
	    				//System.out.println("Dades rebudes: " + NomFitxer);
    					log.setText("Datos recibidos: "+ NomFitxer +"\n" + log.getText());
    					//log.addElement("Datos recibidos: "+ NomFitxer);
    					
	    				//netejem el que ens envia el client per obtindre sols el nom del fitxer
	    				//asi llevariem "GET /"
	    				NomFitxer = NomFitxer.substring(5,NomFitxer.length());
	    				//asi llevariem " HTTP/1.1"
	    				NomFitxer = NomFitxer.substring(0,NomFitxer.length()-9);
	    				
	    				
	    				
	    				File file = new File(this.ruta + NomFitxer);
	    				
	    				//comprobem que existeix el fitxer
	    				if(file.exists() && file.isFile()){
	    					
	    					//cabecera
	    			        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
	    			        SortidaClient.write(httpResponse.getBytes("UTF-8"));
	    					
	    					FileInputStream fos = new FileInputStream(file);
	    			        
	    			        byte[] bytes = new byte[1024];

	    			        int count;
	    			        while ((count = fos.read(bytes)) > 0) {
	    			        	SortidaClient.write(bytes, 0, count);
	    			        }
	    			        
	    			        fos.close();
	    			        
	    			        //System.out.println("Enviades dades al client");
	    			        log.setText("Datos enviados al cliente\n" + log.getText());
	    			        //log.addElement("Datos enviados al cliente");
	    			        
	    				} else {
	    					//System.out.println("El fichero " + this.ruta+ NomFitxer + " no existeix");
	    					log.setText("El fichero " + this.ruta + NomFitxer + " no existe\n" + log.getText());
	    					//log.addElement("El fichero " + this.ruta + NomFitxer);
	    					
	    					//en caso de que se intente acceder a un archivo html se devuelve el archivo de error
	    					if(NomFitxer.indexOf(".html")!=-1){
	    						File fileError = new File(this.ruta + this.errorFile);
		    					FileInputStream fos = new FileInputStream(fileError);
		    			        
		    			        byte[] bytes = new byte[1024];
		    			        
		    			      //cabecera
		    			        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
		    			        SortidaClient.write(httpResponse.getBytes("UTF-8"));
		    			        
		    			        int count;
		    			        while ((count = fos.read(bytes)) > 0) {
		    			        	SortidaClient.write(bytes, 0, count);
		    			        }
		    			        
		    			        fos.close();
	    					} else{
	    						String httpResponse = "HTTP/1.0 404 Not Found\r\n\r\n";
		    			        SortidaClient.write(httpResponse.getBytes("UTF-8"));
		    			        this.SocketConnexio.getOutputStream().write(httpResponse.getBytes("UTF-8"));
	    					}
	    					
	    				}
	    		        		        
	    		        SortidaClient.close();
	    		        
	    		        //si el fil no s'ha tancat es que no s'han enviat dades
    				} else if(!Thread.currentThread().isInterrupted()){
    					//System.out.println("No s'ha rebut nom de fitxer");
    					log.setText("No se ha recibido el nombre del archivo\n" + log.getText());
    					//log.addElement("No se ha recibido el nombre del archivo");
    				} else {
    					//System.out.println("Rebuda se�al per a tancar el servidor");
    				}
    				
    			}catch(Exception e){
    				//e.printStackTrace();
    			}
    			
    		}
    		this.SocketAcollida.close();//tanquem la conexio del servidor
        } catch(Exception e){
        	//e.printStackTrace();
        }
        
	}
	protected void cerrar(){
		try {
			this.SocketAcollida.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}
