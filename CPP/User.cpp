#include "User.h"
#include <fstream>
#include <iostream>

void User::setUser(const string& uname, const string& pwd) {
    username = uname;
    password = pwd;
}

string User::getUsername() const { 
    return username; 
}

string User::getPassword() const { 
    return password; 
}

void User::SaveUser() const {
    ofstream file("users.txt", ios::app);
    if (file.is_open()) {
        file << username << "||" << password << endl;
    }
}

bool User::authenticate(const string& pwd) const {
    return password == pwd;
}