package com.jtk.ps.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jtk.ps.api.model.Tutorial;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
}
