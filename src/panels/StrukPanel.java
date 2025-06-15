package panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import java.util.List;

public class StrukPanel extends JPanel {
    private JTable tableStruk;
    private JPanel detailContainer;
    private JButton btnPrint;
    private JButton btnRefresh;

    // Warna tema
    private static final Color PRIMARY_COLOR = new Color(0, 100, 200);
    private static final Color SECONDARY_COLOR = new Color(230, 240, 255);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);

    public StrukPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Riwayat Transaksi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        btnRefresh = createModernButton("Refresh", PRIMARY_COLOR);
        btnRefresh.addActionListener(e -> loadStrukList());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Split pane untuk membagi kiri dan kanan
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setBorder(null);
        splitPane.setDividerSize(3);

        // Panel kiri (list struk)
        JPanel kiriPanel = new JPanel(new BorderLayout());
        kiriPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Daftar Transaksi",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
        ));
        kiriPanel.setBackground(Color.WHITE);

        tableStruk = new JTable();
        tableStruk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableStruk.setRowHeight(30);
        tableStruk.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableStruk.setShowVerticalLines(false);

        // Styling header tabel
        JTableHeader tableHeader = tableStruk.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(PRIMARY_COLOR);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        JScrollPane scrollStruk = new JScrollPane(tableStruk);
        scrollStruk.setBorder(null);
        kiriPanel.add(scrollStruk, BorderLayout.CENTER);

        // Panel kanan (detail struk + tombol print)
        JPanel kananPanel = new JPanel(new BorderLayout());
        kananPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Detail Transaksi",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
        ));
        kananPanel.setBackground(Color.WHITE);

        // Container untuk struk GUI
        detailContainer = new JPanel(new BorderLayout());
        detailContainer.setBackground(Color.WHITE);

        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnPrint = createModernButton("Cetak Ulang Struk", ACCENT_COLOR);
        btnPrint.addActionListener(e -> printStruk());

        buttonPanel.add(btnPrint);
        kananPanel.add(detailContainer, BorderLayout.CENTER);
        kananPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tambahkan ke splitPane
        splitPane.setLeftComponent(kiriPanel);
        splitPane.setRightComponent(kananPanel);

        add(splitPane, BorderLayout.CENTER);

        // Load data struk
        loadStrukList();

        // Listener klik tabel
        tableStruk.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableStruk.getSelectedRow();
                if (selectedRow != -1) {
                    int trxId = (int) tableStruk.getValueAt(selectedRow, 0);
                    showStrukDetail(trxId);
                }
            }
        });
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

    private void loadStrukList() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:stello_coffee.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, datetime, total FROM transactions ORDER BY datetime DESC")) {

            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Tanggal", "Total"}, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 2) return Double.class;
                    return String.class;
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        formatDateTime(rs.getString("datetime")),
                        rs.getDouble("total")
                });
            }
            tableStruk.setModel(model);

            // Set renderer untuk kolom total (Rupiah)
            tableStruk.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
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

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data transaksi: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatDateTime(String datetime) {
        try {
            SimpleDateFormat fromDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fromDB.parse(datetime);
            SimpleDateFormat toDisplay = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return toDisplay.format(date);
        } catch (Exception e) {
            return datetime;
        }
    }

    private void showStrukDetail(int trxId) {
        // Hapus konten sebelumnya
        detailContainer.removeAll();

        // Ambil data transaksi
        String trxIdStr = "TRX-" + trxId;
        List<Map<String, Object>> items = new ArrayList<>();
        double totalAmount = 0;
        double bayar = 0;
        double kembalian = 0;
        String selectedMethod = "";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:stello_coffee.db")) {
            // Info transaksi
            PreparedStatement pst1 = conn.prepareStatement("SELECT * FROM transactions WHERE id = ?");
            pst1.setInt(1, trxId);
            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                totalAmount = rs1.getDouble("total");
                bayar = rs1.getDouble("bayar");
                kembalian = rs1.getDouble("kembalian");
                selectedMethod = rs1.getString("metode");
            }

            // Item transaksi
            PreparedStatement pst2 = conn.prepareStatement(
                    "SELECT * FROM transaction_items WHERE transaction_id = ?");
            pst2.setInt(1, trxId);
            ResultSet rs2 = pst2.executeQuery();

            while (rs2.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("nama", rs2.getString("product_name"));
                item.put("jumlah", rs2.getInt("quantity"));
                item.put("harga", rs2.getDouble("price"));
                items.add(item);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat detail transaksi: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Buat ReceiptPanel dan tambahkan ke container
        ReceiptPanel receiptPanel = new ReceiptPanel(trxIdStr, selectedMethod, items, totalAmount, bayar, kembalian);
        JScrollPane scrollPane = new JScrollPane(receiptPanel);
        scrollPane.setBorder(null);
        detailContainer.add(scrollPane, BorderLayout.CENTER);

        // Refresh tampilan
        detailContainer.revalidate();
        detailContainer.repaint();
    }

    private void printStruk() {
        int selectedRow = tableStruk.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih transaksi yang akan dicetak",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int trxId = (int) tableStruk.getValueAt(selectedRow, 0);

        // Di sini bisa ditambahkan logika untuk mencetak struk
        // Contoh: buka preview struk atau cetak langsung

        JOptionPane.showMessageDialog(this,
                "Mencetak struk TRX-" + trxId + "\n" +
                        "(Fitur cetak akan diimplementasikan di sini)",
                "Cetak Struk", JOptionPane.INFORMATION_MESSAGE);
    }
}