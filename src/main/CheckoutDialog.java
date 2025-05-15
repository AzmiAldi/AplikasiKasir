package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CheckoutDialog extends JDialog {
    public CheckoutDialog(JFrame parent, ArrayList<CartItem> cart) {
        super(parent, "Checkout", true);
        setSize(300, 300);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        double total = 0;
        for (CartItem item : cart) {
            panel.add(new JLabel(item.getName() + " - Rp" + item.getSubtotal()));
            total += item.getSubtotal();
        }

        JLabel totalLabel = new JLabel("Total: Rp" + total);
        panel.add(totalLabel);

        JTextField bayarField = new JTextField();
        panel.add(new JLabel("Bayar:"));
        panel.add(bayarField);

        JButton bayarBtn = new JButton("Selesai");
        double finalTotal = total;
        bayarBtn.addActionListener(e -> {
            try {
                double bayar = Double.parseDouble(bayarField.getText());
                double kembalian = bayar - finalTotal;
                JOptionPane.showMessageDialog(this, "Kembalian: Rp" + kembalian);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid.");
            }
        });

        panel.add(bayarBtn);
        add(panel);
        setVisible(true);
    }
}
