package com.jtk.ps.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jtk.ps.api.model.CriteriaComponentCourse;

@Repository
public interface CriteriaComponentCourseRepository extends JpaRepository<CriteriaComponentCourse, Integer>{
    
    @Query(value = "select * from criteria_component_course where component_id = :componentId", nativeQuery = true)
    List<CriteriaComponentCourse> findAllByComponentId(Integer componentId);
}
