package ChattingBot;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
public class ChatClient
{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private JFrame frame;
    private JTextField messageField;
    private JTextArea chatArea;
    private PrintWriter out;
    public ChatClient()
    {
        setupGUI();
    }
    private void setupGUI()
    {
        frame = new JFrame("Chat Application");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        frame.add(panel, BorderLayout.SOUTH);
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        frame.setVisible(true);
    }
    private void sendMessage()
    {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && out != null)
        {
            out.println(message);
            messageField.setText("");
        }
    }
    public void start()
    {
        try
        {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            Thread readerThread = new Thread(() -> {
                try
                {
                    String message;
                    while ((message = in.readLine()) != null)
                    {
                        chatArea.append(message + "\n");
                    }
                } catch (IOException e)
                {
                    System.err.println("Error reading from server: " + e.getMessage());
                }
            });
            readerThread.start();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(frame, "Unable to connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args)
    {
        ChatClient client = new ChatClient();
        client.start();
    }
}

