#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <sstream>
#include <cstdlib>
#include <ctime>
using namespace std;

class User {
    string username;
    string password;
public:          
    void setUser(const string& uname, const string& pwd) {
        username = uname;
        password = pwd;
    }

    string getUsername() const { return username; }
    string getPassword() const { return password; }

    void SaveUser() const {
        ofstream file("users.txt", ios::app);
        if (file.is_open()) {
            file << username << "||" << password << endl;
        }
    }

    bool authenticate(const string& pwd) const {
        return password == pwd;
    }
};

class Driver {
    string name;
    int age;
    string gender;
    string car;
    string reg;
    string pass;
public:
    void setDriver(const string& n, const int& a, const string& g, const string& c, const string& r, const string& p) {
        name = n;
        age = a;
        gender = g;
        car = c;
        reg = r;
        pass = p;
    }
    string getName() const { return name; }
    int getAge() const { return age; }
    string getGender() const { return gender; }
    string getCar() const { return car; }
    string getReg() const { return reg; }
    string getPass() const { return pass; }

    void SaveDriver() const {
        ofstream file("drivers.txt", ios::app);
        if (file.is_open()) {
            file << name << "||" << age << "||" << gender << "||" << car << "||" << reg << "||" << pass << endl;
        }
    }

    bool authenticate(const string& p) const {
        return pass == p;
    }
};

class Admin {
public:
    int countLines(const string& filename) const {
        ifstream file(filename);
        int count = 0;
        string line;
        while (getline(file, line)) {
            if (!line.empty()) ++count;
        }
        return count;
    }

    void showStats() const {
        int userCount = countLines("users.txt");
        int driverCount = countLines("drivers.txt");

        int rideSections = 0;
        ifstream rideFile("rides.txt");
        string line;
        while (getline(rideFile, line)) {
            if (line.find("--------------------------") != string::npos) {
                ++rideSections;
            }
        }

        cout << "\n--- Admin Dashboard ---\n";
        cout << "Total Users Registered: " << userCount << endl;
        cout << "Total Drivers Registered: " << driverCount << endl;
        cout << "Total Rides Booked: " << rideSections << endl;
    }

    void showUsers() const {
        ifstream file("users.txt");
        string line;
        cout << "\n--- Registered Users ---\n";
        int i = 1;
        while (getline(file, line)) {
            size_t delim = line.find("||");
            if (delim != string::npos) {
                cout << i++ << ". Username: " << line.substr(0, delim) << endl;
            }
        }
        if (i == 1) cout << "No users found.\n";
    }

    void showDrivers() const {
        ifstream file("drivers.txt");
        string line;
        cout << "\n--- Registered Drivers ---\n";
        int i = 1;
        while (getline(file, line)) {
            vector<string> fields;
            size_t start = 0, end;
            while ((end = line.find("||", start)) != string::npos) {
                fields.push_back(line.substr(start, end - start));
                start = end + 2;
            }
            fields.push_back(line.substr(start)); // last field

            if (fields.size() == 6) {
                cout << i++ << ". Name: " << fields[0]
                     << ", Age: " << fields[1]
                     << ", Gender: " << fields[2]
                     << ", Car: " << fields[3]
                     << ", Reg: " << fields[4] << endl;
            }
        }
        if (i == 1) cout << "No drivers found.\n";
    }

    void showRides() const {
        ifstream file("rides.txt");
        string line;
        cout << "\n--- Booked Rides ---\n";
        int rideNumber = 1;
        string rideDetails;
        while (getline(file, line)) {
            if (line.find("--------------------------") != string::npos) {
                cout << "\nRide #" << rideNumber++ << ":\n" << rideDetails;
                rideDetails.clear();
            } else {
                rideDetails += line + "\n";
            }
        }
        if (rideNumber == 1) cout << "No rides found.\n";
    }
};



class Ride {
    double fare = 0;
    double baseFare = 0;
    double perUnit = 0;
    vector<string> locations;
    int pickupChoice;
    int dropoffChoice;
    int ridechoice;
    string details;
    string selectedRideType;

