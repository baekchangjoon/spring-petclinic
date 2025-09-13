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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for Owner operations
 */
@RestController
@RequestMapping("/api/owners")
@Tag(name = "Owner", description = "Owner management API")
@SecurityRequirement(name = "bearerAuth")
public class OwnerRestController {

	private final OwnerRepository ownerRepository;

	public OwnerRestController(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@GetMapping
	@Operation(summary = "Get all owners", description = "Retrieve a list of all owners")
	public Collection<Owner> getAllOwners(@RequestParam(required = false) String lastName) {
		Collection<Owner> owners = ownerRepository.findAll();
		if (lastName != null && !lastName.isEmpty()) {
			return owners.stream()
				.filter(owner -> owner.getLastName().toLowerCase().startsWith(lastName.toLowerCase()))
				.toList();
		}
		return owners;
	}

	@GetMapping("/{ownerId}")
	@Operation(summary = "Get owner by ID", description = "Retrieve a specific owner by their ID")
	public ResponseEntity<Owner> getOwner(@PathVariable @Parameter(description = "Owner ID") Integer ownerId) {
		Optional<Owner> owner = ownerRepository.findById(ownerId);
		return owner.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@Operation(summary = "Create new owner", description = "Create a new owner")
	public ResponseEntity<Owner> createOwner(@RequestBody Owner owner) {
		Owner savedOwner = ownerRepository.save(owner);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedOwner);
	}

	@PutMapping("/{ownerId}")
	@Operation(summary = "Update owner", description = "Update an existing owner")
	public ResponseEntity<Owner> updateOwner(@PathVariable @Parameter(description = "Owner ID") Integer ownerId,
			@RequestBody Owner owner) {
		if (!ownerRepository.existsById(ownerId)) {
			return ResponseEntity.notFound().build();
		}
		owner.setId(ownerId);
		Owner savedOwner = ownerRepository.save(owner);
		return ResponseEntity.ok(savedOwner);
	}

}
