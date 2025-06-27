#include "Indrive.h"
#include "Admin.h"
#include "Ride.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <cstdlib>
#include <ctime>
#include <cmath>

Indrive::Indrive() : currentuser(nullptr), currentdriver(nullptr) {
    loadUsersFromFile();
    loadDriversFromFile();
}

void Indrive::loadUsersFromFile() {
    ifstream file("users.txt");
    string line;
    while (getline(file, line)) {
        size_t delimiter = line.find("||");
        if (delimiter != string::npos) {
            string uname = line.substr(0, delimiter);
            string pwd = line.substr(delimiter + 2);
            User u;
            u.setUser(uname, pwd);
            users.push_back(u);
        }
    }
}

void Indrive::loadDriversFromFile() {
    ifstream file("drivers.txt");
    string line;
    while (getline(file, line)) {
        vector<string> fields;
        size_t start = 0, end;
        while ((end = line.find("||", start)) != string::npos) {
            fields.push_back(line.substr(start, end - start));
            start = end + 2;
        }
        fields.push_back(line.substr(start));  // last field

        if (fields.size() == 6) {
            try {
                int age = stoi(fields[1]);
                Driver d;
                d.setDriver(fields[0], age, fields[2], fields[3], fields[4], fields[5]);
                drivers.push_back(d);
            } catch (const std::invalid_argument& e) {
                cerr << "Invalid age value in driver record: " << fields[1] << endl;
            }
        }
    }
}

User* Indrive::findUser(const string& uname) {
    for (auto& u : users) {
        if (u.getUsername() == uname)
            return &u;
    }
    return nullptr;
}

Driver* Indrive::findDriver(const string& name) {
    for (auto& d : drivers) {
        if (d.getName() == name)
            return &d;
    }
    return nullptr;
}

void Indrive::assignRandomRide(const Driver& driver) {
    ifstream locFile("locations.txt");
    vector<string> locations;
    string loc;
    while (getline(locFile, loc)) {
        if (!loc.empty()) locations.push_back(loc);
    }

    if (locations.size() < 2) {
        cout << "Not enough locations available to assign a ride.\n";
        return;
    }

    srand(time(0));
    int pickupIndex = rand() % locations.size();
    int dropoffIndex;
    do {
        dropoffIndex = rand() % locations.size();
    } while (dropoffIndex == pickupIndex);

    string pickup = locations[pickupIndex];
    string dropoff = locations[dropoffIndex];

    double baseFare = 250;
    double perUnit = 7;
    double fare = baseFare + abs(dropoffIndex - pickupIndex) * perUnit;
    char ch;

    cout << "Pickup: " << pickup << endl;
    cout << "Drop-off: " << dropoff << endl;
    cout << "Estimated Fare: " << fare << "/-\n";
    cout << "Do you want to accept Ride? ";
    cin >> ch;
    if (ch == 'Y' || ch == 'y') {
        cout << "\nRide Assigned!\n Drive Safely :)\n";
        ofstream outFile("rides.txt", ios::app);
        if (outFile.is_open()) {
            outFile << "Driver: " << driver.getName() << "\n";
            outFile << "Pickup: " << pickup << "\n";
            outFile << "Drop-off: " << dropoff << "\n";
            outFile << "Estimated Fare: " << fare << "/-\n";
            outFile << "--------------------------\n";
        }
    } else {
        cout << ":(\n";
    }
}

void Indrive::registerUser() {
    string uname, pwd;
    cout << "\nEnter Username: ";
    cin >> uname;
    cout << "Enter Password: ";
    cin >> pwd;

    if (findUser(uname)) {
        cout << "User already exists!\n";
        return;
    }

    User u;
    u.setUser(uname, pwd);
    users.push_back(u);
    u.SaveUser();
    cout << "User registered successfully.\n";
}

