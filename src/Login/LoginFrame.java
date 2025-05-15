package Login;

import com.formdev.flatlaf.FlatLightLaf;
import main.DashboardFrame;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private boolean authenticate(String username, String password) {
        return (username.equals("admin") && password.equals("admin123"))
                || (username.equals("cashier") && password.equals("cashier123"));
    }

    public LoginFrame() {

        setTitle("Stello Coffee - Login");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        wrapper.setBackground(new Color(250, 251, 253));

        ImageIcon icon = new ImageIcon("src/assets/cart_icon.png");
        Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaled));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel title = new JLabel("Stello Coffee", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(65, 105, 225)); // blue

        JLabel subtitle = new JLabel("Point of Sale System", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel descLabel = new JLabel("Masukkan akun untuk mengakses POS sistem");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 40));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 40));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setBackground(new Color(65, 105, 225));
        loginButton.setForeground(Color.WHITE);
        loginButton.setHorizontalAlignment(SwingConstants.CENTER);
        loginButton.setFocusPainted(false);

        JLabel creds = new JLabel("");
        creds.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        creds.setForeground(Color.GRAY);
        creds.setHorizontalAlignment(SwingConstants.CENTER);
        creds.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        formPanel.add(loginLabel);
        formPanel.add(descLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginButton);

        wrapper.add(iconLabel, BorderLayout.NORTH);
        wrapper.add(title, BorderLayout.CENTER);
        wrapper.add(subtitle, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(wrapper, BorderLayout.NORTH);
        center.add(formPanel, BorderLayout.CENTER);
        center.add(creds, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        //Fungsi Login
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                // Buka tampilan dashboard
                new DashboardFrame().setVisible(true);
                this.dispose(); // Tutup jendela login
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });
        getRootPane().setDefaultButton(loginButton);
    }
}
