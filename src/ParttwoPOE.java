import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParttwoPOE {
    public static void main(String[] args) {
        // Create frame
        JFrame frame = new JFrame("Ultra Secure Login");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Logo Panel
        JPanel logoPanel = new JPanel();
        ImageIcon logo = new ImageIcon("your-logo.png"); // Replace with your logo path
        JLabel logoLabel = new JLabel(logo);
        logoPanel.add(logoLabel);

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 1, 5, 5));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("🔐 Login");

        loginPanel.add(new JLabel("👤 Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("🔑 Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        // Action for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                JOptionPane.showMessageDialog(frame, "Logging in as: " + username);
            }
        });

        // Add panels to frame
        frame.add(logoPanel, BorderLayout.NORTH);
        frame.add(loginPanel, BorderLayout.CENTER);

        // Show frame
        frame.setVisible(true);
    }
}
