import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.security.SecureRandom;

// Password Strength Analyser | Advanced Programming CIT244 | Ahndre Walters & Joshua Evelyn | IT2B | March 2026
// This application analyzes password strength based on 5 criteria: length, uppercase, lowercase, digits, and special characters

public class PasswordStrengthAnalyser extends JFrame {

    // UI component declarations - password input field with echo character masking
    private JPasswordField passwordField;
    // Checkbox component to toggle password visibility on/off
    private JCheckBox showPasswordCheckBox;
    // Progress bar that visually represents the password strength score
    private JProgressBar strengthBar;
    // Label to display the strength rating text (Weak, Moderate, Strong, Very Strong)
    private JLabel strengthLabel, scoreLabel;
    // Individual feedback labels for each of the five password criteria checks
    private JLabel lengthFeedback, uppercaseFeedback, lowercaseFeedback, digitFeedback, specialFeedback;

    // Color definitions for the application theme - main application background color
    private static final Color BG_APP   = new Color(240, 242, 248);
    // Light grey color for panel backgrounds
    private static final Color BG_PANEL = Color.WHITE;
    // Slightly tinted color for input field backgrounds
    private static final Color BG_INPUT = new Color(247, 248, 252);
    // Border color for separating UI elements
    private static final Color BORDER   = new Color(218, 222, 235);
    // Dark text color for titles and primary text
    private static final Color TXT_DARK = new Color(30,  35,  50);
    // Mid-tone text color for secondary text
    private static final Color TXT_MID  = new Color(90,  95, 115);
    // Light text color for tertiary/hint text
    private static final Color TXT_LITE = new Color(160, 165, 180);

    // Strength level colors: red for weak password
    private static final Color C_WEAK  = new Color(220, 55,  55);
    // Orange color for moderate strength password
    private static final Color C_MOD   = new Color(235, 145,  0);
    // Yellow-green color for strong password
    private static final Color C_STR   = new Color(150, 195,  0);
    // Green color for very strong password
    private static final Color C_VSTR  = new Color(25,  165, 60);
    // Empty grey color when progress bar is at zero
    private static final Color C_EMPTY = new Color(218, 222, 235);

    // Colors for criteria checklist: green dot for passed criterion
    private static final Color PASS = new Color(25,  165, 60);
    // Red dot color for failed criterion
    private static final Color FAIL = new Color(220, 55,  55);

    // Hex color codes used in HTML dot indicators - hex for passed state
    private static final String HEX_PASS = "#19A53C";
    // Hex code for failed state
    private static final String HEX_FAIL = "#DC3737";
    // Hex code for idle/unchecked state
    private static final String HEX_IDLE = "#A0A5B4";

    // Spacing constants for consistent layout alignment - outer padding around main container
    private static final int PAD_OUTER = 28;
    // Side/horizontal padding for left and right edges
    private static final int PAD_SIDE  = 32;
    // Internal padding within card panels
    private static final int PAD_CARD  = 22;
    // Gap between the two columns in the layout
    private static final int GAP_COL   = 20;
    // Vertical gap between cards in a column
    private static final int GAP_CARD  = 16;

    // String constants for password generation - uppercase letters available for use
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // Lowercase letters available for password generation
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    // Numeric digits available for password generation
    private static final String DIGITS = "0123456789";
    // Special characters available for password generation
    private static final String SPECIAL = "!@#$%^&*";
    // Combination of all character sets for random selection
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;
    // Minimum length for generated strong passwords
    private static final int MIN_PASSWORD_LENGTH = 14;

    // Constructor initializes the JFrame and sets up the UI
    public PasswordStrengthAnalyser() {
        // Set the window title shown in the taskbar
        super("Password Strength Analyser");
        // Close the application when the window is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Set initial window size to 880x560 pixels
        setSize(880, 560);
        // Prevent window from being resized smaller than 840x520
        setMinimumSize(new Dimension(840, 520));
        // Center the window on the screen when launched
        setLocationRelativeTo(null);
        // Disable window resizing to maintain layout integrity
        setResizable(false);
        // Build and populate the user interface
        buildUI();
    }

