package ro.uaic.dbxdrgsl.prefschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.uaic.dbxdrgsl.prefschedule.model.Grade;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    /**
     * Find all grades for a specific student by student code
     */
    @Query("SELECT g FROM Grade g JOIN FETCH g.student s JOIN FETCH g.course c WHERE s.code = :studentCode")
    List<Grade> findByStudentCode(@Param("studentCode") String studentCode);
    
    /**
     * Find all grades for a specific course by course code
     */
    @Query("SELECT g FROM Grade g JOIN FETCH g.student s JOIN FETCH g.course c WHERE c.code = :courseCode")
    List<Grade> findByCourseCode(@Param("courseCode") String courseCode);
    
    /**
     * Find a grade for a specific student-course combination
     */
    @Query("SELECT g FROM Grade g JOIN FETCH g.student s JOIN FETCH g.course c WHERE s.code = :studentCode AND c.code = :courseCode")
    Optional<Grade> findByStudentCodeAndCourseCode(@Param("studentCode") String studentCode, @Param("courseCode") String courseCode);
    
    /**
     * Count total grades
     */
    long count();
}
