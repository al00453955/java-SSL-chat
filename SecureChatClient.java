    
      /****************************************************************
      *       ITESM-CEM                                               *
      *       Sistemas Operativos II                                  *
      *       Prof. Rolando Menchaca Mendez                           *
      *                                                               *
      *       SecureChatClient.java	v 0.5			      *
      *                                                               *
      *	      Cliente de un Chat seguro usando SSL y Certificados     *
      *       digitales, tanto por parte del cliente como por         *
      *	      parte del servidor.                                     *
      *                                                               *
      *                                                               *
      *       Autores:                                                *
      *				Erika Vilches González  461595        *        
      *				Gabriel Téllez Morales  453955        *
      *                                                               *
      *                                                               *
      ****************************************************************/

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
 
/*************************** 
     Clase threadClient 
***************************/

class threadClient extends Thread 
{

	// Declaración del flujo de entrada, así como del socket
	
        private Socket socket;
  	private BufferedReader in;
        private String nick;

        // Constructor de la clase threadClient
        
        public threadClient(Socket s, String n) throws IOException 
	{
        
        	// Asignación de s a socket
        
        	socket = s;
                nick = n;
                
                // Creación del flujo de entrada
                
            	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                

	}// fin de constructor threadClient(Socket s) 

        
        // Método run() 
        
	public void run() 
        {
        	try 
                {       
      			for (;;) 
                        {

                        	try
                                {
                                
                                	//Comenzar a escuchar al servidor
	                        
	                                String str = in.readLine();
	                                
	                                /* Imprimir en el cliente todo lo que llegue
	                                   desde el servidor*/
	                                
	                                
	                                
	                                if(str.equals(nick + "SALIR"))
	                                {
	                                        //System.out.println(str);
	                                        //in.close();
	                                        //socket.close();
	                                        break;
	                                }
	                                
	                                System.out.println(str);
                                
                                }//try
                                
                                catch (Exception e) 
                        	{
				
                                	throw new IOException(e.getMessage());
			
                        	}
                                finally
                                {
                                	 
                                }
                                
      			}// fin de for infinito


    		}//fin de try
                 
        	catch (IOException e) 
                {

                   	System.out.println("Error en el hilo de servicio: " + e);
 	   	}

   	 	finally 
                {
    	  		
                        try
                        {
                        	System.out.println("el cliente dejo de escuchar");
                        	in.close();
                        }
                        catch(IOException e) 
	                {
	                                System.out.println("Error al cerrar flujos: " + e);
	                }
                        

		}//fin de finally 
                
 	}//fin de run() 

}//fin de class threadClient
 
 
/*************************** 
   Clase SecureChatClient 
***************************/

public class SecureChatClient {

	// Puerto donde escucha el servidor
        
        static final int SPORT = 1717;
        
	
	// main 
        
        public static void main(String[] args)throws IOException {

		// Revisar los argumentos introducidos
                
                if(args.length<1) {
	                System.out.println("uso: java -Djavax.net.ssl.trustStore=ckeystore -Djavax.net.ssl.trustStorePassword=ckeystore SecureChatClient 127.0.0.1");
	                System.exit(-1);
	        }
	
        	// Guardar dirección del servidor en addr
                
                InetAddress addr = InetAddress.getByName(args[0]);

        	try 
                {
           		 /* Establece el administrador de llaves para realizar
	                    la autentificacion del cliente. Utiliza la 
                            implementacion por defecto de "TrustStore" y de 
                            las rutinas "secureRandom"*/
                         
	                // Fabrica de sockets seguros
                        
                        SSLSocketFactory factory = null;
	                
	                try 
                        {
				/* Establece un administrador de llaves para 
                                   realizar autentificacion del servidor*/
                                
                                SSLContext ctx;
	                        KeyManagerFactory kmf;
	                        KeyStore ks;
	                
	                        // El password del almacen de llaves
                                
	                        char[] passphrase = "ckeystore".toCharArray();

	                        ctx = SSLContext.getInstance("TLS");
	                        kmf = KeyManagerFactory.getInstance("SunX509");

	                        // Tipo de almacen
                                
	                        ks = KeyStore.getInstance("JKS");
	                
	                        //Nombre del almacen de llaves
                                
	                        ks.load(new FileInputStream("ckeystore"), passphrase);

	                        kmf.init(ks, passphrase);
	                        ctx.init(kmf.getKeyManagers(), null, null);

	                        factory = ctx.getSocketFactory();

			} 	
                        
			catch (Exception e) 
                        {
				
                                throw new IOException(e.getMessage());
			
                        }

		
                	/* Creación del socket del cliente con la dirección del
                           servidor y el puerto en el que escucha*/
                
                
                	SSLSocket socket = (SSLSocket)factory.createSocket(addr, SPORT);

		    	
                        // Comienza el handshake entre cliente/servidor
		   	 

			socket.startHandshake();
             
                	
			// Creación del flujo de salida
                         
	                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

	                // Creación de inconsole para leer de la consola

	                BufferedReader inconsole = new BufferedReader(new InputStreamReader(System.in));

	                // Creación de inconsole para leer de la consola 

	                BufferedReader stdin = new BufferedReader(new InputStreamReader( System.in ));

	                // Deplegar información del servidor en el cliente

	                System.out.println("********************************************");
                        System.out.println("*		                           *");
	                System.out.println("*	  Secure Chat Client v 0.5         *");
                        System.out.println("*		                           *");                         
	                System.out.println("*	         BIENVENIDO                *");
                        System.out.println("*		                           *");
                        System.out.println("********************************************");

                        System.out.println("");

	                // Petición del nick al cliente

	                System.out.println("Dame tu nombre de usuario por favor: ");

	                // Leer nick

	                String nick = stdin.readLine();


	                // Enviar nick al servidor

	                out.println(nick);
                        
                        // Desplegar información de bienvenida al usuario
                        
                        System.out.println("Hola " + nick + " :)");
                        System.out.println("Para salir del chat escribe: SALIR");
                        System.out.println("");
                                
			try 
                        {
        	    		// Iniciar hilo de servicio 
                                
                                new threadClient(socket,nick).start();
      			}

                	catch(Exception e) 
                        {
                        
      		  		System.out.println("Error al crear hilo de servicio: " + e);
          			socket.close(); 
                        }

                	
                        try
                        {
	                        
	                        // Ciclo infinito
	                        
	                        for(;;) 
	                        {
	                                
	                                // Leer de consola lo escrito por el cliente                                
	                                
	                                String str = inconsole.readLine();
	                                
	                                /* Condición de salida; enviada al servidor 
	                                   por el cliente*/
	                                
	                                if(str.equals("SALIR")) 
	                                {
	                                        /* Enviar el comando SALIR del cliente 
	                                           hacia el servidor*/
	                                        
	                                        out.println(str);
	                                        
                                                System.out.println("Cerrando");
	                                        break;
	                                }
	                                
	                                /* Enviar lo escrito por el cliente hacia
	                                   el servidor*/
	                                
	                                out.println(str);
                                
                                }// fin de for infinito
                                
                                 
	                        
	                        //socket.close(); 

			}// fin de try
                        
                                	
			catch(IOException e) 
	                {
	                                System.out.println("Error al cerrar flujos: " + e);
	                }
                        
                        finally
                	{
                		out.close();
                	}


		}// fin de try
                
		catch(IOException e) 
	        {
	        	System.out.println("Error al crear la fabrica de sockets: " + e);
	        }
                 
                
                       
                
	
        }// fin del main
        
}//fin de class SecureChatClient