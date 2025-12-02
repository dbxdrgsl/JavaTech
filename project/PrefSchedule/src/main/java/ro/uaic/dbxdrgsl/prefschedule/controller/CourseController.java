package ro.uaic.dbxdrgsl.prefschedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.dbxdrgsl.prefschedule.model.Course;
import ro.uaic.dbxdrgsl.prefschedule.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "API for managing courses")
public class CourseController {
    
    private final CourseService courseService;
    
    @Operation(summary = "Get all courses")
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.findAll());
    }
    
    @Operation(summary = "Get course by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get courses by type")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Course>> getCoursesByType(@PathVariable String type) {
        List<Course> courses = courseService.findByType(type);
        return ResponseEntity.ok(courses);
    }
    
    @Operation(summary = "Get count of courses")
    @GetMapping("/count")
    public ResponseEntity<Long> getCourseCount() {
        long count = courseService.count();
        return ResponseEntity.ok(count);
    }
}
