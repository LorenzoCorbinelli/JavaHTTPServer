package javahttpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JavaHTTPServer 
{
    
    public static void main(String[] args) 
    {
        try 
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Config con = (Config) jaxbUnmarshaller.unmarshal(new File("config.xml"));
            ServerSocket serverConnect = new ServerSocket(con.port);
            System.out.println("Server started.\nListening for connections on port : " + con.port + " ...\n");

            
            // we listen until user halts server execution
            while (true) 
            {
                HTTPServer myServer = new HTTPServer(serverConnect.accept(), con);

                if (con.verbose) 
                {
                    System.out.println("Connecton opened. (" + new Date() + ")");
                }

                // create dedicated thread to manage the client connection
                Thread thread = new Thread((Runnable) myServer);
                thread.start();
            }

        } catch (Exception e) 
        {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }
    
}
