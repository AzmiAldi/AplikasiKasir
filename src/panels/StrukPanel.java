package panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import java.util.List;

public class    StrukPanel extends JPanel {
    private JTable tableStruk;
    private JPanel detailContainer;
    private JButton btnPrint;
    private JButton btnRefresh;
    private JButton btnDelete;
    private JComboBox<String> cmbMetode;
    private JDateChooser dateStartChooser;
    private JDateChooser dateEndChooser;

    // Warna tema
    private static final Color PRIMARY_COLOR = new Color(0, 100, 200);
    private static final Color SECONDARY_COLOR = new Color(230, 240, 255);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

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

        // Panel filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);

        // Tanggal mulai
        filterPanel.add(new JLabel("Dari:"));
        dateStartChooser = new JDateChooser();
        dateStartChooser.setPreferredSize(new Dimension(120, 30));
        dateStartChooser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(dateStartChooser);

        // Tanggal akhir
        filterPanel.add(new JLabel("Sampai:"));
        dateEndChooser = new JDateChooser();
        dateEndChooser.setPreferredSize(new Dimension(120, 30));
        dateEndChooser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(dateEndChooser);

        // Filter metode
        filterPanel.add(new JLabel("Metode:"));
        cmbMetode = new JComboBox<>(new String[]{"Semua", "Tunai", "QRIS", "Debit"});
        cmbMetode.setPreferredSize(new Dimension(100, 30));
        cmbMetode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(cmbMetode);

        // Tombol filter
        JButton btnFilter = createModernButton("Filter", PRIMARY_COLOR);
        btnFilter.setPreferredSize(new Dimension(80, 30));
        btnFilter.addActionListener(e -> loadStrukList());
        filterPanel.add(btnFilter);

        headerPanel.add(filterPanel, BorderLayout.CENTER);

        // Tombol refresh
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

