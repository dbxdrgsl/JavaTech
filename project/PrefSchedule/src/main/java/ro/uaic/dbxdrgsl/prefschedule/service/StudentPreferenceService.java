package ro.uaic.dbxdrgsl.prefschedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.dto.StudentPreferenceDTO;
import ro.uaic.dbxdrgsl.prefschedule.dto.StudentPreferenceResponseDTO;
import ro.uaic.dbxdrgsl.prefschedule.exception.ResourceNotFoundException;
import ro.uaic.dbxdrgsl.prefschedule.model.Course;
import ro.uaic.dbxdrgsl.prefschedule.model.Student;
import ro.uaic.dbxdrgsl.prefschedule.model.StudentPreference;
import ro.uaic.dbxdrgsl.prefschedule.repository.CourseRepository;
import ro.uaic.dbxdrgsl.prefschedule.repository.StudentPreferenceRepository;
import ro.uaic.dbxdrgsl.prefschedule.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentPreferenceService {
    private final StudentPreferenceRepository preferenceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentPreferenceService(StudentPreferenceRepository preferenceRepository,
                                     StudentRepository studentRepository,
                                     CourseRepository courseRepository) {
        this.preferenceRepository = preferenceRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public StudentPreferenceResponseDTO createPreference(StudentPreferenceDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", dto.getStudentId()));
        
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", dto.getCourseId()));

        StudentPreference preference = StudentPreference.builder()
                .student(student)
                .course(course)
                .rankOrder(dto.getRankOrder())
                .build();

        StudentPreference saved = preferenceRepository.save(preference);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Optional<StudentPreferenceResponseDTO> findById(Long id) {
        return preferenceRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Optional<StudentPreference> findEntityById(Long id) {
        return preferenceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<StudentPreferenceResponseDTO> findAll() {
        return preferenceRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentPreferenceResponseDTO> findByStudentId(Long studentId) {
        return preferenceRepository.findByStudentIdOrderByRank(studentId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentPreferenceResponseDTO> findByStudentIdAndPackId(Long studentId, Long packId) {
        return preferenceRepository.findByStudentIdAndPackId(studentId, packId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public StudentPreferenceResponseDTO updatePreference(Long id, StudentPreferenceDTO dto) {
        StudentPreference preference = preferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentPreference", id));

        if (!preference.getStudent().getId().equals(dto.getStudentId())) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", dto.getStudentId()));
            preference.setStudent(student);
        }

        if (!preference.getCourse().getId().equals(dto.getCourseId())) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", dto.getCourseId()));
            preference.setCourse(course);
        }

        preference.setRankOrder(dto.getRankOrder());
        
        StudentPreference saved = preferenceRepository.save(preference);
        return toResponseDTO(saved);
    }

    public void deleteById(Long id) {
        if (!preferenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("StudentPreference", id);
        }
        preferenceRepository.deleteById(id);
    }

    public long count() {
        return preferenceRepository.count();
    }

    private StudentPreferenceResponseDTO toResponseDTO(StudentPreference preference) {
        String packName = preference.getCourse().getPack() != null 
                ? preference.getCourse().getPack().getName() 
                : null;

        return StudentPreferenceResponseDTO.builder()
                .id(preference.getId())
                .studentId(preference.getStudent().getId())
                .studentName(preference.getStudent().getName())
                .studentCode(preference.getStudent().getCode())
                .courseId(preference.getCourse().getId())
                .courseName(preference.getCourse().getName())
                .courseCode(preference.getCourse().getCode())
                .packName(packName)
                .rankOrder(preference.getRankOrder())
                .version(preference.getVersion())
                .build();
    }
}