    void writeToFile(const string& username) {
    ofstream outFile("rides.txt", ios::app);
    if (outFile.is_open()) {
        outFile << "User: " << username << "\n";
        outFile << selectedRideType << " Booked!\n";
        outFile << "Pickup: " << locations[pickupChoice - 1] << "\n";
        outFile << "Drop-off: " << locations[dropoffChoice - 1] << "\n";
        if (selectedRideType == "Courier") {
            outFile << "Details: " << details << "\n";
        }
        outFile << "Estimated Fare: " << fare << "/-\n";
        outFile << "--------------------------\n";
    }

    string userFile = username + "_rides.txt";
    ofstream userOut(userFile, ios::app);
    if (userOut.is_open()) {
        userOut << selectedRideType << " Booked!\n";
        userOut << "Pickup: " << locations[pickupChoice - 1] << "\n";
        userOut << "Drop-off: " << locations[dropoffChoice - 1] << "\n";
        if (selectedRideType == "Courier") {
            userOut << "Details: " << details << "\n";
        }
        userOut << "Estimated Fare: " << fare << "/-\n";
        userOut << "--------------------------\n";
    }
    
}


    void setFareRates(const string& rideType) {
        if (rideType == "Ride") {
            baseFare = 310;
            perUnit = 7;
        } else if (rideType == "Ride Mini") {
            baseFare = 240;
            perUnit = 6;
        } else if (rideType == "Ride A.C") {
            baseFare = 375;
            perUnit = 10;
        } else if (rideType == "Bike") {
            baseFare = 100;
            perUnit = 4;
        } else if (rideType == "Courier") {
            baseFare = 130;
            perUnit = 4;
        }
    }

    void calculateFare() {
        fare = baseFare + abs(dropoffChoice - pickupChoice) * perUnit;
    }

    void commonOutput(const string& rideType, const vector<Driver>& drivers, const string& username) {
    selectedRideType = rideType;
    setFareRates(rideType);
    calculateFare();

    cout << "\nRide Type: " << rideType << "\n";
    cout << "Pickup: " << locations[pickupChoice - 1] << endl;
    cout << "Drop-off: " << locations[dropoffChoice - 1] << endl;

    if (rideType == "Courier") {
        cin.ignore();
        cout << "Enter details: ";
        getline(cin, details);
        cout << "Details: " << details << endl;
    }

    cout << "Estimated Fare: " << fare << "/-" << endl;

    double minFare = fare * 0.85;
    cout << "Would you like to propose a lower fare? (min allowed: " << minFare << "/-): ";
    double newFare;
    cin >> newFare;
    if (newFare >= minFare && newFare <= fare) {
        fare = newFare;
        cout << "Fare adjusted to: " << fare << "/-" << endl;
    } else {
        cout << "Fare adjustment invalid. Original fare applied: " << fare << "/-\n";
    }
    int tip;
    char ch;
    cout<<"Do you want to give tip (y/n): "<<endl;
    cin>>ch;
    if(ch == 'Y' || ch == 'y'){
    	cout<<"Enter the amount of tip: "<<endl;
    	cin>>tip;
    	fare = tip + fare;
    	cout<<"Total Fare: "<<fare<<endl;
	}
	else{
		cout<<"Fare: "<<fare<<endl;
	} 
		

    vector<Driver> eligibleDrivers;
    if (rideType == "Bike") {
        for (const auto& d : drivers) {
            if (d.getCar() == "Bike" || d.getCar() == "bike")
                eligibleDrivers.push_back(d);
        }
    } else {
        eligibleDrivers = drivers;
    }

    if (!eligibleDrivers.empty()) {
        srand(time(0));
        int index = rand() % eligibleDrivers.size();
        const Driver& assignedDriver = eligibleDrivers[index];
        cout << "Driver assigned: " << assignedDriver.getName() << " | Vehicle: " << assignedDriver.getCar()
             << " | Reg: " << assignedDriver.getReg() << endl;
    } else {
        cout << "No available drivers to assign for this ride type.\n";
    }

    writeToFile(username);
}


public:
    Ride() {
        ifstream in("locations.txt");
        string loc;
        while (getline(in, loc)) {
            if (!loc.empty()) locations.push_back(loc);
        }
    }
    

