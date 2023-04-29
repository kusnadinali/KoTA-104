package com.jtk.ps.api.service;

import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtk.ps.api.dto.CompanyNameDto;
import com.jtk.ps.api.dto.ParticipantDto;
import com.jtk.ps.api.dto.RecapitulationResponseDto;
import com.jtk.ps.api.dto.SeminarValueParticipantDto;
import com.jtk.ps.api.dto.SeminarCriteriaDto;
import com.jtk.ps.api.dto.SeminarCriteriaRequestDto;
import com.jtk.ps.api.dto.SeminarFormRequestDto;
import com.jtk.ps.api.dto.SeminarFormResponseDto;
import com.jtk.ps.api.dto.SeminarTotalValueDto;
import com.jtk.ps.api.dto.SeminarValuesDto;
import com.jtk.ps.api.model.Account;
import com.jtk.ps.api.model.Company;
import com.jtk.ps.api.model.EventStore;
import com.jtk.ps.api.model.Participant;
import com.jtk.ps.api.model.SeminarCriteria;
import com.jtk.ps.api.model.SeminarForm;
import com.jtk.ps.api.model.SeminarValues;
import com.jtk.ps.api.repository.AccountRepository;
import com.jtk.ps.api.repository.CompanyRepository;
import com.jtk.ps.api.repository.EventStoreRepository;
import com.jtk.ps.api.repository.ParticipantRepository;
import com.jtk.ps.api.repository.SeminarCriteriaRepository;
import com.jtk.ps.api.repository.SeminarFormRepository;
import com.jtk.ps.api.repository.SeminarValuesRepository;
import com.jtk.ps.api.service.Interface.ISeminarService;

