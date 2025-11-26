package ro.uaic.dbxdrgsl.prefschedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.model.Course;
import ro.uaic.dbxdrgsl.prefschedule.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course findByCode(String code) {
        return courseRepository.findByCode(code);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public List<Course> findByType(String type) {
        return courseRepository.findByType(type);
    }

    public List<Course> findByInstructorNameContaining(String name) {
        return courseRepository.findByInstructorNameContaining(name);
    }

    public List<Course> findOptionalCoursesByPack(Long packId) {
        return courseRepository.findOptionalCoursesByPack(packId);
    }

    public int updateDescription(Long id, String description) {
        return courseRepository.updateDescription(id, description);
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    public long count() {
        return courseRepository.count();
    }
}
