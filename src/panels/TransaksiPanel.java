package panels;

import database.DBConnection;
import database.ShiftDatabaseHelper;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class TransaksiPanel extends JPanel {
    private JPanel produkPanel;
    private JTable keranjangTable;
    private DefaultTableModel keranjangModel;
    private JLabel subtotalLabel;
    private List<HashMap<String, Object>> produkList = new ArrayList<>();
    private List<HashMap<String, Object>> keranjangList = new ArrayList<>();

    // Warna tema
    private static final Color PRIMARY_COLOR = new Color(0, 100, 200);
    private static final Color SECONDARY_COLOR = new Color(230, 240, 255);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    public TransaksiPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Transaksi Penjualan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setDividerSize(3);
        splitPane.setDividerLocation(0.65);

        // Panel Produk (Kiri)
        produkPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        produkPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        produkPanel.setBackground(Color.WHITE);

        JScrollPane produkScroll = new JScrollPane(produkPanel);
        produkScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Daftar Produk",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
        ));
        produkScroll.getViewport().setBackground(Color.WHITE);
        splitPane.setLeftComponent(produkScroll);

        // Panel Keranjang (Kanan)
        JPanel keranjangPanel = new JPanel(new BorderLayout());
        keranjangPanel.setBackground(Color.WHITE);
        keranjangPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Keranjang Belanja",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
        ));

        // Model tabel keranjang
        keranjangModel = new DefaultTableModel(new String[]{"Produk", "Jumlah", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class;
                if (columnIndex == 2) return Double.class;
                return String.class;
            }
        };

        keranjangTable = new JTable(keranjangModel);
        keranjangTable.setRowHeight(30);
        keranjangTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        keranjangTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keranjangTable.setShowVerticalLines(false);
        keranjangTable.setIntercellSpacing(new Dimension(0, 0));

        // Styling header tabel
        JTableHeader tableHeader = keranjangTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(PRIMARY_COLOR);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        // Renderer untuk harga
        keranjangTable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(SECONDARY_COLOR);
                }

                if (isSelected) {
                    c.setBackground(ACCENT_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                }

                if (value instanceof Double) {
                    setText(String.format("Rp%,.0f", value));
                }

                return c;
            }
        });

        // Listener untuk update jumlah
        keranjangModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 1 && row >= 0 && row < keranjangModel.getRowCount()) {
                try {
                    int jumlah = (int) keranjangModel.getValueAt(row, 1);
                    if (jumlah <= 0) {
                        keranjangModel.removeRow(row);
                    } else {
                        double hargaSatuan = 0;
                        for (HashMap<String, Object> produk : produkList) {
                            if (produk.get("name").equals(keranjangModel.getValueAt(row, 0))) {
                                hargaSatuan = (double) produk.get("price");
                                break;
                            }
                        }
                        keranjangModel.setValueAt(jumlah * hargaSatuan, row, 2);
                    }
                    updateSubtotal();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TransaksiPanel.this,
                            "Input jumlah tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JScrollPane keranjangScroll = new JScrollPane(keranjangTable);
        keranjangScroll.setBorder(null);
        keranjangPanel.add(keranjangScroll, BorderLayout.CENTER);

        // Panel bawah keranjang
        subtotalLabel = new JLabel("Subtotal: Rp0");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subtotalLabel.setForeground(PRIMARY_COLOR);
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton checkoutButton = createModernButton("Checkout", PRIMARY_COLOR);
        checkoutButton.addActionListener(e -> handleCheckout());

        JButton hapusButton = createModernButton("Hapus Item", ERROR_COLOR);
        hapusButton.addActionListener(e -> hapusItemDariKeranjang());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(hapusButton);
        buttonPanel.add(checkoutButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(subtotalLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        keranjangPanel.add(bottomPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(keranjangPanel);

        add(splitPane, BorderLayout.CENTER);

        loadProduk();
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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadProduk() {
        produkPanel.removeAll();
        produkList.clear();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM menu_items")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HashMap<String, Object> produk = new HashMap<>();
                produk.put("id", rs.getInt("id"));
                produk.put("name", rs.getString("name"));
                produk.put("price", rs.getDouble("price"));
                produkList.add(produk);

                JButton btn = createProductButton(produk);
                produkPanel.add(btn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data produk: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        produkPanel.revalidate();
        produkPanel.repaint();
    }

    private JButton createProductButton(HashMap<String, Object> produk) {
        String nama = (String) produk.get("name");
        double harga = (double) produk.get("price");

        JButton btn = new JButton("<html><center><b>" + nama + "</b><br>Rp" + (int) harga + "</center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(PRIMARY_COLOR);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        btn.addActionListener(e -> tambahKeKeranjang(produk));
        return btn;
    }

    private void tambahKeKeranjang(HashMap<String, Object> produk) {
        String nama = (String) produk.get("name");
        double harga = (double) produk.get("price");

        boolean found = false;
        for (int i = 0; i < keranjangModel.getRowCount(); i++) {
            if (keranjangModel.getValueAt(i, 0).equals(nama)) {
                int jumlah = (int) keranjangModel.getValueAt(i, 1);
                keranjangModel.setValueAt(jumlah + 1, i, 1);
                keranjangModel.setValueAt((jumlah + 1) * harga, i, 2);
                found = true;
                break;
            }
        }

        if (!found) {
            keranjangModel.addRow(new Object[]{nama, 1, harga});
        }

        updateSubtotal();
    }

    private void updateSubtotal() {
        double total = 0;
        for (int i = 0; i < keranjangModel.getRowCount(); i++) {
            total += (double) keranjangModel.getValueAt(i, 2);
        }
        subtotalLabel.setText(String.format("Subtotal: Rp%,.0f", total));
    }

    private void hapusItemDariKeranjang() {
        int selectedRow = keranjangTable.getSelectedRow();
        if (selectedRow >= 0) {
            keranjangModel.removeRow(selectedRow);
            updateSubtotal();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Pilih item yang ingin dihapus dari keranjang.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleCheckout() {
        if (keranjangModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Keranjang masih kosong.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalAmount = 0;
        for (int i = 0; i < keranjangModel.getRowCount(); i++) {
            totalAmount += (double) keranjangModel.getValueAt(i, 2);
        }

        String[] metode = {"Tunai", "QRIS", "Debit"};
        String selectedMethod = (String) JOptionPane.showInputDialog(
                this,
                "Pilih metode pembayaran:",
                "Metode Pembayaran",
                JOptionPane.QUESTION_MESSAGE,
                null,
                metode,
                metode[0]
        );

        if (selectedMethod == null) return;

        // Panel input pembayaran
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel totalLabel = new JLabel(String.format("Total: Rp%,.0f", totalAmount));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        paymentPanel.add(totalLabel, gbc);

        JLabel paymentLabel = new JLabel("Jumlah Pembayaran:");
        paymentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        gbc.gridwidth = 1;
        paymentPanel.add(paymentLabel, gbc);

        JTextField paymentField = new JTextField(15);
        paymentField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        paymentPanel.add(paymentField, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                paymentPanel,
                "Pembayaran",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        double bayar;
        try {
            bayar = Double.parseDouble(paymentField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Input tidak valid.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (bayar < totalAmount) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah pembayaran kurang.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double kembalian = bayar - totalAmount;

        // Simpan transaksi ke database
        int generatedId = -1;
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // 1. Validasi shift aktif
            int shiftId = ShiftDatabaseHelper.getActiveShiftId(conn);
            if (shiftId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada shift aktif! Harap mulai shift terlebih dahulu.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Verifikasi shift ada di database
            if (!ShiftDatabaseHelper.isShiftExist(conn, shiftId)) {
                JOptionPane.showMessageDialog(this,
                        "Shift tidak valid! ID: " + shiftId,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Simpan transaksi utama
            String sql = "INSERT INTO transactions " +
                    "(datetime, total, bayar, kembalian, metode, shift_id) " +
                    "VALUES (datetime('now', 'localtime'), ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, totalAmount);
                ps.setDouble(2, bayar);
                ps.setDouble(3, kembalian);
                ps.setString(4, selectedMethod);
                ps.setInt(5, shiftId); // Gunakan shiftId yang valid

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Gagal menyimpan transaksi, tidak ada baris yang terpengaruh");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    } else {
                        throw new SQLException("Gagal mendapatkan ID transaksi");
                    }
                }
            }
            conn.commit(); // commit agar transaksi benar-benar tersimpan sebelum insert item

            // 4. Simpan item transaksi
            if (generatedId != -1) {
                try (PreparedStatement psDetail = conn.prepareStatement(
                        "INSERT INTO transaction_items (transaction_id, product_name, quantity, price) " +
                                "VALUES (?, ?, ?, ?)")) {

                    for (int i = 0; i < keranjangModel.getRowCount(); i++) {
                        psDetail.setInt(1, generatedId);
                        psDetail.setString(2, (String) keranjangModel.getValueAt(i, 0));
                        psDetail.setInt(3, (int) keranjangModel.getValueAt(i, 1));
                        psDetail.setDouble(4, (double) keranjangModel.getValueAt(i, 2) / (int) keranjangModel.getValueAt(i, 1));
                        psDetail.addBatch();
                    }
                    psDetail.executeBatch();
                }
            } else {
                throw new SQLException("Gagal menyimpan item transaksi karena ID transaksi tidak valid.");
            }

            // 5. Update total penjualan shift
            ShiftDatabaseHelper.updateShiftSales(conn, shiftId, totalAmount);

            conn.commit(); // Commit transaksi jika semua berhasil

            // Siapkan data untuk struk
            String trxId = "TRX-" + generatedId;
            List<HashMap<String, Object>> items = new ArrayList<>();
            for (int i = 0; i < keranjangModel.getRowCount(); i++) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("nama", keranjangModel.getValueAt(i, 0));
                item.put("jumlah", keranjangModel.getValueAt(i, 1));
                item.put("harga", (double) keranjangModel.getValueAt(i, 2) / (int) keranjangModel.getValueAt(i, 1));
                items.add(item);
            }

            // Tampilkan struk
            new ReceiptFrame(trxId, selectedMethod, items, totalAmount, bayar, kembalian).setVisible(true);

            // Reset keranjang
            keranjangModel.setRowCount(0);
            updateSubtotal();

            JOptionPane.showMessageDialog(this,
                    "Transaksi berhasil disimpan.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback jika error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan transaksi: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Kembalikan ke mode auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}