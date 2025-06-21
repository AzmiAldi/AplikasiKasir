package Login;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField newPasswordField;

    public ForgotPasswordDialog(Frame parent) {
        super(parent, "Reset Password", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);

        // Header Panel
        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 100, 200));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Email Field
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        // New Password Field
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Password Baru:"), gbc);

        gbc.gridx = 1;
        newPasswordField = new JPasswordField(15);
        formPanel.add(newPasswordField, gbc);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.setBackground(Color.WHITE);
        add(wrapperPanel, BorderLayout.CENTER);

        // Button Panel
        JButton resetButton = new JButton("Reset Password");
        resetButton.setBackground(new Color(255, 193, 7)); // Amber color
        resetButton.setForeground(Color.BLACK);
        resetButton.setFocusPainted(false);
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setPreferredSize(new Dimension(150, 35));
        resetButton.addActionListener(e -> handleReset());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleReset() {
        String email = emailField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());

        if (email.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua field wajib diisi.",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                    "Format email tidak valid",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = AuthController.resetPassword(email, newPassword);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Password berhasil direset!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Email tidak terdaftar dalam sistem",
                    "Reset Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}