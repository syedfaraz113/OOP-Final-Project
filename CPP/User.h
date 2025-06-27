#ifndef USER_H
#define USER_H

#include <string>
using namespace std;

class User {
private:
    string username;
    string password;

public:
    void setUser(const string& uname, const string& pwd);
    string getUsername() const;
    string getPassword() const;
    void SaveUser() const;
    bool authenticate(const string& pwd) const;
};

#endif