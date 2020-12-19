    
      /****************************************************************
      *       ITESM-CEM                                               *
      *       Sistemas Operativos II                                  *
      *       Prof. Rolando Menchaca Mendez                           *
      *                                                               *
      *       SecureChatServer.java	v 0.5			      *
      *                                                               *
      *	      Servidor de Chat seguro usando SSL y Certificados       *
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
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

/*************************** 
     Clase threadClient 
***************************/         
         
class threadServer extends Thread 
{

	// Declaración de los flujos de entrada y salida, así como del socket
        
        private Socket socket;
  	private BufferedReader in;
  	private PrintWriter out;
        private PrintWriter outg;
        private PrintWriter outk; 

	/* Declaración de los arreglos soc[] y flags[], los cuales guardaran
           la información necesaria para realizar el envío del mensaje de
           broadcast a todos los clientes con sockets abiertos en ese momento*/
        
        private static Socket[] soc = new Socket[15];
        private static boolean[] flags = new boolean[15];
        
        /* Variable estática que indica la correspondecia de cada cliente
           en los arreglos soc[] y flags[]*/
        
        private static int index;
        private int index_cliente;
        
        
        // Constructor de la clase threadServer
        
  	public threadServer(Socket s) throws IOException 
        {
    		
        	// Asignación de s a socket
                
                socket = s;
                
                // Creación del flujo de entrada
    		
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                
           
	}// fin de threadServer(Socket s)

	
        // Método run()
        
        public void run() {
        
        	try 
                {
                        // Leer el nick enviado por el cliente
                        
			String nick = in.readLine();
                        
                        /* Almacenar el socket correspondiente al cliente en
                           en el arreglo estático de sockets, el cual servirá
                           después para hacer el envio del mensaje de broadcast
                           hacia todos los clientes*/
                        
                        soc[index] = (Socket)socket;
	                
                        /* El arreglo flags[] es un arreglo booleano que sirve
                           para ver que sockets están abiertos o cerrados, 
                           existiendo une correspondencia directa entre el
                           arreglo soc[] de sockets, es decir, para cada cliente
                           hay una correspondencia en soc[] y en flags[]*/
                        
                        flags[index] = true;
                        
                        // Guardar en index_cliente el indice de el cliente 
                        
	                index_cliente = index;
                        
                        /* Aumentar la variable estática index para asignar
                           a los demás clientes su correspondiente indice en
                           so[] y flags[]*/ 
                        
                        index++;
                        
                        /* Muestra en la consola del servidor, el nick del 
                           cliente, así como su número de cliente*/
                        
                        System.out.println(nick + " es el cliente conectado numero " + index_cliente);
                        
                        // Enviar el mensaje de conexión del cliente
                        
                        for (int i = 0; i < index; i++) 
                                {  
     
                                        /* Revisar el estado del socket
                                           (abierto o cerrado) de cada uno de
                                           los clientes*/
                                        
                                        
                                        if(flags[i]==true)
                                        {
                                        
                                        	// Prueba para ver a cuantos clientes se lo manda
                                                
                                        	System.out.println("Enviando mensaje de inicio de sesion de " + nick + " a cliente número " + i + "...");
                                                
                                                // Crear un socket temporal 
                                                
                                                Socket gab = (Socket)soc[i];

	                                        // Crear el flujo de salida
                                                
                                                out =   new PrintWriter(new BufferedWriter(new OutputStreamWriter(gab.getOutputStream())), true);   

	                                        // Mandar el mensaje al cliente
                                                
                                                out.println("SE HA CONECTADO " + nick);
                                        
                                        }//if
                                        else 
                                        {
                                            	continue;
                                        }//else
                                       

				}// fin de for para enviar broadcast
                        
                        
                        // Ciclo infinito
                        
                        for (;;) 
                        {        			

                                //Comenzar a escuchar a los clientes
                                
                                String str = in.readLine();
                                
                                //Condición de salida; enviada por cada cliente
                                
                                if (str.equals("SALIR")) 
                                {
                                        
                                        
                                        Socket temp = (Socket)soc[index_cliente];

	                                        // Crear el flujo de salida
                                                
                                        outg =   new PrintWriter(new BufferedWriter(new OutputStreamWriter(temp.getOutputStream())), true);   

	                                        // Mandar el mensaje al cliente
                                                
                                        //System.out.println(nick + "SALIR");
                                        outg.println(nick + "SALIR");
                                        
                                        
                                        
                                        // Enviar el mensaje de desconexión del cliente
                                        
                                        for (int i = 0; i < index; i++) 
                                	{  
     
                                        	/* Revisar el estado del socket
	                                           (abierto o cerrado) de cada uno de
	                                           los clientes*/
	                                        
	                                        if(flags[i]==true)
	                                        {
	                                        
	                                                // Prueba para ver a cuantos clientes se lo manda
	                                                
	                                                //System.out.println("Enviando mensaje brodcast de " + nick + " a cliente número " + i + "...");
	                                                
	                                                // Crear un socket temporal 
	                                                
	                                                Socket adios = (Socket)soc[i];

	                                                // Crear el flujo de salida
	                                                
	                                                outk =   new PrintWriter(new BufferedWriter(new OutputStreamWriter(adios.getOutputStream())), true);   

	                                                // Mandar el mensaje al cliente
	                                                
	                                                outk.println("SE HA DESCONECTADO " + nick);
	                                        
	                                        }//if
	                                        else 
	                                        {
	                                                continue;
	                                        }//else
					}//for broadcast	                                        

                                        flags[index_cliente] = false;                                        

                                        
                                        // Prueba
                                        //System.out.println("SI LLEGA AQUI");
                                         
                                 	break;
                                       
                                } //if
                              
                                //Desplegar mensajes de clientes en servidor
                                
                                System.out.println(nick + " dice: " + str);        			
                           
                           	/* Comienzo de la rutina de broadcast hacia
                                   cada uno de los clientes*/
                                   
                                for (int i = 0; i < index; i++) 
                                {  
     
                                        /* Revisar el estado del socket
                                           (abierto o cerrado) de cada uno de
                                           los clientes*/
                                        
                                        
                                        if(flags[i]==true)
                                        {
                                        
                                        	// Prueba para ver a cuantos clientes se lo manda
                                                
                                        	System.out.println("Enviando mensaje brodcast de " + nick + " a cliente número " + i + "...");
                                                
                                                // Crear un socket temporal 
                                                
                                                Socket gab = (Socket)soc[i];

	                                        // Crear el flujo de salida
                                                
                                                out =   new PrintWriter(new BufferedWriter(new OutputStreamWriter(gab.getOutputStream())), true);   

	                                        // Mandar el mensaje al cliente
                                                
                                                out.println(nick + " dice: " + str);
                                        
                                        }//if
                                        else 
                                        {
                                            	continue;
                                        }//else
                                       

				}// fin de for para enviar broadcast
                                
                                
                                 
      			}// fin de for infinito
                        
                        // Prueba
                        //System.out.println("SE METE??????????????");
                        
                     	/* Mensaje en el servidor para indicar la 
                           finalización del servicio*/
                        
                        //System.out.println("Termina hilo de servicio...");
                        
    		
		}//fin de try	
                
                catch (IOException e) 
                {
	    		System.out.println("Error en el hilo de servicio: " + e);
                        
 	   	}
                
                finally
                {
		        try
                        {
                	// Pruebas de cerrar flujos 
                        in.close();
	                
                        }
                        catch(IOException e) 
	                {
	                                System.out.println("Error al cerrar flujos: " + e);
	                }
                }
                
                


	}//fin de run()

}//fin de class threadServer


