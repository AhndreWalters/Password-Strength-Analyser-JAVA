import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Password Strength Analyser | Advanced Programming CIT244 | Ahndre Walters & Joshua Evelyn | IT2B | March 2026

public class PasswordStrengthAnalyser extends JFrame {

    // UI components
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JProgressBar strengthBar;
    private JLabel strengthLabel, scoreLabel;
    private JLabel lengthFeedback, uppercaseFeedback, lowercaseFeedback, digitFeedback, specialFeedback;

    // App colours
    private static final Color BG_APP   = new Color(240, 242, 248);
    private static final Color BG_PANEL = Color.WHITE;
    private static final Color BG_INPUT = new Color(247, 248, 252);
    private static final Color BORDER   = new Color(218, 222, 235);
    private static final Color TXT_DARK = new Color(30,  35,  50);
    private static final Color TXT_MID  = new Color(90,  95, 115);
    private static final Color TXT_LITE = new Color(160, 165, 180);

    // Strength level colours
    private static final Color C_WEAK  = new Color(220, 55,  55);
    private static final Color C_MOD   = new Color(235, 145,  0);
    private static final Color C_STR   = new Color(150, 195,  0);
    private static final Color C_VSTR  = new Color(25,  165, 60);
    private static final Color C_EMPTY = new Color(218, 222, 235);

    // Pass and fail colours
    private static final Color PASS = new Color(25,  165, 60);
    private static final Color FAIL = new Color(220, 55,  55);

    // Hex strings used inside HTML dot labels
    private static final String HEX_PASS = "#19A53C";
    private static final String HEX_FAIL = "#DC3737";
    private static final String HEX_IDLE = "#A0A5B4";

    // Shared spacing constants so everything lines up
    private static final int PAD_OUTER = 28; // body top/bottom padding
    private static final int PAD_SIDE  = 32; // body and header left/right padding
    private static final int PAD_CARD  = 22; // internal card padding (all sides)
    private static final int GAP_COL   = 20; // gap between the two columns
    private static final int GAP_CARD  = 16; // gap between the two left cards

