package com.jtk.ps.api.service.Interface;

import java.util.List;

import com.jtk.ps.api.dto.ComponentCourseDto;
import com.jtk.ps.api.dto.CourseFormRequestDto;
import com.jtk.ps.api.dto.CourseFormResponseDto;
import com.jtk.ps.api.dto.CriteriaEvaluationFormDto;
import com.jtk.ps.api.dto.EvaluationFormResponseDto;
import com.jtk.ps.api.model.CourseForm;

public interface ICourseService {
    
    List<CourseFormResponseDto> getAllCourse();

    CourseForm createCourseForm(CourseFormRequestDto courseFormRequestDto);

    CourseFormResponseDto getDetailCourse(Integer idForm);

    void updateCourseForm(Integer idForm, CourseFormRequestDto courseFormRequestDto);
    
    void deleteCourseForm(Integer idForm);

    List<ComponentCourseDto> getComponentByCourseForm(Integer idForm);

    List<EvaluationFormResponseDto> getEvaluationForm(Integer prodiId);

    List<CriteriaEvaluationFormDto> getCriteriaByEvaluationForm(String formType,Integer prodiId);

    void updateCriteriaComponent();

    // void updateComponentCourse(Integer idComponent);
}
