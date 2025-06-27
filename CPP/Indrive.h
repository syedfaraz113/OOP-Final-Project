#ifndef INDRIVE_H
#define INDRIVE_H

#include <vector>
#include "User.h"
#include "Driver.h"
using namespace std;

class Indrive {
private:
    vector<User> users;
    User* currentuser;
    vector<Driver> drivers;
    Driver* currentdriver;

    void loadUsersFromFile();
    void loadDriversFromFile();
    User* findUser(const string& uname);
    Driver* findDriver(const string& name);
    void assignRandomRide(const Driver& driver);

public:
    Indrive();
    void registerUser();
    void loginUser();
    void registerDriver();
    void driverMenu();
    void adminMenu();
    void menu();
};

#endif