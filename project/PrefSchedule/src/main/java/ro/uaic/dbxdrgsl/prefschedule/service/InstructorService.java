package ro.uaic.dbxdrgsl.prefschedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.model.Instructor;
import ro.uaic.dbxdrgsl.prefschedule.repository.InstructorRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InstructorService {
    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Optional<Instructor> findById(Long id) {
        return instructorRepository.findById(id);
    }

    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    public List<Instructor> findByNameContaining(String name) {
        return instructorRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Instructor> findInstructorsWithCourses() {
        return instructorRepository.findInstructorsWithCourses();
    }

    public int updateEmail(Long id, String email) {
        return instructorRepository.updateEmail(id, email);
    }

    public void deleteById(Long id) {
        instructorRepository.deleteById(id);
    }
}
