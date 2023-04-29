package com.jtk.ps.api.service.Interface;

import java.util.List;

import com.jtk.ps.api.dto.CompanyNameDto;
import com.jtk.ps.api.dto.ParticipantDto;
import com.jtk.ps.api.dto.RecapitulationResponseDto;
import com.jtk.ps.api.dto.SeminarCriteriaDto;
import com.jtk.ps.api.dto.SeminarCriteriaRequestDto;
import com.jtk.ps.api.dto.SeminarFormRequestDto;
import com.jtk.ps.api.dto.SeminarFormResponseDto;
import com.jtk.ps.api.dto.SeminarValuesDto;
import com.jtk.ps.api.model.SeminarCriteria;
import com.jtk.ps.api.model.SeminarForm;

public interface ISeminarService {
    
    List<CompanyNameDto> getAllCompany();

    List<ParticipantDto> getAllParticipantByCompany(Integer idCompany);

    List<SeminarCriteriaDto> getAllSeminarCriteria();

    SeminarForm createSeminarForm(SeminarFormRequestDto seminarFormRequestDto);

    SeminarCriteria createSeminarCriteria(SeminarCriteriaRequestDto seminarCriteriaRequestDto) ;
    
    void updateSeminarCriteria(Integer idSeminarCriteria, SeminarCriteriaRequestDto seminarCriteriaRequestDto);
    
    void deleteSeminarCriteria(Integer idSeminarCriteria);

    List<SeminarFormResponseDto> findSeminarFormByParticipantId(Integer idParticipant);
    
    void updateSeminarForm(Integer idForm, SeminarFormRequestDto seminarFormRequestDto);

    void updateSeminarValues(Integer idForm, List<SeminarValuesDto> seminarValuesDtos);

    RecapitulationResponseDto getRecapitulation(Integer prodiId, Integer year);
    // belum dibuat

    // public void getSeminarFormBySeminarFormIdAndExaminerId(Integer formId, Integer examinerId);

    // public void getSeminarFormByExaminerId();




}
