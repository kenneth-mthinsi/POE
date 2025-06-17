/*
 * This Java file combines a user login/registration system with a simple chat interface.
 * The Login class manages user authentication and registration, while the QuickChat class
 * provides a GUI for sending messages once a user has successfully logged in.
 *
 * Key features include:
 * - Swing-based GUI components for interactive interfaces.
 * - Validation for username, password, and South African phone number formats.
 * - Password hashing using SHA-256 for secure user information storage.
 * - Persistent storage of user data in a text file ("user_data.txt").
 * - A QuickChat interface that displays a chat history and allows sending messages.
 */

package poe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;
import javax.lang.model.SourceVersion;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Login extends JFrame {
    // UI fields for login/registration
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JLabel statusLabel;

    // File to store user credentials and details persistently
    private static final String DATA_FILE = "user_data.txt";

    /**
     * Constructor sets up the initial welcome screen.
     */
    public Login() {
        // Configure the main window of the login system.
        setTitle("User Login & Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setUndecorated(true); // Remove window borders
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the welcome panel with a welcome message.
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to QuickChat!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Create buttons for navigation from the welcome screen.
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton nextButton = new JButton("Next");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(nextButton);
        buttonPanel.add(exitButton);
        welcomePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the welcome panel to the frame.
        add(welcomePanel);

        // Button actions: move to login screen or exit the application.
        nextButton.addActionListener(e -> showLoginScreen());
        exitButton.addActionListener(e -> System.exit(0));

        // Allow use of the Enter key to proceed via the default button.
        getRootPane().setDefaultButton(nextButton);

        setVisible(true);
    }

    /**
     * Sets up and displays the login and registration user interface.
     */
    private void showLoginScreen() {
        getContentPane().removeAll(); // Remove the welcome panel.
        setLayout(new BorderLayout());

        // Create the login panel with a grid layout.
        JPanel loginPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        // Username input with instructions.
        loginPanel.add(new JLabel("Username (1-7 chars, must have _):"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        // Password input with instructions.
        loginPanel.add(new JLabel("Password (min 8 chars, 1 uppercase, 1 number, 1 symbol):"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        // Phone number input with instructions.
        loginPanel.add(new JLabel("Cell Number (+27xxxxxxxxx):"));
        phoneField = new JTextField();
        loginPanel.add(phoneField);

        // Create buttons for registration, login, and viewing registered users.
        JButton registerButton = new JButton("Register");
        loginPanel.add(registerButton);
        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton);
        JButton viewUsersButton = new JButton("View Registered Users");
        loginPanel.add(viewUsersButton);

        // Label for showing status messages (errors, confirmations).
        statusLabel = new JLabel("");
        loginPanel.add(statusLabel);

        add(loginPanel, BorderLayout.CENTER);

        // Additional navigation buttons.
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton nextButton = new JButton("Next");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(nextButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners to handle registration, login, and viewing users.
        registerButton.addActionListener(e -> registerUser());
        loginButton.addActionListener(e -> loginUser());
        viewUsersButton.addActionListener(e -> displayRegisteredUsers());
        nextButton.addActionListener(e -> statusLabel.setText("Fill in the details and proceed!"));
        exitButton.addActionListener(e -> System.exit(0));

        // Refresh the UI to reflect new components.
        revalidate();
        repaint();
    }

    /**
     * Registers a new user after validating the inputs.
     * User details (username, hashed password, and phone) are appended to the
     * data file if validations pass.
     */
    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();

        String passwordValidation = getPasswordValidationMessage(password);
        if (!validateUsername(username)) {
            statusLabel.setText("Invalid username format.");
            return;
        }
        if (passwordValidation != null) {
            statusLabel.setText(passwordValidation);
            return;
        }
        if (!validatePhoneNumber(phone)) {
            statusLabel.setText("Invalid phone format.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(username + "," + hashPassword(password) + "," + phone);
            writer.newLine();
            statusLabel.setText("Registration successful!");
        } catch (IOException e) {
            statusLabel.setText("Error saving user details.");
        }
    }

    /**
     * Authenticates a user by reading and matching stored credentials.
     * If the entered username and hashed password match, the user is welcomed and
     * the QuickChat window is launched.
     */
    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(username) && userDetails[1].equals(hashPassword(password))) {
                    // Welcome the user using the part of the username before the underscore.
                    JOptionPane.showMessageDialog(this, "Welcome " + username.split("_")[0] + "!");
                    // Launch the QuickChat interface.
                    new QuickChat(username).setVisible(true);
                    dispose(); // Close the login window.
                    return;
                }
            }
            statusLabel.setText("Login failed. Username or password incorrect.");
        } catch (IOException e) {
            statusLabel.setText("Error reading user details.");
        }
    }

    /**
     * Reads and displays all registered user details in a dialog.
     */
    private void displayRegisteredUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            StringBuilder users = new StringBuilder("Registered Users:\n");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length >= 3) {
                    users.append("Username: ").append(userDetails[0])
                         .append(", Cell Number: ").append(userDetails[2])
                         .append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, users.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data.");
        }
    }

    /**
     * Validates the username ensuring it is 1-7 characters long and composed of
     * letters, digits, or underscores.
     */
    private boolean validateUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{1,7}$");
    }

    /**
     * Validates the password according to specified rules:
     * - Minimum 8 characters.
     * - Contains an uppercase letter.
     * - Contains a number.
     * - Contains a special symbol (@$!%*#?&).
     *
     * Returns a string message if validation fails; otherwise, returns null.
     */
    private String getPasswordValidationMessage(String password) {
        if (password.length() < 8)
            return "Password must be at least 8 characters.";
        if (!password.matches(".*[A-Z].*"))
            return "Password must contain at least one uppercase letter.";
        if (!password.matches(".*\\d.*"))
            return "Password must contain at least one number.";
        if (!password.matches(".*[@$!%*#?&].*"))
            return "Password must contain at least one special character.";
        return null;
    }

    /**
     * Validates the phone number against the South African format (+27xxxxxxxxx).
     */
    private boolean validatePhoneNumber(String phone) {
        return phone.matches("^\\+27\\d{9}$");
    }

    /**
     * Hashes the user's password using SHA-256 and returns the hash as a hexadecimal string.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password.", e);
        }
    }

    /**
     * Main method: launches the Login interface as the entry point of the application.
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }

    SourceVersion getSupportedSourceVersion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

/*
 * The QuickChat class represents a simple chat interface used after a user logs in.
 * It lets the user send messages to a recipient (specified by phone number) and
 * displays the chat history in a text area. The class stores messages in an ArrayList.
 */
   
    class QuickChat extends JFrame {
    private final String currentUser;
    private final JTextPane chatPane;             // For rich styled text.
    private final StyledDocument chatDocument;
    private final JTextField phoneNumberField;      // Input field for recipient's phone number.
    private final JTextField messageField;          // Input field for typing the message.
    private final ArrayList<ChatMessage> messageHistory;  // List to store all messages.
    private final ArrayList<ChatMessage> deletedMessages; // Deleted messages (for recovery).
    private JLabel statusLabel;                     // Label to display recipient’s online status.
    private boolean recipientOnline = true;         // Simulated online status flag.

    // Enum for tracking message tick status.
    private enum MessageStatus {
        SENT, DELIVERED, READ
    }

    // Inner class to store chat message details.
    private class ChatMessage {
        String sender;         // "You" for user messages or "AI Bot" for bot responses.
        String messageText;    // The actual message content.
        MessageStatus status;  // Tick status (only applicable for user messages).
        boolean isFromUser;    // True if message originated from the user.

        ChatMessage(String sender, String messageText, MessageStatus status, boolean isFromUser) {
            this.sender = sender;
            this.messageText = messageText;
            this.status = status;
            this.isFromUser = isFromUser;
        }
    }

    /**
     * Constructor: sets up the chat interface with header, chat area, and input panel including extra buttons.
     * @param username The current user's name.
     */
    public QuickChat(String username) {
        this.currentUser = username;
        messageHistory = new ArrayList<>();
        deletedMessages = new ArrayList<>();

        // Configure the main window.
        setTitle("QuickChat");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Full-screen mode.
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---- HEADER PANEL: Welcome & Online Status ----
        JLabel welcomeLabel = new JLabel("Welcome to QuickChat, " + currentUser + "!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        statusLabel = new JLabel("Recipient Status: Online");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setForeground(Color.GREEN);

        JButton toggleStatusButton = new JButton("Toggle Recipient Status");
        toggleStatusButton.addActionListener(e -> {
            // Toggle recipient online status and update color.
            recipientOnline = !recipientOnline;
            statusLabel.setText("Recipient Status: " + (recipientOnline ? "Online" : "Offline"));
            statusLabel.setForeground(recipientOnline ? Color.GREEN : Color.RED);
            // If recipient comes online, update pending messages.
            if (recipientOnline) {
                updatePendingMessages();
            }
        });

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(welcomeLabel);
        headerPanel.add(statusLabel);
        headerPanel.add(toggleStatusButton);
        add(headerPanel, BorderLayout.NORTH);

        // ---- CHAT AREA ----
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(new Font("Arial", Font.PLAIN, 14));
        chatDocument = chatPane.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(chatPane);
        add(scrollPane, BorderLayout.CENTER);

        // ---- INPUT PANEL (South) ----
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.add(new JLabel("Recipient Phone Number:"));
        phoneNumberField = new JTextField();
        inputPanel.add(phoneNumberField);

        inputPanel.add(new JLabel("Message:"));
        messageField = new JTextField();
        inputPanel.add(messageField);

        JButton sendButton = new JButton("Send");
        inputPanel.add(sendButton);

        JButton viewHistoryButton = new JButton("View Message History");
        inputPanel.add(viewHistoryButton);

        JButton deleteButton = new JButton("Delete Last Message");
        inputPanel.add(deleteButton);

        JButton recoverButton = new JButton("Recover Deleted Messages");
        inputPanel.add(recoverButton);

        JButton exitButton = new JButton("Exit");
        inputPanel.add(exitButton);
        
        JButton logoutButton = new JButton("logout");
        inputPanel.add(logoutButton);

        add(inputPanel, BorderLayout.SOUTH);

        // ---- BUTTON ACTIONS ----
        sendButton.addActionListener(e -> logMessage());
        viewHistoryButton.addActionListener(e -> viewMessageHistory());
        deleteButton.addActionListener(e -> deleteLastMessage());
        recoverButton.addActionListener(e -> recoverDeletedMessages());
        exitButton.addActionListener(e -> System.exit(0));
        

        setVisible(true);
    }

    /**
     * Logs a new message when "Send" is pressed. Validates input, creates a ChatMessage,
     * schedules tick status updates, and triggers an AI Bot response if applicable.
     */
    private void logMessage() {
        String phoneNumber = phoneNumberField.getText().trim();
        String messageText = messageField.getText().trim();

        if (phoneNumber.isEmpty() || messageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number and message cannot be empty!");
            return;
        }
        
        // Check if the user is conversing with the AI Bot.
        boolean chattingWithBot = phoneNumber.equalsIgnoreCase("AI") || phoneNumber.equalsIgnoreCase("bot");
        
        // Create the user's outgoing message.
        ChatMessage newMsg = new ChatMessage("You", "To " + phoneNumber + ": " + messageText,
                                               MessageStatus.SENT, true);
        messageHistory.add(newMsg);
        refreshChatArea();

        // Clear the input fields.
        phoneNumberField.setText("");
        messageField.setText("");

        // For AI Bot conversation, force the recipient to be online.
        if (chattingWithBot) {
            recipientOnline = true;
            statusLabel.setText("Recipient Status: Online (AI Bot)");
            statusLabel.setForeground(Color.GREEN);
            scheduleTickUpdate(newMsg);

            // After a delay, trigger the AI Bot response.
            new Timer(3000, (ActionEvent e) -> {
                botRespond(messageText);
                ((Timer)e.getSource()).stop();
            }).start();
        } else {
            scheduleTickUpdate(newMsg);
        }
    }

    /**
     * Schedules timers to update a message's tick status:
     * – After 2 seconds: updates SENT to DELIVERED.
     * – After 4 seconds: updates DELIVERED to READ.
     * @param msg The message to update.
     */
    private void scheduleTickUpdate(ChatMessage msg) {
        if (msg.isFromUser && recipientOnline) {
            new Timer(2000, (ActionEvent e) -> {
                if (recipientOnline && msg.status == MessageStatus.SENT) {
                    msg.status = MessageStatus.DELIVERED;
                    refreshChatArea();
                }
                ((Timer)e.getSource()).stop();
            }).start();

            new Timer(4000, (ActionEvent e) -> {
                if (recipientOnline && msg.status == MessageStatus.DELIVERED) {
                    msg.status = MessageStatus.READ;
                    refreshChatArea();
                }
                ((Timer)e.getSource()).stop();
            }).start();
        }
    }

    /**
     * Updates any pending messages (still marked as SENT) to DELIVERED when the recipient comes online,
     * and schedules further update to READ.
     */
    private void updatePendingMessages() {
        for (ChatMessage m : messageHistory) {
            if (m.isFromUser && m.status == MessageStatus.SENT) {
                m.status = MessageStatus.DELIVERED;
            }
        }
        refreshChatArea();
        for (ChatMessage m : messageHistory) {
            if (m.isFromUser && m.status == MessageStatus.DELIVERED) {
                new Timer(2000, (ActionEvent e) -> {
                    if (recipientOnline && m.status == MessageStatus.DELIVERED) {
                        m.status = MessageStatus.READ;
                        refreshChatArea();
                    }
                    ((Timer)e.getSource()).stop();
                }).start();
            }
        }
    }

    /**
     * Simulates an AI Bot response using simple keyword-based logic.
     * @param userMsg The user's original message.
     */
    private void botRespond(String userMsg) {
        String response = getBotResponse(userMsg);
        // AI Bot messages are automatically displayed as READ.
        ChatMessage botMsg = new ChatMessage("AI Bot", response, MessageStatus.READ, false);
        messageHistory.add(botMsg);
        refreshChatArea();
    }

    /**
     * Provides a basic response based on keywords.
     * @param userMsg The user's message.
     * @return The AI Bot's response.
     */
    private String getBotResponse(String userMsg) {
        String lowerMsg = userMsg.toLowerCase();
        if (lowerMsg.contains("hello")) {
            return "Hello there! How can I help you today?";
        } else if (lowerMsg.contains("how are you")) {
            return "I'm doing great, thanks for asking!";
        } else if (lowerMsg.contains("bye")) {
            return "Goodbye! Have a nice day!";
        } else {
            return "I'm not sure I understand. Could you please elaborate?";
        }
    }

    /**
     * Refreshes the chat pane by clearing and reappending every message in the history,
     * using styled text to include message tick markers.
     */
    private void refreshChatArea() {
        chatPane.setText("");
        for (ChatMessage msg : messageHistory) {
            appendMessageToChat(msg);
        }
    }

    /**
     * Appends a single ChatMessage to the chat pane along with its tick markers.
     * @param msg The message to display.
     */
    private void appendMessageToChat(ChatMessage msg) {
        try {
            // Base style for the message.
            SimpleAttributeSet normal = new SimpleAttributeSet();
            StyleConstants.setFontFamily(normal, "Arial");
            StyleConstants.setFontSize(normal, 14);

            // Create the message line.
            String line = msg.sender + ": " + msg.messageText;
            chatDocument.insertString(chatDocument.getLength(), line, normal);

            // Append tick markers for user messages.
            if (msg.isFromUser) {
                String tick = "";
                SimpleAttributeSet tickStyle = new SimpleAttributeSet();
                StyleConstants.setFontFamily(tickStyle, "Arial");
                StyleConstants.setFontSize(tickStyle, 12);
                if (null != msg.status) switch (msg.status) {
                    case SENT -> {
                        tick = "  ✓ (Sent)";
                        StyleConstants.setForeground(tickStyle, Color.GRAY);
                    }
                    case DELIVERED -> {
                        tick = "  ✓✓ (Delivered)";
                        StyleConstants.setForeground(tickStyle, Color.GRAY);
                    }
                    case READ -> {
                        tick = "  ✓✓ (Read)";
                        StyleConstants.setForeground(tickStyle, Color.BLUE);
                    }
                    default -> {
                    }
                }
                chatDocument.insertString(chatDocument.getLength(), tick, tickStyle);
            }
            chatDocument.insertString(chatDocument.getLength(), "\n", normal);
        } catch (BadLocationException e) {
        }
    }

    /**
     * Displays the full message history (with tick statuses) in a dialog.
     */
    private void viewMessageHistory() {
        StringBuilder history = new StringBuilder();
        for (ChatMessage msg : messageHistory) {
            history.append(msg.sender)
                   .append(": ")
                   .append(msg.messageText)
                   .append(" [")
                   .append(msg.status)
                   .append("]\n");
        }
        JOptionPane.showMessageDialog(this, history.toString(), "Message History", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Deletes the last message from the history and moves it to a deleted messages list.
     */
    private void deleteLastMessage() {
        if (!messageHistory.isEmpty()) {
            ChatMessage removed = messageHistory.remove(messageHistory.size() - 1);
            deletedMessages.add(removed);
            refreshChatArea();
        }
    }

    /**
     * Recovers all deleted messages back into the main message history.
     */
    private void recoverDeletedMessages() {
        if (deletedMessages.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No deleted messages to recover.");
            return;
        }
        messageHistory.addAll(deletedMessages);
        deletedMessages.clear();
        refreshChatArea();
        
    }
}
/*  src/poe/Main.java  ─────────────────────────────────────────────
    Compile :  javac  poe/Main.java
    Run     :  java   poe.Main
──────────────────────────────────────────────────────────────────*/

/*─────────────────────────────
  1.  PLAIN MESSAGE OBJECT
─────────────────────────────*/
class Message {
    String id;
    String sender;
    String recipient;
    String body;
    String hash;

    Message(String sender, String recipient, String body) {
        this.id        = UUID.randomUUID().toString();
        this.sender    = sender;
        this.recipient = recipient;
        this.body      = body;
        this.hash      = sha256(body);
    }

    /* helper */
    static String sha256(String txt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(txt.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { return ""; }
    }

    /* persistence helpers */
    String serialise() {
        return id + "|" + sender + "|" + recipient + "|" + body.replace("|","/") + "|" + hash;
    }
    static Message parse(String line) {
        String[] p = line.split("\\|", 5);
        if (p.length != 5) return null;
        Message m = new Message(p[1], p[2], p[3]);
        m.id   = p[0];
        m.hash = p[4];
        return m;
    }

    @Override public String toString() {
        return "[" + id + "] " + sender + " → " + recipient + " : " + body;
    }
}

/*─────────────────────────────
  2.  SIMPLE REPOSITORY
─────────────────────────────*/
class Repo {
    /* explicit java.util so no class-shadowing can break generics */
    static java.util.List<Message> sent        = new java.util.ArrayList<>();
    static java.util.List<Message> disregarded = new java.util.ArrayList<>();
    static java.util.List<Message> stored      = new java.util.ArrayList<>();
    static java.util.List<String>  hashes      = new java.util.ArrayList<>();
    static java.util.List<String>  ids         = new java.util.ArrayList<>();

    /* mutate */
    static void recordSent(Message m)        { add(m, sent);        }
    static void recordDisregarded(Message m) { add(m, disregarded); }
    private static void add(Message m, java.util.List<Message> list) {
        list.add(m); hashes.add(m.hash); ids.add(m.id);
    }

    /* persistence (plain txt) */
    static void load(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                Message m = Message.parse(ln);
                if (m != null) { stored.add(m); hashes.add(m.hash); ids.add(m.id); }
            }
        } catch (IOException ignored) { }
    }
    static void save(String file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Message m : sent) pw.println(m.serialise());
        } catch (IOException ignored) { }
    }

    /* required features */
    static void listSendersRecipients() {
        for (Message m : sent) System.out.println(m.sender + " → " + m.recipient);
    }
    static void showLongest() {
        Message longest = null;
        for (Message m : sent)
            if (longest == null || m.body.length() > longest.body.length()) longest = m;
        System.out.println(longest == null ? "No messages" : longest);
    }
    static Message findById(String id) {
        for (Message m : sent) if (m.id.equals(id)) return m; return null;
    }

    /* ←─ you asked to keep THIS signature ─────────────────────── */
    static java.util.List<Message> findByRecipient(String recipient) {
        java.util.List<Message> result = new java.util.ArrayList<>();
        for (Message m : sent) {
            if (m.recipient.equalsIgnoreCase(recipient)) {
                result.add(m);
            }
        }
        return result;
    }
    /* ──────────────────────────────────────────────────────────── */

    static boolean deleteByHash(String h) {
        Iterator<Message> it = sent.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.hash.equals(h)) { it.remove(); hashes.remove(h); return true; }
        }
        return false;
    }
    static void report() {
        System.out.println("──── SENT MESSAGES ────");
        for (Message m : sent) System.out.println(m);
        System.out.println("───────────────────────");
        
        Repo.load("messages.txt");              // bring previous run into memory
Scanner in = new Scanner(System.in);
        String user = "SL_M";                   // fake login

loop:
while (true) {
    System.out.println("""
        1  Send message
        2  List sender → recipient
        3  Longest message
        4  Find by ID
        5  Messages for recipient
        6  Delete by hash
        7  Full report
        0  Exit""");
    switch (in.nextLine().trim()) {
        case "1" -> {
            System.out.print("Recipient: "); String r = in.nextLine();
            System.out.print("Body     : "); String b = in.nextLine();
            Message m = new Message(user, r, b);
            if (b.trim().length() < 3) Repo.recordDisregarded(m);
            else Repo.recordSent(m);
            System.out.println("Sent (ID=" + m.id + ")");
        }
        case "2" -> Repo.listSendersRecipients();
        case "3" -> Repo.showLongest();
        case "4" -> {
            System.out.print("ID: ");
            Message m = Repo.findById(in.nextLine());
            System.out.println(m==null? "Not found" : m);
        }
        case "5" -> {
            System.out.print("Recipient: ");
            String r = in.nextLine();
            for (Message m : Repo.findByRecipient(r)) {
                System.out.println(m);
            }
        }
        case "6" -> {
            System.out.print("Hash: ");
            System.out.println(Repo.deleteByHash(in.nextLine()) ? "Deleted" : "Not found");
        }
        case "7" -> Repo.report();
        case "0" -> { break loop; }
        default  -> System.out.println("Bad option");
    }
}
Repo.save("messages.txt");               // persist for next run
System.out.println("Good-bye!");

    }
}    
