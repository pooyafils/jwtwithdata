package com.example.demosecurity.config;

import java.util.Optional;

public interface ApplicationUserDao {
    public Optional<ApplicationUser> selectApplicationUserByUsername(String name);
}
