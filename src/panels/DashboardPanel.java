package panels;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class DashboardPanel extends JPanel {
    private JLabel lblTotalPenjualan, lblTotalTransaksi, lblProdukTerjual, lblShiftAktif;
    private JLabel lblTitlePenjualan, lblTitleTransaksi, lblTitleProduk, lblTitleShift;

    public DashboardPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));

        JLabel header = new JLabel("Dashboard");
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(new Color(30, 50, 100));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(header, BorderLayout.WEST);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(65, 105, 225));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshData());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Stats panel - menggunakan GridBagLayout untuk responsif
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Baris pertama
        gbc.gridx = 0;
        gbc.gridy = 0;
        statsPanel.add(createStatCard("Total Penjualan", "Rp 0", new Color(70, 130, 180)), gbc);

        gbc.gridx = 1;
        statsPanel.add(createStatCard("Jumlah Transaksi", "0", new Color(60, 179, 113)), gbc);

        // Baris kedua
        gbc.gridx = 0;
        gbc.gridy = 1;
        statsPanel.add(createStatCard("Produk Terjual", "0", new Color(205, 92, 92)), gbc);

        gbc.gridx = 1;
        statsPanel.add(createStatCard("Shift Aktif", "Tidak Ada", new Color(138, 43, 226)), gbc);

        // Scroll pane untuk responsif di berbagai ukuran
        JScrollPane scrollPane = new JScrollPane(statsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Load data pertama kali
        refreshData();
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Header dengan gradient
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(color);
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Simpan referensi untuk judul
        if (title.equals("Total Penjualan")) lblTitlePenjualan = lblTitle;
        else if (title.equals("Jumlah Transaksi")) lblTitleTransaksi = lblTitle;
        else if (title.equals("Produk Terjual")) lblTitleProduk = lblTitle;
        else if (title.equals("Shift Aktif")) lblTitleShift = lblTitle;

        header.add(lblTitle);
        card.add(header, BorderLayout.NORTH);

        // Value panel
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setBackground(Color.WHITE);
        valuePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(new Color(50, 50, 50));
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Simpan referensi untuk nilai
        if (title.equals("Total Penjualan")) lblTotalPenjualan = lblValue;
        else if (title.equals("Jumlah Transaksi")) lblTotalTransaksi = lblValue;
        else if (title.equals("Produk Terjual")) lblProdukTerjual = lblValue;
        else if (title.equals("Shift Aktif")) lblShiftAktif = lblValue;

        valuePanel.add(lblValue);
        card.add(valuePanel, BorderLayout.CENTER);

        // Footer dengan efek hover
        JPanel footer = new JPanel();
        footer.setBackground(new Color(245, 245, 245));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        card.add(footer, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.darker(), 2),
                        BorderFactory.createEmptyBorder(19, 19, 19, 19)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        return card;
    }

    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection conn = database.DBConnection.getConnection()) {
                    // Total penjualan
                    try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(total) FROM transactions");
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            double total = rs.getDouble(1);
                            SwingUtilities.invokeLater(() ->
                                    lblTotalPenjualan.setText(String.format("Rp %, .0f", total))
                            );
                        }
                    }

                    // Total transaksi
                    try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM transactions");
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            SwingUtilities.invokeLater(() -> {
                                lblTotalTransaksi.setText(String.valueOf(count));
                                lblTitleTransaksi.setText("Jumlah Transaksi (" + count + ")");
                            });
                        }
                    }

                    // Produk terjual
                    try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(quantity) FROM transaction_items");
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int sold = rs.getInt(1);
                            SwingUtilities.invokeLater(() -> {
                                lblProdukTerjual.setText(String.valueOf(sold));
                                lblTitleProduk.setText("Produk Terjual (" + sold + ")");
                            });
                        }
                    }

                    // Shift aktif
                    try (PreparedStatement ps = conn.prepareStatement("SELECT cashier FROM shifts WHERE end_time IS NULL");
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String cashier = rs.getString(1);
                            SwingUtilities.invokeLater(() -> {
                                lblShiftAktif.setText("Kasir: " + cashier);
                                lblTitleShift.setText("Shift Aktif");
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                lblShiftAktif.setText("Tidak Ada");
                                lblTitleShift.setText("Shift Aktif");
                            });
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(
                                    DashboardPanel.this,
                                    "Gagal memuat data dashboard:\n" + e.getMessage(),
                                    "Database Error",
                                    JOptionPane.ERROR_MESSAGE
                            )
                    );
                }
                return null;
            }
        };
        worker.execute();
    }
}