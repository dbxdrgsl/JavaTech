package ro.uaic.dbxdrgsl.prefschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.messaging.GradeEvent;
import ro.uaic.dbxdrgsl.prefschedule.model.Course;
import ro.uaic.dbxdrgsl.prefschedule.model.Grade;
import ro.uaic.dbxdrgsl.prefschedule.model.Student;
import ro.uaic.dbxdrgsl.prefschedule.repository.CourseRepository;
import ro.uaic.dbxdrgsl.prefschedule.repository.GradeRepository;
import ro.uaic.dbxdrgsl.prefschedule.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {
    
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    
    /**
     * Process a grade event received from QuickGrade.
     * Only processes grades for COMPULSORY courses.
     * Validates that both student and course exist in the database.
     * 
     * @param event The grade event to process
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public void processGradeEvent(GradeEvent event) {
        log.info("Processing grade event: studentCode={}, courseCode={}, grade={}", 
                event.getStudentCode(), event.getCourseCode(), event.getGrade());
        
        // 1. Find student by code
        Optional<Student> studentOpt = studentRepository.findByCode(event.getStudentCode());
        if (studentOpt.isEmpty()) {
            String errorMsg = "Student not found with code: " + event.getStudentCode();
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        Student student = studentOpt.get();
        
        // 2. Find course by code
        Optional<Course> courseOpt = courseRepository.findByCode(event.getCourseCode());
        if (courseOpt.isEmpty()) {
            String errorMsg = "Course not found with code: " + event.getCourseCode();
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        Course course = courseOpt.get();
        
        // 3. Filter: Only process COMPULSORY courses
        if (!"COMPULSORY".equalsIgnoreCase(course.getType())) {
            String errorMsg = "Grade rejected: Course is not compulsory (type=" + course.getType() + ")";
            log.warn(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        
        // 4. Check if grade already exists for this student-course combination
        Optional<Grade> existingGrade = gradeRepository.findByStudentCodeAndCourseCode(
                event.getStudentCode(), event.getCourseCode());
        
        Grade grade;
        if (existingGrade.isPresent()) {
            // Update existing grade
            grade = existingGrade.get();
            grade.setGrade(event.getGrade());
            grade.setProcessedAt(LocalDateTime.now());
            grade.setNotes("Updated from QuickGrade");
            log.info("Updated existing grade with id={}", grade.getId());
        } else {
            // Create new grade
            grade = Grade.builder()
                    .student(student)
                    .course(course)
                    .grade(event.getGrade())
                    .receivedAt(LocalDateTime.now())
                    .processedAt(LocalDateTime.now())
                    .notes("Received from QuickGrade")
                    .build();
            log.info("Created new grade entry");
        }
        
        // 5. Save to database
        Grade savedGrade = gradeRepository.save(grade);
        log.info("Successfully saved grade with id={} for student={} in course={}", 
                savedGrade.getId(), student.getCode(), course.getCode());
    }
    
    /**
     * Get all grades for a specific student
     */
    public List<Grade> getGradesByStudentCode(String studentCode) {
        return gradeRepository.findByStudentCode(studentCode);
    }
    
    /**
     * Get all grades for a specific course
     */
    public List<Grade> getGradesByCourseCode(String courseCode) {
        return gradeRepository.findByCourseCode(courseCode);
    }
    
    /**
     * Get all grades
     */
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }
    
    /**
     * Get a specific grade by student and course codes
     */
    public Optional<Grade> getGrade(String studentCode, String courseCode) {
        return gradeRepository.findByStudentCodeAndCourseCode(studentCode, courseCode);
    }
}
