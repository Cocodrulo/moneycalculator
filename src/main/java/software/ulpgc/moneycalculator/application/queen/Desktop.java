package software.ulpgc.moneycalculator.application.queen;

import software.ulpgc.moneycalculator.architecture.control.Command;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.Money;
import software.ulpgc.moneycalculator.architecture.ui.CurrencyDialog;
import software.ulpgc.moneycalculator.architecture.ui.MoneyDialog;
import software.ulpgc.moneycalculator.architecture.ui.MoneyDisplay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
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
        this.setTitle("Money Calculator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(BG_COLOR);
        this.getContentPane().add(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("Currency Converter", SwingConstants.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(ACCENT_COLOR);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(createLabel("Amount:"), gbc);
        
        gbc.gridx = 1;
        inputAmount = createTextField();
        mainPanel.add(inputAmount, gbc);
        
        gbc.gridx = 0; 
        gbc.gridy++;
        mainPanel.add(createLabel("From:"), gbc);
        
        gbc.gridx = 1;
        inputCurrency = createCurrencyComboBox();
        mainPanel.add(inputCurrency, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel arrowLabel = new JLabel("↓", SwingConstants.CENTER);
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        arrowLabel.setForeground(Color.LIGHT_GRAY);
        mainPanel.add(arrowLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        mainPanel.add(createLabel("To:"), gbc);
        
        gbc.gridx = 1;
        outputCurrency = createCurrencyComboBox();
        mainPanel.add(outputCurrency, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(createLabel("Result:"), gbc);
        
        gbc.gridx = 1;
        outputAmount = createTextField();
        outputAmount.setEditable(false);
        outputAmount.setBackground(new Color(48, 48, 48)); // Slightly different for read-only
        mainPanel.add(outputAmount, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JButton exchangeButton = createStyledButton("Exchange");
        exchangeButton.addActionListener(e -> commands.get("exchange").execute());
        mainPanel.add(exchangeButton, gbc);

        return mainPanel;
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
        
        // Custom UI to fix Arrow visibility
        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                javax.swing.plaf.basic.BasicArrowButton button = new javax.swing.plaf.basic.BasicArrowButton(
                    javax.swing.plaf.basic.BasicArrowButton.SOUTH,
                    COMPONENT_BG_COLOR, // Start background
                    COMPONENT_BG_COLOR, // Shadow
                    TEXT_COLOR,         // Dark Shadow (used for arrow shape borders in some LAFs, or contrast)
                    COMPONENT_BG_COLOR  // Highlight
                ) {
                    @Override
                    public void paint(Graphics g) {
                        super.paint(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setColor(TEXT_COLOR);
                        // Access protected/private fields isn't easy here, let's just draw a simple triangle
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
        });

        ((JComponent) comboBox.getRenderer()).setBorder(new EmptyBorder(5, 5, 5, 5));
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 30, 10, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
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