        // Tombol hapus di panel kiri
        btnDelete = createModernButton("Hapus Transaksi", ERROR_COLOR);
        btnDelete.addActionListener(e -> deleteTransaction());
        btnDelete.setEnabled(false);

        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomLeftPanel.setBackground(Color.WHITE);
        bottomLeftPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomLeftPanel.add(btnDelete);
        kiriPanel.add(bottomLeftPanel, BorderLayout.SOUTH);

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
        btnPrint.setEnabled(false);

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
                    btnPrint.setEnabled(true);
                    btnDelete.setEnabled(true);
                } else {
                    btnPrint.setEnabled(false);
                    btnDelete.setEnabled(false);
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
        // Format tanggal untuk SQL
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = "";
        String endDate = "";

        if (dateStartChooser.getDate() != null) {
            startDate = sdf.format(dateStartChooser.getDate());
        }
        if (dateEndChooser.getDate() != null) {
            endDate = sdf.format(dateEndChooser.getDate());
        }

        // Dapatkan metode yang dipilih
        String metode = (String) cmbMetode.getSelectedItem();
        metode = metode.equals("Semua") ? "" : metode;

        // Buat query dengan filter
        StringBuilder query = new StringBuilder(
                "SELECT id, datetime, total, metode FROM transactions WHERE 1=1"
        );

        if (!startDate.isEmpty()) {
            query.append(" AND datetime >= '").append(startDate).append(" 00:00:00'");
        }
        if (!endDate.isEmpty()) {
            query.append(" AND datetime <= '").append(endDate).append(" 23:59:59'");
        }
        if (!metode.isEmpty()) {
            query.append(" AND metode = '").append(metode).append("'");
        }

        query.append(" ORDER BY datetime DESC");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:stello_coffee.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {

            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Tanggal", "Total", "Metode"}, 0) {
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
                        rs.getDouble("total"),
                        rs.getString("metode")
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
            SimpleDateFormat toDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

        // Cetak struk
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Cetak Struk TRX-" + trxId);

        // Buat printable dari detail container
        Printable printable = new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Skala komponen agar pas di halaman
                double scale = Math.min(
                        pageFormat.getImageableWidth() / detailContainer.getWidth(),
                        pageFormat.getImageableHeight() / detailContainer.getHeight()
                );
                g2d.scale(scale, scale);

                detailContainer.printAll(g2d);
                return Printable.PAGE_EXISTS;
            }
        };

        job.setPrintable(printable);

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this,
                        "Struk berhasil dicetak",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this,
                        "Gagal mencetak struk: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTransaction() {
        int selectedRow = tableStruk.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih transaksi yang akan dihapus",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int trxId = (int) tableStruk.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus transaksi TRX-" + trxId + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:stello_coffee.db")) {
            // Mulai transaksi
            conn.setAutoCommit(false);

            // Hapus detail transaksi
            PreparedStatement pst1 = conn.prepareStatement(
                    "DELETE FROM transaction_items WHERE transaction_id = ?");
            pst1.setInt(1, trxId);
            pst1.executeUpdate();

            // Hapus transaksi utama
            PreparedStatement pst2 = conn.prepareStatement(
                    "DELETE FROM transactions WHERE id = ?");
            pst2.setInt(1, trxId);
            int rowsAffected = pst2.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(this,
                        "Transaksi berhasil dihapus",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadStrukList();
                detailContainer.removeAll();
                detailContainer.revalidate();
                detailContainer.repaint();
                btnPrint.setEnabled(false);
                btnDelete.setEnabled(false);
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus transaksi",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus transaksi: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// Kelas untuk date picker (JDateChooser)
class JDateChooser extends JPanel {
    private JTextField txtDate;
    private JButton btnCalendar;
    private Calendar calendar;

    public JDateChooser() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(120, 30));
        setBackground(Color.WHITE);

        txtDate = new JTextField();
        txtDate.setEditable(false);
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDate.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        btnCalendar = new JButton("📅");
        btnCalendar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCalendar.setFocusPainted(false);
        btnCalendar.setBackground(Color.WHITE);
        btnCalendar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btnCalendar.setPreferredSize(new Dimension(30, 30));
        btnCalendar.addActionListener(e -> showCalendar());

        add(txtDate, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);
    }

    private void showCalendar() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Pilih Tanggal");
        dialog.setModal(true);
        dialog.setSize(300, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        calendar = Calendar.getInstance();
        JCalendar calendarPane = new JCalendar();
        calendarPane.setPreferredSize(new Dimension(280, 240));

        JButton btnSelect = new JButton("Pilih");
        btnSelect.addActionListener(e -> {
            calendar = calendarPane.getCalendar();
            setDate(calendar.getTime());
            dialog.dispose();
        });

        panel.add(calendarPane, BorderLayout.CENTER);
        panel.add(btnSelect, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public Date getDate() {
        return calendar != null ? calendar.getTime() : null;
    }

    public void setDate(Date date) {
        if (date == null) return;
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtDate.setText(sdf.format(date));
    }
}

// Komponen kalender sederhana
class JCalendar extends JPanel {
    private Calendar calendar;
    private JLabel lblMonthYear;
    private JButton btnPrev, btnNext;
    private JPanel daysPanel;

    public JCalendar() {
        setLayout(new BorderLayout());
        calendar = Calendar.getInstance();

        // Header (bulan dan tahun)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));

        btnPrev = new JButton("←");
        btnPrev.setFocusPainted(false);
        btnPrev.addActionListener(e -> changeMonth(-1));

        btnNext = new JButton("→");
        btnNext.setFocusPainted(false);
        btnNext.addActionListener(e -> changeMonth(1));

        lblMonthYear = new JLabel("", JLabel.CENTER);
        lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateMonthYear();

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(btnPrev);
        buttonPanel.add(btnNext);

        headerPanel.add(buttonPanel, BorderLayout.WEST);
        headerPanel.add(lblMonthYear, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Nama hari
        String[] dayNames = {"M", "S", "S", "R", "K", "J", "S"};
        JPanel daysHeader = new JPanel(new GridLayout(1, 7));
        daysHeader.setBackground(new Color(220, 220, 220));
        for (String day : dayNames) {
            JLabel lbl = new JLabel(day, JLabel.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            daysHeader.add(lbl);
        }
        add(daysHeader, BorderLayout.CENTER);

        // Panel hari
        daysPanel = new JPanel(new GridLayout(0, 7));
        updateCalendar();
        add(daysPanel, BorderLayout.SOUTH);
    }

    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id"));
        lblMonthYear.setText(sdf.format(calendar.getTime()));
    }

    private void changeMonth(int amount) {
        calendar.add(Calendar.MONTH, amount);
        updateMonthYear();
        updateCalendar();
    }

    private void updateCalendar() {
        daysPanel.removeAll();

        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Tambahkan sel kosong untuk hari sebelum hari pertama
        for (int i = 1; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Tambahkan hari dalam bulan
        for (int day = 1; day <= daysInMonth; day++) {
            JButton btnDay = new JButton(String.valueOf(day));
            btnDay.setFocusPainted(false);
            btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            // Highlight hari ini
            Calendar today = Calendar.getInstance();
            if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    day == today.get(Calendar.DAY_OF_MONTH)) {
                btnDay.setBackground(new Color(200, 230, 255));
            }

            final int selectedDay = day;
            btnDay.addActionListener(e -> {
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                repaint();
            });

            daysPanel.add(btnDay);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    public Calendar getCalendar() {
        return calendar;
    }
}