    public PasswordStrengthAnalyser() {
        super("Password Strength Analyser");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 560);
        setMinimumSize(new Dimension(840, 520));
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Header — same side padding as the body so title aligns with card edges
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(18, PAD_SIDE, 18, PAD_SIDE)));
        JPanel hText = new JPanel(); hText.setLayout(new BoxLayout(hText, BoxLayout.Y_AXIS)); hText.setBackground(BG_PANEL);
        hText.add(lbl("Password Strength Analyser", Font.BOLD, 24, TXT_DARK));
        hText.add(Box.createVerticalStrut(4));
        hText.add(lbl("CIT244 - Advanced Programming  |  Ahndre Walters & Joshua Evelyn", Font.PLAIN, 13, TXT_LITE));
        header.add(hText, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Body — columns in CENTER, clear button pinned to SOUTH
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_APP);
        body.setBorder(new EmptyBorder(PAD_OUTER, PAD_SIDE, PAD_OUTER, PAD_SIDE));

        // Two equal columns
        JPanel columns = new JPanel(new GridLayout(1, 2, GAP_COL, 0));
        columns.setBackground(BG_APP);

        // Left column — input card on top, bar card pinned to bottom
        JPanel left = new JPanel(new BorderLayout(0, GAP_CARD));
        left.setBackground(BG_APP);

        // Input card
        RoundedPanel inputCard = card();
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);
        passwordField.setBackground(BG_INPUT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(10, 14, 10, 14)));
        showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        showPasswordCheckBox.setBackground(BG_PANEL); showPasswordCheckBox.setForeground(TXT_MID);
        showPasswordCheckBox.setFocusPainted(false); showPasswordCheckBox.setAlignmentX(LEFT_ALIGNMENT);
        showPasswordCheckBox.addActionListener(e ->
                passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '\u2022'));
        inputCard.add(lbl("Enter Your Password", Font.BOLD, 16, TXT_DARK));
        inputCard.add(Box.createVerticalStrut(3));
        inputCard.add(lbl("Type below to analyse in real time", Font.PLAIN, 13, TXT_LITE));
        inputCard.add(Box.createVerticalStrut(16));
        inputCard.add(passwordField);
        inputCard.add(Box.createVerticalStrut(10));
        inputCard.add(showPasswordCheckBox);

        // Strength bar card
        RoundedPanel barCard = card();
        strengthLabel = lbl("", Font.BOLD, 14, TXT_LITE);
        scoreLabel = lbl("0 / 5", Font.BOLD, 12, Color.WHITE);
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(TXT_LITE);
        scoreLabel.setBorder(new EmptyBorder(4, 10, 4, 10));

        // Strength heading row — label left, rating + score badge right
        JPanel barTopRow = new JPanel(new BorderLayout());
        barTopRow.setBackground(BG_PANEL);
        barTopRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        ratingRow.setBackground(BG_PANEL);
        ratingRow.add(strengthLabel); ratingRow.add(scoreLabel);
        barTopRow.add(lbl("Strength", Font.BOLD, 15, TXT_DARK), BorderLayout.WEST);
        barTopRow.add(ratingRow, BorderLayout.EAST);

        strengthBar = new JProgressBar(0, 100);
        strengthBar.setValue(0); strengthBar.setStringPainted(false); strengthBar.setBorderPainted(false);
        strengthBar.setBackground(C_EMPTY); strengthBar.setForeground(C_EMPTY);
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        strengthBar.setAlignmentX(LEFT_ALIGNMENT);
        // Override UI so Windows respects the custom bar colour
        strengthBar.setUI(new BasicProgressBarUI() {
            protected Color getSelectionBackground() { return Color.WHITE; }
            protected Color getSelectionForeground() { return Color.WHITE; }
        });
        barCard.add(barTopRow);
        barCard.add(Box.createVerticalStrut(12));
        barCard.add(strengthBar);

        left.add(inputCard, BorderLayout.CENTER);
        left.add(barCard,   BorderLayout.SOUTH);

        // Right column — checklist card stretches to match the full left column height
        RoundedPanel checkCard = new RoundedPanel(18);
        checkCard.setLayout(new BoxLayout(checkCard, BoxLayout.Y_AXIS));
        checkCard.setBackground(BG_PANEL);
        checkCard.setBorder(new EmptyBorder(PAD_CARD, PAD_CARD, PAD_CARD, PAD_CARD));

        checkCard.add(lbl("Criteria Checklist", Font.BOLD, 16, TXT_DARK));
        checkCard.add(Box.createVerticalStrut(3));
        checkCard.add(lbl("All five must pass for a very strong password", Font.PLAIN, 13, TXT_LITE));
        checkCard.add(Box.createVerticalStrut(18));

        // Five criterion rows — each is a light rounded strip the full width of the card
        lengthFeedback    = criterionLbl("At least 8 characters");
        uppercaseFeedback = criterionLbl("Contains an uppercase letter  (A - Z)");
        lowercaseFeedback = criterionLbl("Contains a lowercase letter  (a - z)");
        digitFeedback     = criterionLbl("Contains a number  (0 - 9)");
        specialFeedback   = criterionLbl("Contains a special character  (! @ # $ % ^ & *)");

        for (JLabel l : new JLabel[]{ lengthFeedback, uppercaseFeedback, lowercaseFeedback, digitFeedback, specialFeedback }) {
            RoundedPanel row = new RoundedPanel(8);
            row.setLayout(new BorderLayout());
            row.setBackground(BG_INPUT);
            row.setBorder(new EmptyBorder(10, 14, 10, 14));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.add(l, BorderLayout.CENTER);
            checkCard.add(row);
            checkCard.add(Box.createVerticalStrut(6));
        }

        columns.add(left);
        columns.add(checkCard);

        // Clear button — full width, same height as a card row, sits below both columns
        JButton clearBtn = new RoundedButton("Clear", 10);
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        clearBtn.setBackground(new Color(200, 0, 0));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false); clearBtn.setBorderPainted(false); clearBtn.setOpaque(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(100, 46));
        clearBtn.addActionListener(e -> {
            passwordField.setText("");
            showPasswordCheckBox.setSelected(false);
            passwordField.setEchoChar('\u2022');
            passwordField.requestFocusInWindow();
            resetUI();
        });

        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setBackground(BG_APP);
        btnWrap.setBorder(new EmptyBorder(GAP_CARD, 0, 0, 0));
        btnWrap.add(clearBtn, BorderLayout.CENTER);

        body.add(columns, BorderLayout.CENTER);
        body.add(btnWrap,  BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);

        // Analyse the password on every keystroke
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { analyse(); }
            public void removeUpdate(DocumentEvent e)  { analyse(); }
            public void changedUpdate(DocumentEvent e) { analyse(); }
        });
    }

    private void analyse() {
        String pw = new String(passwordField.getPassword());

        // If field is empty, reset and stop
        if (pw.isEmpty()) { resetUI(); return; }

        // Check each criterion
        boolean len = pw.length() >= 8;
        boolean up  = pw.matches(".*[A-Z].*");
        boolean low = pw.matches(".*[a-z].*");
        boolean dig = pw.matches(".*[0-9].*");
        boolean spc = pw.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        // 1 point per passing criterion
        int score = (len?1:0) + (up?1:0) + (low?1:0) + (dig?1:0) + (spc?1:0);

        // Update each checklist label
        setLbl(lengthFeedback,    len, "At least 8 characters");
        setLbl(uppercaseFeedback, up,  "Contains an uppercase letter  (A - Z)");
        setLbl(lowercaseFeedback, low, "Contains a lowercase letter  (a - z)");
        setLbl(digitFeedback,     dig, "Contains a number  (0 - 9)");
        setLbl(specialFeedback,   spc, "Contains a special character  (! @ # $ % ^ & *)");

        // Update bar and score badge
        strengthBar.setValue(score * 20);
        scoreLabel.setText(score + " / 5");

        // Apply colour and rating text based on score
        if      (score <= 1) applyStrength(C_WEAK, "Weak");
        else if (score <= 3) applyStrength(C_MOD,  "Moderate");
        else if (score == 4) applyStrength(C_STR,  "Strong");
        else                 applyStrength(C_VSTR, "Very Strong");
    }

    // Applies strength colour to the bar, label, and score badge
    private void applyStrength(Color c, String rating) {
        strengthBar.setForeground(c);
        strengthLabel.setText(rating); strengthLabel.setForeground(c);
        scoreLabel.setBackground(c);
    }

    // Updates a criterion label with a green or red dot using HTML
    private void setLbl(JLabel label, boolean passed, String text) {
        label.setText(dotHtml(passed ? HEX_PASS : HEX_FAIL, text));
    }

    // Resets everything back to the default idle state
    private void resetUI() {
        strengthBar.setValue(0); strengthBar.setForeground(C_EMPTY);
        strengthLabel.setText(""); strengthLabel.setForeground(TXT_LITE);
        scoreLabel.setText("0 / 5"); scoreLabel.setBackground(TXT_LITE);
        String[] texts = { "At least 8 characters", "Contains an uppercase letter  (A - Z)",
                "Contains a lowercase letter  (a - z)", "Contains a number  (0 - 9)",
                "Contains a special character  (! @ # $ % ^ & *)" };
        JLabel[] labels = { lengthFeedback, uppercaseFeedback, lowercaseFeedback, digitFeedback, specialFeedback };
        for (int i = 0; i < labels.length; i++)
            labels[i].setText(dotHtml(HEX_IDLE, texts[i]));
    }

    // Builds the HTML string that puts a coloured dot and text on the same line
    private String dotHtml(String hex, String text) {
        return "<html><font color='" + hex + "'>&#9679;</font>&nbsp;&nbsp;<font color='#5A5F73'>" + text + "</font></html>";
    }

    // Creates a styled JLabel
    private JLabel lbl(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color); l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // Creates a white rounded card panel with consistent padding
    private RoundedPanel card() {
        RoundedPanel p = new RoundedPanel(18);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(PAD_CARD, PAD_CARD, PAD_CARD, PAD_CARD));
        return p;
    }

    // Creates a criterion label with an idle grey dot using HTML
    private JLabel criterionLbl(String text) {
        JLabel l = new JLabel(dotHtml(HEX_IDLE, text));
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // Rounded panel used as a card container
    static class RoundedPanel extends JPanel {
        private final int r;
        RoundedPanel(int r) { this.r = r; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
            g2.dispose();
        }
    }

    // Rounded pill-shaped button
    static class RoundedButton extends JButton {
        private final int r;
        RoundedButton(String text, int r) { super(text); this.r = r; setContentAreaFilled(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
            g2.dispose(); super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }
            new PasswordStrengthAnalyser().setVisible(true);
        });
    }
}