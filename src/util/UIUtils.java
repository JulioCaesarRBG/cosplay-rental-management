package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * UI Utilities for common GUI operations
 */
public class UIUtils {
    
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.System.DATETIME_FORMAT);
    
    /**
     * Create styled button with consistent appearance
     */
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.PLAIN, AppConstants.UI.BODY_FONT_SIZE));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, AppConstants.UI.BUTTON_HEIGHT));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Create primary action button
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, new Color(AppConstants.UI.PRIMARY_COLOR[0], 
                                                AppConstants.UI.PRIMARY_COLOR[1], 
                                                AppConstants.UI.PRIMARY_COLOR[2]));
    }
    
    /**
     * Create secondary action button
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = createStyledButton(text, Color.GRAY);
        return button;
    }
    
    /**
     * Create success button
     */
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, new Color(AppConstants.UI.SUCCESS_COLOR[0], 
                                                AppConstants.UI.SUCCESS_COLOR[1], 
                                                AppConstants.UI.SUCCESS_COLOR[2]));
    }
    
    /**
     * Create danger button
     */
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, new Color(AppConstants.UI.ERROR_COLOR[0], 
                                                AppConstants.UI.ERROR_COLOR[1], 
                                                AppConstants.UI.ERROR_COLOR[2]));
    }
    
    /**
     * Show success message dialog
     */
    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show error message dialog
     */
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show warning message dialog
     */
    public static void showWarningMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmDialog(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, 
                                                 JOptionPane.YES_NO_OPTION, 
                                                 JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Format currency value for display
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "IDR 0";
        }
        return "IDR " + NumberFormat.getNumberInstance(new Locale("id", "ID")).format(amount);
    }
    
    /**
     * Format date time for display
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Create and configure text field with validation
     */
    public static JTextField createValidatedTextField(int maxLength) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Tahoma", Font.PLAIN, AppConstants.UI.BODY_FONT_SIZE));
        textField.setPreferredSize(new Dimension(200, AppConstants.UI.TEXT_FIELD_HEIGHT));
        
        // Add document filter for max length
        ((javax.swing.text.AbstractDocument) textField.getDocument()).setDocumentFilter(
            new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, 
                                       javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                    if (fb.getDocument().getLength() + string.length() <= maxLength) {
                        super.insertString(fb, offset, string, attr);
                    }
                }
                
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, 
                                  javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    int currentLength = fb.getDocument().getLength();
                    int newLength = currentLength - length + (text != null ? text.length() : 0);
                    if (newLength <= maxLength) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });
        
        return textField;
    }
    
    /**
     * Create numeric-only text field
     */
    public static JTextField createNumericTextField() {
        JTextField textField = createValidatedTextField(15);
        
        // Add key listener to allow only numbers
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE && c != '.') {
                    evt.consume();
                }
            }
        });
        
        return textField;
    }
    
    /**
     * Clear all text fields in a container
     */
    public static void clearTextFields(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JComboBox) {
                ((JComboBox<?>) component).setSelectedIndex(0);
            } else if (component instanceof Container) {
                clearTextFields((Container) component);
            }
        }
    }
    
    /**
     * Set enabled state for all components in container
     */
    public static void setComponentsEnabled(Container container, boolean enabled) {
        for (Component component : container.getComponents()) {
            component.setEnabled(enabled);
            if (component instanceof Container) {
                setComponentsEnabled((Container) component, enabled);
            }
        }
    }
    
    /**
     * Create loading dialog
     */
    public static JDialog createLoadingDialog(JFrame parent, String message) {
        JDialog dialog = new JDialog(parent, "Loading", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("Tahoma", Font.PLAIN, AppConstants.UI.BODY_FONT_SIZE));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(label, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        
        return dialog;
    }
    
    /**
     * Show loading dialog with task execution
     */
    public static void executeWithLoading(JFrame parent, String message, Runnable task) {
        JDialog loadingDialog = createLoadingDialog(parent, message);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                task.run();
                return null;
            }
            
            @Override
            protected void done() {
                loadingDialog.dispose();
            }
        };
        
        worker.execute();
        loadingDialog.setVisible(true);
    }
    
    /**
     * Validate required fields in a container
     */
    public static boolean validateRequiredFields(Container container, List<JComponent> requiredFields) {
        for (JComponent field : requiredFields) {
            if (field instanceof JTextField) {
                if (((JTextField) field).getText().trim().isEmpty()) {
                    field.requestFocus();
                    showErrorMessage(container, "Semua field yang wajib harus diisi.");
                    return false;
                }
            } else if (field instanceof JComboBox) {
                if (((JComboBox<?>) field).getSelectedIndex() == -1) {
                    field.requestFocus();
                    showErrorMessage(container, "Semua field yang wajib harus dipilih.");
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Add hover effect to button
     */
    public static void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }
    
    /**
     * Center window on screen
     */
    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        
        window.setLocation(x, y);
    }
    
    /**
     * Set application icon for window
     */
    public static void setApplicationIcon(Window window) {
        try {
            ImageIcon icon = new ImageIcon(UIUtils.class.getResource(AppConstants.UI.ICON_PATH));
            if (window instanceof JFrame) {
                ((JFrame) window).setIconImage(icon.getImage());
            } else if (window instanceof JDialog) {
                ((JDialog) window).setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            AppLogger.logWarning("Could not set application icon: " + e.getMessage());
        }
    }
    
    /**
     * Apply consistent styling to table
     */
    public static void styleTable(JTable table) {
        table.setFont(new Font("Tahoma", Font.PLAIN, AppConstants.UI.BODY_FONT_SIZE));
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(AppConstants.UI.PRIMARY_COLOR[0], 
                                             AppConstants.UI.PRIMARY_COLOR[1], 
                                             AppConstants.UI.PRIMARY_COLOR[2], 100));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        
        // Style header
        if (table.getTableHeader() != null) {
            table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, AppConstants.UI.BODY_FONT_SIZE));
            table.getTableHeader().setBackground(new Color(AppConstants.UI.PRIMARY_COLOR[0], 
                                                          AppConstants.UI.PRIMARY_COLOR[1], 
                                                          AppConstants.UI.PRIMARY_COLOR[2]));
            table.getTableHeader().setForeground(Color.WHITE);
        }
    }
}