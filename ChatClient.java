import java.io.*;
import java.net.*;
public class ChatClient
{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader userInput;
    public static void main(String[] args)
    {
        ChatClient client = new ChatClient();
        client.start();
    }
    public void start()
    {
        try
        {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the server!");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));
            Thread readThread = new Thread(new ReadMessages());
            readThread.start();
            sendMessage();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendMessage()
    {
        try
        {
            System.out.println("Enter your name: ");
            String name = userInput.readLine();
            out.println(name);
            while (true)
            {
                String message = userInput.readLine();
                if (message.equalsIgnoreCase("exit"))
                {
                    out.println(message);
                    break;
                }
                out.println(message);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private class ReadMessages implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null)
                {
                    System.out.println(serverMessage);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

