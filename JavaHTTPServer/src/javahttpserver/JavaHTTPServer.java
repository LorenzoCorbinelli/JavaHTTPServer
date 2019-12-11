package javahttpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

public class JavaHTTPServer 
{
    public static int port = 8080;
    public static boolean verbose = true;
    
    public static void main(String[] args) 
    {
        try 
        {
            ServerSocket serverConnect = new ServerSocket(port);
            System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");

            // we listen until user halts server execution
            while (true) 
            {
                HTTPServer myServer = new HTTPServer(serverConnect.accept(),verbose);

                if (verbose) 
                {
                    System.out.println("Connecton opened. (" + new Date() + ")");
                }

                // create dedicated thread to manage the client connection
                Thread thread = new Thread((Runnable) myServer);
                thread.start();
            }

        } catch (IOException e) 
        {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }
    
}
