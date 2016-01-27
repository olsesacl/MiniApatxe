

import java.io.*;
import java.net.*;
import java.util.*;


public class NostreApatxe implements Runnable{
	
	private String ruta;
	private int port;
	private String errorFile;
	private Socket SocketConnexio = null;
	private ServerSocket SocketAcollida = null;
	
	public NostreApatxe(String ruta, int port, String errorFile) {
		this.ruta = ruta;
		this.port = port;
		this.errorFile = errorFile;
	}
	
	public static void checkErrorFile(String errorFile, String ruta){
		
		BufferedWriter writer = null;
		
		//obtindre ruta actual
		File miDir = new File (".");
		
		try {
			File file = new File(miDir.getCanonicalPath() + ruta + errorFile);
			
			if(!file.isFile()){
				
				//añadimos a la variable los datos que se insertaran en el archivo
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
    				System.out.println("\nEsperant conexio...");
    				
    				 this.SocketConnexio = this.SocketAcollida.accept();// Servidor esperant conexio
    				
    				SortidaClient = new DataOutputStream(this.SocketConnexio.getOutputStream());
    				EntradaDesdeClient = new BufferedReader(new InputStreamReader(this.SocketConnexio.getInputStream()));
    				
    				System.out.println("Conexio acceptada" + this.SocketConnexio.toString());
    				
    				NomFitxer = EntradaDesdeClient.readLine();
    				
    				//verifiquem que no es null, null el gastarem quan vullgam tancar el servidor
    				if(!NomFitxer.equals("")){
    				
	    				System.out.println("Dades rebudes: " + NomFitxer);
	    				//netejem el que ens envia el client per obtindre sols el nom del fitxer
	    				//asi llevariem "GET /"
	    				NomFitxer = NomFitxer.substring(5,NomFitxer.length());
	    				//asi llevariem " HTTP/1.1"
	    				NomFitxer = NomFitxer.substring(0,NomFitxer.length()-9);
	    				
	    				
	    				//obtindre ruta actual
	    				File miDir = new File (".");
	    				
	    				File file = new File(miDir.getCanonicalPath() + this.ruta + NomFitxer);
	    				
	    				//comprobem que existeix el fitxer
	    				if(file.exists() && file.isFile()){
	    					FileInputStream fos = new FileInputStream(file);
	    			        
	    			        byte[] bytes = new byte[1024];
	    			        
	    			        int count;
	    			        while ((count = fos.read(bytes)) > 0) {
	    			        	SortidaClient.write(bytes, 0, count);
	    			        }
	    			        
	    			        fos.close();
	    			        
	    			        System.out.println("Enviades dades al client");
	    			        
	    				} else {
	    					System.out.println("El fichero " + miDir.getCanonicalPath() + this.ruta+ NomFitxer + " no existeix");
	    					
	    					//en caso de que se intente acceder a un archivo html se devuelve el archivo de error
	    					if(NomFitxer.indexOf(".html")!=-1){
	    						File fileError = new File(miDir.getCanonicalPath() + this.ruta + this.errorFile);
		    					FileInputStream fos = new FileInputStream(fileError);
		    			        
		    			        byte[] bytes = new byte[1024];
		    			        
		    			        int count;
		    			        while ((count = fos.read(bytes)) > 0) {
		    			        	SortidaClient.write(bytes, 0, count);
		    			        }
		    			        
		    			        fos.close();
	    					}
	    					
	    				}
	    		        		        
	    		        SortidaClient.close();
	    		        
	    		        //si el fil no s'ha tancat es que no s'han enviat dades
    				} else if(!Thread.currentThread().isInterrupted()){
    					System.out.println("No s'ha rebut nom de fitxer");
    				} else {
    					System.out.println("Rebuda señal per a tancar el servidor");
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
	public void cerrar(){
		try {
			this.SocketAcollida.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
public static void main(String[] args) throws Exception{
		
		
		//archivo donde guardamos las propiedades
		Properties props = new Properties();
		File configFile = new File("config.properties");
		
		//comprobamos si existe
		if(!configFile.exists()){
			
			//si no existe preparamos los datos que se cargaran en el archivo
			props.setProperty("PORT", "7000");
			props.setProperty("PATH", "/");
			props.setProperty("ERRORFILE", "error.html");
			
			//guardamos la configuracion en el archivo
			FileWriter writer = new FileWriter(configFile);
			props.store(writer, "CONFIGURACIÓN MINIAPACHE");
			writer.close();
		}
		 
		FileReader reader = new FileReader(configFile);
		 
		 
		// cargamos las propiedades del archivo
		props.load(reader);
		
		reader.close();
		
		
		//cargamos los datos, mandamos los datos por defecto para el caso de que no pueda cargar el archivo
		
		int port = Integer.parseInt(props.getProperty("PORT", "7000"));
		String ruta = props.getProperty("PATH", "/");
		String errorFile = props.getProperty("ERRORFILE", "error.html");
		
		
		//verificamos si existe el fichero, sino creamos uno por defecto
		checkErrorFile(errorFile, ruta);
		
		
		//arrancamos el servidor en otro thread
		NostreApatxe a = new NostreApatxe(ruta, port, errorFile);
		Thread apache = new Thread(a);
		
		apache.start();
		
		boolean seguir = true;
		BufferedReader comandos = new BufferedReader(new InputStreamReader(System.in));;
		
		while(seguir){
			//leemos los comandos que se inserten por consola (de momento solo sera para cerrar el servidor)
			if(comandos.readLine().equalsIgnoreCase("exit")){
				seguir = false;
			}
		}
		
		//con esto interrumpimos que siga la ejecución del hilo
		apache.interrupt();
		a.cerrar();
		
		
		//como el hilo espera una conexión la realizamos para que se cierre debidamente
		/*String ip = InetAddress.getLocalHost().getHostAddress();
		Socket clientSocket = new Socket(ip, port);
		DataOutputStream sortidaAlServidor = new DataOutputStream(clientSocket.getOutputStream());
		sortidaAlServidor.writeBytes("\n");
		clientSocket.close();*/
		
	}

}
