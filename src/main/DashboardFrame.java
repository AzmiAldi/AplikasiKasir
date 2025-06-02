package main;

import Login.LoginFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private JComboBox<String> itemComboBox;
    private JTextArea receiptArea;
    private double total = 0.0;

    public DashboardFrame() {
        setTitle("Stello Coffee - POS");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel itemLabel = new JLabel("Select Item:");
        itemComboBox = new JComboBox<>();
        JButton addButton = new JButton("Add to Order");

        loadMenuItems();

        addButton.addActionListener(e -> addItemToReceipt());

        topPanel.add(itemLabel);
        topPanel.add(itemComboBox);
        topPanel.add(addButton);

        // Center Panel
        receiptArea = new JTextArea(10, 30);
        receiptArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiptArea);

        //bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton checkoutButton = new JButton("Checkout");
        JButton logoutButton = new JButton("Logout");

        checkoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Total: $" + total);
            receiptArea.setText("");
            total = 0.0;
        });

        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        bottomPanel.add(checkoutButton);
        bottomPanel.add(logoutButton);

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadMenuItems() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM menu_items");

            while (rs.next()) {
                itemComboBox.addItem(rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addItemToReceipt() {
        String itemName = (String) itemComboBox.getSelectedItem();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT price FROM menu_items WHERE name = ?");
            stmt.setString(1, itemName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("price");
                total += price;
                receiptArea.append(itemName + " - $" + price + "\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}