import java.io.*;
import java.util.*;
import java.util.Random;

class User {
    private String username;
    private String password;

    public void setUser(String uname, String pwd) {
        username = uname;
        password = pwd;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void saveUser() {
        try (PrintWriter file = new PrintWriter(new FileWriter("users.txt", true))) {
            file.println(username + "||" + password);
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    public boolean authenticate(String pwd) {
        return password.equals(pwd);
    }
}

class Driver {
    private String name;
    private int age;
    private String gender;
    private String car;
    private String reg;
    private String pass;

    public void setDriver(String n, int a, String g, String c, String r, String p) {
        name = n;
        age = a;
        gender = g;
        car = c;
        reg = r;
        pass = p;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getCar() { return car; }
    public String getReg() { return reg; }
    public String getPass() { return pass; }

    public void saveDriver() {
        try (PrintWriter file = new PrintWriter(new FileWriter("drivers.txt", true))) {
            file.println(name + "||" + age + "||" + gender + "||" + car + "||" + reg + "||" + pass);
        } catch (IOException e) {
            System.out.println("Error saving driver: " + e.getMessage());
        }
    }

    public boolean authenticate(String p) {
        return pass.equals(p);
    }
}

class Admin {
    public int countLines(String filename) {
        int count = 0;
        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            while (file.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            return 0;
        }
        return count;
    }

    public void showStats() {
        int userCount = countLines("users.txt");
        int driverCount = countLines("drivers.txt");

        int rideSections = 0;
        try (BufferedReader rideFile = new BufferedReader(new FileReader("rides.txt"))) {
            String line;
            while ((line = rideFile.readLine()) != null) {
                if (line.contains("--------------------------")) {
                    rideSections++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading rides file: " + e.getMessage());
        }

        System.out.println("\n--- Admin Dashboard ---");
        System.out.println("Total Users Registered: " + userCount);
        System.out.println("Total Drivers Registered: " + driverCount);
        System.out.println("Total Rides Booked: " + rideSections);
    }

    public void showUsers() {
        System.out.println("\n--- Registered Users ---");
        int i = 1;
        try (BufferedReader file = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = file.readLine()) != null) {
                String[] parts = line.split("\\|\\|");
                if (parts.length >= 2) {
                    System.out.println(i++ + ". Username: " + parts[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }
        if (i == 1) System.out.println("No users found.");
    }

    public void showDrivers() {
        System.out.println("\n--- Registered Drivers ---");
        int i = 1;
        try (BufferedReader file = new BufferedReader(new FileReader("drivers.txt"))) {
            String line;
            while ((line = file.readLine()) != null) {
                String[] fields = line.split("\\|\\|");
                if (fields.length == 6) {
                    System.out.println(i++ + ". Name: " + fields[0] +
                            ", Age: " + fields[1] +
                            ", Gender: " + fields[2] +
                            ", Car: " + fields[3] +
                            ", Reg: " + fields[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading drivers file: " + e.getMessage());
        }
        if (i == 1) System.out.println("No drivers found.");
    }

    public void showRides() {
        System.out.println("\n--- Booked Rides ---");
        try (BufferedReader file = new BufferedReader(new FileReader("rides.txt"))) {
            String line;
            int rideNumber = 1;
            StringBuilder rideDetails = new StringBuilder();
            while ((line = file.readLine()) != null) {
                if (line.contains("--------------------------")) {
                    System.out.println("\nRide #" + rideNumber++ + ":\n" + rideDetails);
                    rideDetails = new StringBuilder();
                } else {
                    rideDetails.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading rides file: " + e.getMessage());
        }
    }
}

class Ride {
    private double fare = 0;
    private double baseFare = 0;
    private double perUnit = 0;
    private List<String> locations = new ArrayList<>();
    private int pickupChoice;
    private int dropoffChoice;
    private int rideChoice;
    private String details;
    private String selectedRideType;

    public Ride() {
        try (BufferedReader in = new BufferedReader(new FileReader("locations.txt"))) {
            String loc;
            while ((loc = in.readLine()) != null) {
                if (!loc.isEmpty()) locations.add(loc);
            }
        } catch (IOException e) {
            System.out.println("Error reading locations file: " + e.getMessage());
        }
    }

    private void writeToFile(String username) {
        try (PrintWriter outFile = new PrintWriter(new FileWriter("rides.txt", true))) {
            outFile.println("User: " + username);
            outFile.println(selectedRideType + " Booked!");
            outFile.println("Pickup: " + locations.get(pickupChoice - 1));
            outFile.println("Drop-off: " + locations.get(dropoffChoice - 1));
            if (selectedRideType.equals("Courier")) {
                outFile.println("Details: " + details);
            }
            outFile.println("Estimated Fare: " + fare + "/-");
            outFile.println("--------------------------");
        } catch (IOException e) {
            System.out.println("Error writing to rides file: " + e.getMessage());
        }

        String userFile = username + "_rides.txt";
        try (PrintWriter userOut = new PrintWriter(new FileWriter(userFile, true))) {
            userOut.println(selectedRideType + " Booked!");
            userOut.println("Pickup: " + locations.get(pickupChoice - 1));
            userOut.println("Drop-off: " + locations.get(dropoffChoice - 1));
            if (selectedRideType.equals("Courier")) {
                userOut.println("Details: " + details);
            }
            userOut.println("Estimated Fare: " + fare + "/-");
            userOut.println("--------------------------");
        } catch (IOException e) {
            System.out.println("Error writing user ride history: " + e.getMessage());
        }
    }

    private void setFareRates(String rideType) {
        switch (rideType) {
            case "Ride":
                baseFare = 310;
                perUnit = 7;
                break;
            case "Ride Mini":
                baseFare = 240;
                perUnit = 6;
                break;
            case "Ride A.C":
                baseFare = 375;
                perUnit = 10;
                break;
            case "Bike":
                baseFare = 100;
                perUnit = 4;
                break;
            case "Courier":
                baseFare = 130;
                perUnit = 4;
                break;
        }
    }

    private void calculateFare() {
        fare = baseFare + Math.abs(dropoffChoice - pickupChoice) * perUnit;
    }

    private void commonOutput(String rideType, List<Driver> drivers, String username) {
        selectedRideType = rideType;
        setFareRates(rideType);
        calculateFare();

        System.out.println("\nRide Type: " + rideType);
        System.out.println("Pickup: " + locations.get(pickupChoice - 1));
        System.out.println("Drop-off: " + locations.get(dropoffChoice - 1));

        if (rideType.equals("Courier")) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter details: ");
            details = scanner.nextLine();
            System.out.println("Details: " + details);
        }

        System.out.println("Estimated Fare: " + fare + "/-");

        double minFare = fare * 0.85;
        System.out.print("Would you like to propose a lower fare? (min allowed: " + minFare + "/-): ");
        Scanner scanner = new Scanner(System.in);
        double newFare = scanner.nextDouble();
        if (newFare >= minFare && newFare <= fare) {
            fare = newFare;
            System.out.println("Fare adjusted to: " + fare + "/-");
        } else {
            System.out.println("Fare adjustment invalid. Original fare applied: " + fare + "/-");
        }

        System.out.print("Do you want to give tip (y/n): ");
        char ch = scanner.next().charAt(0);
        if (ch == 'Y' || ch == 'y') {
            System.out.print("Enter the amount of tip: ");
            int tip = scanner.nextInt();
            fare += tip;
            System.out.println("Total Fare: " + fare);
        } else {
            System.out.println("Fare: " + fare);
        }

        List<Driver> eligibleDrivers = new ArrayList<>();
        if (rideType.equals("Bike")) {
            for (Driver d : drivers) {
                if (d.getCar().equalsIgnoreCase("Bike")) {
                    eligibleDrivers.add(d);
                }
            }
        } else {
            eligibleDrivers.addAll(drivers);
        }

        if (!eligibleDrivers.isEmpty()) {
            Random rand = new Random();
            int index = rand.nextInt(eligibleDrivers.size());
            Driver assignedDriver = eligibleDrivers.get(index);
            System.out.println("Driver assigned: " + assignedDriver.getName() +
                    " | Vehicle: " + assignedDriver.getCar() +
                    " | Reg: " + assignedDriver.getReg());
        } else {
            System.out.println("No available drivers to assign for this ride type.");
        }

        writeToFile(username);
    }

    public void bookRide(List<Driver> drivers, String username) {
        if (locations.size() < 2) {
            System.out.println("Not enough locations available to book a ride.");
            return;
        }

        System.out.println("\nAvailable Locations:");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ". " + locations.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nChoose your pickup location (1-" + locations.size() + "): ");
        pickupChoice = scanner.nextInt();

        System.out.print("Choose your drop-off location (1-" + locations.size() + "): ");
        dropoffChoice = scanner.nextInt();

        if (pickupChoice < 1 || pickupChoice > locations.size() ||
                dropoffChoice < 1 || dropoffChoice > locations.size() ||
                pickupChoice == dropoffChoice) {
            System.out.println("Invalid pickup/drop-off choices.");
            return;
        }

        System.out.println("\n1. Ride\n2. Ride Mini\n3. Ride A.C\n4. Bike\n5. Courier");
        System.out.print("Select Ride type: ");
        rideChoice = scanner.nextInt();

        switch (rideChoice) {
            case 1: commonOutput("Ride", drivers, username); break;
            case 2: commonOutput("Ride Mini", drivers, username); break;
            case 3: commonOutput("Ride A.C", drivers, username); break;
            case 4: commonOutput("Bike", drivers, username); break;
            case 5: commonOutput("Courier", drivers, username); break;
            default: System.out.println("Invalid Ride Type Selected."); break;
        }
    }

    public void viewRideHistory(String username) {
        String userFile = username + "_rides.txt";
        try (BufferedReader inFile = new BufferedReader(new FileReader(userFile))) {
            String line;
            System.out.println("\n--- Ride History ---");
            while ((line = inFile.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("No ride history found for user: " + username);
        }
    }
}

class Indrive {
    private List<User> users = new ArrayList<>();
    private User currentUser = null;
    private List<Driver> drivers = new ArrayList<>();
    private Driver currentDriver = null;

    public Indrive() {
        loadUsersFromFile();
        loadDriversFromFile();
    }

    private void loadUsersFromFile() {
        try (BufferedReader file = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = file.readLine()) != null) {
                String[] parts = line.split("\\|\\|");
                if (parts.length >= 2) {
                    User u = new User();
                    u.setUser(parts[0], parts[1]);
                    users.add(u);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void loadDriversFromFile() {
        try (BufferedReader file = new BufferedReader(new FileReader("drivers.txt"))) {
            String line;
            while ((line = file.readLine()) != null) {
                String[] fields = line.split("\\|\\|");
                if (fields.length == 6) {
                    try {
                        int age = Integer.parseInt(fields[1]);
                        Driver d = new Driver();
                        d.setDriver(fields[0], age, fields[2], fields[3], fields[4], fields[5]);
                        drivers.add(d);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid age value in driver record: " + fields[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading drivers: " + e.getMessage());
        }
    }

    private User findUser(String uname) {
        for (User u : users) {
            if (u.getUsername().equals(uname)) {
                return u;
            }
        }
        return null;
    }

    private Driver findDriver(String name) {
        for (Driver d : drivers) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        return null;
    }

    private void assignRandomRide(Driver driver) {
        List<String> locations = new ArrayList<>();
        try (BufferedReader locFile = new BufferedReader(new FileReader("locations.txt"))) {
            String loc;
            while ((loc = locFile.readLine()) != null) {
                if (!loc.isEmpty()) locations.add(loc);
            }
        } catch (IOException e) {
            System.out.println("Error reading locations: " + e.getMessage());
        }

        if (locations.size() < 2) {
            System.out.println("Not enough locations available to assign a ride.");
            return;
        }

        Random rand = new Random();
        int pickupIndex = rand.nextInt(locations.size());
        int dropoffIndex;
        do {
            dropoffIndex = rand.nextInt(locations.size());
        } while (dropoffIndex == pickupIndex);

        String pickup = locations.get(pickupIndex);
        String dropoff = locations.get(dropoffIndex);

        double baseFare = 250;
        double perUnit = 7;
        double fare = baseFare + Math.abs(dropoffIndex - pickupIndex) * perUnit;

        System.out.println("Pickup: " + pickup);
        System.out.println("Drop-off: " + dropoff);
        System.out.println("Estimated Fare: " + fare + "/-");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to accept Ride? (y/n): ");
        char ch = scanner.next().charAt(0);

        if (ch == 'Y' || ch == 'y') {
            System.out.println("\nRide Assigned!\nDrive Safely :)");
            try (PrintWriter outFile = new PrintWriter(new FileWriter("rides.txt", true))) {
                outFile.println("Driver: " + driver.getName());
                outFile.println("Pickup: " + pickup);
                outFile.println("Drop-off: " + dropoff);
                outFile.println("Estimated Fare: " + fare + "/-");
                outFile.println("--------------------------");
            } catch (IOException e) {
                System.out.println("Error saving ride: " + e.getMessage());
            }
        } else {
            System.out.println(":(");
        }
    }

    public void registerUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter Username: ");
        String uname = scanner.nextLine();
        System.out.print("Enter Password: ");
        String pwd = scanner.nextLine();

        if (findUser(uname) != null) {
            System.out.println("User already exists!");
            return;
        }

        User u = new User();
        u.setUser(uname, pwd);
        users.add(u);
        u.saveUser();
        System.out.println("User registered successfully.");
    }

    public void loginUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String uname = scanner.nextLine();
        System.out.print("Password: ");
        String pwd = scanner.nextLine();

        User user = findUser(uname);
        if (user != null && user.authenticate(pwd)) {
            currentUser = user;
            System.out.println("Login successful.");

            int choice;
            do {
                System.out.println("\n--- User Panel ---");
                System.out.println("1. Book Ride");
                System.out.println("2. View Ride History");
                System.out.println("3. Logout");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1: {
                        Ride r = new Ride();
                        r.bookRide(drivers, currentUser.getUsername());
                        break;
                    }
                    case 2: {
                        Ride r = new Ride();
                        r.viewRideHistory(currentUser.getUsername());
                        break;
                    }
                    case 3: System.out.println("Logging out..."); break;
                    default: System.out.println("Invalid choice."); break;
                }
            } while (choice != 3);
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    public void registerDriver() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter Your Name: ");
        String n = scanner.nextLine();
        System.out.print("Enter Your Age: ");
        int a = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter Your Gender: ");
        String g = scanner.nextLine();
        System.out.print("Enter Your Car Name: ");
        String c = scanner.nextLine();
        System.out.print("Enter Your Car Registration: ");
        String r = scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String p = scanner.nextLine();

        if (findDriver(n) != null) {
            System.out.println("Driver already exists!");
            return;
        }

        Driver d = new Driver();
        d.setDriver(n, a, g, c, r, p);
        drivers.add(d);
        d.saveDriver();
        System.out.println("Driver registered successfully.");
    }

    public void driverMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Driver Login ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

        Driver driver = findDriver(name);
        if (driver != null && driver.authenticate(pass)) {
            currentDriver = driver;
            System.out.println("Login successful.");

            int choice;
            do {
                System.out.println("\n--- Driver Panel ---");
                System.out.println("1. Find Ride");
                System.out.println("2. Logout");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1: assignRandomRide(driver); break;
                    case 2: System.out.println("Logging out..."); break;
                    default: System.out.println("Invalid choice."); break;
                }
            } while (choice != 2);
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    public void adminMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Admin Login ---");
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if (username.equals("admin") && password.equals("admin123")) {
            Admin admin = new Admin();
            int choice;
            do {
                System.out.println("\n--- Admin Panel ---");
                System.out.println("1. Show Stats");
                System.out.println("2. Show All Users");
                System.out.println("3. Show All Drivers");
                System.out.println("4. Show All Rides");
                System.out.println("5. Back to Main Menu");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1: admin.showStats(); break;
                    case 2: admin.showUsers(); break;
                    case 3: admin.showDrivers(); break;
                    case 4: admin.showRides(); break;
                    case 5: System.out.println("Returning to main menu..."); break;
                    default: System.out.println("Invalid choice."); break;
                }
            } while (choice != 5);
        } else {
            System.out.println("Invalid admin credentials!");
        }
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n--- BUCKLEUP ---");
            System.out.println("1. Register User");
            System.out.println("2. Login and Book Ride");
            System.out.println("3. Register Driver");
            System.out.println("4. Driver Login");
            System.out.println("5. Admin Login");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1: registerUser(); break;
                case 2: loginUser(); break;
                case 3: registerDriver(); break;
                case 4: driverMenu(); break;
                case 5: adminMenu(); break;
                case 6: System.out.println("Exiting..."); break;
                default: System.out.println("Invalid choice."); break;
            }
        } while (choice != 6);
    }
}

public class Main {
    public static void main(String[] args) {

        try {
            DatabaseManager.initializeDatabase();
            new IndriveGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IndriveGUI.main(args);
    }
}



