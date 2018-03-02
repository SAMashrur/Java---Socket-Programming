import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server() {
		super("Server");
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		//add(userText, BorderLayout.SOUTH);
		
		chatWindow = new JTextArea();
		//chatWindow.setEditable(false);
		//chatWindow.setLineWrap(true);
        //chatWindow.setWrapStyleWord(true);
		//chatWindow.setAlignmentX(BOTTOM_ALIGNMENT);
		//add( new JScrollPane(chatWindow));
		add( new JScrollPane(chatWindow), BorderLayout.CENTER);
		
		setSize(300,300);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void startRunning() {
		try {
			server = new ServerSocket( 6789, 100);
			while(true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
				}
				catch(EOFException eofException) {
					showMessage("\n Server ended the connection");
				}
				catch (IOException ioException) {
					// TODO Auto-generated catch block
					ioException.printStackTrace();
				}
				finally {
					closeChat();
				}
			}
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//wait for Connection, then display Connection information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for Connection...\n");
		connection = server.accept();
		showMessage("Connected to " + connection.getInetAddress().getHostName());
	}
	
	//Set up the Streams - pathways of Communication to and from the other PC 
	//get Stream to send and receive data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now Setup \n");
	}
	
	//during the Chat
	private void whileChatting() throws IOException{
		String message = "You are now connected!";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				//have a Chat
				message = (String) input.readObject();
				showMessage("\n" + message);
			}
			catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n user message not send");
			}
		}while(! message.equals("CLIENT - END"));
	}
	
	//closeChat() -> close streams and sockets after done chatting 
	public void closeChat() {
		showMessage("\n Closing connections...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioexception) {
			ioexception.printStackTrace();
		}
	}
	
	//sendMesage(String s) -> send a message to client
	private void sendMessage(String message) {
		try {
			output.writeObject("Server - " + message);
			output.flush();
			showMessage("\n Server - " + message);
		}
		catch(IOException ioexception) {
			chatWindow.append("\nError : message not send");
		}
	}
	
	//showMessage(String s) -> show message to server chatWindow by updating it
	private void showMessage(final String text) {
		//chatWindow.setEditable(true);
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}
		);
		//chatWindow.setEditable(false);
	}
	
	//ableToType(Bool b) -> typing permission
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					userText.setEditable(tof);
				}
			}
		);
	}
}









