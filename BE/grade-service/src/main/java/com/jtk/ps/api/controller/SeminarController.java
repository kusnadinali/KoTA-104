package com.jtk.ps.api.controller;


import java.util.List;

// import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jtk.ps.api.dto.SeminarCriteriaRequestDto;
import com.jtk.ps.api.dto.SeminarFormRequestDto;
import com.jtk.ps.api.dto.SeminarValuesDto;
import com.jtk.ps.api.service.SeminarService;
import com.jtk.ps.api.util.ResponseHandler;

@RestController
@RequestMapping("/api/seminar/")
public class SeminarController {
    
    // private static final Logger LOGGER = LoggerFactory.getLogger(SeminarService.class);

    @Autowired
    private SeminarService seminarService;

    @GetMapping("/company") // checked
    public ResponseEntity<Object> getAllCompany(){
        return ResponseHandler.generateResponse("Get all company succeed",HttpStatus.OK, seminarService.getAllCompany());
    }

    @GetMapping("/company/{idCompany}") // checked
    public ResponseEntity<Object> getParticipantByCompany(@PathVariable("idCompany") Integer idCompany){
        return ResponseHandler.generateResponse("Get all participant by company",HttpStatus.OK,seminarService.getAllParticipantByCompany(idCompany));
    }

    @GetMapping("/criteria") // checked
    public ResponseEntity<Object> getAllSeminarCriteria(){
        return ResponseHandler.generateResponse("Get All Seminar Criteria succeed",HttpStatus.OK,seminarService.getAllSeminarCriteria());
    }

    @PostMapping("/criteria") // checked
    public ResponseEntity<Object> createSeminarCriteria(@RequestBody SeminarCriteriaRequestDto newCriteria){
        seminarService.createSeminarCriteria(newCriteria);
        return ResponseHandler.generateResponse("Create Criteria Seminar Form succeed",HttpStatus.OK);
    }

    @PutMapping("/criteria/update/{id_criteria}") // checked
    public ResponseEntity<Object> updateSeminarCriteria(@PathVariable("id_criteria") Integer idCriteria,@RequestBody SeminarCriteriaRequestDto newCriteria){
        seminarService.updateSeminarCriteria(idCriteria, newCriteria);
        return ResponseHandler.generateResponse("Update Criteria Seminar Form succeed",HttpStatus.OK);
    }

    @DeleteMapping("/criteria/delete/{id_criteria}") // checked
    public ResponseEntity<Object> deleteSeminarCriteria(@PathVariable("id_criteria") Integer idCriteria){
        seminarService.deleteSeminarCriteria(idCriteria);
        return ResponseHandler.generateResponse("Deleted Criteria Seminar Form succeed",HttpStatus.OK);
    }

    @GetMapping("/form/participant/{id}")  // checked
    public ResponseEntity<Object> findAllSeminarFormByParticipantId(@PathVariable("id") Integer id){
        return ResponseHandler.generateResponse("Get All Seminar Form By Participant Id Succeed",HttpStatus.OK,seminarService.findSeminarFormByParticipantId(id));
    }

    @PutMapping("/form/{id_form}/values") // checked (update n insert)
    public ResponseEntity<Object> updateValueSeminarByFormId(@PathVariable("id_form") Integer id_form, @RequestBody List<SeminarValuesDto> seminarvalues){
        seminarService.updateSeminarValues(id_form, seminarvalues);
        return ResponseHandler.generateResponse("Update Seminar Values succeed",HttpStatus.OK);
    }

    @PutMapping("/form/{id_form}") // checked (update n insert)
    public ResponseEntity<Object> updateSeminarForm(@PathVariable("id_form") Integer id_form, @RequestBody SeminarFormRequestDto seminarForm){
        seminarService.updateSeminarForm(id_form, seminarForm);
        return ResponseHandler.generateResponse("Update Seminar Form succeed",HttpStatus.OK);
    }

    @GetMapping("/recapitulation") // checked
    public ResponseEntity<Object> recapitulationSeminarValues(@RequestParam("year") Integer year, @RequestParam("prodiId") Integer prodiId){
        return ResponseHandler.generateResponse("Get All Seminar Form By Participant Id Succeed",HttpStatus.OK,seminarService.getRecapitulation(prodiId, year));
    }

    @GetMapping("/examiner") // checked
    public ResponseEntity<Object> getExaminerSeminar(){
        return ResponseHandler.generateResponse("Get All Examiner Succeed",HttpStatus.OK,seminarService.getExaminer());
    }

    // @PutMapping("/values/{id_form}")
    // public ResponseEntity<Object> updateValueSeminarByFormId(@PathVariable("id_form") Integer idForm,@RequestBody List<SeminarValuesDto> newValue){
        
    //     return ResponseHandler.generateResponse("Update Seminar Form succeed",HttpStatus.OK);
    // }
}
