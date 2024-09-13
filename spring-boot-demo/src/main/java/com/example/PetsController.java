package com.example;

import com.myapp.api.PetApi;
import com.myapp.model.Pet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetsController {
    private final PetApi petApi;

    public PetsController(PetApi petApi) {
        this.petApi = petApi;
    }

    @GetMapping()
    public List<Pet> all() {
        return petApi.listPets() ;
    }
}
