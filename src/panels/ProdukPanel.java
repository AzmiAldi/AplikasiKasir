package panels;

import database.DBConnection;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class ProdukPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    // Warna tema
    private static final Color PRIMARY_COLOR = new Color(0, 100, 200);
    private static final Color SECONDARY_COLOR = new Color(230, 240, 255);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);

    public ProdukPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Manajemen Produk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Tabel Produk dengan styling modern
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Harga", "Stok", "Kategori"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Membuat tabel tidak bisa diedit langsung
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        // Styling header tabel
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(PRIMARY_COLOR);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        // Renderer untuk harga (Rupiah)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Warna selang-seling baris
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(SECONDARY_COLOR);
                }

                // Highlight baris terpilih
                if (isSelected) {
                    c.setBackground(ACCENT_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                }

                // Format kolom harga
                if (column == 2 && value != null && !value.toString().startsWith("Rp")) {
                    try {
                        double price = Double.parseDouble(value.toString());
                        setText(rupiahFormat.format(price));
                    } catch (NumberFormatException e) {
                        setText(value.toString());
                    }
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Panel Tombol dengan styling modern
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnTambah = createModernButton("Tambah", PRIMARY_COLOR);
        JButton btnEdit = createModernButton("Edit", PRIMARY_COLOR);
        JButton btnHapus = createModernButton("Hapus", new Color(200, 50, 50));

        btnTambah.addActionListener(e -> showProdukForm(null));
        btnEdit.addActionListener(e -> editSelectedProduct());
        btnHapus.addActionListener(e -> deleteSelectedProduct());

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efek hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"), // Disimpan sebagai double untuk formatting di renderer
                        rs.getInt("stock"),
                        rs.getString("category")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data produk: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProdukForm(Integer productId) {
        // Buat form dengan styling modern
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Komponen form
        JLabel titleLabel = new JLabel(productId == null ? "Tambah Produk Baru" : "Edit Produk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Nama:"), gbc);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Harga:"), gbc);

        JTextField priceField = new JTextField(20);
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Stok:"), gbc);

        JTextField stockField = new JTextField(20);
        stockField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Kategori:"), gbc);

        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Minuman", "Dessert"});
        categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(categoryBox, gbc);

        // Isi data jika edit
        if (productId != null) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM menu_items WHERE id = ?")) {
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    priceField.setText(String.valueOf(rs.getDouble("price")));
                    stockField.setText(String.valueOf(rs.getInt("stock")));
                    categoryBox.setSelectedItem(rs.getString("category"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int result = JOptionPane.showOptionDialog(this, panel,
                productId == null ? "Tambah Produk" : "Edit Produk",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{"Simpan", "Batal"}, "Simpan");

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                if (productId == null) {
                    // Insert new product
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO menu_items (name, price, stock, category) VALUES (?, ?, ?, ?)");
                    ps.setString(1, nameField.getText());
                    ps.setDouble(2, Double.parseDouble(priceField.getText()));
                    ps.setInt(3, Integer.parseInt(stockField.getText()));
                    ps.setString(4, categoryBox.getSelectedItem().toString());
                    ps.executeUpdate();
                } else {
                    // Update existing product
                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE menu_items SET name=?, price=?, stock=?, category=? WHERE id=?");
                    ps.setString(1, nameField.getText());
                    ps.setDouble(2, Double.parseDouble(priceField.getText()));
                    ps.setInt(3, Integer.parseInt(stockField.getText()));
                    ps.setString(4, categoryBox.getSelectedItem().toString());
                    ps.setInt(5, productId);
                    ps.executeUpdate();
                }
                refreshTable();
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedProduct() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            showProdukForm(id);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin diedit.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedProduct() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus produk ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM menu_items WHERE id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    refreshTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Gagal menghapus produk: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
}