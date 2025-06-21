package Login;

import main.DashboardFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("Stello Coffee - Login");
        setSize(450, 650); // Slightly increased height to accommodate labels
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 249, 250));

        // Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Header section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Logo
        ImageIcon icon = new ImageIcon("src/assets/cart_icon.png");
        Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaled));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Title
        JLabel title = new JLabel("Stello Coffee", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(65, 105, 225));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Point of Sale System", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(108, 117, 125));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        headerPanel.add(iconLabel);
        headerPanel.add(title);
        headerPanel.add(subtitle);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Form title
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form description
        JLabel descLabel = new JLabel("Masukkan akun untuk mengakses POS sistem");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Username section
        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(73, 80, 87));
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);

        JTextField usernameField = createFormField();
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        // Password section
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(73, 80, 87));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);

        JPasswordField passwordField = createPasswordField();
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Login button
        JButton loginButton = createPrimaryButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = String.valueOf(passwordField.getPassword()).trim();

                boolean success = AuthController.login(username, password);

                if (success) {
                    JOptionPane.showMessageDialog(null, "Login berhasil!");
                    new DashboardFrame().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Username atau password salah.");
                }
            }
        });

        // Secondary buttons panel
        JPanel secondaryButtonsPanel = new JPanel();
        secondaryButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        secondaryButtonsPanel.setBackground(Color.WHITE);

        JButton registerButton = createTextButton("Daftar", new Color(0, 120, 215));
        registerButton.addActionListener(e -> new RegisterFrame().setVisible(true));

        JButton forgotPasswordButton = createTextButton("Lupa Password?", new Color(108, 117, 125));
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordButton.addActionListener(e -> new ForgotPasswordDialog(this).setVisible(true));

        secondaryButtonsPanel.add(registerButton);
        secondaryButtonsPanel.add(forgotPasswordButton);

        // Add components to form panel
        formPanel.add(loginLabel);
        formPanel.add(descLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(secondaryButtonsPanel);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(loginButton);
    }

    private JTextField createFormField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(65, 105, 225));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createTextButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setForeground(color);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}