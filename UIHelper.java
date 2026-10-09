package com.faculty.management.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Utility helper for building modern, professional Swing UI components.
 */
public class UIHelper {

    /**
     * Creates a card panel with clean border and white surface.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.CARD_BG);
        panel.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));
        return panel;
    }

    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = createCardPanel();
        panel.setLayout(layout);
        return panel;
    }

    /**
     * Creates a styled modern button with hover color transition.
     */
    public static JButton createButton(String text, Color bg, Color fg, Color hoverBg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_REGULAR_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, UITheme.PRIMARY, Color.WHITE, UITheme.PRIMARY_HOVER);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, UITheme.SUCCESS, Color.WHITE, new Color(13, 148, 136));
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, UITheme.DANGER, Color.WHITE, new Color(220, 38, 38));
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = createButton(text, new Color(241, 245, 249), UITheme.TEXT_DARK, new Color(226, 232, 240));
        btn.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(8, 16, 8, 16)
        ));
        return btn;
    }

    /**
     * Creates a styled text field with rounded-look border and inner padding.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(UITheme.FONT_REGULAR);
        tf.setBackground(Color.WHITE);
        tf.setForeground(UITheme.TEXT_DARK);
        tf.setCaretColor(UITheme.PRIMARY);
        tf.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    /**
     * Creates a styled password field with inner padding.
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(UITheme.FONT_REGULAR);
        pf.setBackground(Color.WHITE);
        pf.setForeground(UITheme.TEXT_DARK);
        pf.setCaretColor(UITheme.PRIMARY);
        pf.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }

    /**
     * Creates a styled ComboBox.
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(UITheme.FONT_REGULAR);
        combo.setBackground(Color.WHITE);
        combo.setForeground(UITheme.TEXT_DARK);
        combo.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(4, 6, 4, 6)
        ));
        return combo;
    }

    /**
     * Creates an analytics KPI card tile.
     */
    public static JPanel createKpiCard(String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));

        // Accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(100, 4));
        accentBar.setBackground(accentColor);
        card.add(accentBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.CARD_BG);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(UITheme.FONT_KPI_TITLE);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(UITheme.FONT_KPI_VAL);
        lblVal.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(lblVal);
        content.add(Box.createVerticalStrut(4));
        content.add(lblSub);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Creates a view header block with title and subtitle.
     */
    public static JPanel createHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSub, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Styles a JTable for modern flat appearance with status badges and padding.
     */
    public static void styleTable(JTable table) {
        table.setFont(UITheme.FONT_REGULAR);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(UITheme.TEXT_DARK);
        table.setGridColor(UITheme.BORDER);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.FONT_REGULAR_BOLD);
        header.setBackground(UITheme.TABLE_HEADER_BG);
        header.setForeground(UITheme.TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(new MatteBorder(0, 0, 2, 0, UITheme.BORDER));
        header.setReorderingAllowed(false);

        // Striped and padded cell renderer with Badge support
        BadgeCellRenderer renderer = new BadgeCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    /**
     * Custom cell renderer that highlights statuses with colored badge pills.
     */
    public static class BadgeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setBorder(new EmptyBorder(0, 12, 0, 12));

            String text = (value != null) ? value.toString() : "";
            String upper = text.toUpperCase();

            if (!isSelected) {
                if (row % 2 == 1) {
                    label.setBackground(UITheme.TABLE_ROW_ALT);
                } else {
                    label.setBackground(Color.WHITE);
                }
            }

            // Status badges
            if (upper.contains("ELIGIBLE") && !upper.contains("NOT")) {
                label.setForeground(new Color(5, 150, 105)); // Green
                label.setFont(UITheme.FONT_REGULAR_BOLD);
            } else if (upper.contains("NOT ELIGIBLE") || upper.contains("REJECTED") || upper.contains("REPEAT") || upper.equals("E")) {
                label.setForeground(new Color(220, 38, 38)); // Red
                label.setFont(UITheme.FONT_REGULAR_BOLD);
            } else if (upper.contains("PENDING")) {
                label.setForeground(new Color(217, 119, 6)); // Amber
                label.setFont(UITheme.FONT_REGULAR_BOLD);
            } else if (upper.contains("APPROVED") || upper.equals("NORMAL")) {
                label.setForeground(new Color(37, 99, 235)); // Blue
                label.setFont(UITheme.FONT_REGULAR_BOLD);
            } else if (upper.startsWith("A+") || upper.equals("A") || upper.equals("A-")) {
                label.setForeground(new Color(5, 150, 105)); // Green
                label.setFont(UITheme.FONT_REGULAR_BOLD);
            } else {
                if (!isSelected) {
                    label.setForeground(UITheme.TEXT_DARK);
                }
                label.setFont(UITheme.FONT_REGULAR);
            }

            return label;
        }
    }
}
