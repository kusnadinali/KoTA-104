package com.jtk.ps.api.model;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_values")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseValues {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer value;

    private Integer criteriaId;

    private Integer mentorValuesId;

    private Integer selfAssessmentValuesId;

    private Integer seminarValuesId;

    private Integer industryValuesId;

    private Integer participantId;

    private Date created_date;
}
