#include "Driver.h"
#include <fstream>
#include <iostream>

void Driver::setDriver(const string& n, const int& a, const string& g, const string& c, const string& r, const string& p) {
    name = n;
    age = a;
    gender = g;
    car = c;
    reg = r;
    pass = p;
}

string Driver::getName() const { 
    return name; 
}

int Driver::getAge() const { 
    return age; 
}

string Driver::getGender() const { 
    return gender; 
}

string Driver::getCar() const { 
    return car; 
}

string Driver::getReg() const { 
    return reg; 
}

string Driver::getPass() const { 
    return pass; 
}

void Driver::SaveDriver() const {
    ofstream file("drivers.txt", ios::app);
    if (file.is_open()) {
        file << name << "||" << age << "||" << gender << "||" << car << "||" << reg << "||" << pass << endl;
    }
}

bool Driver::authenticate(const string& p) const {
    return pass == p;
}