void Indrive::loginUser() {
    string uname, pwd;
    cout << "Username: ";
    cin >> uname;
    cout << "Password: ";
    cin >> pwd;

    User* user = findUser(uname);
    if (user && user->authenticate(pwd)) {
        currentuser = user;
        cout << "Login successful.\n";

        int choice;
        do {
            cout << "\n--- User Panel ---\n";
            cout << "1. Book Ride\n";
            cout << "2. View Ride History\n";
            cout << "3. Logout\n";
            cout << "Enter your choice: ";
            cin >> choice;

            switch (choice) {
                case 1: {
                    Ride r;
                    r.bookRide(drivers, currentuser->getUsername());
                    break;
                }
                case 2: {
                    Ride r; 
                    r.viewRideHistory(currentuser->getUsername()); 
                    break;
                }
                case 3: {
                    cout << "Logging out...\n"; 
                    break;
                }
                default: 
                    cout << "Invalid choice.\n"; 
                    break;
            }
        } while (choice != 3);
    } else {
        cout << "Login failed. Invalid credentials.\n";
    }
}

void Indrive::registerDriver() {
    string n, g, c, r, p;
    int a;
    cout << "\nEnter Your Name: ";
    cin >> n;
    cout << "Enter Your Age: ";
    cin >> a;
    cout << "Enter Your Gender: ";
    cin >> g;
    cout << "Enter Your Car Name: ";
    cin >> c;
    cout << "Enter Your Car Registration: ";
    cin >> r;
    cout << "Enter Your Password: ";
    cin >> p;

    if (findDriver(n)) {
        cout << "Driver already exists!\n";
        return;
    }

    Driver d;
    d.setDriver(n, a, g, c, r, p);
    drivers.push_back(d);
    d.SaveDriver();
    cout << "Driver registered successfully.\n";
}

void Indrive::driverMenu() {
    string name, pass;
    cout << "\n--- Driver Login ---\n";
    cout << "Enter Name: ";
    cin >> name;
    cout << "Enter Password: ";
    cin >> pass;

    Driver* driver = findDriver(name);
    if (driver && driver->authenticate(pass)) {
        currentdriver = driver;
        cout << "Login successful.\n";

        int choice;
        do {
            cout << "\n--- Driver Panel ---\n";
            cout << "1. Find Ride\n";
            cout << "2. Logout\n";
            cout << "Enter your choice: ";
            cin >> choice;

            switch (choice) {
                case 1: assignRandomRide(*driver); break;
                case 2: cout << "Logging out...\n"; break;
                default: cout << "Invalid choice.\n"; break;
            }
        } while (choice != 2);
    } else {
        cout << "Login failed. Invalid credentials.\n";
    }
}

void Indrive::adminMenu() {
    string username, password;
    cout << "\n--- Admin Login ---\n";
    cout << "Enter admin username: ";
    cin >> username;
    cout << "Enter admin password: ";
    cin >> password;

    if (username == "admin" && password == "admin123") {
        Admin admin;
        int choice;
        do {
            cout << "\n--- Admin Panel ---\n";
            cout << "1. Show Stats\n";
            cout << "2. Show All Users\n";
            cout << "3. Show All Drivers\n";
            cout << "4. Show All Rides\n";
            cout << "5. Back to Main Menu\n";
            cout << "Enter your choice: ";
            cin >> choice;

            switch (choice) {
                case 1: admin.showStats(); break;
                case 2: admin.showUsers(); break;
                case 3: admin.showDrivers(); break;
                case 4: admin.showRides(); break;
                case 5: cout << "Returning to main menu...\n"; break;
                default: cout << "Invalid choice.\n"; break;
            }
        } while (choice != 5);
    } else {
        cout << "Invalid admin credentials!\n";
    }
}

void Indrive::menu() {
    int choice;
    do {
        cout << "\n--- BUCKLEUP! ---\n";
        cout << "1. Register User\n";
        cout << "2. Login and Book Ride\n";
        cout << "3. Register Driver\n";
        cout << "4. Driver Login\n";
        cout << "5. Admin Login\n";
        cout << "6. Exit\n";
        cout << "Enter your choice: ";
        cin >> choice;

        switch (choice) {
            case 1: registerUser(); break;
            case 2: loginUser(); break;
            case 3: registerDriver(); break;
            case 4: driverMenu(); break;
            case 5: adminMenu(); break;
            case 6: cout << "Exiting...\n"; break;
            default: cout << "Invalid choice.\n"; break;
        }
    } while (choice != 6);
}