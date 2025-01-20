package ChattingBot;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer
{
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    public static void main(String[] args) throws IOException
    {
        System.out.println("Chat server started...");
        ServerSocket serverSocket = new ServerSocket(12345);
        try
        {
            while (true)
            {
                new ClientHandler(serverSocket.accept()).start();
            }
        }
        finally
        {
            serverSocket.close();
        }
    }
    private static class ClientHandler extends Thread
    {
        private Socket socket;
        private PrintWriter out;
        public ClientHandler(Socket socket)
        {
            this.socket = socket;
        }
        public void run()
        {
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters)
                {
                    clientWriters.add(out);
                }
                String message;
                while ((message = in.readLine()) != null)
                {
                    System.out.println("Received: " + message);
                    broadcastMessage(message);
                }
            }
            catch (IOException e)
            {
                System.err.println("Error handling client: " + e.getMessage());
            }
            finally
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
                synchronized (clientWriters)
                {
                    clientWriters.remove(out);
                }
            }
        }
        private void broadcastMessage(String message)
        {
            synchronized (clientWriters)
            {
                for (PrintWriter writer : clientWriters)
                {
                    writer.println(message);
                }
            }
        }
    }
}

