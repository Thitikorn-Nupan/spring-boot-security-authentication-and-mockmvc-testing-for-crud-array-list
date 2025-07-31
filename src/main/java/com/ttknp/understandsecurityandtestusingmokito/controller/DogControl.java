package com.ttknp.understandsecurityandtestusingmokito.controller;

import com.ttknp.understandsecurityandtestusingmokito.entity.Dog;
import com.ttknp.understandsecurityandtestusingmokito.service.DogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class DogControl {

    private final DogService dogService;
    private final Logger log = LoggerFactory.getLogger(DogControl.class);

    public DogControl() {
        dogService = new DogService();
    }

    @GetMapping(value = {"/",""})
    private ResponseEntity server() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping(value = "/dogs")
    private ResponseEntity reads() {
        return ResponseEntity.status(202).body(dogService.getDogs());
    }

    @GetMapping(value = "/dogs/{id}")
    private ResponseEntity read(@PathVariable Long id) {
        return ResponseEntity.status(202).body(dogService.getDog(id));
    }

    @GetMapping(value = "/dogs/by")
    private ResponseEntity readWithParam(@RequestParam Long id) {
        return ResponseEntity.status(202).body(dogService.getDog(id));
    }

    @PostMapping(value = "/dogs")
    private ResponseEntity create(@RequestBody Dog dog) {
        return ResponseEntity.status(202).body(dogService.addDog(dog));
    }

    @PutMapping(value = "/dogs/{id}")
    private ResponseEntity update(@RequestBody Dog dog, @PathVariable Long id) {
        return ResponseEntity.status(202).body(dogService.editDog(dog,id));
    }

    @DeleteMapping(value = "/dogs/{id}")
    private ResponseEntity delete( @PathVariable Long id) {
        return ResponseEntity.status(202).body(dogService.removeDog(id));
    }

}