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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PhoneInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/phone")
@Tag(name = "PhoneController", description = "API for managing phone numbers")
public class PhoneControllerV1 {
    
    @Autowired
    private PhoneInputAdapterRest phoneInputAdapterRest;
    
    @Operation(summary = "Get all phones", description = "Retrieves all phone numbers from the specified database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of phones"),
        @ApiResponse(responseCode = "400", description = "Invalid database parameter provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PhoneResponse> getAllPhones(
            @Parameter(description = "Database to use (MARIA or MONGO)", required = true) 
            @RequestParam String database) {
        log.info("GET /api/v1/phone/list?database={} - Get all phones", database);
        return phoneInputAdapterRest.findAll(database.toUpperCase());
    }
    
    @Operation(summary = "Get all phones of a person", description = "Retrieves all phone numbers associated with a specific person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of phones"),
        @ApiResponse(responseCode = "404", description = "Person not found"),
        @ApiResponse(responseCode = "400", description = "Invalid database or person ID parameter provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/person/{personId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PhoneResponse> getPhonesByPerson(
            @Parameter(description = "Person ID (CC)", required = true) 
            @PathVariable String personId,
            @Parameter(description = "Database to use (MARIA or MONGO)", required = true) 
            @RequestParam String database) {
        log.info("GET /api/v1/phone/person/{}?database={} - Get phones by person", personId, database);
        return phoneInputAdapterRest.findByPersonId(personId, database.toUpperCase());
    }
    
    @Operation(summary = "Create a new phone", description = "Creates a new phone record with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Phone successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Person not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public PhoneResponse createPhone(
            @Parameter(description = "Phone details", required = true) 
            @RequestBody PhoneRequest request) {
        log.info("POST /api/v1/phone - Create phone");
        return phoneInputAdapterRest.create(request);
    }
    
    @Operation(summary = "Update a phone", description = "Updates an existing phone with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Phone successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Phone or Person not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(path = "/{database}/{phoneNumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PhoneResponse updatePhone(
            @Parameter(description = "Database to use (MARIA or MONGO)", required = true) 
            @PathVariable String database, 
            @Parameter(description = "Phone number to update", required = true) 
            @PathVariable String phoneNumber, 
            @Parameter(description = "Updated phone details", required = true) 
            @RequestBody PhoneRequest request) {
        log.info("PUT /api/v1/phone/{}/{} - Update phone", database, phoneNumber);
        return phoneInputAdapterRest.edit(phoneNumber, request);
    }
    
    @Operation(summary = "Find phone by number", description = "Retrieves a phone record by its number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the phone"),
        @ApiResponse(responseCode = "404", description = "Phone not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{database}/{phoneNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PhoneResponse findByNumber(
            @Parameter(description = "Database to use (MARIA or MONGO)", required = true) 
            @PathVariable String database, 
            @Parameter(description = "Phone number to find", required = true) 
            @PathVariable String phoneNumber) {
        log.info("GET /api/v1/phone/{}/{} - Find phone by number", database, phoneNumber);
        return phoneInputAdapterRest.findByNumber(phoneNumber, database.toUpperCase());
    }
    
    @Operation(summary = "Delete a phone", description = "Deletes a phone record by its number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Phone successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Phone not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(path = "/{database}/{phoneNumber}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Database to use (MARIA or MONGO)", required = true) 
            @PathVariable String database, 
            @Parameter(description = "Phone number to delete", required = true) 
            @PathVariable String phoneNumber) {
        log.info("DELETE /api/v1/phone/{}/{} - Delete phone", database, phoneNumber);
        phoneInputAdapterRest.delete(phoneNumber, database.toUpperCase());
    }
} 