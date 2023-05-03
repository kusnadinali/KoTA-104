package com.jtk.ps.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtk.ps.api.dto.ComponentAndCriteriasDto;
import com.jtk.ps.api.dto.ComponentCourseDto;
import com.jtk.ps.api.dto.CourseFormRequestDto;
import com.jtk.ps.api.dto.CourseFormResponseDto;
import com.jtk.ps.api.dto.CriteriaBodyDto;
import com.jtk.ps.api.dto.CriteriaEvaluationFormDto;
import com.jtk.ps.api.dto.EvaluationFormResponseDto;
import com.jtk.ps.api.dto.RecapitulationCourseDto;
import com.jtk.ps.api.model.AssessmentAspect;
import com.jtk.ps.api.model.ComponentCourse;
import com.jtk.ps.api.model.CourseForm;
import com.jtk.ps.api.model.CriteriaComponentCourse;
import com.jtk.ps.api.model.EvaluationForm;
import com.jtk.ps.api.model.EventStore;
import com.jtk.ps.api.model.SelfAssessmentAspect;
import com.jtk.ps.api.model.SeminarCriteria;
import com.jtk.ps.api.model.SupervisorGradeAspect;
import com.jtk.ps.api.repository.AssessmentAspectRepository;
import com.jtk.ps.api.repository.ComponentCourseRepository;
import com.jtk.ps.api.repository.CourseFormRepository;
import com.jtk.ps.api.repository.CriteriaComponentCourseRepository;
import com.jtk.ps.api.repository.EvaluationFormRepository;
import com.jtk.ps.api.repository.EventStoreRepository;
import com.jtk.ps.api.repository.SelfAssessmentAspectRepository;
import com.jtk.ps.api.repository.SeminarCriteriaRepository;
import com.jtk.ps.api.repository.SupervisorGradeAspectRepository;
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
    private ObjectMapper objectMapper;

    private void eventStoreHandler(String entityId, String eventType, Object object,Integer eventDataId){
        try {
            EventStore eventStore = new EventStore();

            eventStore.setEntityId(entityId);
            eventStore.setEventType(eventType);
            eventStore.setEventTime(LocalDateTime.now());
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
            componentCourse.setIsAverage(0);
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

            eventStoreHandler("course_form", "COURSE_FORM_DELETE", courseFormRepository.save(c), c.getId());
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
		// TODO Auto-generated method stub
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
                }else if(p.getSelfAssessmentCriteriaId() != null){
                    criteriaTemp.setAspectFormId(p.getSelfAssessmentCriteriaId());
                }else if(p.getSeminarCriteriaId() != null){
                    criteriaTemp.setAspectFormId(p.getSeminarCriteriaId());
                }else if(p.getSupervisorCriteriaId() != null){
                    criteriaTemp.setAspectFormId(p.getSupervisorCriteriaId());
                }

                criteriaForResponses.add(criteriaTemp);
            });

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
            if(isExist == 1){
                criteriaComponentCourseRepository.delete(o);
                eventStoreHandler("criteria_component_course", "CRITERIA_COMPONENT_COURSE_DELETE",o, o.getId());
            }
        });

        // create criteria baru jika masih ada sisah
        newCriterias.getCriteria_data().forEach(n -> {
            if(doneUpdateOrDelete.contains(n.getId())){
                CriteriaComponentCourse newTemp = new CriteriaComponentCourse();
                
                newTemp.setBobotCriteria(n.getBobotCriteria());
                newTemp.setComponentId(n.getComponentId());
                newTemp.setNameForm(n.getNameForm());
                newTemp.setTypeForm(n.getTypeForm());
                
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
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
