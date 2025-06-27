#include "Admin.h"
#include <fstream>
#include <iostream>
#include <vector>

int Admin::countLines(const string& filename) const {
    ifstream file(filename);
    int count = 0;
    string line;
    while (getline(file, line)) {
        if (!line.empty()) ++count;
    }
    return count;
}

void Admin::showStats() const {
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

void Admin::showUsers() const {
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

void Admin::showDrivers() const {
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

void Admin::showRides() const {
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