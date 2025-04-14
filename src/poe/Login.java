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
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}

/*
 * The QuickChat class represents a simple chat interface used after a user logs in.
 * It lets the user send messages to a recipient (specified by phone number) and
 * displays the chat history in a text area. The class stores messages in an ArrayList.
 */
class QuickChat extends JFrame {
    private final String currentUser;
    private final JTextArea chatArea;       // Area to display chat history.
    private final JTextField phoneNumberField; // Input field for recipient's phone number.
    private final JTextField messageField;  // Input field for typing the message.
    private final ArrayList<String> messageHistory; // List to store sent messages.

    /**
     * Constructor for QuickChat: sets up the chat interface using Swing components.
     * @param username The username (passed from Login) used in the welcome message.
     */
    public QuickChat(String username) {
        this.currentUser = username;
        messageHistory = new ArrayList<>();

        // Configure the QuickChat window.
        setTitle("QuickChat");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode.
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // NORTH: Display a welcome message using the current user's name.
        JLabel welcomeLabel = new JLabel("Welcome to QuickChat, " + currentUser + "!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // CENTER: Create a non-editable text area for chat messages wrapped in a scroll pane.
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // SOUTH: Build an input panel with fields for entering the recipient's phone number and message.
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new JLabel("Recipient Phone Number:"));
        phoneNumberField = new JTextField();
        inputPanel.add(phoneNumberField);

        inputPanel.add(new JLabel("Message:"));
        messageField = new JTextField();
        inputPanel.add(messageField);

        // Create a button for sending messages.
        JButton sendButton = new JButton("Send");
        inputPanel.add(sendButton);

        // Create a button to display the history of messages.
        JButton viewHistoryButton = new JButton("View Message History");
        inputPanel.add(viewHistoryButton);

        add(inputPanel, BorderLayout.SOUTH);

        // Button actions: log the message when "Send" is pressed,
        // and display the message history when "View Message History" is pressed.
        sendButton.addActionListener(e -> logMessage());
        viewHistoryButton.addActionListener(e -> displayMessageHistory());

        setVisible(true);
    }

    /**
     * Logs a message to the chat.
     * The method retrieves the input recipient phone number and message text,
     * validates that neither is empty, formats the message, appends it to the chat area,
     * and clears the input fields afterwards.
     */
    private void logMessage() {
        String phoneNumber = phoneNumberField.getText().trim();
        String messageText = messageField.getText().trim();

        if (phoneNumber.isEmpty() || messageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number and message cannot be empty!");
            return;
        }

        String logEntry = "To " + phoneNumber + ": " + messageText;
        messageHistory.add(logEntry);
        chatArea.append(logEntry + "\n");

        // Clear fields after sending.
        phoneNumberField.setText("");
        messageField.setText("");
    }

    /**
     * Displays the full message history in a dialog box.
     */
    private void displayMessageHistory() {
        StringBuilder history = new StringBuilder();
        for (String msg : messageHistory) {
            history.append(msg).append("\n");
        }
        JOptionPane.showMessageDialog(this, history.toString(), "Message History", JOptionPane.INFORMATION_MESSAGE);
    }
}
