package com.jtk.ps.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtk.ps.api.dto.ComponentAndCriteriasDto;
import com.jtk.ps.api.dto.ComponentCourseDto;
import com.jtk.ps.api.dto.CourseFormRequestDto;
import com.jtk.ps.api.dto.CourseFormResponseDto;
import com.jtk.ps.api.dto.CriteriaBodyDto;
import com.jtk.ps.api.dto.CriteriaEvaluationFormDto;
import com.jtk.ps.api.dto.EvaluationFormResponseDto;
import com.jtk.ps.api.dto.RecapitulationComponentDto;
import com.jtk.ps.api.dto.RecapitulationCourseDto;
import com.jtk.ps.api.dto.RecapitulationCriteriaDto;
import com.jtk.ps.api.dto.RecapitulationParticipantDto;
import com.jtk.ps.api.model.Account;
import com.jtk.ps.api.model.AssessmentAspect;
import com.jtk.ps.api.model.ComponentCourse;
import com.jtk.ps.api.model.CourseForm;
import com.jtk.ps.api.model.CourseValues;
import com.jtk.ps.api.model.CriteriaComponentCourse;
import com.jtk.ps.api.model.EvaluationForm;
import com.jtk.ps.api.model.EventStore;
import com.jtk.ps.api.model.Participant;
import com.jtk.ps.api.model.SelfAssessmentAspect;
import com.jtk.ps.api.model.SelfAssessmentGrade;
import com.jtk.ps.api.model.SeminarCriteria;
import com.jtk.ps.api.model.SeminarValues;
import com.jtk.ps.api.model.SupervisorGradeAspect;
import com.jtk.ps.api.model.SupervisorGradeResult;
import com.jtk.ps.api.model.Valuation;
import com.jtk.ps.api.repository.AccountRepository;
import com.jtk.ps.api.repository.AssessmentAspectRepository;
import com.jtk.ps.api.repository.ComponentCourseRepository;
import com.jtk.ps.api.repository.CourseFormRepository;
import com.jtk.ps.api.repository.CourseValuesRepository;
import com.jtk.ps.api.repository.CriteriaComponentCourseRepository;
import com.jtk.ps.api.repository.EvaluationFormRepository;
import com.jtk.ps.api.repository.EvaluationRepository;
import com.jtk.ps.api.repository.EventStoreRepository;
import com.jtk.ps.api.repository.ParticipantRepository;
import com.jtk.ps.api.repository.SelfAssessmentAspectRepository;
import com.jtk.ps.api.repository.SelfAssessmentGradeRepository;
import com.jtk.ps.api.repository.SeminarCriteriaRepository;
import com.jtk.ps.api.repository.SeminarValuesRepository;
import com.jtk.ps.api.repository.SupervisorGradeAspectRepository;
import com.jtk.ps.api.repository.SupervisorGradeResultRepository;
import com.jtk.ps.api.repository.ValuationRepository;
import com.jtk.ps.api.service.Interface.ICourseService;

@Service
public class CourseService implements ICourseService{

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    @Lazy
    private CourseFormRepository courseFormRepository;

    @Autowired
    @Lazy
    private EventStoreRepository eventStoreRepository;

    @Autowired
    @Lazy
    private ComponentCourseRepository componentCourseRepository;

    @Autowired
    @Lazy
    private EvaluationFormRepository evaluationFormRepository;

    @Autowired
    @Lazy
    private AssessmentAspectRepository assessmentAspectRepository;

    @Autowired
    @Lazy
    private SelfAssessmentAspectRepository selfAssessmentAspectRepository;

    @Autowired
    @Lazy
    private SupervisorGradeAspectRepository supervisorGradeAspectRepository;

    @Autowired
    @Lazy
    private SeminarCriteriaRepository seminarCriteriaRepository;

    @Autowired
    @Lazy
    private CriteriaComponentCourseRepository criteriaComponentCourseRepository;

    @Autowired
    @Lazy
    private CourseValuesRepository courseValuesRepository;

    @Autowired
    @Lazy
    private ParticipantRepository participantRepository;

    @Autowired
    @Lazy
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private EvaluationRepository evaluationRepository;

    @Autowired
    @Lazy
    private SelfAssessmentGradeRepository selfAssessmentGradeRepository;

