#include "Ride.h"
#include <iostream>
#include <fstream>
#include <cstdlib>
#include <ctime>
#include <cmath>

Ride::Ride() : fare(0), baseFare(0), perUnit(0), pickupChoice(0), dropoffChoice(0), ridechoice(0) {
    ifstream in("locations.txt");
    string loc;
    while (getline(in, loc)) {
        if (!loc.empty()) locations.push_back(loc);
    }
}

void Ride::writeToFile(const string& username) {
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

void Ride::setFareRates(const string& rideType) {
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

void Ride::calculateFare() {
    fare = baseFare + abs(dropoffChoice - pickupChoice) * perUnit;
}

void Ride::commonOutput(const string& rideType, const vector<Driver>& drivers, const string& username) {
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
    cout << "Do you want to give tip (y/n): " << endl;
    cin >> ch;
    if (ch == 'Y' || ch == 'y') {
        cout << "Enter the amount of tip: " << endl;
        cin >> tip;
        fare = tip + fare;
        cout << "Total Fare: " << fare << endl;
    } else {
        cout << "Fare: " << fare << endl;
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

void Ride::bookRide(const vector<Driver>& drivers, const string& username) {
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

void Ride::viewRideHistory(const string& username) {
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