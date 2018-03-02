import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private String message = "";
	private String serverIP;
	
	public Client(String host) {
		super("Client");
		
		setServerIP(host);
		
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
		
		chatWindow = new JTextArea();
		add( new JScrollPane(chatWindow), BorderLayout.CENTER);	
	
		setSize(300, 300);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//Chatting operation
	public void startRunning() {
		try {
			connectToServer();	//waitForConnection()
			setupStreams();
			whileChatting();
		}
		catch(EOFException eofException) {
			showMessage("\nClient ended the connection");
		} 
		catch (IOException ioException) {
			// TODO Auto-generated catch block
			ioException.printStackTrace();
		}
		finally {
			closeChat();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Waiting for Connection...\n");
		connection = new Socket(InetAddress.getByName(getServerIP()), 6789);
		showMessage("Connected to " + connection.getInetAddress().getHostName());
	}
	
	//setup streams to send and receive messages
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are now Setup\n");
	}
	
	//whileChatting() with Server
	private void whileChatting() throws IOException{
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}
			catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n user message not send");
			}
		}while( !message.equals("Client - END"));
	}
	
	//closing chatting operation -> close the streams & sockets
	public void closeChat() {
		showMessage("\nClosing connections...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//send message to server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}
		catch(IOException ioException){
			chatWindow.append("\nError: message not send");
		}
	}
	
	//show message on chatWindow by updating part of the GUI
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(message);
				}
			}
		);
	}
	
	//typing permission 
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					userText.setEditable(tof);
				}
			}
		);
	}
	
	private void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	} 
	private String getServerIP() {
		return serverIP;
	}
}
