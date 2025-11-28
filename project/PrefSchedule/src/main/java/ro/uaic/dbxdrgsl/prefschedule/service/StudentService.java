package ro.uaic.dbxdrgsl.prefschedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.model.Student;
import ro.uaic.dbxdrgsl.prefschedule.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> findByCode(String code) {
        return studentRepository.findByCode(code);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findByYear(int year) {
        return studentRepository.findByYear(year);
    }

    public List<Student> findByEmailDomain(String domain) {
        return studentRepository.findByEmailDomain(domain);
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public long count() {
        return studentRepository.count();
    }
}
