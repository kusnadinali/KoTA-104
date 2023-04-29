package com.jtk.ps.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jtk.ps.api.model.SeminarValues;

@Repository
public interface SeminarValuesRepository extends JpaRepository<SeminarValues,Integer>{
    
    @Query(value = "select exists(select * from seminar_values sf where sf.seminar_form_id = :form and sf.seminar_criteria_id = :criteria);",nativeQuery = true)
    Integer isFormWithCriteriaExist(@Param("form") Integer form, @Param("criteria") Integer criteria);

    @Query(value = "SELECT COALESCE(MAX(id), 0) AS max_id FROM seminar_values;",nativeQuery = true)
    Integer maxFormId();

    @Query(value = "select * from seminar_values sf where sf.seminar_form_id = :form and sf.seminar_criteria_id = :criteria",nativeQuery = true)
    SeminarValues findByFormAndCriteria(@Param("form") Integer idForm,@Param("criteria") Integer idCriteria);

    @Query(value = "select * from seminar_values sf where sf.seminar_form_id = :form",nativeQuery = true)
    List<SeminarValues> findAllByForm(@Param("form") Integer idForm);

    @Query(value = "SELECT coalesce(SUM(sv.value / 100 * sc.criteria_bobot),0) AS total_nilai FROM seminar_values sv JOIN seminar_criteria sc ON sc.id = sv.seminar_criteria_id WHERE  sv.seminar_form_id = :idForm", nativeQuery = true)
    Float totalSeminarValuesByForm(@Param("idForm") Integer idForm);
}
