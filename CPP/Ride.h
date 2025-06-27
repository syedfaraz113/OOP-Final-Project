#ifndef RIDE_H
#define RIDE_H

#include <string>
#include <vector>
#include "Driver.h"
using namespace std;

class Ride {
private:
    double fare;
    double baseFare;
    double perUnit;
    vector<string> locations;
    int pickupChoice;
    int dropoffChoice;
    int ridechoice;
    string details;
    string selectedRideType;

    void writeToFile(const string& username);
    void setFareRates(const string& rideType);
    void calculateFare();
    void commonOutput(const string& rideType, const vector<Driver>& drivers, const string& username);

public:
    Ride();
    void bookRide(const vector<Driver>& drivers, const string& username);
    void viewRideHistory(const string& username);
};

#endif