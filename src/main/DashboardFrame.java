package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DashboardFrame extends JFrame {
    private ArrayList<MenuItem> menuList;
    private ArrayList<CartItem> cart;

    public DashboardFrame() {
        setTitle("Dashboard Kasir Cafe");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        menuList = new ArrayList<>();
        cart = new ArrayList<>();

        menuList.add(new MenuItem("Kopi", 15000));
        menuList.add(new MenuItem("Teh", 10000));
        menuList.add(new MenuItem("Nasi Goreng", 25000));

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        for (MenuItem item : menuList) {
            JButton btn = new JButton(item.getName() + " - Rp" + item.getPrice());
            btn.addActionListener(e -> {
                cart.add(new CartItem(item.getName(), item.getPrice(), 1));
                JOptionPane.showMessageDialog(this, item.getName() + " ditambahkan ke keranjang.");
            });
            menuPanel.add(btn);
        }

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> {
            new CheckoutDialog(this, cart);
        });

        mainPanel.add(new JScrollPane(menuPanel), BorderLayout.CENTER);
        mainPanel.add(checkoutButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}
