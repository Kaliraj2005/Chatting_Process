import java.io.*;
import java.net.*;
import java.util.*;
public class ChatServer
{
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static final int PORT = 12345;
    public static void main(String[] args)
    {
        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Server started on port " + PORT);
            while (true)
            {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket, clientHandlers);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
class ClientHandler implements Runnable
{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static Set<ClientHandler> clientHandlers;
    private String clientName;
    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers)
    {
        this.socket = socket;
        ClientHandler.clientHandlers = clientHandlers;
    }
    @Override
    public void run()
    {
        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Enter your name:");
            clientName = in.readLine();
            System.out.println(clientName + " has joined the chat!");
            broadcast(clientName + " has joined the chat!");
            String message;
            while ((message = in.readLine()) != null)
            {
                if (message.equalsIgnoreCase("exit"))
                {
                    break;
                }
                broadcast(clientName + ": " + message);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                clientHandlers.remove(this);
                socket.close();
                broadcast(clientName + " has left the chat!");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void broadcast(String message)
    {
        for (ClientHandler client : clientHandlers)
        {
            client.out.println(message);
        }
    }
}