/*************************** 
   Clase SecureChatserver 
***************************/
                      
public class SecureChatServer 
{
	
        // Puerto donde escucha el servidor
	
        static final int PORT = 1717;
        
        // Declaración del socket del servidor
        
	static private ServerSocket ss;
        
        // main

	public static void main(String[] args) throws IOException 
        {
        
        	try 
                {
			// Fabrica de sockets seguros
                        
                        ServerSocketFactory ssf =getSecureServerSocketFactory();
			
                        /* Asignar a ss un socket seguro con el puerto 
                           donde escucha el servidor*/
                        
                        ss = ssf.createServerSocket(PORT);

			// Requiere autentificacion del cliente
			
                        ((SSLServerSocket)ss).setNeedClientAuth(true);
                        
                        /* Si todo salió bien, ahora arrancar el servidor :)
                           y deplegar información del servidor en su misma consola*/

	                System.out.println("********************************************");
                        System.out.println("*		                           *");
	                System.out.println("*	  Secure Chat Server v 0.5         *");
                        System.out.println("*		                           *");                         
	                System.out.println("*	          @ITESM-CEM               *");
                        System.out.println("*		                           *");
                        System.out.println("********************************************");

                        System.out.println("");
                        
                        System.out.println("Arranca Servidor");
 			
                        // Ciclo infinito
                        
                        for(;;) 
                        {
     		   		
                                /* Socket para cliente generado por el socket 
                                   que escucha*/
                                
                                Socket socket = ss.accept();
                                
                                /* Se envia a consola un mensaje con la 
                                   aceptación de la conexión segura 
                                   del cliente*/
                                
     		   		System.out.println("Conexion Segura Aceptada...");
       		 		
                                try 
                                {
                                	// Iniciar hilo de servicio
                                        
        	  			new threadServer(socket).start();
      		  		} 
                                
                                catch(Exception e) 
                                {
      		  		        System.out.println("Error al crear hilo de servicio: " + e);
          				socket.close();
     		   		}
                                
      			
                        }// fin de for infinito 
                        
    		}// fin de try 
                
                finally 
                {
      			System.out.println("Servidor a punto de apagarse");
                        ss.close();
    		
                }// fin de finally
  	
        }// fin del main


        // Fábrica de sockets seguros
        
	private static ServerSocketFactory getSecureServerSocketFactory() 
        {
		
                // Declaración de la fábrica ssf
                
                SSLServerSocketFactory ssf = null;
    		
                try 
                {

			/* Establece un administrador de llaves para realizar
		           autentificacion del servidor*/
                        
			SSLContext ctx;
			KeyManagerFactory kmf;
			KeyStore ks;

			// El password del almacen de llaves
                        
			char[] passphrase = "skeystore".toCharArray();

			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
                        
                        // Tipo de almacen
                        
			ks = KeyStore.getInstance("JKS");
                        
                        //Nombre del almacen de llaves
                        
			ks.load(new FileInputStream("skeystore"), passphrase);
			
                        kmf.init(ks, passphrase);
			ctx.init(kmf.getKeyManagers(), null, null);
			
                        ssf = ctx.getServerSocketFactory();
			return ssf;
                        
    		} 
                
                catch (Exception e) 
                {
			e.printStackTrace();
		}
                
		return null;
                
 	}//fin de getSecureServerSocketFactory()

}//fin de class SecureChatServer