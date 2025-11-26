package ro.uaic.dbxdrgsl.prefschedule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.dbxdrgsl.prefschedule.model.Student;
import ro.uaic.dbxdrgsl.prefschedule.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // CREATE - Add a new student
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student savedStudent = studentService.save(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    // READ - Get all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }

    // READ - Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Get student by code
    @GetMapping("/code/{code}")
    public ResponseEntity<Student> getStudentByCode(@PathVariable String code) {
        return studentService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Get students by year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Student>> getStudentsByYear(@PathVariable int year) {
        List<Student> students = studentService.findByYear(year);
        return ResponseEntity.ok(students);
    }

    // UPDATE - Update an existing student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.findById(id)
                .map(existingStudent -> {
                    student.setId(id);
                    Student updatedStudent = studentService.save(student);
                    return ResponseEntity.ok(updatedStudent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Delete a student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        return studentService.findById(id)
                .map(student -> {
                    studentService.deleteById(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get count of students
    @GetMapping("/count")
    public ResponseEntity<Long> getStudentCount() {
        long count = studentService.count();
        return ResponseEntity.ok(count);
    }
}
