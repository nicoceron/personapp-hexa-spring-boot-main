package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@ResponseBody
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> findAll(@PathVariable String database) {
		log.info("GET /api/v1/persona/{} - Find all personas", database);
		return personaInputAdapterRest.findAll(database.toUpperCase());
	}
	
	@ResponseBody
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer count(@PathVariable String database) {
		log.info("GET /api/v1/persona/{}/count - Count personas", database);
		return personaInputAdapterRest.count(database.toUpperCase());
	}
	
	@ResponseBody
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse findById(@PathVariable String database, @PathVariable Integer id) {
		log.info("GET /api/v1/persona/{}/{} - Find persona by ID", database, id);
		return personaInputAdapterRest.findById(id, database.toUpperCase());
	}
	
	@ResponseBody
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse create(@RequestBody PersonaRequest request) {
		log.info("POST /api/v1/persona - Create persona");
		return personaInputAdapterRest.create(request);
	}
	
	@ResponseBody
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse edit(@PathVariable Integer id, @RequestBody PersonaRequest request) {
		log.info("PUT /api/v1/persona/{} - Edit persona", id);
		return personaInputAdapterRest.edit(id, request);
	}
	
	@ResponseBody
	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse delete(@PathVariable String database, @PathVariable Integer id) {
		log.info("DELETE /api/v1/persona/{}/{} - Delete persona", database, id);
		return personaInputAdapterRest.delete(id, database.toUpperCase());
	}
}
