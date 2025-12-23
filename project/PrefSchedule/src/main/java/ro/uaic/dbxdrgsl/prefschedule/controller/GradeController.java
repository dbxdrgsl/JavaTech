package ro.uaic.dbxdrgsl.prefschedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.uaic.dbxdrgsl.prefschedule.model.Grade;
import ro.uaic.dbxdrgsl.prefschedule.service.GradeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "API for viewing grades received from QuickGrade")
public class GradeController {
    
    private final GradeService gradeService;
    
    @Operation(summary = "Get all grades")
    @GetMapping
    public ResponseEntity<List<Grade>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }
    
    @Operation(summary = "Get grades by student code")
    @GetMapping("/student/{studentCode}")
    public ResponseEntity<List<Grade>> getGradesByStudent(@PathVariable String studentCode) {
        List<Grade> grades = gradeService.getGradesByStudentCode(studentCode);
        return ResponseEntity.ok(grades);
    }
    
    @Operation(summary = "Get grades by course code")
    @GetMapping("/course/{courseCode}")
    public ResponseEntity<List<Grade>> getGradesByCourse(@PathVariable String courseCode) {
        List<Grade> grades = gradeService.getGradesByCourseCode(courseCode);
        return ResponseEntity.ok(grades);
    }
    
    @Operation(summary = "Get a specific grade by student and course")
    @GetMapping("/student/{studentCode}/course/{courseCode}")
    public ResponseEntity<Grade> getGrade(
            @PathVariable String studentCode,
            @PathVariable String courseCode) {
        Optional<Grade> grade = gradeService.getGrade(studentCode, courseCode);
        return grade.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
