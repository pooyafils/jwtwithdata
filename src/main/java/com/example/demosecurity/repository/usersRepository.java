package com.example.demosecurity.repository;

import com.example.demosecurity.model.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface usersRepository  extends JpaRepository< users,Integer> {
    users findByUsername(String username);
}
