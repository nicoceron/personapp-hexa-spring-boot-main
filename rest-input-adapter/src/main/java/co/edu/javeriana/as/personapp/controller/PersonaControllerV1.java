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

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(@PathVariable String database) {
		log.info("Into personas REST API");
		return personaInputAdapterRest.findAll(database.toUpperCase());
	}
	
	@PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request) {
		log.info("esta en el metodo crearTarea en el controller del api");
		return personaInputAdapterRest.create(request);
	}
	
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse findById(@PathVariable String database, @PathVariable Long id) {
		log.info("GET /api/v1/persona/{}/{} - Find persona by ID", database, id);
		return personaInputAdapterRest.findById(id, database.toUpperCase());
	}
	
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse edit(@PathVariable Long id, @RequestBody PersonaRequest request) {
		log.info("PUT /api/v1/persona/{} - Edit persona", id);
		return personaInputAdapterRest.edit(id, request);
	}
	
	@DeleteMapping(path = "/{database}/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String database, @PathVariable Long id) {
		log.info("DELETE /api/v1/persona/{}/{} - Delete persona", database, id);
		personaInputAdapterRest.delete(id, database.toUpperCase());
	}
	
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer count(@PathVariable String database) {
		log.info("GET /api/v1/persona/{}/count - Count personas", database);
		return personaInputAdapterRest.count(database.toUpperCase());
	}
	
	@GetMapping(path = "/{database}/{id}/phones", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PhoneResponse> getPhones(@PathVariable String database, @PathVariable Long id) {
		log.info("GET /api/v1/persona/{}/{}/phones - Get phones for persona", database, id);
		return personaInputAdapterRest.getPhones(id, database.toUpperCase());
	}
	
	@GetMapping(path = "/{database}/{id}/studies", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<StudyResponse> getStudies(@PathVariable String database, @PathVariable Long id) {
		log.info("GET /api/v1/persona/{}/{}/studies - Get studies for persona", database, id);
		return personaInputAdapterRest.getStudies(id, database.toUpperCase());
	}
}