    @Autowired
    @Lazy
    private SupervisorGradeResultRepository supervisorGradeResultRepository;

    @Autowired
    @Lazy
    private ValuationRepository valuationRepository;

    @Autowired
    @Lazy
    private SeminarValuesRepository seminarValuesRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private void eventStoreHandler(String entityId, String eventType, Object object,Integer eventDataId){
        try {
            EventStore eventStore = new EventStore();

            eventStore.setEntityId(entityId);
            eventStore.setEventType(eventType);
            eventStore.setEventTime(LocalDateTime.now().plusYears(1));
            eventStore.setEventData(objectMapper.writeValueAsString(object));
            eventStore.setEventDataId(eventDataId);

            eventStoreRepository.save(eventStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CourseFormResponseDto> getAllCourse() {
        List<CourseForm> courseForms = courseFormRepository.findAllCourse();

        List<CourseFormResponseDto> courseFormResponseDtos = new ArrayList<>();
        courseForms.forEach(c -> {

            CourseFormResponseDto temp = new CourseFormResponseDto();

            temp.setId(c.getId());
            temp.setKode(c.getKode());
            temp.setProdiId(c.getProdiId());
            temp.setSks(c.getSks());
            temp.setTahunAjaranEnd(c.getTahunAjaranEnd());
            temp.setTahunAjaranStart(c.getTahunAjaranStart());
            temp.setName(c.getName());

            courseFormResponseDtos.add(temp);
        });
        return courseFormResponseDtos;
    }

    private void create6ComponentCourses(Integer idForm){
        String[] component = {
            "EAS Praktek", "EAS Teori", "ETS Praktek", "ETS Teori", "Lain-lain Praktek", "Lain-lain Teori"
        };

        for (int i = 0; i < 6; i++) {
            ComponentCourse componentCourse = new ComponentCourse();
            
            componentCourse.setBobotComponent(0);
            componentCourse.setCourseId(idForm);
            componentCourse.setIsAverage(1);
            componentCourse.setName(component[i]);

            componentCourse = componentCourseRepository.save(componentCourse);
            eventStoreHandler("component_course", "COMPONENT_COURSE_ADDED", componentCourse, componentCourse.getId());
        }
    }

    @Override
    public CourseForm createCourseForm(CourseFormRequestDto courseFormRequestDto) {
        
        CourseForm courseForm = new CourseForm();

        courseForm.setKode(courseFormRequestDto.getKode());
        courseForm.setName(courseFormRequestDto.getName());
        courseForm.setProdiId(courseFormRequestDto.getProdiId());
        courseForm.setSks(courseFormRequestDto.getSks());
        courseForm.setTahunAjaranEnd(courseFormRequestDto.getTahunAjaranEnd());
        courseForm.setTahunAjaranStart(courseFormRequestDto.getTahunAjaranStart());
        courseForm.setIsDeleted(0);

        courseForm = courseFormRepository.save(courseForm);

        // membuat komponen
        create6ComponentCourses(courseForm.getId());

        // mencatat event
        eventStoreHandler("course_form", "COURSE_FORM_ADDED", courseForm, courseForm.getId());

        return courseForm;
    }

    @Override
    public CourseFormResponseDto getDetailCourse(Integer idForm) {

        CourseFormResponseDto courseFormResponseDto = new CourseFormResponseDto();

        Optional<CourseForm> cOptional = courseFormRepository.findById(idForm);

        cOptional.ifPresent(c -> {
            courseFormResponseDto.setId(c.getId());
            courseFormResponseDto.setKode(c.getKode());
            courseFormResponseDto.setName(c.getName());
            courseFormResponseDto.setProdiId(c.getProdiId());
            courseFormResponseDto.setSks(c.getSks());
            courseFormResponseDto.setTahunAjaranEnd(c.getTahunAjaranEnd());
            courseFormResponseDto.setTahunAjaranStart(c.getTahunAjaranStart());
        });
        
        return courseFormResponseDto;
    }

    @Override
    public void deleteCourseForm(Integer idForm) {
        Optional<CourseForm> courseForm = courseFormRepository.findById(idForm);

        courseForm.ifPresent(c -> {
            c.setIsDeleted(1);

            eventStoreHandler("course_form", "COURSE_FORM_UPDATE", courseFormRepository.save(c), c.getId());
        });
    }

    @Override
    public void updateCourseForm(Integer idForm, CourseFormRequestDto courseFormRequestDto) {
        
        Optional<CourseForm> newCourseForm = courseFormRepository.findById(idForm);

        newCourseForm.ifPresent(c -> {
            c.setKode(courseFormRequestDto.getKode());
            c.setName(courseFormRequestDto.getName());
            c.setProdiId(courseFormRequestDto.getProdiId());
            c.setSks(courseFormRequestDto.getSks());
            c.setTahunAjaranEnd(courseFormRequestDto.getTahunAjaranEnd());
            c.setTahunAjaranStart(courseFormRequestDto.getTahunAjaranStart());

            eventStoreHandler("course_form", "COURSE_FORM_UPDATE", courseFormRepository.save(c), c.getId());
        });
    }

    @Override
    public List<EvaluationFormResponseDto> getEvaluationForm(Integer prodiId) {
        
        String[] formNames = {
            "Industri", "Pembimbing", "Self Assessment", "Seminar"
        };

        List<EvaluationFormResponseDto> evaluationForms = new ArrayList<>();

        List<EvaluationForm> industriForms = evaluationFormRepository.findAllByProdiId(prodiId);

        for (int i = 0; i < formNames.length; i++) {
            if(formNames[i] == "Industri"){
                industriForms.forEach(c -> {
                    EvaluationFormResponseDto temp = new EvaluationFormResponseDto();
                    temp.setFormType("Industri "+String.valueOf(c.getNumEvaluation()));
                    temp.setFormName("Industri");

                    evaluationForms.add(temp);
                });
            }else{
                EvaluationFormResponseDto temp = new EvaluationFormResponseDto();

                temp.setFormType(formNames[i]);
                temp.setFormName(formNames[i]);

                evaluationForms.add(temp);
            }
        }

        return evaluationForms;
    }

    @Override
    public List<CriteriaEvaluationFormDto> getCriteriaByEvaluationForm(String formType, Integer prodiId) {

        List<CriteriaEvaluationFormDto> criteriaEvaluationForms = new ArrayList<>();
        String[] numEvaluation = formType.split(" ");
        switch (formType) {
            case "Pembimbing":
                List<SupervisorGradeAspect> pembimbingAspects = supervisorGradeAspectRepository.findAll();

                pembimbingAspects.forEach(p -> {
                    CriteriaEvaluationFormDto temp = new CriteriaEvaluationFormDto();

                    temp.setId(p.getId());
                    temp.setName(p.getDescription());

                    criteriaEvaluationForms.add(temp);
                });
                break;
            case "Self Assessment":
                List<SelfAssessmentAspect> selfAssessmentAspects = selfAssessmentAspectRepository.findAll();

                selfAssessmentAspects.forEach(s -> {
                    CriteriaEvaluationFormDto temp = new CriteriaEvaluationFormDto();

                    temp.setId(s.getId());
                    temp.setName(s.getName());

                    criteriaEvaluationForms.add(temp);
                });
                break;
            case "Seminar":
                List<SeminarCriteria> seminarCriterias = seminarCriteriaRepository.findAllBySelected();

                seminarCriterias.forEach(s -> {
                    CriteriaEvaluationFormDto temp = new CriteriaEvaluationFormDto();

                    temp.setId(s.getId());
                    temp.setName(s.getCriteriaName());

                    criteriaEvaluationForms.add(temp);
                });
                break;
        
            default:
                LOGGER.info(String.format("******* value fo numEvaluation %s", numEvaluation[1]));
                List<AssessmentAspect> industriAspects = assessmentAspectRepository.findAllByNumEvaluation(Integer.parseInt(numEvaluation[1]),prodiId);

                industriAspects.forEach(i -> {
                    CriteriaEvaluationFormDto temp = new CriteriaEvaluationFormDto();

                    temp.setId(i.getId());
                    temp.setName(i.getAspectName());

                    criteriaEvaluationForms.add(temp);
                });
                break;
        }

        return criteriaEvaluationForms;
    }

	@Override
	public List<ComponentCourseDto> getComponentByCourseForm(Integer idForm) {
		
        List<ComponentCourseDto> response = new ArrayList<>();

        List<ComponentCourse> componentCourses = componentCourseRepository.findAllByFormId(idForm);

        componentCourses.forEach(c -> {
            ComponentCourseDto temp = new ComponentCourseDto();
            
            temp.setBobotComponent(c.getBobotComponent());
            temp.setCourseId(c.getCourseId());
            temp.setId(c.getId());
            temp.setIsAverage(c.getIsAverage());
            temp.setName(c.getName());

            response.add(temp);
        });
		return response;
	}

	@Override
	public void updateComponent(List<ComponentCourseDto> componentCourseDtos) {
		componentCourseDtos.forEach(c -> {
            Optional<ComponentCourse> componentCourse = componentCourseRepository.findById(c.getId());

            componentCourse.ifPresent(p -> {
                p.setBobotComponent(c.getBobotComponent());
                p.setCourseId(c.getCourseId());
                p.setIsAverage(c.getIsAverage());
                p.setName(c.getName());

                eventStoreHandler("component_course", "COMPONENT_COURSE_UPDATE", componentCourseRepository.save(p), p.getId());
            });
        });
	}

    

	@Override
    public List<ComponentAndCriteriasDto> getCriteriaComponentByCourseFormId(Integer idForm) {
        
        List<ComponentAndCriteriasDto> response = new ArrayList<>();

        List<ComponentCourse> componentCourses = componentCourseRepository.findAllByFormId(idForm);
        

        componentCourses.forEach(c -> {
            ComponentAndCriteriasDto temp = new ComponentAndCriteriasDto();
            
            temp.setId(c.getId());
            temp.setIsAverage(c.getIsAverage());
            temp.setName(c.getName());

            List<CriteriaComponentCourse> criteriaComponentCourses = criteriaComponentCourseRepository.findAllByComponentId(c.getId());

            List<CriteriaBodyDto> criteriaForResponses = new ArrayList<>();

            // mengambil data criteria pada komponen tertentu
            criteriaComponentCourses.forEach(p -> {
                CriteriaBodyDto criteriaTemp = new CriteriaBodyDto();

                criteriaTemp.setComponentId(p.getComponentId());
                criteriaTemp.setId(p.getId());;
                criteriaTemp.setNameForm(p.getNameForm());
                criteriaTemp.setTypeForm(p.getTypeForm());
                criteriaTemp.setBobotCriteria(p.getBobotCriteria());

                if(p.getIndustryCriteriaId() != null){
                    criteriaTemp.setAspectFormId(p.getIndustryCriteriaId());
                    criteriaTemp.setAspectName(criteriaComponentCourseRepository.getNameAspectFromIndustry(p.getIndustryCriteriaId()));
                }else if(p.getSelfAssessmentCriteriaId() != null){
                    criteriaTemp.setAspectFormId(p.getSelfAssessmentCriteriaId());
                    criteriaTemp.setAspectName(criteriaComponentCourseRepository.getNameAspectFromSelfAssessment(p.getSelfAssessmentCriteriaId()));
                }else if(p.getSeminarCriteriaId() != null){
                    criteriaTemp.setAspectFormId(p.getSeminarCriteriaId());
                    criteriaTemp.setAspectName(criteriaComponentCourseRepository.getNameAspectFromSeminar(p.getSeminarCriteriaId()));
                }else if(p.getSupervisorCriteriaId() != null){
                    criteriaTemp.setAspectFormId(p.getSupervisorCriteriaId());
                    criteriaTemp.setAspectName(criteriaComponentCourseRepository.getNameAspectFromSupervisor(p.getSupervisorCriteriaId()));
                }

                criteriaForResponses.add(criteriaTemp);
            });
            // LOGGER.info(String.format("%%%%%%%%%%% => ", temp.getName()));

            temp.setCriteria_data(criteriaForResponses);

            response.add(temp);
        });

        return response;
    }

    @Override
	public void updateOrInsertCriteriaComponent(ComponentAndCriteriasDto newCriterias) {

        List<CriteriaComponentCourse> oldCriterias = criteriaComponentCourseRepository.findAllByComponentId(newCriterias.getId());

        List<Integer> doneUpdateOrDelete = new ArrayList<>();

        oldCriterias.forEach(o -> {
            Integer isExist = 0;
            
            // mencari tahu apakah criteria ini dihapus atau tidak
            for (int i = 0; i < newCriterias.getCriteria_data().size(); i++) {
                CriteriaBodyDto n = newCriterias.getCriteria_data().get(i);
                if(o.getId() == n.getId()){
                    o.setBobotCriteria(n.getBobotCriteria());
                    
                    isExist = 1;
                    doneUpdateOrDelete.add(n.getId());
                    eventStoreHandler("criteria_component_course", "CRITERIA_COMPONENT_COURSE_UPDATE",criteriaComponentCourseRepository.save(o), o.getId());
                }
            }
            // jika criteria tidak ada pada newCriteria maka akan dihapus
            // menentukan soft delete atau hard delete
            if(isExist == 0){
                LOGGER.info(String.format("year now ==> %d", Integer.valueOf(Year.now().plusYears(1).toString())));
                LOGGER.info(String.format("nilai dari is criteria %d", courseValuesRepository.isCriteriaInYearNowUse(o.getId(), Integer.valueOf(Year.now().plusYears(1).toString()))));

                // soft delete
                if(courseValuesRepository.isCriteriaInBeforeYearUse(o.getId(), Integer.valueOf(Year.now().plusYears(1).toString())) > 0){
                    o.setIsDeleted(1);
                    // menghapus values pada tahun sekarang dan criteria id tersebut
                    courseValuesRepository.deleteAllInCriteriaIdAndYear(o.getId(), Integer.valueOf(Year.now().plusYears(1).toString()));
                    eventStoreHandler("criteria_component_course", "CRITERIA_COMPONENT_COURSE_UPDATE",criteriaComponentCourseRepository.save(o), o.getId());
                }else{ //hard delete
                    
                    courseValuesRepository.deleteAllInCriteriaIdAndYear(o.getId(), Integer.valueOf(Year.now().plusYears(1).toString()));
                    
                    criteriaComponentCourseRepository.delete(o);

                    eventStoreHandler("criteria_component_course", "CRITERIA_COMPONENT_COURSE_DELETE",o, o.getId());
                }
            }
        });
        
        // create criteria baru jika masih ada sisah
        newCriterias.getCriteria_data().forEach(n -> {
            if(doneUpdateOrDelete.contains(n.getId()) == false){
                CriteriaComponentCourse newTemp = new CriteriaComponentCourse();
                
                newTemp.setBobotCriteria(n.getBobotCriteria());
                newTemp.setComponentId(n.getComponentId());
                newTemp.setNameForm(n.getNameForm());
                newTemp.setTypeForm(n.getTypeForm());
                newTemp.setIsDeleted(0);
                
                switch(n.getNameForm()){
                    case "Industri":
                        newTemp.setIndustryCriteriaId(n.getAspectFormId());
                        break;
                    case "Seminar":
                        newTemp.setSeminarCriteriaId(n.getAspectFormId());
                        break;
                    case "Pembimbing":
                        newTemp.setSupervisorCriteriaId(n.getAspectFormId());
                        break;
                    case "Self Assessment":
                        newTemp.setSelfAssessmentCriteriaId(n.getAspectFormId());
                        break;
                }
                newTemp = criteriaComponentCourseRepository.save(newTemp);
                eventStoreHandler("criteria_component_course", "CRITERIA_COMPONENT_COURSE_ADDED", newTemp, newTemp.getId());
            }
        });

        // update is average pada component 
        Optional<ComponentCourse> newComponent = componentCourseRepository.findById(newCriterias.getId());

        newComponent.ifPresent(c -> {
            c.setIsAverage(newCriterias.getIsAverage());

            eventStoreHandler("component_course", "COMPONENT_COURSE_UPDATE", componentCourseRepository.save(c), c.getId());
        });
	}

    @Override
    public List<RecapitulationCourseDto> getAllRecapitulationByYearAndProdiId(Integer year, Integer prodiId) {
        // tampilkan untuk tahun sekarang aja dulu
        // cari mata kuliah dengan tahun dan prodiId 
        final Integer yearNow = Integer.parseInt(Year.now().plusYears(1).toString());
        List<CourseForm> courseForms = new ArrayList<>();
        if(year == yearNow){
            courseForms = courseFormRepository.findAllCourseByYearAndProdiId(year, prodiId);
        }else{
            courseForms = courseFormRepository.findAllOldCourseByYearAndProdiId(year, prodiId);
        }

        List<RecapitulationCourseDto> responseCourses = new ArrayList<>();

        courseForms.forEach(f -> {
            
            RecapitulationCourseDto tempCourseDtos = new RecapitulationCourseDto();

            tempCourseDtos.setIdCourse(f.getId());
            tempCourseDtos.setNameCourse(f.getName());
            tempCourseDtos.setKode(f.getKode());
            // sekarang mencari peserta yang ada pada mata kuliah tersebut
            // mencari peserta pada year dan prodiId
            
            tempCourseDtos.setParticipant_data(
                getListParticipantForRecapitulation(year, prodiId,f)
            );
            responseCourses.add(tempCourseDtos);
        });

        return responseCourses;
    }

    private List<RecapitulationParticipantDto> getListParticipantForRecapitulation(Integer year, Integer prodiId,CourseForm form){
        List<RecapitulationParticipantDto> responseParticipantDtos = new ArrayList<>();

        List<Participant> participants = participantRepository.findAllByYearAndProdi(year, prodiId);

        participants.forEach(p -> {
            
            RecapitulationParticipantDto tempParticipantDto = new RecapitulationParticipantDto();

            tempParticipantDto.setIdParticipant(p.getId());
            tempParticipantDto.setName(p.getName());

            Optional<Account> account = accountRepository.findById(p.getAccountId());
            account.ifPresent(a -> {
                tempParticipantDto.setNim(a.getUsername());
            });

            // ***************************** Component *****************************
            tempParticipantDto.setComponent_data(
                getListComponentRecapitulation(year, prodiId, form, p)
            );

            // // setelah menghitung semua kriterianya
            tempParticipantDto.setTotal_course(
                getTotalCourse(tempParticipantDto.getComponent_data())
            );

            responseParticipantDtos.add(tempParticipantDto);
        });
        return responseParticipantDtos;
    }

    private Float getTotalCourse(List<RecapitulationComponentDto> courses){
        float totalCourse = 0f;

        for(RecapitulationComponentDto c : courses){
            if(c.getBobotComponent() != 0){
                totalCourse += (c.getTotalValueComponent() / 100) * c.getBobotComponent();
            }
        }
        return totalCourse;
    }

    private List<RecapitulationComponentDto> getListComponentRecapitulation(Integer year, Integer prodiId,CourseForm form,Participant participant){
        
        List<RecapitulationComponentDto> tempRecapitulationComponentDtos = new ArrayList<>();
        Integer yearNow = Integer.parseInt(Year.now().plusYears(1).toString());
        List<ComponentCourse> componentCourses = componentCourseRepository.findAllByFormId(form.getId());

        componentCourses.forEach(c -> {
                
            RecapitulationComponentDto tempRecapitulationComponentDto = new RecapitulationComponentDto();
            int isAverage = c.getIsAverage();
            tempRecapitulationComponentDto.setIdComponent(c.getId());
            tempRecapitulationComponentDto.setNameComponent(c.getName());
            if(year == yearNow){
                tempRecapitulationComponentDto.setBobotComponent(c.getBobotComponent());
            }else{
                EventStore eventStoreComponent = eventStoreRepository.getLastUpdateBobotComponentCourse(year, c.getId());
                try {
                    JsonNode rootNode = objectMapper.readTree(eventStoreComponent.getEventData());
                    tempRecapitulationComponentDto.setBobotComponent(
                        rootNode.get("bobotComponent").asInt()
                    );
                    isAverage = rootNode.get("isAverage").asInt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // ***************************** Criteria and Values *****************************
            tempRecapitulationComponentDto.setCriteria_data(
                getListCriteriaRecapitulation(year, prodiId, c, participant)
            );

            tempRecapitulationComponentDto.setTotalValueComponent(
                getTotalComponent(isAverage,tempRecapitulationComponentDto.getCriteria_data())
            );

            tempRecapitulationComponentDtos.add(tempRecapitulationComponentDto);
        });

        return tempRecapitulationComponentDtos;
    }

    private Float getTotalComponent(Integer isAverage, List<RecapitulationCriteriaDto> listValues){
        float totalComponent = 0f;
        if(listValues.size() == 0){
            return totalComponent;
        }

        if(isAverage == 1){
            for(RecapitulationCriteriaDto v : listValues){
                totalComponent += v.getValue();
            }
            totalComponent = totalComponent / listValues.size();
        }else{
            for(RecapitulationCriteriaDto v : listValues){
                totalComponent += (v.getValue()/100) * v.getBobot();
            }
        }
        return totalComponent;
    }

    private List<RecapitulationCriteriaDto> getListCriteriaRecapitulation(Integer year, Integer prodiId, ComponentCourse component, Participant participant){

        List<RecapitulationCriteriaDto> tCriteriaDtos = new ArrayList<>();
        Integer yearNow = Integer.parseInt(Year.now().plusYears(1).toString());

        List<CriteriaComponentCourse> criteriaComponentCourses = new ArrayList<>();
        if(year == yearNow){
            criteriaComponentCourses = criteriaComponentCourseRepository.findAllByComponentId(component.getId());
        }else{
            criteriaComponentCourses = criteriaComponentCourseRepository.findCriteriaLastYear(year, component.getId());
        }

        criteriaComponentCourses.forEach(d -> {
            RecapitulationCriteriaDto tempRecapitulationCriteriaDto = new RecapitulationCriteriaDto();
            
            if(year == yearNow){
                tempRecapitulationCriteriaDto.setBobot(d.getBobotCriteria());
            }else{
                EventStore eventStoreCriteria = eventStoreRepository.getLastUpdateBobotCriteriaComponent(year, d.getId());
                try {
                    JsonNode rootNode = objectMapper.readTree(eventStoreCriteria.getEventData());
                    tempRecapitulationCriteriaDto.setBobot(
                        rootNode.get("bobotCriteria").asInt()
                    );
                } catch (Exception e) {
                    e.printStackTrace();;
                }
            }
            tempRecapitulationCriteriaDto.setNameForm(d.getNameForm());
            tempRecapitulationCriteriaDto.setFormType(d.getTypeForm());
            tempRecapitulationCriteriaDto.setNameAspect(getAspectNameInForm(d.getIndustryCriteriaId(), d.getSeminarCriteriaId(), d.getSupervisorCriteriaId(), d.getSelfAssessmentCriteriaId(), d.getNameForm()));
            tempRecapitulationCriteriaDto.setIdCriteria(d.getId());
            tempRecapitulationCriteriaDto.setValue(findValueByCriteriaIdAndParticipantId(d,participant.getId()));

            tCriteriaDtos.add(tempRecapitulationCriteriaDto);
        });

        return tCriteriaDtos;
    }

    private Float findValueByCriteriaIdAndParticipantId(CriteriaComponentCourse criteria, Integer pId){

        Optional<CourseValues> courseValues = courseValuesRepository.findByCriteriaIdAndParticipantId(criteria.getId(), pId);
        CourseValues newValues = new CourseValues();
        float value = 0f;
        
        if(courseValues.isPresent()){
            if(criteria.getBobotCriteria() == 100){
                value = courseValues.get().getValue();
            }else{
                value = courseValues.get().getValue() * criteria.getBobotCriteria();
            }
        }else{
            // jika nilai blm dimasukan ke tabel
            switch(criteria.getNameForm()){
                case "Industri":
                //    mengambil nilai pada form 
                    Optional<Valuation> vIndustri = valuationRepository.findValueByParticipant(criteriaComponentCourseRepository.getNameAspectFromIndustry(criteria.getIndustryCriteriaId()),Integer.parseInt(criteria.getTypeForm().substring(9)), pId);
                    if(vIndustri.isPresent()){
                        value = (float) vIndustri.get().getValue();
                        newValues.setCriteriaId(criteria.getId());
                        newValues.setIndustryValuesId(vIndustri.get().getId());
                        newValues.setCreated_date(LocalDate.now().plusYears(1));
                        newValues.setParticipantId(pId);
                        newValues.setValue(value);

                        courseValuesRepository.save(newValues);
                    }else{
                        value = (float) 0;
                    }
                    break;
                case "Seminar":
                    value = seminarValuesRepository.findValuesByCriteriaIdAndParticipantId(criteria.getSeminarCriteriaId(), pId);
                    if(value != (float) 0){
                        List<SeminarValues> tSeminarValues = seminarValuesRepository.findAllValuesCriteriaByParticipant(criteria.getSeminarCriteriaId(), pId);
                        String seminarValuesId = new String();

                        for (int i = 0; i < tSeminarValues.size(); i++) {
                            seminarValuesId = seminarValuesId.concat("-"+tSeminarValues.get(i).getId());
                        }

                        seminarValuesId = seminarValuesId.substring(1,seminarValuesId.length());

                        newValues.setCreated_date(LocalDate.now().plusYears(1));
                        newValues.setCriteriaId(criteria.getId());
                        newValues.setSeminarValuesId(seminarValuesId);
                        newValues.setParticipantId(pId);
                        newValues.setValue(value);

                        courseValuesRepository.save(newValues);
                    }else{
                        value = (float) 0;
                    }
                    break;
                case "Pembimbing":
                    value = supervisorGradeResultRepository.findValuesByAspectIdAndParticipantId(criteria.getSupervisorCriteriaId(), pId);

                    if(value != (float) 0){
                        List<SupervisorGradeResult> tGradeResults = supervisorGradeResultRepository.findAllValuesAspectByParticipant(criteria.getSupervisorCriteriaId(), pId);
                        String supervisorValuesId = new String();

                        for (int i = 0; i < tGradeResults.size(); i++) {
                            supervisorValuesId = supervisorValuesId.concat("-"+tGradeResults.get(i).getId());
                        }

                        supervisorValuesId = supervisorValuesId.substring(1,supervisorValuesId.length());

                        newValues.setCreated_date(LocalDate.now().plusYears(1));
                        newValues.setCriteriaId(criteria.getId());
                        newValues.setMentorValuesId(supervisorValuesId);
                        newValues.setParticipantId(pId);
                        newValues.setValue(value);

                        courseValuesRepository.save(newValues);
                    }
                    break;
                case "Self Assessment":
                    value = selfAssessmentGradeRepository.findValuesByCriteriaIdAndParticipantId(criteria.getSelfAssessmentCriteriaId(), pId);
                    if(value != (float) 0){
                        List<SelfAssessmentGrade> tAssessmentGrades = selfAssessmentGradeRepository.findAllValuesCriteriaByParticipant(criteria.getSelfAssessmentCriteriaId(), pId);
                        String assessmentValuesId = new String();

                        for (int i = 0; i < tAssessmentGrades.size(); i++) {
                            assessmentValuesId = assessmentValuesId.concat("-"+tAssessmentGrades.get(i).getId());
                        }

                        assessmentValuesId = assessmentValuesId.substring(1,assessmentValuesId.length());

                        newValues.setCreated_date(LocalDate.now().plusYears(1));
                        newValues.setCriteriaId(criteria.getId());
                        newValues.setSelfAssessmentValuesId(assessmentValuesId);
                        newValues.setValue(value);
                        newValues.setParticipantId(pId);

                        courseValuesRepository.save(newValues);
                    }else{
                        value = (float) 0;
                    }
                    break;
            }
        }

        return value;
    }
    
    private String getAspectNameInForm(Integer idIndustri, Integer idSeminar, Integer idPembimbing, Integer idSelfAssessment, String formName){
        String aspectName = "";
        switch(formName){
            case "Industri":
                Optional<AssessmentAspect> aOptional = assessmentAspectRepository.findById(idIndustri);
                aspectName = aOptional.map(AssessmentAspect::getAspectName).orElse("");
                break;
            case "Seminar":
                Optional<SeminarCriteria> seminarCriteriaOptional = seminarCriteriaRepository.findById(idSeminar);
                aspectName = seminarCriteriaOptional.map(SeminarCriteria::getCriteriaName).orElse("");
                break;
            case "Pembimbing":
                Optional<SupervisorGradeAspect> supervisorGradeAspectOptional = supervisorGradeAspectRepository.findById(idPembimbing);
                aspectName = supervisorGradeAspectOptional.map(SupervisorGradeAspect::getDescription).orElse("");
                break;
            case "Self Assessment":
                Optional<SelfAssessmentAspect> selfAssessmentAspectOptional = selfAssessmentAspectRepository.findById(idSelfAssessment);
                aspectName = selfAssessmentAspectOptional.map(SelfAssessmentAspect::getName).orElse("");
                break;
            default:
                aspectName = "";
                break;
        }

        return aspectName;
    }
    
}
