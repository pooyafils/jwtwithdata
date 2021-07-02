package com.example.demosecurity.repository;

import com.example.demosecurity.model.PersonInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<PersonInfo, Integer> {
    PersonInfo findById(int id);

}
