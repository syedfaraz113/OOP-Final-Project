#ifndef DRIVER_H
#define DRIVER_H

#include <string>
using namespace std;

class Driver {
private:
    string name;
    int age;
    string gender;
    string car;
    string reg;
    string pass;

public:
    void setDriver(const string& n, const int& a, const string& g, const string& c, const string& r, const string& p);
    string getName() const;
    int getAge() const;
    string getGender() const;
    string getCar() const;
    string getReg() const;
    string getPass() const;
    void SaveDriver() const;
    bool authenticate(const string& p) const;
};

#endif