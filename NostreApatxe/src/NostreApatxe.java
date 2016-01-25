

import java.io.*;
import java.net.*;
import java.util.*;

public class NostreApatxe {
	public static void main(String[] args) throws IOException {
		final int PORT = 7000;
		
		String MissatgePeticio = null;
		String NomFitxer;
		
		DataOutputStream SortidaClient = null;
        BufferedReader EntradaDesdeClient = null;
        
        Boolean continuar = true;
        
		while(continuar){
			try{
				System.out.println("Esperant conexio...");
				ServerSocket SocketAcollida = new ServerSocket(PORT);
				Socket SocketConnexio = SocketAcollida.accept();// Servidor esperant conexio
				
				SortidaClient = new DataOutputStream(SocketConnexio.getOutputStream());
				EntradaDesdeClient = new BufferedReader(new InputStreamReader(SocketConnexio.getInputStream()));
				
				System.out.println("Conexio acceptada" + SocketConnexio.toString());
				//SortidaClient.writeBytes("Conexio establerta\n");
				
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
		        
		        File file = new File(miDir.getCanonicalPath() + "/web/"+ NomFitxer);
		        byte[] bFile = new byte[(int) file.length()];
		        
		        //convert file into array of bytes
			    fileInputStream = new FileInputStream(file);
			    fileInputStream.read(bFile);
			    fileInputStream.close();
			    
			    //SortidaClient.write(bFile,0,bFile.length);
			    SortidaClient.writeBytes(file + "\n");
				
				
				System.out.println("Enviades dades al client: " + file);
				SocketAcollida.close();//tanquem la conexio del servidor
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
	}

}
