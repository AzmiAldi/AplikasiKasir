package main;

import Login.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class DashboardFrame extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private HashMap<String, JPanel> panelMap;

    public DashboardFrame() {
        setTitle("Dashboard - Stello Coffee");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(65, 105, 225));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        // CardLayout Panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        panelMap = new HashMap<>();

        // Tambahkan halaman konten (dummy dulu)
        panelMap.put("dashboard", createPagePanel("Halaman Dashboard"));
        panelMap.put("produk", new panels.ProdukPanel());
        panelMap.put("transaksi", new panels.TransaksiPanel());
        panelMap.put("struk", new panels.StrukPanel());
        panelMap.put("shift", createPagePanel("Halaman Shift"));

        // Tambahkan ke mainPanel
        for (String key : panelMap.keySet()) {
            mainPanel.add(panelMap.get(key), key);
        }

        // Tombol Sidebar
        JButton btnDashboard = createSidebarButton("Dashboard", "src/assets/dashboard.png", "dashboard");
        JButton btnProduk = createSidebarButton("Produk", "src/assets/produk.png", "produk");
        JButton btnTransaksi = createSidebarButton("Transaksi", "src/assets/transaksi.png", "transaksi");
        JButton btnStruk = createSidebarButton("Struk", "src/assets/struk.png", "struk");
        JButton btnShift = createSidebarButton("Shift", "src/assets/shift.png", "shift");
        JButton btnLogout = createSidebarButton("Logout", "src/assets/logout.png", "logout");

        btnLogout.setBackground(new Color(100, 149, 237));
        btnLogout.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                new Login.LoginFrame().setVisible(true);
            }
        });

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnProduk);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnTransaksi);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnStruk);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnShift);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(20));

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Tampilkan halaman default
        cardLayout.show(mainPanel, "dashboard");
    }

    // Membuat tombol sidebar
    private JButton createSidebarButton(String text, String iconPath, String cardName) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton button = new JButton(text, new ImageIcon(image));
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setIconTextGap(10);
        button.setFocusPainted(false);

        // Aksi navigasi
        if (!cardName.equals("logout")) {
            button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        }

        return button;
    }

    // Panel Dummy untuk tiap halaman
    private JPanel createPagePanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        new DashboardFrame().setVisible(true);
    }
}
