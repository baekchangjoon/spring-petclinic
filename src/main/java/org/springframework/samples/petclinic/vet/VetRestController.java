package org.springframework.samples.petclinic.vet;

import java.util.Collection;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for Vet operations
 */
@RestController
@RequestMapping("/api/vets")
@Tag(name = "Vet", description = "Veterinarian management API")
@SecurityRequirement(name = "bearerAuth")
public class VetRestController {

	private final VetRepository vetRepository;

	public VetRestController(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	@GetMapping
	@Operation(summary = "Get all veterinarians", description = "Retrieve a list of all veterinarians")
	public Collection<Vet> getAllVets(
			@RequestParam(required = false) @Parameter(description = "Filter by specialty") String specialty) {
		Collection<Vet> vets = vetRepository.findAll();
		if (specialty != null && !specialty.isEmpty()) {
			return vets.stream()
				.filter(vet -> vet.getSpecialties()
					.stream()
					.anyMatch(spec -> spec.getName().toLowerCase().contains(specialty.toLowerCase())))
				.toList();
		}
		return vets;
	}

	@GetMapping("/{vetId}")
	@Operation(summary = "Get veterinarian by ID", description = "Retrieve a specific veterinarian by their ID")
	public Vet getVet(@PathVariable @Parameter(description = "Vet ID") Integer vetId) {
		return vetRepository.findAll().stream().filter(vet -> vet.getId().equals(vetId)).findFirst().orElse(null);
	}

}
