#ifndef ADMIN_H
#define ADMIN_H

#include <string>
using namespace std;

class Admin {
private:
    int countLines(const string& filename) const;

public:
    void showStats() const;
    void showUsers() const;
    void showDrivers() const;
    void showRides() const;
};

#endif