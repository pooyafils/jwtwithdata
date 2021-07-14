package com.example.demosecurity.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import static com.example.demosecurity.config.ApplicationUserRole.*;


@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUsers() {
        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser(
                        "user",
                        passwordEncoder.encode("password"),
                        USER.grantedAuthoritySet(),
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "pass",
                        passwordEncoder.encode("pass"),
                        ADMIN.grantedAuthoritySet(),
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "tom",
                        passwordEncoder.encode("pass"),
                        ADMINTRAINER.grantedAuthoritySet(),
                        true,
                        true,
                        true,
                        true
                )
        );

        return applicationUsers;
    }

}