    void bookRide(const vector<Driver>& drivers, const string& username) {
        if (locations.size() < 2) {
            cout << "Not enough locations available to book a ride.\n";
            return;
        }

        cout << "\nAvailable Locations:\n";
        for (size_t i = 0; i < locations.size(); ++i)
            cout << i + 1 << ". " << locations[i] << endl;

        cout << "\nChoose your pickup location (1-" << locations.size() << "): ";
        cin >> pickupChoice;

        cout << "Choose your drop-off location (1-" << locations.size() << "): ";
        cin >> dropoffChoice;

        if (pickupChoice < 1 || pickupChoice > locations.size() ||
            dropoffChoice < 1 || dropoffChoice > locations.size() ||
            pickupChoice == dropoffChoice) {
            cout << "Invalid pickup/drop-off choices.\n";
            return;
        }

        cout << "\n1. Ride\n2. Ride Mini\n3. Ride A.C\n4. Bike\n5. Courier\n";
        cout << "Select Ride type: ";
        cin >> ridechoice;

        switch (ridechoice) {
            case 1: commonOutput("Ride", drivers, username); break;
            case 2: commonOutput("Ride Mini", drivers, username); break;
            case 3: commonOutput("Ride A.C", drivers, username); break;
            case 4: commonOutput("Bike", drivers, username); break;
            case 5: commonOutput("Courier", drivers, username); break;
            default: cout << "Invalid Ride Type Selected.\n"; break;
        }
    }
    void viewRideHistory(const string& username) {
    string userFile = username + "_rides.txt";
    ifstream inFile(userFile);
    if (inFile.is_open()) {
        string line;
        cout << "\n--- Ride History ---\n";
        while (getline(inFile, line)) {
            cout << line << endl;
        }
    } else {
        cout << "No ride history found for user: " << username << endl;
    }
}

};

class Indrive {
    vector<User> users;
    User* currentuser = nullptr;
    vector<Driver> drivers;
    Driver* currentdriver = nullptr;

    void loadUsersFromFile() {
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

    void loadDriversFromFile() {
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


    User* findUser(const string& uname) {
        for (auto& u : users) {
            if (u.getUsername() == uname)
                return &u;
        }
        return nullptr;
    }

    Driver* findDriver(const string& name) {
        for (auto& d : drivers) {
            if (d.getName() == name)
                return &d;
        }
        return nullptr;
    }
    void assignRandomRide(const Driver& driver) {
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
    cout<<"Do you want to accept Ride? ";
    cin>>ch;
    if(ch== 'Y' || ch == 'y'){
    	cout<<"\nRide Assigned!\n Drive Safely :)\n";
	ofstream outFile("rides.txt", ios::app);
    if (outFile.is_open()) {
        outFile << "Driver: " << driver.getName() << "\n";
        outFile << "Pickup: " << pickup << "\n";
        outFile << "Drop-off: " << dropoff << "\n";
        outFile << "Estimated Fare: " << fare << "/-\n";
        outFile << "--------------------------\n";
    }}
    else 
    cout<<":(";
}



public:
    Indrive() {
        loadUsersFromFile();
        loadDriversFromFile();
    }

    void registerUser() {
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

    void loginUser() {
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
                case 2: {Ride r; r.viewRideHistory(currentuser->getUsername()); break;}
                case 3: {cout << "Logging out...\n"; break;}
                default: cout << "Invalid choice.\n"; break;
            }
        } while (choice != 3);
    } else {
        cout << "Login failed. Invalid credentials.\n";
    }
}



    void registerDriver() {
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
    
    void driverMenu() {
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

    
void adminMenu() {
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


    void menu() {
    int choice;
    do {
        cout << "\n--- BUCKLEUP! ---\n";
	cout << "1. Register User\n";
	cout << "2. Login and Book Ride\n";
	cout << "3. Register Driver\n";
	cout << "4. Driver Login\n"; // <-- Added
	cout << "5. Admin Login\n";
	cout << "6. Exit\n";
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
    } while (choice != 5);
}

};

int main() {
    Indrive app;
    app.menu();
    return 0;
}
