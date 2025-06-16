package panels;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReceiptPanel extends JPanel {
    // Warna modern
    private static final Color PRIMARY_COLOR = new Color(0, 100, 200);
    private static final Color SECONDARY_COLOR = new Color(150, 150, 150);
    private static final Color ACCENT_COLOR = new Color(0, 150, 100);

    public ReceiptPanel(String trxId, String selectedMethod, List<Map<String, Object>> items,
                        double totalAmount, double bayar, double kembalian) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

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

        for (Map<String, Object> item : items) {
            String nama = (String) item.get("nama");
            Integer jumlah = (Integer) item.get("jumlah");
            Double harga = (Double) item.get("harga");
            addItemRow(itemsPanel, nama, jumlah, harga);
        }

        // Bagian total dengan styling berbeda
        JSeparator totalSeparator = createModernSeparator();

        JPanel totalPanel = new JPanel(new GridLayout(0, 2, 10, 7));
        totalPanel.setBackground(new Color(0, 0, 0, 0));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addTotalRow(totalPanel, "Subtotal:", formatRupiah(totalAmount));
        addTotalRow(totalPanel, "Tunai:", formatRupiah(bayar));
        addTotalRow(totalPanel, "Kembali:", formatRupiah(kembalian));

        // Footer dengan pesan terima kasih
        JLabel thankYouLabel = new JLabel("Terima kasih telah berkunjung", SwingConstants.CENTER);
        thankYouLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        thankYouLabel.setForeground(SECONDARY_COLOR);
        thankYouLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel socialLabel = new JLabel("IG: @stellocoffee | FB: Stello Coffee", SwingConstants.CENTER);
        socialLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        socialLabel.setForeground(SECONDARY_COLOR);
        socialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Menambahkan semua komponen
        add(Box.createVerticalStrut(10));
        add(titleLabel);
        add(subtitleLabel);
        add(Box.createVerticalStrut(5));
        add(addressLabel);
        add(Box.createVerticalStrut(15));
        add(separator);
        add(Box.createVerticalStrut(15));
        add(infoPanel);
        add(Box.createVerticalStrut(20));
        add(itemsPanel);
        add(Box.createVerticalStrut(15));
        add(totalSeparator);
        add(totalPanel);
        add(Box.createVerticalStrut(20));
        add(thankYouLabel);
        add(Box.createVerticalStrut(5));
        add(socialLabel);
        add(Box.createVerticalStrut(10));
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
        return String.format("Rp%,d", (int) amount).replace(",", ".");
    }
}