@Service
public class SeminarService implements ISeminarService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SeminarService.class);

    @Autowired
    @Lazy
    private CompanyRepository companyRepository;

    @Autowired
    @Lazy
    private ParticipantRepository participantRepository;

    @Autowired
    @Lazy
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private SeminarFormRepository seminarFormRepository;

    @Autowired
    @Lazy
    private SeminarValuesRepository seminarValuesRepository;

    @Autowired
    @Lazy
    private SeminarCriteriaRepository seminarCriteriaRepository;

    @Autowired
    @Lazy
    private EventStoreRepository eventStoreRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private void eventStoreHandler(String entityId, String eventType, Object object){
        try {
            EventStore eventStore = new EventStore();

            eventStore.setEntityId(entityId);
            eventStore.setEventType(eventType);
            eventStore.setEventTime(LocalDateTime.now());
            eventStore.setEventData(objectMapper.writeValueAsString(object));

            eventStoreRepository.save(eventStore);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public List<CompanyNameDto> getAllCompany() {
        List<Company> company = companyRepository.findAll();

        List<CompanyNameDto> companyNameDtos = new ArrayList<>();
        company.forEach(c -> {
            CompanyNameDto companyNameDtoTemp = new CompanyNameDto();
            companyNameDtoTemp.setId(c.getId());
            companyNameDtoTemp.setName(c.getCompanyName());
            companyNameDtos.add(companyNameDtoTemp);
        });
        
        return companyNameDtos;
    }

    @Override
    public List<ParticipantDto> getAllParticipantByCompany(Integer idCompany) {
        // LOGGER.info(String.format("Year now %s", "2023");
        List<Participant> participants = participantRepository.findParticipantByCompany(idCompany);
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participants.forEach(c -> {
            ParticipantDto tempParticipantDto = new ParticipantDto();
            Optional<Account> account = accountRepository.findById(c.getAccountId());
            account.ifPresent(a -> {
                tempParticipantDto.setNim(a.getUsername());
            });
            tempParticipantDto.setId(c.getId());
            tempParticipantDto.setName(c.getName());
            tempParticipantDto.setProdi_id(c.getProdiId());
            tempParticipantDto.setStatus_cv(c.getStatusCv());
            tempParticipantDto.setYear(c.getYear());
            tempParticipantDto.setAccount_id(c.getAccountId());
            


            participantDtos.add(tempParticipantDto);
        });

        return participantDtos;
    }

    @Override
    public SeminarForm createSeminarForm(SeminarFormRequestDto seminarFormRequestDto) {
        SeminarForm newSeminarForm = new SeminarForm();

        newSeminarForm.setComment(seminarFormRequestDto.getComment());
        newSeminarForm.setDateSeminar(seminarFormRequestDto.getDateSeminar());
        newSeminarForm.setExaminerId(seminarFormRequestDto.getExaminerId());
        newSeminarForm.setParticipantId(seminarFormRequestDto.getParticipantId());

        newSeminarForm = seminarFormRepository.save(newSeminarForm);

        return newSeminarForm;
    }

    @Override
    public SeminarCriteria createSeminarCriteria(SeminarCriteriaRequestDto seminarCriteriaRequestDto) {
        SeminarCriteria newSeminarCriteria = new SeminarCriteria();

        newSeminarCriteria.setCriteriaName(seminarCriteriaRequestDto.getCriteriaName());
        newSeminarCriteria.setCriteriaBobot(Float.valueOf(0));
        newSeminarCriteria.setIsDeleted(0);
        newSeminarCriteria.setIsSelected(0);
        
        newSeminarCriteria = seminarCriteriaRepository.save(newSeminarCriteria);

        eventStoreHandler("seminar_criteria", "SEMINAR_CRITERIA_ADDED", newSeminarCriteria);
        
        return newSeminarCriteria;
    }

    @Override
    public void updateSeminarCriteria(Integer idSeminarCriteria, SeminarCriteriaRequestDto criteriaRequestDto) {
        
        LOGGER.info(String.format("*** value of criteria ==> %s", criteriaRequestDto.toString()));
        Optional<SeminarCriteria> criteriaUpdate = seminarCriteriaRepository.findById(idSeminarCriteria);

        criteriaUpdate.ifPresent(c -> {
            if(criteriaRequestDto.getCriteriaName() != null){
                c.setCriteriaName(criteriaRequestDto.getCriteriaName());
            }
            if(criteriaRequestDto.getCriteriaBobot() !=null){
                c.setCriteriaBobot(criteriaRequestDto.getCriteriaBobot());
            }
            if(criteriaRequestDto.getIsSelected() != null){
                c.setIsSelected(criteriaRequestDto.getIsSelected());
            }

            SeminarCriteria temp = seminarCriteriaRepository.save(c);
            eventStoreHandler("seminar_criteria", "SEMINAR_CRITERIA_UPDATE", temp);
        });
    }

    @Override
    public void deleteSeminarCriteria(Integer idSeminarCriteria) {
        
        Optional<SeminarCriteria> criteriaDeleted = seminarCriteriaRepository.findById(idSeminarCriteria);
        
        criteriaDeleted.ifPresent(c ->{
            c.setIsDeleted(1);

            // criteria deleted
            eventStoreHandler("seminar_criteria", "SEMINAR_CRITERIA_DELETED", seminarCriteriaRepository.save(c));
        });
    }

    @Override
    public List<SeminarCriteriaDto> getAllSeminarCriteria() {
        
        List<SeminarCriteria> data = seminarCriteriaRepository.findAllByIsDeleted(0);

        List<SeminarCriteriaDto> response = new ArrayList<>();
        data.forEach(c ->{
            SeminarCriteriaDto temp = new SeminarCriteriaDto();            
            temp.setId(c.getId());
            temp.setCriteriaName(c.getCriteriaName());
            temp.setCriteriaBobot(c.getCriteriaBobot());
            temp.setIsSelected(c.getIsSelected());
            response.add(temp);
        });

        return response;
    }

    @Override
    public List<SeminarFormResponseDto> findSeminarFormByParticipantId(Integer idParticipant) {
        Integer isExist = seminarFormRepository.isSeminarFormExistByParticipantId(idParticipant);

        List<SeminarFormResponseDto> response = new ArrayList<>();

        if(isExist == 1){
            LOGGER.info("*****  Ada");
            List<SeminarForm> seminarForms = seminarFormRepository.findAllByParticipantId(idParticipant);
            seminarForms.forEach(s -> {
                SeminarFormResponseDto temp = new SeminarFormResponseDto();

                temp.setId(s.getId());
                temp.setParticipantId(s.getParticipantId());
                temp.setExaminerId(s.getExaminerId());
                temp.setExaminerType(s.getExaminerType());
                temp.setDateSeminar(s.getDateSeminar());
                temp.setComment(s.getComment());
                
                response.add(temp);
            });
        }else{
            LOGGER.info("*****  Tidak Ada");
            for(int i = 0; i<3; i++){
                SeminarForm seminarForm = new SeminarForm();
                SeminarFormResponseDto temp = new SeminarFormResponseDto();

                seminarForm.setParticipantId(idParticipant);
                seminarForm.setExaminerType(i+1);
                temp.setParticipantId(idParticipant);
                temp.setExaminerType(i+1);
                temp.setId(seminarFormRepository.maxFormId()+1);
                
                seminarFormRepository.save(seminarForm);
                response.add(temp);
            }

        }
        return response;
    }

    @Override
    public void updateSeminarForm(Integer idForm, SeminarFormRequestDto seminarFormRequestDto) {
        Optional<SeminarForm> seminarForm = seminarFormRepository.findById(idForm);

        seminarForm.ifPresent(c ->{
            c.setComment(seminarFormRequestDto.getComment());
            c.setDateSeminar(seminarFormRequestDto.getDateSeminar());
            c.setExaminerId(seminarFormRequestDto.getExaminerId());
            
            seminarFormRepository.save(c);
        });
    }

    @Override
    public void updateSeminarValues(Integer idForm, List<SeminarValuesDto> seminarValuesDtos){
        
        seminarValuesDtos.forEach(c -> {
            if(seminarValuesRepository.isFormWithCriteriaExist(idForm, c.getSeminarCriteriaId()) == 1){
                SeminarValues newSV = seminarValuesRepository.findByFormAndCriteria(idForm, c.getSeminarCriteriaId());
                newSV.setValue(c.getValue());

                seminarValuesRepository.save(newSV);
            }else{
                SeminarValues newSV = new SeminarValues();
                newSV.setSeminarCriteriaId(c.getSeminarCriteriaId());
                newSV.setSeminarFormId(idForm);
                newSV.setValue(c.getValue());

                seminarValuesRepository.save(newSV);
            }
        });
    }

    private Object getRecapitulationByTypeForm(Integer year, Integer prodiId, Integer formType){
        List<Participant> participants = participantRepository.findAllByYearAndProdi(year, prodiId);

        if(formType != 0){
            List<SeminarValueParticipantDto> penguji = new ArrayList<>();

            participants.forEach(p ->{
                Optional<SeminarForm> sFTemp = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), formType);
    
                sFTemp.ifPresent(sftemp -> {
    
                    List<SeminarValues> values = seminarValuesRepository.findAllByForm(sftemp.getId());
    
                    // proses memasukan identitas peserta
                    ParticipantDto participantDto = new ParticipantDto();
                    participantDto.setId(p.getId());
                    participantDto.setAccount_id(p.getAccountId());
                    participantDto.setName(p.getName());
                    participantDto.setProdi_id(p.getProdiId());
                    participantDto.setStatus_cv(p.getYear());
                    participantDto.setYear(p.getYear());
    
                    Optional<Account> account = accountRepository.findById(p.getAccountId());
                    account.ifPresent(a -> {
                        participantDto.setNim(a.getUsername());
                    });
    
                    // proses memasukan nilai
                    SeminarValueParticipantDto seminarValueParticipantDto = new SeminarValueParticipantDto();
                    seminarValueParticipantDto.setNilai(values);
                    seminarValueParticipantDto.setPeserta(participantDto);
                    seminarValueParticipantDto.setNilaiTotal(seminarValuesRepository.totalSeminarValuesByForm(sftemp.getId()));
    
                    penguji.add(seminarValueParticipantDto);
                });
            });
            return penguji;
        }else{
            List<SeminarTotalValueDto> nilaiTotal     = new ArrayList<>();

            participants.forEach(p ->{
                Optional<SeminarForm> form1 = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 1);
                Optional<SeminarForm> form2 = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 2);
                Optional<SeminarForm> form3 = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 3);
    
                if(form1.isPresent() && form2.isPresent() && form3.isPresent()){
                    // proses memasukan identitas peserta
                    ParticipantDto participantDto = new ParticipantDto();
                    participantDto.setId(p.getId());
                    participantDto.setAccount_id(p.getAccountId());
                    participantDto.setName(p.getName());
                    participantDto.setProdi_id(p.getProdiId());
                    participantDto.setStatus_cv(p.getYear());
                    participantDto.setYear(p.getYear());
    
                    Optional<Account> account = accountRepository.findById(p.getAccountId());
                    account.ifPresent(a -> {
                        participantDto.setNim(a.getUsername());
                    });
    
                    SeminarTotalValueDto seminarTotalValueDto = new SeminarTotalValueDto();
                    seminarTotalValueDto.setParticipant(participantDto);
                    // mengambil rata rata dari setiap form penilaian seminar
                    seminarTotalValueDto.setNilaiTotal(
                        (
                            seminarValuesRepository.totalSeminarValuesByForm(form1.get().getId()) + 
                            seminarValuesRepository.totalSeminarValuesByForm(form2.get().getId()) +
                            seminarValuesRepository.totalSeminarValuesByForm(form3.get().getId())
                        )/3
                    );
                    nilaiTotal.add(seminarTotalValueDto);
                }
            });
            return nilaiTotal;
        }
    }

    @Override
    public RecapitulationResponseDto getRecapitulation(Integer prodiId, Integer year) {

        RecapitulationResponseDto response = new RecapitulationResponseDto();

        List<Participant> participants = participantRepository.findAllByYearAndProdi(year, prodiId);
        
        List<SeminarValueParticipantDto> penguji1 = new ArrayList<>();
        List<SeminarValueParticipantDto> penguji2 = new ArrayList<>();
        List<SeminarValueParticipantDto> penguji3 = new ArrayList<>();
        List<SeminarTotalValueDto> nilaiTotal     = new ArrayList<>();

        // penguji 1
        participants.forEach(p ->{
            Optional<SeminarForm> sFTemp = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 1);

            sFTemp.ifPresent(sftemp -> {

                List<SeminarValues> values = seminarValuesRepository.findAllByForm(sftemp.getId());

                // proses memasukan identitas peserta
                ParticipantDto participantDto = new ParticipantDto();
                participantDto.setId(p.getId());
                participantDto.setAccount_id(p.getAccountId());
                participantDto.setName(p.getName());
                participantDto.setProdi_id(p.getProdiId());
                participantDto.setStatus_cv(p.getYear());
                participantDto.setYear(p.getYear());

                Optional<Account> account = accountRepository.findById(p.getAccountId());
                account.ifPresent(a -> {
                    participantDto.setNim(a.getUsername());
                });

                // proses memasukan nilai
                SeminarValueParticipantDto seminarValueParticipantDto = new SeminarValueParticipantDto();
                seminarValueParticipantDto.setNilai(values);
                seminarValueParticipantDto.setPeserta(participantDto);
                seminarValueParticipantDto.setNilaiTotal(seminarValuesRepository.totalSeminarValuesByForm(sftemp.getId()));

                penguji1.add(seminarValueParticipantDto);
            });
        });

        // penguji 2
        participants.forEach(p ->{
            Optional<SeminarForm> sFTemp = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 2);

            sFTemp.ifPresent(sftemp -> {

                List<SeminarValues> values = seminarValuesRepository.findAllByForm(sftemp.getId());

                // proses memasukan identitas peserta
                ParticipantDto participantDto = new ParticipantDto();
                participantDto.setId(p.getId());
                participantDto.setAccount_id(p.getAccountId());
                participantDto.setName(p.getName());
                participantDto.setProdi_id(p.getProdiId());
                participantDto.setStatus_cv(p.getYear());
                participantDto.setYear(p.getYear());

                Optional<Account> account = accountRepository.findById(p.getAccountId());
                account.ifPresent(a -> {
                    participantDto.setNim(a.getUsername());
                });

                // proses memasukan nilai
                SeminarValueParticipantDto seminarValueParticipantDto = new SeminarValueParticipantDto();
                seminarValueParticipantDto.setNilai(values);
                seminarValueParticipantDto.setPeserta(participantDto);
                seminarValueParticipantDto.setNilaiTotal(seminarValuesRepository.totalSeminarValuesByForm(sftemp.getId()));

                penguji2.add(seminarValueParticipantDto);
            });
        });

        // penguji 3
        participants.forEach(p ->{
            Optional<SeminarForm> sFTemp = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 3);

            sFTemp.ifPresent(sftemp -> {

                List<SeminarValues> values = seminarValuesRepository.findAllByForm(sftemp.getId());

                // proses memasukan identitas peserta
                ParticipantDto participantDto = new ParticipantDto();
                participantDto.setId(p.getId());
                participantDto.setAccount_id(p.getAccountId());
                participantDto.setName(p.getName());
                participantDto.setProdi_id(p.getProdiId());
                participantDto.setStatus_cv(p.getYear());
                participantDto.setYear(p.getYear());

                Optional<Account> account = accountRepository.findById(p.getAccountId());
                account.ifPresent(a -> {
                    participantDto.setNim(a.getUsername());
                });

                // proses memasukan nilai
                SeminarValueParticipantDto seminarValueParticipantDto = new SeminarValueParticipantDto();
                seminarValueParticipantDto.setNilai(values);
                seminarValueParticipantDto.setPeserta(participantDto);
                seminarValueParticipantDto.setNilaiTotal(seminarValuesRepository.totalSeminarValuesByForm(sftemp.getId()));

                penguji3.add(seminarValueParticipantDto);
            });
        });

        // nilai total
        participants.forEach(p ->{
            Optional<SeminarForm> form1 = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 1);
            Optional<SeminarForm> form2 = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 2);
            Optional<SeminarForm> form3 = seminarFormRepository.findByParticipantAndTypeForm(p.getId(), 3);

            if(form1.isPresent() && form2.isPresent() && form3.isPresent()){
                // proses memasukan identitas peserta
                ParticipantDto participantDto = new ParticipantDto();
                participantDto.setId(p.getId());
                participantDto.setAccount_id(p.getAccountId());
                participantDto.setName(p.getName());
                participantDto.setProdi_id(p.getProdiId());
                participantDto.setStatus_cv(p.getYear());
                participantDto.setYear(p.getYear());

                Optional<Account> account = accountRepository.findById(p.getAccountId());
                account.ifPresent(a -> {
                    participantDto.setNim(a.getUsername());
                });

                SeminarTotalValueDto seminarTotalValueDto = new SeminarTotalValueDto();
                seminarTotalValueDto.setParticipant(participantDto);
                // mengambil rata rata dari setiap form penilaian seminar
                seminarTotalValueDto.setNilaiTotal(
                    (
                        seminarValuesRepository.totalSeminarValuesByForm(form1.get().getId()) + 
                        seminarValuesRepository.totalSeminarValuesByForm(form2.get().getId()) +
                        seminarValuesRepository.totalSeminarValuesByForm(form3.get().getId())
                    )/3
                );
                nilaiTotal.add(seminarTotalValueDto);
            }
        });
        
        response.setNilai_penguji_1(penguji1);
        response.setNilai_penguji_2(penguji2);
        response.setNilai_pembimbing(penguji3);
        response.setNilai_total_seminar(nilaiTotal);

        return response;
    }
}
