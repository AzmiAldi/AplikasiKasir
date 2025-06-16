package panels;

import database.DBConnection;
import database.ShiftDatabaseHelper;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ShiftPanel extends JPanel {
    private JTable shiftTable;
    private DefaultTableModel tableModel;
    private JButton btnStartShift, btnEndShift;
    private JLabel activeShiftLabel;
    private int activeShiftId = -1;

    private static final Color PRIMARY_COLOR = new Color(0, 100, 200);
    private static final Color SECONDARY_COLOR = new Color(230, 240, 255);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);

    public ShiftPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Manajemen Shift Kasir");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        activeShiftLabel = new JLabel("Tidak ada shift aktif");
        activeShiftLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activeShiftLabel.setForeground(WARNING_COLOR);
        headerPanel.add(activeShiftLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Mulai", "Selesai", "Kasir", "Total Penjualan"}, 0) {
            public Class<?> getColumnClass(int col) {
                return col == 4 ? Double.class : String.class;
            }
        };

        shiftTable = new JTable(tableModel);
        shiftTable.setRowHeight(30);
        shiftTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shiftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shiftTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        shiftTable.getTableHeader().setBackground(PRIMARY_COLOR);
        shiftTable.getTableHeader().setForeground(Color.WHITE);

        shiftTable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? Color.WHITE : SECONDARY_COLOR);
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

        JScrollPane scrollPane = new JScrollPane(shiftTable);
        scrollPane.setBorder(null);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Riwayat Shift", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Tombol
        btnStartShift = createModernButton("Mulai Shift Baru", SUCCESS_COLOR);
        btnStartShift.addActionListener(e -> startNewShift());

        btnEndShift = createModernButton("Akhiri Shift Aktif", WARNING_COLOR);
        btnEndShift.addActionListener(e -> endActiveShift());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnStartShift);
        buttonPanel.add(btnEndShift);

        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);

        loadShiftData();
        checkActiveShift();
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
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

    private void loadShiftData() {
        tableModel.setRowCount(0);
        List<Map<String, Object>> shifts = ShiftDatabaseHelper.getAllShifts();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Map<String, Object> shift : shifts) {
            Timestamp start = (Timestamp) shift.get("start_time");
            Timestamp end = (Timestamp) shift.get("end_time");

            tableModel.addRow(new Object[]{
                    shift.get("id"),
                    (start != null ? sdf.format(start) : "-"),
                    (end != null ? sdf.format(end) : "-"),
                    shift.get("cashier"),
                    shift.get("total_sales")
            });
        }
    }

    private void checkActiveShift() {
        try (Connection conn = DBConnection.getConnection()) {
            Map<String, Object> shift = ShiftDatabaseHelper.getActiveShift(conn);  // Tambahkan parameter conn

            if (shift != null) {
                activeShiftId = (int) shift.get("id");
                activeShiftLabel.setText("Shift aktif: ID-" + activeShiftId + " | Kasir: " + shift.get("cashier"));
                activeShiftLabel.setForeground(SUCCESS_COLOR);
                btnStartShift.setEnabled(false);
                btnEndShift.setEnabled(true);
            } else {
                activeShiftId = -1;
                activeShiftLabel.setText("Tidak ada shift aktif");
                activeShiftLabel.setForeground(WARNING_COLOR);
                btnStartShift.setEnabled(true);
                btnEndShift.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memeriksa shift aktif: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshShiftStatus() {
        try (Connection conn = DBConnection.getConnection()) {
            // Periksa status shift aktif
            Map<String, Object> activeShift = ShiftDatabaseHelper.getActiveShift(conn);

            if (activeShift != null) {
                activeShiftId = (int) activeShift.get("id");
                activeShiftLabel.setText("Shift aktif: ID-" + activeShiftId
                        + " | Kasir: " + activeShift.get("cashier")
                        + " | Mulai: " + activeShift.get("start_time"));
                activeShiftLabel.setForeground(SUCCESS_COLOR);
                btnStartShift.setEnabled(false);
                btnEndShift.setEnabled(true);
            } else {
                activeShiftId = -1;
                activeShiftLabel.setText("Tidak ada shift aktif");
                activeShiftLabel.setForeground(WARNING_COLOR);
                btnStartShift.setEnabled(true);
                btnEndShift.setEnabled(false);
            }

            // Refresh tabel riwayat shift
            loadShiftData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat status shift: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startNewShift() {
        String cashierName = JOptionPane.showInputDialog(this, "Masukkan Nama Kasir:");
        if (cashierName == null || cashierName.trim().isEmpty()) return;

        try (Connection conn = DBConnection.getConnection()) {
            // Pastikan tidak ada shift aktif
            if (ShiftDatabaseHelper.hasActiveShift(conn)) {
                JOptionPane.showMessageDialog(this,
                        "Masih ada shift aktif. Akhiri shift sebelumnya terlebih dahulu!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = ShiftDatabaseHelper.startNewShift(conn, cashierName);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Shift baru dimulai untuk kasir: " + cashierName,
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshShiftStatus(); // Perbarui tampilan
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal memulai shift!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void endActiveShift() {
        if (activeShiftId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada shift aktif!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin mengakhiri shift ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            double totalSales = ShiftDatabaseHelper.endShift(conn, activeShiftId);

            String message;
            if (totalSales > 0) {
                message = "Shift berhasil diakhiri\nTotal Penjualan: Rp" + String.format("%,.0f", totalSales);
            } else {
                message = "Shift berhasil diakhiri\nTidak ada penjualan selama shift ini";
            }

            JOptionPane.showMessageDialog(this, message, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshShiftStatus(); // Perbarui tampilan setelah mengakhiri shift
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal mengakhiri shift: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
