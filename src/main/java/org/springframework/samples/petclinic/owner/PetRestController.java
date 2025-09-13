package org.springframework.samples.petclinic.owner;

import java.util.Collection;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for Pet operations
 */
@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pet", description = "Pet management API")
@SecurityRequirement(name = "bearerAuth")
public class PetRestController {

	private final OwnerRepository ownerRepository;

	private final PetTypeRepository petTypeRepository;

	public PetRestController(OwnerRepository ownerRepository, PetTypeRepository petTypeRepository) {
		this.ownerRepository = ownerRepository;
		this.petTypeRepository = petTypeRepository;
	}

	@GetMapping("/types")
	@Operation(summary = "Get all pet types", description = "Retrieve a list of all pet types")
	public Collection<PetType> getPetTypes() {
		return petTypeRepository.findPetTypes();
	}

	@GetMapping("/{petId}")
	@Operation(summary = "Get pet by ID", description = "Retrieve a specific pet by their ID")
	public ResponseEntity<Pet> getPet(@PathVariable @Parameter(description = "Pet ID") Integer petId) {
		// Find pet by searching through all owners
		for (Owner owner : ownerRepository.findAll()) {
			Pet pet = owner.getPet(petId);
			if (pet != null) {
				return ResponseEntity.ok(pet);
			}
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	@Operation(summary = "Create new pet", description = "Create a new pet for an owner")
	public ResponseEntity<Pet> createPet(@RequestBody Pet pet, @Parameter(description = "Owner ID") Integer ownerId) {
		Optional<Owner> ownerOpt = ownerRepository.findById(ownerId);
		if (ownerOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Owner owner = ownerOpt.get();
		owner.addPet(pet);
		ownerRepository.save(owner);

		return ResponseEntity.status(HttpStatus.CREATED).body(pet);
	}

	@PutMapping("/{petId}")
	@Operation(summary = "Update pet", description = "Update an existing pet")
	public ResponseEntity<Pet> updatePet(@PathVariable @Parameter(description = "Pet ID") Integer petId,
			@RequestBody Pet pet) {
		// Find and update pet
		for (Owner owner : ownerRepository.findAll()) {
			Pet existingPet = owner.getPet(petId);
			if (existingPet != null) {
				existingPet.setName(pet.getName());
				existingPet.setBirthDate(pet.getBirthDate());
				existingPet.setType(pet.getType());
				ownerRepository.save(owner);
				return ResponseEntity.ok(existingPet);
			}
		}
		return ResponseEntity.notFound().build();
	}

}
