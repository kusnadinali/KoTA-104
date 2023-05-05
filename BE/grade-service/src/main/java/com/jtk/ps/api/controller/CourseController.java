package com.jtk.ps.api.controller;

import java.util.List;

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

import com.jtk.ps.api.dto.ComponentAndCriteriasDto;
import com.jtk.ps.api.dto.ComponentCourseDto;
import com.jtk.ps.api.dto.CourseFormRequestDto;
import com.jtk.ps.api.service.CourseService;
import com.jtk.ps.api.util.ResponseHandler;

@RestController
@RequestMapping("/api/courses/")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @GetMapping("/form")
    public ResponseEntity<Object> getAllCourseForm(){
        return ResponseHandler.generateResponse("Get all Course Form succeed",HttpStatus.OK, courseService.getAllCourse());
    }

    @PostMapping("/form")
    public ResponseEntity<Object> createCourseForm(@RequestBody CourseFormRequestDto newCourse){
        courseService.createCourseForm(newCourse);
        return ResponseHandler.generateResponse("Create Course Form succeed",HttpStatus.OK);
    }

    @GetMapping("/form/{idForm}")
    public ResponseEntity<Object> getDetailCourse(@PathVariable("idForm") Integer idForm){
        return ResponseHandler.generateResponse("Get Detail Course succeed",HttpStatus.OK, courseService.getDetailCourse(idForm));
    }

    @PutMapping("/form/update/{idForm}")
    public ResponseEntity<Object> updateCourseForm(@PathVariable("idForm") Integer idForm,@RequestBody CourseFormRequestDto newCourseForm){
        courseService.updateCourseForm(idForm, newCourseForm);
        return ResponseHandler.generateResponse("Update Course Form succeed",HttpStatus.OK);
    }

    @DeleteMapping("/form/delete/{idForm}")
    public ResponseEntity<Object> deleteCourseForm(@PathVariable("idForm") Integer idForm){
        courseService.deleteCourseForm(idForm);
        return ResponseHandler.generateResponse("Deleted Course Form succeed",HttpStatus.OK);
    }

    @GetMapping("/criteria/evaluation-form/{prodiId}")
    public ResponseEntity<Object> getEvaluationFormByProdiId(@PathVariable("prodiId") Integer prodiId){
        return ResponseHandler.generateResponse("Get Evaluation Forms succeed",HttpStatus.OK, courseService.getEvaluationForm(prodiId));
    }

    @GetMapping("/criteria/evaluation-form/aspect")
    public ResponseEntity<Object> getAspectEvaluationForm(@RequestParam("formType") String formType,@RequestParam("prodiId") Integer prodiId){
        return ResponseHandler.generateResponse("Get Aspects Evaluation succeed",HttpStatus.OK, courseService.getCriteriaByEvaluationForm(formType,prodiId));
    }

    @GetMapping("/component/course-form/{idForm}")
    public ResponseEntity<Object> getComponentByCourseForm(@PathVariable("idForm") Integer idForm){
        return ResponseHandler.generateResponse("Get All Component By Course Form Id succeed",HttpStatus.OK, courseService.getComponentByCourseForm(idForm));
    }

    @PutMapping("/component/update")
    public ResponseEntity<Object> updateComponentCourse(@RequestBody List<ComponentCourseDto> newComponentCourses){
        courseService.updateComponent(newComponentCourses);
        return ResponseHandler.generateResponse("Update Component Course Form succeed",HttpStatus.OK);
    }

    @GetMapping("/component/criteria/form/{idForm}")
    public ResponseEntity<Object> getCriteriaComponentByCourseFormId(@PathVariable("idForm") Integer idForm){
        return ResponseHandler.generateResponse("Get All Component By Course Form Id succeed",HttpStatus.OK, courseService.getCriteriaComponentByCourseFormId(idForm));
    }

    @PutMapping("/component/criteria/update")
    public ResponseEntity<Object> updateAllCriteriaInComponentCourse(@RequestBody ComponentAndCriteriasDto newCriterias){
        courseService.updateOrInsertCriteriaComponent(newCriterias);
        return ResponseHandler.generateResponse("Update Criteria in Component Course Form succeed",HttpStatus.OK);
    }
}
