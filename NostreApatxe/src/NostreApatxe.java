

import java.io.*;
import java.net.*;
import java.util.*;


public class NostreApatxe {
	public static void main(String[] args) throws IOException {
		
		final int PORT = 7000;
		final String RUTA= "/web/";
		
		String NomFitxer;
		
		DataOutputStream SortidaClient = null;
        BufferedReader EntradaDesdeClient = null;
        
        ServerSocket SocketAcollida = new ServerSocket(PORT);
        
        Boolean continuar = true;
        
		while(continuar){
			try{
				System.out.println("\nEsperant conexio...");
				
				Socket SocketConnexio = SocketAcollida.accept();// Servidor esperant conexio
				
				SortidaClient = new DataOutputStream(SocketConnexio.getOutputStream());
				EntradaDesdeClient = new BufferedReader(new InputStreamReader(SocketConnexio.getInputStream()));
				
				System.out.println("Conexio acceptada" + SocketConnexio.toString());
				
				NomFitxer = EntradaDesdeClient.readLine();
				System.out.println("Dades rebudes: " + NomFitxer);
				
				//netejem el que ens envia el client per obtindre sols el nom del fitxer
				
				//asi llevariem "GET /"
				NomFitxer = NomFitxer.substring(5,NomFitxer.length());
				//asi llevariem " HTTP/1.1"
				NomFitxer = NomFitxer.substring(0,NomFitxer.length()-9);
				
				FileInputStream fileInputStream=null;
				
				//obtindre ruta actual
				File miDir = new File (".");
				
				File file = new File(miDir.getCanonicalPath() + RUTA+ NomFitxer);
				
				//comprobem que existeix el fitxer
				if(file.exists()){
					FileInputStream fos = new FileInputStream(file);
			        
			        byte[] bytes = new byte[1024];
			        
			        int count;
			        while ((count = fos.read(bytes)) > 0) {
			        	SortidaClient.write(bytes, 0, count);
			        }
			        
			        fos.close();
				} else {
					System.out.println("El fichero " + miDir.getCanonicalPath() + "/web/"+ NomFitxer + " no existeix");
				}
		        		        
		        SortidaClient.close();
				
				
				System.out.println("Enviades dades al client");
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		SocketAcollida.close();//tanquem la conexio del servidor
	}

}
