package com.example.demosecurity.controller;

import com.example.demosecurity.model.PersonInfo;
import com.example.demosecurity.model.Users;
import com.example.demosecurity.repository.PersonRepository;
import com.example.demosecurity.repository.UsersRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api( description = "this is person-info")
@RestController
@RequestMapping("/person")
public class PersonController {
PersonRepository personRepository;
    UsersRepository userRpository;

    public PersonController(PersonRepository personRepository, UsersRepository userRpository) {
        this.personRepository = personRepository;
        this.userRpository = userRpository;
    }

    @ApiOperation(value = "this is method for getting person-info")
    @GetMapping
    public ResponseEntity getAllPersons() {
        return ResponseEntity.ok(personRepository.findAll());
    }
    @ApiOperation(value = "getting person-info by id")
    @GetMapping("/{id}")
    public ResponseEntity getPersonById (@PathVariable int id){
        return  ResponseEntity.ok(personRepository.findById(id));
    }
    @ApiOperation(value = "save student info")
    @PostMapping("/myt")

    public  ResponseEntity savePersonInfo(@Valid @RequestBody PersonInfo personInfo, BindingResult bindingResult){
        System.out.println(bindingResult.hasErrors()+"error field");
        if (bindingResult.hasErrors()){
            return ResponseEntity.ok(bindingResult.getFieldError());
        }
        else {
        //  return ResponseEntity.ok(personRepository.save(personInfo));
            personRepository.save(personInfo);
            return ResponseEntity.ok(personRepository.findAll());

        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity deletePerson(@PathVariable int id){
        PersonInfo delete=personRepository.findById(id);
        personRepository.delete(delete);
        return ResponseEntity.ok(personRepository.findAll());
    }
    @PutMapping("/{id}")
public ResponseEntity edit(@PathVariable int id,@RequestBody PersonInfo personInfo){
        PersonInfo personInfo1=personRepository.findById(id);
        personInfo1.setName(personInfo.getName());
        personRepository.save(personInfo1);
        return ResponseEntity.ok(personRepository.findAll());

}

    @ApiOperation(value = "save student info")
    @PostMapping("/register")

    public  ResponseEntity registerPerson(@Valid @RequestBody PersonInfo personInfo, BindingResult bindingResult){
        System.out.println(bindingResult.hasErrors()+"error field");
        if (bindingResult.hasErrors()){
            return ResponseEntity.ok(bindingResult.getFieldError());
        }
        else {
            //  return ResponseEntity.ok(personRepository.save(personInfo));
            personRepository.save(personInfo);
            return ResponseEntity.ok(personRepository.findAll());

        }

    }
    @DeleteMapping("deleteStudent/{id}")
    public ResponseEntity deleteAdmin(@PathVariable int id){
        PersonInfo delete=personRepository.findById(id);
        personRepository.delete(delete);
        return ResponseEntity.ok(personRepository.findAll());
    }
    @ApiOperation(value = "this is method for getting person-info")
    @GetMapping("/getAll")
    public ResponseEntity getAdeminUser() {
        return ResponseEntity.ok(personRepository.findAll());
    }


    @GetMapping("/showMyLoginPage")
    public ResponseEntity thisislogin(){

        return ResponseEntity.ok("this is the login");
    }

    @RequestMapping("/access-deniedSS")
    public ResponseEntity ccedenied(){

        return ResponseEntity.ok("access-denied");
    }
    @PostMapping("/registerencode")
    public ResponseEntity register(@RequestBody Users a){
        String pass=a.getPassword();
        String b=hash(a.getPassword());
        a.setPassword(b);
        userRpository.save(a);
        return ResponseEntity.ok("created");
    }
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }
}
