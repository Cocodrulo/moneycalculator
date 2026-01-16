package software.ulpgc.moneycalculator.application.queen;

import software.ulpgc.moneycalculator.architecture.control.Command;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.Money;
import software.ulpgc.moneycalculator.architecture.ui.CurrencyDialog;
import software.ulpgc.moneycalculator.architecture.ui.MoneyDialog;
import software.ulpgc.moneycalculator.architecture.ui.MoneyDisplay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Desktop extends JFrame {
    private final Map<String, Command> commands;
    private final List<Currency> currencies;
    private JTextField inputAmount;
    private JComboBox<Currency> inputCurrency;
    private JTextField outputAmount;
    private JComboBox<Currency> outputCurrency;

    private static final Color BG_COLOR = new Color(33, 33, 33); // Dark Gray
    private static final Color ACCENT_COLOR = new Color(66, 165, 245); // Lighter Blue for contrast
    private static final Color TEXT_COLOR = new Color(245, 245, 245); // Off-White
    private static final Color COMPONENT_BG_COLOR = new Color(66, 66, 66); // Darker Gray for inputs
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public Desktop(List<Currency> currencies) throws HeadlessException {
        this.commands = new HashMap<>();
        this.currencies = currencies;
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(BG_COLOR);
        this.getContentPane().add(createTitleBar(), BorderLayout.NORTH);
        this.getContentPane().add(createMainPanel(), BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        });
    }

    private boolean isMaximized = false;
    private Rectangle normalBounds;
    private JButton maximizeButton;

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(BG_COLOR);
        titleBar.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Money Calculator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleBar.add(titleLabel, BorderLayout.WEST);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlsPanel.setBackground(BG_COLOR);

        JButton minimizeButton = createControlButton("−");
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        controlsPanel.add(minimizeButton);

        maximizeButton = createControlButton("□");
        maximizeButton.addActionListener(e -> toggleMaximize());
        controlsPanel.add(maximizeButton);

        JButton closeButton = createControlButton("×");
        closeButton.addActionListener(e -> System.exit(0));
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(200, 50, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(BG_COLOR);
            }
        });
        controlsPanel.add(closeButton);
        
        titleBar.add(controlsPanel, BorderLayout.EAST);

        final Point[] dragOffset = {null};
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                dragOffset[0] = e.getPoint();
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleMaximize();
                }
            }
        });
        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (!isMaximized) {
                    Point current = e.getLocationOnScreen();
                    setLocation(current.x - dragOffset[0].x, current.y - dragOffset[0].y);
                }
            }
        });
        
        return titleBar;
    }

    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setForeground(TEXT_COLOR);
        button.setBackground(BG_COLOR);
        button.setBorder(new EmptyBorder(5, 12, 5, 12));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(true);
                if (button.getText().equals("×")) {
                    button.setBackground(new Color(200, 50, 50));
                } else {
                    button.setBackground(new Color(66, 66, 66));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(false);
                button.setBackground(BG_COLOR);
            }
        });
        return button;
    }

    private void toggleMaximize() {
        if (isMaximized) {
            setBounds(normalBounds);
            isMaximized = false;
            maximizeButton.setText("□");
            setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        } else {
            normalBounds = getBounds();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            isMaximized = true;
            maximizeButton.setText("❐");
            setShape(null);
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        mainPanel.add(createHeaderLabel(), gbcWith(0, 0, 2));

        mainPanel.add(createLabel("Amount:"), gbcWith(0, 1, 1));
        
        gbc = gbcWith(1, 1, 1);
        inputAmount = createTextField();
        mainPanel.add(inputAmount, gbc);
        
        mainPanel.add(createLabel("From:"), gbcWith(0, 2, 1));
        
        gbc = gbcWith(1, 2, 1);
        inputCurrency = createCurrencyComboBox();
        mainPanel.add(inputCurrency, gbc);

        mainPanel.add(createArrowLabel(), gbcWith(0, 3, 2));

        mainPanel.add(createLabel("To:"), gbcWith(0, 4, 1));
        
        gbc = gbcWith(1, 4, 1);
        outputCurrency = createCurrencyComboBox();
        mainPanel.add(outputCurrency, gbc);

        mainPanel.add(createLabel("Result:"), gbcWith(0, 5, 1));
        
        gbc = gbcWith(1, 5, 1);
        outputAmount = createTextField();
        outputAmount.setEditable(false);
        outputAmount.setBackground(new Color(48, 48, 48));
        mainPanel.add(outputAmount, gbc);

        gbc = gbcWith(0, 6, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(createExchangeButton(), gbc);

        return mainPanel;
    }

    private GridBagConstraints gbcWith(int x, int y, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        return gbc;
    }

    private JLabel createHeaderLabel() {
        JLabel label = new JLabel("Currency Converter", SwingConstants.CENTER);
        label.setFont(HEADER_FONT);
        label.setForeground(ACCENT_COLOR);
        return label;
    }

    private JLabel createArrowLabel() {
        JLabel label = new JLabel("↓", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        field.setFont(INPUT_FONT);
        field.setBackground(COMPONENT_BG_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), 
            new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JComboBox<Currency> createCurrencyComboBox() {
        JComboBox<Currency> comboBox = new JComboBox<>(currencies.toArray(new Currency[0]));
        comboBox.setFont(INPUT_FONT);
        comboBox.setBackground(COMPONENT_BG_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setEditable(false);
        comboBox.setRenderer(new CurrencyRenderer());
        comboBox.setUI(new DarkComboBoxUI());
        return comboBox;
    }

    private JButton createExchangeButton() {
        JButton button = createStyledButton();
        button.addActionListener(e -> commands.get("exchange").execute());
        return button;
    }

    private JButton createStyledButton() {
        JButton button = new JButton("Exchange");
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 30, 10, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // --- Inner Classes for UI ---

    private class CurrencyRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setOpaque(true);
            
            if (value instanceof Currency(String code, String country, String imageUrl)) {
                label.setText(String.format("%s - %s", code, country));
                label.setIcon(loadIcon(imageUrl));
            } else {
                label.setText(value != null ? value.toString() : "");
                label.setIcon(null);
            }
            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            
            // index == -1 means we're rendering the selected item in the ComboBox button area
            if (index == -1) {
                label.setBackground(COMPONENT_BG_COLOR);
                label.setForeground(TEXT_COLOR);
            } else if (isSelected) {
                label.setBackground(ACCENT_COLOR);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(COMPONENT_BG_COLOR);
                label.setForeground(TEXT_COLOR);
            }
            return label;
        }
    }

    private static class DarkComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {

        @Override
        protected void installDefaults() {
            super.installDefaults();
            comboBox.setBackground(COMPONENT_BG_COLOR);
            comboBox.setForeground(TEXT_COLOR);
        }
        

        
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(COMPONENT_BG_COLOR);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        @Override
        public void paint(Graphics g, JComponent c) {
            g.setColor(COMPONENT_BG_COLOR);
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
            super.paint(g, c);
        }

        @Override
        protected JButton createArrowButton() {
            javax.swing.plaf.basic.BasicArrowButton button = new javax.swing.plaf.basic.BasicArrowButton(
                javax.swing.plaf.basic.BasicArrowButton.SOUTH,
                COMPONENT_BG_COLOR, COMPONENT_BG_COLOR, TEXT_COLOR, COMPONENT_BG_COLOR
            ) {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_COLOR);
                    int w = getWidth();
                    int h = getHeight();
                    int size = Math.min(w, h) / 4;
                    int x = (w - size) / 2;
                    int y = (h - size) / 2;
                    g2.fillPolygon(new int[]{x, x + size, x + size / 2}, new int[]{y, y, y + size}, 3);
                }
            };
            button.setBackground(COMPONENT_BG_COLOR);
            button.setBorder(BorderFactory.createEmptyBorder());
            return button;
        }

        @Override
        protected ComboPopup createPopup() {
            javax.swing.plaf.basic.BasicComboPopup popup = new javax.swing.plaf.basic.BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane scroller = super.createScroller();
                    scroller.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                        @Override
                        protected void configureScrollBarColors() {
                            this.thumbColor = new Color(80, 80, 80);
                            this.trackColor = COMPONENT_BG_COLOR;
                        }
                    });
                    return scroller;
                }
            };
            popup.getList().setBackground(COMPONENT_BG_COLOR);
            popup.getList().setForeground(TEXT_COLOR);
            popup.getList().setSelectionBackground(ACCENT_COLOR);
            popup.getList().setSelectionForeground(TEXT_COLOR);
            return popup;
        }
    }

    private final Map<String, ImageIcon> imageCache = new HashMap<>();

    private ImageIcon loadIcon(String url) {
        if (url == null || url.isEmpty()) return null;
        if (imageCache.containsKey(url)) return imageCache.get(url);
        
        imageCache.put(url, null);
        new Thread(() -> {
            try {
                java.net.URL imgUrl = URI.create(url).toURL();
                java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(imgUrl);
                if (image != null) {
                    Image scaled = image.getScaledInstance(25, 18, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaled);
                    imageCache.put(url, icon);
                    SwingUtilities.invokeLater(this::repaint);
                }
            } catch (Exception ignored) {}
        }).start();
        return null;
    }

    public void addCommand(String name, Command command) {
        this.commands.put(name, command);
    }

    public MoneyDialog moneyDialog() {
        return () -> new Money(inputAmount(), inputCurrency());
    }

    public CurrencyDialog currencyDialog() {
        return this::outputCurrency;
    }

    public MoneyDisplay moneyDisplay() {
        return money -> outputAmount.setText(String.format("%.2f %s", money.amount(), money.currency().code()));
    }

    private double inputAmount() {
        try {
            return Double.parseDouble(inputAmount.getText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Currency inputCurrency() {
        return (Currency) inputCurrency.getSelectedItem();
    }

    private Currency outputCurrency() {
        return (Currency) outputCurrency.getSelectedItem();
    }
}
