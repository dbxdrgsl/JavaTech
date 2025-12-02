package ro.uaic.dbxdrgsl.prefschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.uaic.dbxdrgsl.prefschedule.model.StudentPreference;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPreferenceRepository extends JpaRepository<StudentPreference, Long> {
    
    // Derived query - find by student ID
    List<StudentPreference> findByStudentId(Long studentId);
    
    // Derived query - find by course ID
    List<StudentPreference> findByCourseId(Long courseId);
    
    // Derived query - find by student and course
    Optional<StudentPreference> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    // JPQL query - find preferences for a student ordered by rank
    @Query("SELECT sp FROM StudentPreference sp WHERE sp.student.id = :studentId ORDER BY sp.rankOrder ASC")
    List<StudentPreference> findByStudentIdOrderByRank(@Param("studentId") Long studentId);
    
    // JPQL query - find preferences for courses in a specific pack
    @Query("SELECT sp FROM StudentPreference sp WHERE sp.student.id = :studentId AND sp.course.pack.id = :packId ORDER BY sp.rankOrder ASC")
    List<StudentPreference> findByStudentIdAndPackId(@Param("studentId") Long studentId, @Param("packId") Long packId);
    
    // Delete preferences by student ID
    void deleteByStudentId(Long studentId);
}
