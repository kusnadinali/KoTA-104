package com.jtk.ps.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jtk.ps.api.model.CourseForm;

@Repository
public interface CourseFormRepository extends JpaRepository<CourseForm,Integer>{
    
    @Query(value = "select * from course_form where is_deleted = 0", nativeQuery = true)
    List<CourseForm> findAllCourse();

    @Query(value = "select * from course_form where is_deleted = 0 and tahun_ajaran_start = :year and prodi_id = :prodiId",nativeQuery = true)
    List<CourseForm> findAllCourseByYearAndProdiId(Integer year, Integer prodiId);

}
