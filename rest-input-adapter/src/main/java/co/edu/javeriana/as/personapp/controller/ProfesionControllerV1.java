package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/profesion")
@Tag(name = "ProfesionController", description = "API for managing professions")
public class ProfesionControllerV1 {

    @Autowired
    private ProfesionInputAdapterRest profesionInputAdapterRest;

    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all professions", description = "Retrieves all professions from the specified database")
    public List<ProfesionResponse> profesiones(@PathVariable String database) {
        log.info("Into profesiones REST API for database: {}", database);
        return profesionInputAdapterRest.findAll(database.toUpperCase());
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Create profession", description = "Creates a new profession in the specified database")
    public ProfesionResponse crearProfesion(@RequestBody ProfesionRequest request) {
        log.info("Request to create profession: {}", request);
        return profesionInputAdapterRest.create(request);
    }

    @GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find profession by ID", description = "Retrieves a profession by its ID from the specified database")
    public ProfesionResponse findById(@PathVariable String database, @PathVariable Integer id) {
        log.info("Request to find profession by ID: {} in database: {}", id, database);
        return profesionInputAdapterRest.findById(id, database.toUpperCase());
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update profession", description = "Updates an existing profession")
    public ProfesionResponse editProfesion(@PathVariable Integer id, @RequestBody ProfesionRequest request) {
        log.info("Request to edit profession with ID: {}. New data: {}", id, request);
        // Assuming the ID in the path is the authoritative one.
        // The request body's ID, if present, might be used for validation or ignored.
        return profesionInputAdapterRest.edit(id, request);
    }

    @DeleteMapping(path = "/{database}/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete profession", description = "Deletes a profession by its ID from the specified database")
    public void deleteProfesion(@PathVariable String database, @PathVariable Integer id) {
        log.info("Request to delete profession with ID: {} from database: {}", id, database);
        // The adapter's delete method returns a ProfesionResponse,
        // but for a DELETE HTTP method, typically no body is returned (204 No Content).
        // So, we call the adapter method but don't use its return value here.
        profesionInputAdapterRest.delete(id, database.toUpperCase());
    }
} 