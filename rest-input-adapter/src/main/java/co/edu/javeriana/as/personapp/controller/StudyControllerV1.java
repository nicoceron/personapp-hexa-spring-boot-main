package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import co.edu.javeriana.as.personapp.adapter.StudyInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/study")
@Tag(name = "StudyController", description = "API for managing studies (academic records)")
public class StudyControllerV1 {

    @Autowired
    private StudyInputAdapterRest studyInputAdapterRest;

    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all studies", description = "Retrieves all studies from the specified database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "400", description = "Invalid database option supplied")
    })
    public List<StudyResponse> getAllStudies(@PathVariable String database) {
        log.info("Request to retrieve all studies from database: {}", database);
        return studyInputAdapterRest.findAll(database.toUpperCase());
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create study", description = "Creates a new study record in the specified database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Study successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or study already exists")
    })
    public StudyResponse createStudy(@RequestBody StudyRequest request) {
        log.info("Request to create study: {}", request);
        return studyInputAdapterRest.create(request);
    }

    @GetMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find study by Person ID and Profession ID", description = "Retrieves a study by the composite ID from the specified database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved study"),
            @ApiResponse(responseCode = "400", description = "Invalid database option or ID format supplied"),
            @ApiResponse(responseCode = "404", description = "Study not found")
    })
    public StudyResponse findStudyById(@PathVariable String database, @PathVariable Integer personId, @PathVariable Integer professionId) {
        log.info("Request to find study by Person ID: {}, Profession ID: {} in database: {}", personId, professionId, database);
        return studyInputAdapterRest.findById(personId, professionId, database.toUpperCase());
    }

    @PutMapping(path = "/{personId}/{professionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update study", description = "Updates an existing study record based on Person ID and Profession ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Study successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input, ID format, or database option in request body"),
            @ApiResponse(responseCode = "404", description = "Study not found")
    })
    public StudyResponse updateStudy(@PathVariable Integer personId, @PathVariable Integer professionId, @RequestBody StudyRequest request) {
        log.info("Request to update study with Person ID: {}, Profession ID: {}. New data: {}", personId, professionId, request);
        return studyInputAdapterRest.edit(personId, professionId, request);
    }

    @DeleteMapping(path = "/{database}/{personId}/{professionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete study", description = "Deletes a study record by Person ID and Profession ID from the specified database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Study successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid database option or ID format supplied"),
            @ApiResponse(responseCode = "404", description = "Study not found")
    })
    public void deleteStudy(@PathVariable String database, @PathVariable Integer personId, @PathVariable Integer professionId) {
        log.info("Request to delete study with Person ID: {}, Profession ID: {} from database: {}", personId, professionId, database);
        studyInputAdapterRest.delete(personId, professionId, database.toUpperCase());
    }
} 