    // Builds the complete user interface with header, body, and components
    private void buildUI() {
        // Use BorderLayout to position header, body, and other elements
        setLayout(new BorderLayout());

        // Create header panel with title and course information
        JPanel header = new JPanel(new BorderLayout());
        // Set header background to white
        header.setBackground(BG_PANEL);
        // Add bottom border and padding to the header
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(18, PAD_SIDE, 18, PAD_SIDE)));
        // Create a vertical box layout for header text elements
        JPanel hText = new JPanel();
        hText.setLayout(new BoxLayout(hText, BoxLayout.Y_AXIS));
        // Set header text panel background
        hText.setBackground(BG_PANEL);
        // Add main title label in bold 24pt font
        hText.add(lbl("Password Strength Analyser", Font.BOLD, 24, TXT_DARK));
        // Add vertical spacing below title
        hText.add(Box.createVerticalStrut(4));
        // Add subtitle with course info and author names in light text
        hText.add(lbl("CIT244 - Advanced Programming  |  Ahndre Walters & Joshua Evelyn", Font.PLAIN, 13, TXT_LITE));
        // Add header text to the left side of header panel
        header.add(hText, BorderLayout.WEST);
        // Add header panel to top of frame
        add(header, BorderLayout.NORTH);

        // Create main body panel containing two columns and clear button
        JPanel body = new JPanel(new BorderLayout());
        // Set body background to light app color
        body.setBackground(BG_APP);
        // Set outer padding for the body panel
        body.setBorder(new EmptyBorder(PAD_OUTER, PAD_SIDE, PAD_OUTER, PAD_SIDE));

        // Create two-column layout: left for input, right for criteria checklist
        JPanel columns = new JPanel(new GridLayout(1, 2, GAP_COL, 0));
        // Set columns background to match body
        columns.setBackground(BG_APP);

        // Left column contains password input card and strength bar card
        JPanel left = new JPanel(new BorderLayout(0, GAP_CARD));
        // Set left column background
        left.setBackground(BG_APP);

        // Input card with password field and show/hide checkbox
        RoundedPanel inputCard = card();
        // Create password field with masked input by default
        passwordField = new JPasswordField();
        // Set font for password field to SansSerif plain 15pt
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        // Limit password field height to 46 pixels
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        // Align password field to the left
        passwordField.setAlignmentX(LEFT_ALIGNMENT);
        // Set input field background color
        passwordField.setBackground(BG_INPUT);
        // Add border and padding to password field
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(10, 14, 10, 14)));

        // Checkbox to toggle password visibility
        showPasswordCheckBox = new JCheckBox("Show password");
        // Set checkbox font to SansSerif plain 13pt
        showPasswordCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        // Set checkbox background to white
        showPasswordCheckBox.setBackground(BG_PANEL);
        // Set checkbox text color to mid-tone
        showPasswordCheckBox.setForeground(TXT_MID);
        // Remove focus border around checkbox
        showPasswordCheckBox.setFocusPainted(false);
        // Align checkbox to the left
        showPasswordCheckBox.setAlignmentX(LEFT_ALIGNMENT);
        // Add action listener to toggle password visibility when checkbox is clicked
        showPasswordCheckBox.addActionListener(e ->
                passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '\u2022'));

        // Add components to input card - title label
        inputCard.add(lbl("Enter Your Password", Font.BOLD, 16, TXT_DARK));
        // Add vertical spacing
        inputCard.add(Box.createVerticalStrut(3));
        // Add subtitle label
        inputCard.add(lbl("Type below to analyse in real time", Font.PLAIN, 13, TXT_LITE));
        // Add larger vertical spacing
        inputCard.add(Box.createVerticalStrut(16));
        // Add password field to card
        inputCard.add(passwordField);
        // Add spacing before checkbox
        inputCard.add(Box.createVerticalStrut(10));

        // Create horizontal panel for checkbox and generate button
        JPanel checkboxAndGenPanel = new JPanel(new BorderLayout(8, 0));
        // Set panel background to transparent
        checkboxAndGenPanel.setBackground(BG_PANEL);
        // Set panel alignment
        checkboxAndGenPanel.setAlignmentX(LEFT_ALIGNMENT);
        // Add checkbox to left side
        checkboxAndGenPanel.add(showPasswordCheckBox, BorderLayout.WEST);

        // Create generate password button with green color
        JButton generateBtn = new RoundedButton("Generate", 8);
        // Set button font to bold 12pt
        generateBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        // Set button background to green
        generateBtn.setBackground(C_VSTR);
        // Set button text to white
        generateBtn.setForeground(Color.WHITE);
        // Remove focus border on button
        generateBtn.setFocusPainted(false);
        // Disable default button border painting
        generateBtn.setBorderPainted(false);
        // Make button background fully opaque
        generateBtn.setOpaque(false);
        // Set cursor to hand pointer when hovering
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Set button size to compact dimensions
        generateBtn.setPreferredSize(new Dimension(90, 32));
        // Set maximum size for layout management
        generateBtn.setMaximumSize(new Dimension(90, 32));
        // Add action listener to generate strong password when clicked
        generateBtn.addActionListener(e -> {
            // Generate a strong password using secure random
            String generatedPassword = generateStrongPassword();
            // Set generated password into password field
            passwordField.setText(generatedPassword);
            // Uncheck the show password checkbox
            showPasswordCheckBox.setSelected(false);
            // Reset echo character to bullet point
            passwordField.setEchoChar('\u2022');
            // Focus cursor to password field
            passwordField.requestFocusInWindow();
        });

        // Add generate button to right side of checkbox panel
        checkboxAndGenPanel.add(generateBtn, BorderLayout.EAST);
        // Add checkbox and button panel to input card
        inputCard.add(checkboxAndGenPanel);

        // Strength bar card showing visual feedback and score
        RoundedPanel barCard = card();
        // Create label for strength rating text
        strengthLabel = lbl("", Font.BOLD, 14, TXT_LITE);
        // Create score badge showing current strength score
        scoreLabel = lbl("0 / 5", Font.BOLD, 12, Color.WHITE);
        // Make score label opaque for background color display
        scoreLabel.setOpaque(true);
        // Set score badge background color to light grey initially
        scoreLabel.setBackground(TXT_LITE);
        // Add padding to score badge
        scoreLabel.setBorder(new EmptyBorder(4, 10, 4, 10));

        // Top row with strength label on left and score badge on right
        JPanel barTopRow = new JPanel(new BorderLayout());
        // Set row background
        barTopRow.setBackground(BG_PANEL);
        // Align row to left
        barTopRow.setAlignmentX(LEFT_ALIGNMENT);
        // Create panel for rating information with right-aligned flow
        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        // Set rating row background
        ratingRow.setBackground(BG_PANEL);
        // Add strength label to rating row
        ratingRow.add(strengthLabel);
        // Add score badge to rating row
        ratingRow.add(scoreLabel);
        // Add "Strength" title to left of bar row
        barTopRow.add(lbl("Strength", Font.BOLD, 15, TXT_DARK), BorderLayout.WEST);
        // Add rating row with strength label and score to right of bar row
        barTopRow.add(ratingRow, BorderLayout.EAST);

        // Progress bar visual indicator for password strength
        strengthBar = new JProgressBar(0, 100);
        // Initialize progress bar value to 0
        strengthBar.setValue(0);
        // Disable text display on progress bar
        strengthBar.setStringPainted(false);
        // Remove border around progress bar
        strengthBar.setBorderPainted(false);
        // Set background color to empty grey
        strengthBar.setBackground(C_EMPTY);
        // Set foreground color (the filled portion) to empty grey initially
        strengthBar.setForeground(C_EMPTY);
        // Limit progress bar height to 16 pixels
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        // Align progress bar to left
        strengthBar.setAlignmentX(LEFT_ALIGNMENT);
        // Override UI to ensure custom colors display on Windows systems
        strengthBar.setUI(new BasicProgressBarUI() {
            // Override selection background color
            protected Color getSelectionBackground() { return Color.WHITE; }
            // Override selection foreground color
            protected Color getSelectionForeground() { return Color.WHITE; }
        });
        // Add bar row to card
        barCard.add(barTopRow);
        // Add spacing before progress bar
        barCard.add(Box.createVerticalStrut(12));
        // Add progress bar to card
        barCard.add(strengthBar);

        // Add cards to left column - input card at top
        left.add(inputCard, BorderLayout.CENTER);
        // Add strength bar card at bottom
        left.add(barCard,   BorderLayout.SOUTH);

        // Right column contains criteria checklist
        RoundedPanel checkCard = new RoundedPanel(18);
        // Set checklist layout to vertical box
        checkCard.setLayout(new BoxLayout(checkCard, BoxLayout.Y_AXIS));
        // Set checklist background to white
        checkCard.setBackground(BG_PANEL);
        // Set padding inside checklist card
        checkCard.setBorder(new EmptyBorder(PAD_CARD, PAD_CARD, PAD_CARD, PAD_CARD));

        // Add checklist title
        checkCard.add(lbl("Criteria Checklist", Font.BOLD, 16, TXT_DARK));
        // Add vertical spacing
        checkCard.add(Box.createVerticalStrut(3));
        // Add checklist subtitle
        checkCard.add(lbl("All five must pass for a very strong password", Font.PLAIN, 13, TXT_LITE));
        // Add spacing before criteria rows
        checkCard.add(Box.createVerticalStrut(18));

        // Create five criteria feedback labels with colored dot indicators
        lengthFeedback    = criterionLbl("At least 8 characters");
        uppercaseFeedback = criterionLbl("Contains an uppercase letter  (A - Z)");
        lowercaseFeedback = criterionLbl("Contains a lowercase letter  (a - z)");
        digitFeedback     = criterionLbl("Contains a number  (0 - 9)");
        specialFeedback   = criterionLbl("Contains a special character  (! @ # $ % ^ & *)");

        // Display each criterion in a rounded row with proper styling
        for (JLabel l : new JLabel[]{ lengthFeedback, uppercaseFeedback, lowercaseFeedback, digitFeedback, specialFeedback }) {
            // Create rounded panel for each criterion row
            RoundedPanel row = new RoundedPanel(8);
            // Set row layout to center content
            row.setLayout(new BorderLayout());
            // Set row background to input color
            row.setBackground(BG_INPUT);
            // Add padding inside row
            row.setBorder(new EmptyBorder(10, 14, 10, 14));
            // Limit row height to 42 pixels
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            // Align row to left
            row.setAlignmentX(LEFT_ALIGNMENT);
            // Add criterion label to row center
            row.add(l, BorderLayout.CENTER);
            // Add row to checklist card
            checkCard.add(row);
            // Add small spacing between criterion rows
            checkCard.add(Box.createVerticalStrut(6));
        }

        // Add left and right columns to two-column layout
        columns.add(left);
        // Add checklist to two-column layout
        columns.add(checkCard);

        // Clear button to reset all fields and UI
        JButton clearBtn = new RoundedButton("Clear", 10);
        // Set button font to bold 15pt
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        // Set button background to red
        clearBtn.setBackground(new Color(200, 0, 0));
        // Set button text to white
        clearBtn.setForeground(Color.WHITE);
        // Remove focus border on button
        clearBtn.setFocusPainted(false);
        // Disable default button border painting
        clearBtn.setBorderPainted(false);
        // Make button background fully opaque
        clearBtn.setOpaque(false);
        // Set cursor to hand pointer when hovering
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Set button size to 100x46 pixels
        clearBtn.setPreferredSize(new Dimension(100, 46));
        // Add action listener to clear password and reset UI when clicked
        clearBtn.addActionListener(e -> {
            // Clear password field text
            passwordField.setText("");
            // Uncheck the show password checkbox
            showPasswordCheckBox.setSelected(false);
            // Reset echo character to bullet point
            passwordField.setEchoChar('\u2022');
            // Focus cursor back to password field
            passwordField.requestFocusInWindow();
            // Reset all UI elements to initial state
            resetUI();
        });

        // Wrapper panel for clear button
        JPanel btnWrap = new JPanel(new BorderLayout());
        // Set wrapper background
        btnWrap.setBackground(BG_APP);
        // Add spacing above button
        btnWrap.setBorder(new EmptyBorder(GAP_CARD, 0, 0, 0));
        // Add button to wrapper panel center
        btnWrap.add(clearBtn, BorderLayout.CENTER);

        // Add two-column layout to body center
        body.add(columns, BorderLayout.CENTER);
        // Add button wrapper to body bottom
        body.add(btnWrap,  BorderLayout.SOUTH);
        // Add body panel to frame center
        add(body, BorderLayout.CENTER);

        // Add document listener to analyze password on every keystroke
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            // Listener method called when text is inserted
            public void insertUpdate(DocumentEvent e)  { analyse(); }
            // Listener method called when text is removed
            public void removeUpdate(DocumentEvent e)  { analyse(); }
            // Listener method called when text attributes change
            public void changedUpdate(DocumentEvent e) { analyse(); }
        });
    }

    // Generates a cryptographically strong random password that meets all criteria
    private String generateStrongPassword() {
        // Create SecureRandom instance for cryptographically secure random number generation
        SecureRandom random = new SecureRandom();
        // Create StringBuilder to build the password string
        StringBuilder password = new StringBuilder();

        // Add one random uppercase letter to ensure criterion 2 is met
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        // Add one random lowercase letter to ensure criterion 3 is met
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        // Add one random digit to ensure criterion 4 is met
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        // Add one random special character to ensure criterion 5 is met
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill remaining characters up to minimum length with random characters from all sets
        while (password.length() < MIN_PASSWORD_LENGTH) {
            // Append random character from all available characters
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Convert StringBuilder to character array for shuffling
        char[] passwordArray = password.toString().toCharArray();
        // Shuffle the password characters to randomize position of required characters
        for (int i = passwordArray.length - 1; i > 0; i--) {
            // Generate random index between 0 and current position
            int j = random.nextInt(i + 1);
            // Swap current character with random character (Fisher-Yates shuffle)
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        // Convert character array back to string and return generated password
        return new String(passwordArray);
    }

    // Analyzes the password and updates UI based on 5 strength criteria
    private void analyse() {
        // Get current password from password field as string
        String pw = new String(passwordField.getPassword());

        // Reset UI if password field is empty
        if (pw.isEmpty()) { resetUI(); return; }

        // Check criterion 1: Password length is at least 8 characters
        boolean len = pw.length() >= 8;

        // Check criterion 2: Contains at least one uppercase letter (A-Z)
        boolean up  = pw.matches(".*[A-Z].*");

        // Check criterion 3: Contains at least one lowercase letter (a-z)
        boolean low = pw.matches(".*[a-z].*");

        // Check criterion 4: Contains at least one numeric digit (0-9)
        boolean dig = pw.matches(".*[0-9].*");

        // Check criterion 5: Contains at least one special character (!@#$%^&* etc)
        boolean spc = pw.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        // Calculate score: 1 point per passed criterion (max 5)
        int score = (len?1:0) + (up?1:0) + (low?1:0) + (dig?1:0) + (spc?1:0);

        // Update length criterion label with colored dot and text
        setLbl(lengthFeedback,    len, "At least 8 characters");
        // Update uppercase criterion label with colored dot and text
        setLbl(uppercaseFeedback, up,  "Contains an uppercase letter  (A - Z)");
        // Update lowercase criterion label with colored dot and text
        setLbl(lowercaseFeedback, low, "Contains a lowercase letter  (a - z)");
        // Update digit criterion label with colored dot and text
        setLbl(digitFeedback,     dig, "Contains a number  (0 - 9)");
        // Update special character criterion label with colored dot and text
        setLbl(specialFeedback,   spc, "Contains a special character  (! @ # $ % ^ & *)");

        // Update progress bar value (20% per criterion passed)
        strengthBar.setValue(score * 20);

        // Update score display with current score and total (X / 5)
        scoreLabel.setText(score + " / 5");

        // Apply strength color and rating label based on score
        if      (score <= 1) applyStrength(C_WEAK, "Weak");
            // Apply moderate strength color and label for score 2-3
        else if (score <= 3) applyStrength(C_MOD,  "Moderate");
            // Apply strong color and label for score 4
        else if (score == 4) applyStrength(C_STR,  "Strong");
            // Apply very strong color and label for score 5
        else                 applyStrength(C_VSTR, "Very Strong");
    }

    // Updates the strength bar, label, and score badge with specified color and rating text
    private void applyStrength(Color c, String rating) {
        // Set the filled portion of progress bar to specified color
        strengthBar.setForeground(c);
        // Set strength label text to rating string
        strengthLabel.setText(rating);
        // Set strength label text color to match bar color
        strengthLabel.setForeground(c);
        // Set score badge background color to match rating color
        scoreLabel.setBackground(c);
    }

    // Updates a criterion label with a green dot (passed) or red dot (failed)
    private void setLbl(JLabel label, boolean passed, String text) {
        // Set label HTML content with colored dot and text based on pass/fail status
        label.setText(dotHtml(passed ? HEX_PASS : HEX_FAIL, text));
    }

    // Resets all UI elements to their default idle state
    private void resetUI() {
        // Set progress bar value back to zero
        strengthBar.setValue(0);
        // Set progress bar color back to empty grey
        strengthBar.setForeground(C_EMPTY);
        // Clear strength label text
        strengthLabel.setText("");
        // Set strength label color back to light grey
        strengthLabel.setForeground(TXT_LITE);
        // Reset score display to "0 / 5"
        scoreLabel.setText("0 / 5");
        // Reset score badge background to light grey
        scoreLabel.setBackground(TXT_LITE);

        // Array of criterion text strings in same order as criteria
        String[] texts = { "At least 8 characters", "Contains an uppercase letter  (A - Z)",
                "Contains a lowercase letter  (a - z)", "Contains a number  (0 - 9)",
                "Contains a special character  (! @ # $ % ^ & *)" };
        // Array of criterion labels in same order
        JLabel[] labels = { lengthFeedback, uppercaseFeedback, lowercaseFeedback, digitFeedback, specialFeedback };
        // Loop through each criterion and reset to idle state
        for (int i = 0; i < labels.length; i++)
            // Set label HTML with idle grey dot
            labels[i].setText(dotHtml(HEX_IDLE, texts[i]));
    }

    // Builds HTML string with colored dot indicator and text on the same line
    private String dotHtml(String hex, String text) {
        // Create HTML string with colored bullet dot and text in grey color
        return "<html><font color='" + hex + "'>&#9679;</font>&nbsp;&nbsp;<font color='#5A5F73'>" + text + "</font></html>";
    }

    // Creates a styled JLabel with specified font, style, size, and color
    private JLabel lbl(String text, int style, int size, Color color) {
        // Create new label with specified text
        JLabel l = new JLabel(text);
        // Set font with style (BOLD or PLAIN) and size
        l.setFont(new Font("SansSerif", style, size));
        // Set text color
        l.setForeground(color);
        // Align label to left side
        l.setAlignmentX(LEFT_ALIGNMENT);
        // Return configured label
        return l;
    }

    // Creates a white rounded card panel with consistent padding for content
    private RoundedPanel card() {
        // Create rounded panel with 18-pixel corner radius
        RoundedPanel p = new RoundedPanel(18);
        // Set layout to vertical box layout
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        // Set background to white
        p.setBackground(BG_PANEL);
        // Add consistent padding around content
        p.setBorder(new EmptyBorder(PAD_CARD, PAD_CARD, PAD_CARD, PAD_CARD));
        // Return configured card panel
        return p;
    }

    // Creates a criterion label with a grey idle dot using HTML formatting
    private JLabel criterionLbl(String text) {
        // Create label with HTML formatted dot and text in idle grey color
        JLabel l = new JLabel(dotHtml(HEX_IDLE, text));
        // Set font to SansSerif plain 13pt
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        // Align label to left
        l.setAlignmentX(LEFT_ALIGNMENT);
        // Return configured criterion label
        return l;
    }

    // Rounded panel class used for card containers with smooth rounded corners
    static class RoundedPanel extends JPanel {
        // Field to store corner radius value
        private final int r;

        // Constructor accepts corner radius parameter
        RoundedPanel(int r) {
            // Store radius value
            this.r = r;
            // Set panel to not paint default opaque background
            setOpaque(false);
        }

        // Paints a rounded rectangle background for the panel
        protected void paintComponent(Graphics g) {
            // Create Graphics2D context from Graphics object
            Graphics2D g2 = (Graphics2D) g.create();
            // Enable anti-aliasing for smooth rounded edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Set drawing color to panel background color
            g2.setColor(getBackground());
            // Draw filled rounded rectangle across entire panel
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
            // Dispose graphics resources
            g2.dispose();
        }
    }

    // Rounded pill-shaped button class for consistent rounded button styling
    static class RoundedButton extends JButton {
        // Field to store corner radius value
        private final int r;

        // Constructor accepts button text and corner radius parameter
        RoundedButton(String text, int r) {
            // Call parent constructor with button text
            super(text);
            // Store radius value
            this.r = r;
            // Disable default content area fill so custom paint is used
            setContentAreaFilled(false);
        }

        // Paints a rounded rectangle background for the button
        protected void paintComponent(Graphics g) {
            // Create Graphics2D context from Graphics object
            Graphics2D g2 = (Graphics2D) g.create();
            // Enable anti-aliasing for smooth rounded corners
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Set drawing color to button background color
            g2.setColor(getBackground());
            // Draw filled rounded rectangle across entire button
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
            // Dispose graphics resources
            g2.dispose();
            // Call parent paint method to draw button text and content
            super.paintComponent(g);
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        // Execute UI initialization on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            // Try block for setting system look and feel
            try {
                // Set look and feel to match current system (Windows, Mac, Linux)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            // Catch block if look and feel cannot be set
            catch (Exception e) {
                // Print error stack trace to console
                e.printStackTrace();
            }
            // Create new instance of application and display it
            new PasswordStrengthAnalyser().setVisible(true);
        });
    }
}
