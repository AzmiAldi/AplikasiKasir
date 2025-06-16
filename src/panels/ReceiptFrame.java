package panels;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptFrame extends JFrame {

    // Warna modern
    private static final Color PRIMARY_COLOR = new Color(0, 100, 200);
    private static final Color SECONDARY_COLOR = new Color(150, 150, 150);
    private static final Color ACCENT_COLOR = new Color(0, 150, 100);

    public ReceiptFrame(String trxId, String selectedMethod, List<HashMap<String, Object>> items,
                        double totalAmount, double bayar, double kembalian) {
        setTitle("Struk Pembayaran");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama dengan gradient background
        JPanel receiptPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 240, 255);
                Color color2 = Color.WHITE;
                g2d.setPaint(new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header dengan logo dan nama toko
        JLabel titleLabel = new JLabel("STELLO COFFEE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Coffee & Kitchen", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel addressLabel = new JLabel("Jl. Raya Serang-Pandeglang No. 42", SwingConstants.CENTER);
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        addressLabel.setForeground(SECONDARY_COLOR);
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Garis pemisah modern
        JSeparator separator = createModernSeparator();

        // Informasi transaksi
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent
        addInfoRow(infoPanel, "No. Transaksi:", trxId);
        addInfoRow(infoPanel, "Tanggal:", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        addInfoRow(infoPanel, "Metode:", selectedMethod);
        addInfoRow(infoPanel, "Kasir:", "Admin");

        // Daftar item dengan styling modern
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(0, 0, 0, 0));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Header daftar produk
        JLabel itemsHeader = new JLabel("Daftar Pembelian:");
        itemsHeader.setFont(new Font("Arial", Font.BOLD, 12));
        itemsHeader.setForeground(SECONDARY_COLOR);
        itemsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemsPanel.add(itemsHeader);
        itemsPanel.add(Box.createVerticalStrut(5));

        if (items != null && !items.isEmpty()) {
            for (Map<String, Object> item : items) {
                try {
                    // Coba kedua format key untuk kompatibilitas
                    String nama = item.containsKey("product_name") ?
                            (String) item.get("product_name") : (String) item.get("nama");

                    Integer jumlah = item.containsKey("quantity") ?
                            (Integer) item.get("quantity") : (Integer) item.get("jumlah");

                    Double harga = item.containsKey("price") ?
                            (Double) item.get("price") : (Double) item.get("harga");

                    if (nama != null && jumlah != null && harga != null) {
                        addItemRow(itemsPanel, nama, jumlah, harga);
                    } else {
                        System.err.println("Format item tidak dikenali: " + item);
                        // Tambahkan fallback UI
                        JLabel errorLabel = new JLabel("Item tidak valid - " + item.toString());
                        errorLabel.setForeground(Color.RED);
                        itemsPanel.add(errorLabel);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing item: " + item);
                    e.printStackTrace();
                }
            }
        } else {
            JLabel noItemsLabel = new JLabel("Tidak ada item pembelian", SwingConstants.CENTER);
            noItemsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            noItemsLabel.setForeground(SECONDARY_COLOR);
            itemsPanel.add(noItemsLabel);
        }

        itemsPanel.add(Box.createVerticalStrut(10));
        JSeparator itemsSeparator = createModernSeparator();
        itemsPanel.add(itemsSeparator);

        // Bagian total dengan styling berbeda
        JSeparator totalSeparator = createModernSeparator();

        JPanel totalPanel = new JPanel(new GridLayout(0, 2, 10, 7));
        totalPanel.setBackground(new Color(0, 0, 0, 0));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addTotalRow(totalPanel, "Subtotal:", formatRupiah(totalAmount));
        addTotalRow(totalPanel, "Tunai:", formatRupiah(bayar));
        addTotalRow(totalPanel, "Kembali:", "<html><b>" + formatRupiah(kembalian) + "</b></html>");

        // Footer dengan pesan terima kasih
        JLabel thankYouLabel = new JLabel("Terima kasih telah berkunjung!", SwingConstants.CENTER);
        thankYouLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        thankYouLabel.setForeground(SECONDARY_COLOR);
        thankYouLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel socialLabel = new JLabel("| IG: @stellocoffee |", SwingConstants.CENTER);
        socialLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        socialLabel.setForeground(SECONDARY_COLOR);
        socialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Menambahkan semua komponen
        receiptPanel.add(Box.createVerticalStrut(10));
        receiptPanel.add(titleLabel);
        receiptPanel.add(subtitleLabel);
        receiptPanel.add(Box.createVerticalStrut(5));
        receiptPanel.add(addressLabel);
        receiptPanel.add(Box.createVerticalStrut(15));
        receiptPanel.add(separator);
        receiptPanel.add(Box.createVerticalStrut(15));
        receiptPanel.add(infoPanel);
        receiptPanel.add(Box.createVerticalStrut(20));
        receiptPanel.add(itemsPanel);
        receiptPanel.add(Box.createVerticalStrut(15));
        receiptPanel.add(totalSeparator);
        receiptPanel.add(totalPanel);
        receiptPanel.add(Box.createVerticalStrut(20));
        receiptPanel.add(thankYouLabel);
        receiptPanel.add(Box.createVerticalStrut(5));
        receiptPanel.add(socialLabel);
        receiptPanel.add(Box.createVerticalStrut(10));

        add(receiptPanel);
    }

    private JSeparator createModernSeparator() {
        JSeparator separator = new JSeparator() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
                g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
            }
        };
        separator.setPreferredSize(new Dimension(0, 10));
        return separator;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Arial", Font.BOLD, 12));
        labelLbl.setForeground(SECONDARY_COLOR);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        valueLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(labelLbl);
        panel.add(valueLbl);
    }

    private void addItemRow(JPanel panel, String name, int qty, double price) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(new Color(0, 0, 0, 0));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        detailPanel.setBackground(new Color(0, 0, 0, 0));

        JLabel qtyLabel = new JLabel(qty + " × ");
        JLabel priceLabel = new JLabel(formatRupiah(price));
        JLabel totalLabel = new JLabel(" = " + formatRupiah(price * qty));

        qtyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalLabel.setForeground(PRIMARY_COLOR);

        detailPanel.add(qtyLabel);
        detailPanel.add(priceLabel);
        detailPanel.add(totalLabel);

        rowPanel.add(nameLabel, BorderLayout.WEST);
        rowPanel.add(detailPanel, BorderLayout.EAST);

        panel.add(rowPanel);
    }

    private void addTotalRow(JPanel panel, String label, String value) {
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Arial", Font.BOLD, 13));
        valueLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        if (label.equals("Kembali:")) {
            labelLbl.setForeground(ACCENT_COLOR);
            valueLbl.setForeground(ACCENT_COLOR);
        }

        panel.add(labelLbl);
        panel.add(valueLbl);
    }

    private String formatRupiah(double amount) {
        return String.format("Rp %,d", (int) amount).replace(",", ".");
    }
}