package javahttpserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class HTTPServer implements Runnable
{
    private Socket socket;
    private final boolean verbose;
    static final File WEB_ROOT = new File("..");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    
    public HTTPServer(Socket s, boolean verbose)
    {
        socket = s;
        this.verbose = verbose;
    }
    
    @Override
    public void run()
    {
        // we manage our particular client connection
        BufferedReader in = null; 
        PrintWriter out = null; 
        BufferedOutputStream dataOut = null;
        String fileRequested = null;
		
        try 
        {
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(socket.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(socket.getOutputStream());

            // get first line of the request from the client
            String input = in.readLine();
            // we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
            // we get file requested
            fileRequested = parse.nextToken().toLowerCase();

            // we support only GET and HEAD methods, we check
            if (!method.equals("GET")  &&  !method.equals("HEAD")) 
            {
                if (verbose) 
                {
                    System.out.println("501 Not Implemented : " + method + " method.");
                }

                // we return the not supported file to the client
                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
                int fileLength = (int) file.length();
                String contentMimeType = "text/html";
                //read content to return to client
                byte[] fileData = readFileData(file, fileLength);

                // we send HTTP Headers with data to client
                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + contentMimeType);
                out.println("Content-length: " + fileLength);
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                // file
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();

            } else 
            {
                // GET or HEAD method
                if (fileRequested.endsWith("/")) 
                {
                    fileRequested += DEFAULT_FILE;
                }

                File file = new File(WEB_ROOT, fileRequested);
                System.out.println(file.getAbsolutePath());
                int fileLength = (int) file.length();
                String content = getContentType(fileRequested);

                if (method.equals("GET")) 
                { // GET method so we return content
                    byte[] fileData = readFileData(file, fileLength);
                   // send HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    out.println("Content-length: " + fileLength);
                    out.println(); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer

                    dataOut.write(fileData, 0, fileLength);
                    dataOut.flush();
                }

                if (verbose) 
                {
                    System.out.println("File " + fileRequested + " of type " + content + " returned");
                }

            }

        } catch (FileNotFoundException fnfe) 
        {
            try 
            {
                fileNotFound(out, dataOut, fileRequested);
            } catch (IOException ioe) 
            {
                System.err.println("Error with file not found exception : " + ioe.getMessage());
            }

        } catch (IOException ioe) 
        {
            System.err.println("Server error : " + ioe);
        } finally 
        {
            try 
            {
                in.close();
                out.close();
                dataOut.close();
                socket.close(); // we close socket connection
            } catch (Exception e) 
            {
                System.err.println("Error closing stream : " + e.getMessage());
            } 

            if (verbose) 
            {
                System.out.println("Connection closed.\n");
            }
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException 
    {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
                fileIn = new FileInputStream(file);
                fileIn.read(fileData);
        } finally {
                if (fileIn != null) 
                        fileIn.close();
        }

        return fileData;
    }
	
    // return supported MIME Types
    private String getContentType(String fileRequested) 
    {
        if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
            return "text/html";
        if(fileRequested.endsWith(".css"))
            return "text/css";
        return "text/plain";
    }
	
    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException
    {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);
        System.out.println(fileRequested);
        if(fileRequested.equals("/pippo.php"))
        {
            out.println("HTTP/1.1 301 Moved Permanently");
            out.println("Location: http://localhost:8080/pippo.html");
            out.println("Server: Java HTTP Server from SSaurel : 1.0");
            out.println("Date: " + new Date());
            out.println("Content-type: " + content);
            out.println("Content-length: " + fileLength);
            out.flush();
        }
        else
        {
            File Dir = new File(WEB_ROOT, (fileRequested+'/'));
            if(Dir.exists())
            {
                out.println("HTTP/1.1 301 Moved Permanently");
                out.println("Location: "+(fileRequested+'/'));
                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + content);
                out.println("Content-length: " + (int)Dir.length());
                out.println();
                out.flush();
            }
            else
            {
                out.println("HTTP/1.1 404 File Not Found");
                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + content);
                out.println("Content-length: " + fileLength);
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer

                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
            }
            if (verbose) 
            {
                System.out.println("File " + fileRequested + " not found");
            }
        }
    }
}
