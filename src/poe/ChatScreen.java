/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poe;

/**
 *
 * @author RC_Student_lab
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ChatScreen extends JFrame {

private JTextArea chatArea;
private JTextField messageField;
private JButton sendButton;
private JLabel profilePicture;
private JLabel name;
private JLabel onlineStatus;
private JLabel tickLabel;

public ChatScreen() {
createGUI();
}

private void createGUI() {
// Set up the frame
setSize(400, 600);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setTitle("Chat Screen");
setLayout(null);

// Create the profile picture
profilePicture = new JLabel();
try {
// Ensure the image file exists
profilePicture.setIcon(new ImageIcon("profile_picture.png")); // Replace with your actual image path
} catch (Exception e) {
profilePicture.setText("No Image"); // Placeholder text if image is missing
profilePicture.setHorizontalAlignment(JLabel.CENTER);
}
profilePicture.setBounds(10, 10, 50, 50);

// Create the name label
name = new JLabel("Kenneth Mthinsi");
name.setFont(new Font("Arial", Font.BOLD, 14)); // Add a consistent font style
name.setBounds(70, 10, 200, 20);

// Create the online status label
onlineStatus = new JLabel("Online");
onlineStatus.setFont(new Font("Arial", Font.PLAIN, 12));
onlineStatus.setBounds(70, 30, 100, 20);
onlineStatus.setForeground(Color.GREEN);

// Create the tick label
tickLabel = new JLabel("\u2713\u2713"); // Double grey tick
tickLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Consistent font size
tickLabel.setBounds(350, 550, 20, 20);

// Create the chat area
chatArea = new JTextArea();
chatArea.setEditable(false);
chatArea.setLineWrap(true); // Enable line wrapping for better readability
chatArea.setWrapStyleWord(true); // Wrap at word boundaries
chatArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Set a readable font
JScrollPane scrollPane = new JScrollPane(chatArea); // Add a scroll pane for long chats
scrollPane.setBounds(10, 70, 370, 450);

// Create the message field
messageField = new JTextField();
messageField.setBounds(10, 530, 300, 30);
messageField.setFont(new Font("Arial", Font.PLAIN, 14));

// Create the send button
sendButton = new JButton("Send");
sendButton.setBounds(320, 530, 60, 30);
sendButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
String message = messageField.getText().trim();
if (!message.isEmpty()) {
chatArea.append("You: " + message + "\n");
messageField.setText("");
// Simulate AI bot response
String response = getBotResponse(message);
chatArea.append("Bot: " + response + "\n");

// Update the tick label to indicate the message was sent
tickLabel.setText("\u2713\u2713"); // Double grey tick

// Update the tick label to double blue tick after a short delay
Timer timer = new Timer(1000, new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
tickLabel.setText("\u2714\u2714"); // Double blue tick
}
});
timer.setRepeats(false); // Ensure the timer runs only once
timer.start();
}
}
});

// Add components to the frame
add(profilePicture);
add(name);
add(onlineStatus);
add(tickLabel);
add(scrollPane); // Add scroll pane instead of the raw text area
add(messageField);
add(sendButton);

// Make the frame visible
setVisible(true);
}

private String getBotResponse(String message) {
// A very basic AI bot response simulator
if (message.equalsIgnoreCase("hello")) {
return "Hi! How are you?";
} else {
return "im good thanks";
}
}

public static void main(String[] args) {
SwingUtilities.invokeLater(() -> new ChatScreen()); // Ensure thread-safety when starting the app
}
}
