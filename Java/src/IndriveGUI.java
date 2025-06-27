import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.math.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.stream.*;
import javax.swing.UIManager.*;
import java.security.*;
import javax.swing.filechooser.*;
import javax.swing.colorchooser.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.CompoundBorder;
import java.beans.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.jar.*;
import java.applet.*;
import java.awt.geom.*;
import javax.swing.event.EventListenerList;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.tree.*;
import java.awt.dnd.*;
import java.awt.im.*;
import java.awt.im.spi.*;
import java.awt.print.*;
import java.lang.annotation.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.multi.*;
import javax.swing.plaf.synth.*;
import javax.swing.plaf.nimbus.*;
import javax.swing.text.rtf.*;
import java.lang.management.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.prefs.*;
import java.util.zip.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IndriveGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private List<User> users;
    private List<Driver> drivers;

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(102, 126, 234);
    private final Color SECONDARY_COLOR = new Color(118, 75, 162);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);

    public IndriveGUI() {
        users = new ArrayList<>();
        drivers = new ArrayList<>();
        loadUsersFromFile();
        loadDriversFromFile();
        initializeGUI();


    }

    private void initializeGUI() {
        setTitle("BuckleUp - Ride Booking Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set up card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create pages
        mainPanel.add(createHomePage(), "HOME");
        mainPanel.add(createLoginPage(), "LOGIN");
        mainPanel.add(createRegisterPage(), "REGISTER");
        mainPanel.add(createUserDashboard(), "USER_DASHBOARD");
        mainPanel.add(createDriverDashboard(), "DRIVER_DASHBOARD");

        add(mainPanel);

        // Show home page initially
        cardLayout.show(mainPanel, "HOME");
    }

    private JPanel createHomePage() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 40, 50, 40));

        // Logo and title
        JLabel logoLabel = new JLabel("BUCKLEUP", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        logoLabel.setForeground(PRIMARY_COLOR);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Your Ride, Your Price", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton loginBtn = createStyledButton("Login", PRIMARY_COLOR);
        JButton registerBtn = createStyledButton("Register", SECONDARY_COLOR);
        JButton adminBtn = createStyledButton("Admin Access", new Color(220, 53, 69));

        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        adminBtn.addActionListener(e -> showAdminLogin());

        // Add components with spacing
        panel.add(logoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(adminBtn);

        return panel;
    }

    private JPanel createLoginPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User type selection
        JPanel userTypePanel = new JPanel(new GridLayout(1, 2, 5, 0));
        userTypePanel.setMaximumSize(new Dimension(400, 40));
        userTypePanel.setBackground(BACKGROUND_COLOR);

        ButtonGroup userTypeGroup = new ButtonGroup();
        JRadioButton userRadio = new JRadioButton("User", true);
        JRadioButton driverRadio = new JRadioButton("Driver");

        userTypeGroup.add(userRadio);
        userTypeGroup.add(driverRadio);
        userTypePanel.add(userRadio);
        userTypePanel.add(driverRadio);

        // Input fields
        JTextField usernameField = createStyledTextField("Username/Name");
        JPasswordField passwordField = createStyledPasswordField("Password");

        // Buttons
        JButton loginBtn = createStyledButton("Login", PRIMARY_COLOR);
        JButton backBtn = createStyledButton("← Back to Home", new Color(108, 117, 125));

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                showMessage("Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userRadio.isSelected()) {
                if (authenticateUser(username, password)) {
                    showMessage("Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "USER_DASHBOARD");
                } else {
                    showMessage("Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (authenticateDriver(username, password)) {
                    showMessage("Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "DRIVER_DASHBOARD");
                } else {
                    showMessage("Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        // Add components
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(userTypePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(backBtn);

        return panel;
    }

    private JPanel createRegisterPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Join Us Today", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User type selection
        JPanel userTypePanel = new JPanel(new GridLayout(1, 2, 5, 0));
        userTypePanel.setMaximumSize(new Dimension(400, 40));
        userTypePanel.setBackground(BACKGROUND_COLOR);

        ButtonGroup userTypeGroup = new ButtonGroup();
        JRadioButton userRadio = new JRadioButton("User", true);
        JRadioButton driverRadio = new JRadioButton("Driver");

        userTypeGroup.add(userRadio);
        userTypeGroup.add(driverRadio);
        userTypePanel.add(userRadio);
        userTypePanel.add(driverRadio);

        // Common fields
        JTextField usernameField = createStyledTextField("Username/Name");
        JPasswordField passwordField = createStyledPasswordField("Password");

        // Driver-specific fields
        JPanel driverFieldsPanel = new JPanel();
        driverFieldsPanel.setLayout(new BoxLayout(driverFieldsPanel, BoxLayout.Y_AXIS));
        driverFieldsPanel.setBackground(BACKGROUND_COLOR);
        driverFieldsPanel.setVisible(false);

        JTextField ageField = createStyledTextField("Age");
        JTextField genderField = createStyledTextField("Gender");
        JTextField carField = createStyledTextField("Car Name");
        JTextField regField = createStyledTextField("Car Registration");

        driverFieldsPanel.add(ageField);
        driverFieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        driverFieldsPanel.add(genderField);
        driverFieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        driverFieldsPanel.add(carField);
        driverFieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        driverFieldsPanel.add(regField);

        // Toggle driver fields visibility
        driverRadio.addActionListener(e -> driverFieldsPanel.setVisible(true));
        userRadio.addActionListener(e -> driverFieldsPanel.setVisible(false));

        // Buttons
        JButton registerBtn = createStyledButton("Register", PRIMARY_COLOR);
        JButton backBtn = createStyledButton("← Back to Home", new Color(108, 117, 125));

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                showMessage("Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userRadio.isSelected()) {
                if (registerUser(username, password)) {
                    showMessage("User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "LOGIN");
                } else {
                    showMessage("User already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String ageText = ageField.getText().trim();
                String gender = genderField.getText().trim();
                String car = carField.getText().trim();
                String reg = regField.getText().trim();

                if (ageText.isEmpty() || gender.isEmpty() || car.isEmpty() || reg.isEmpty()) {
                    showMessage("Please fill in all driver fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int age = Integer.parseInt(ageText);
                    if (registerDriver(username, age, gender, car, reg, password)) {
                        showMessage("Driver registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "LOGIN");
                    } else {
                        showMessage("Driver already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    showMessage("Please enter a valid age", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        // Add components
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(userTypePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(driverFieldsPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backBtn);

        return panel;
    }

    private JPanel createUserDashboard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("User Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton bookRideBtn = createStyledButton("Book Ride", PRIMARY_COLOR);
        JButton viewHistoryBtn = createStyledButton("View Ride History", SECONDARY_COLOR);
        JButton logoutBtn = createStyledButton("Logout", new Color(220, 53, 69));

        bookRideBtn.addActionListener(e -> showMessage("Book Ride feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE));
        viewHistoryBtn.addActionListener(e -> showMessage("View History feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE));
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(bookRideBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(viewHistoryBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(logoutBtn);

        return panel;
    }

    private JPanel createDriverDashboard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Driver Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton findRideBtn = createStyledButton("Find Ride", PRIMARY_COLOR);
        JButton logoutBtn = createStyledButton("Logout", new Color(220, 53, 69));

        findRideBtn.addActionListener(e -> showMessage("Find Ride feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE));
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(findRideBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(logoutBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(400, 40));
        field.setToolTipText(placeholder);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(400, 40));
        field.setToolTipText(placeholder);
        return field;
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void showAdminLogin() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Admin Login",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if ("admin".equals(username) && "admin123".equals(password)) {
                showMessage("Admin login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showAdminDashboard();
            } else {
                showMessage("Invalid admin credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAdminDashboard() {
        String[] options = {"Show Stats", "Show Users", "Show Drivers", "Show Rides", "Close"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(this,
                    "Select an option:",
                    "Admin Dashboard",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0: showStats(); break;
                case 1: showAllUsers(); break;
                case 2: showAllDrivers(); break;
                case 3: showAllRides(); break;
                default: return;
            }
        }
    }

    private void showStats() {
        int userCount = users.size();
        int driverCount = drivers.size();
        int rideCount = countRides();

        String stats = String.format("Total Users: %d\nTotal Drivers: %d\nTotal Rides: %d",
                userCount, driverCount, rideCount);
        showMessage(stats, "System Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAllUsers() {
        if (users.isEmpty()) {
            showMessage("No users found.", "Users", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder userList = new StringBuilder("Registered Users:\n\n");
        for (int i = 0; i < users.size(); i++) {
            userList.append((i + 1)).append(". ").append(users.get(i).getUsername()).append("\n");
        }

        JTextArea textArea = new JTextArea(userList.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "All Users", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAllDrivers() {
        if (drivers.isEmpty()) {
            showMessage("No drivers found.", "Drivers", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder driverList = new StringBuilder("Registered Drivers:\n\n");
        for (int i = 0; i < drivers.size(); i++) {
            Driver d = drivers.get(i);
            driverList.append((i + 1)).append(". Name: ").append(d.getName())
                    .append(", Age: ").append(d.getAge())
                    .append(", Car: ").append(d.getCar())
                    .append(", Reg: ").append(d.getReg()).append("\n");
        }

        JTextArea textArea = new JTextArea(driverList.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        JOptionPane.showMessageDialog(this, scrollPane, "All Drivers", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAllRides() {
        try (BufferedReader reader = new BufferedReader(new FileReader("rides.txt"))) {
            StringBuilder rides = new StringBuilder("Booked Rides:\n\n");
            String line;
            int rideNumber = 1;
            StringBuilder rideDetails = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.contains("--------------------------")) {
                    rides.append("Ride #").append(rideNumber++).append(":\n").append(rideDetails).append("\n");
                    rideDetails = new StringBuilder();
                } else {
                    rideDetails.append(line).append("\n");
                }
            }

            if (rides.length() <= 20) {
                showMessage("No rides found.", "Rides", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(rides.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(this, scrollPane, "All Rides", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            showMessage("No rides found.", "Rides", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private int countRides() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("rides.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("--------------------------")) {
                    count++;
                }
            }
        } catch (IOException e) {
            return 0;
        }
        return count;
    }

    private void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|");
                if (parts.length >= 2) {
                    User user = new User();
                    user.setUser(parts[0], parts[1]);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, which is fine
        }
    }

    private void loadDriversFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("drivers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|");
                if (parts.length == 6) {
                    try {
                        int age = Integer.parseInt(parts[1]);
                        Driver driver = new Driver();
                        driver.setDriver(parts[0], age, parts[2], parts[3], parts[4], parts[5]);
                        drivers.add(driver);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid age in driver record: " + parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, which is fine
        }
    }

    private boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.authenticate(password)) {
                return true;
            }
        }
        return false;
    }

    private boolean authenticateDriver(String name, String password) {
        for (Driver driver : drivers) {
            if (driver.getName().equals(name) && driver.authenticate(password)) {
                return true;
            }
        }
        return false;
    }

    private boolean registerUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false; // User already exists
            }
        }

        User newUser = new User();
        newUser.setUser(username, password);
        users.add(newUser);
        newUser.saveUser();
        return true;
    }

    private boolean registerDriver(String name, int age, String gender, String car, String reg, String password) {
        for (Driver driver : drivers) {
            if (driver.getName().equals(name)) {
                return false; // Driver already exists
            }
        }

        Driver newDriver = new Driver();
        newDriver.setDriver(name, age, gender, car, reg, password);
        drivers.add(newDriver);
        newDriver.saveDriver();
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new IndriveGUI().setVisible(true);
            DatabaseManager databaseManager = new DatabaseManager();

        });
    }
}
