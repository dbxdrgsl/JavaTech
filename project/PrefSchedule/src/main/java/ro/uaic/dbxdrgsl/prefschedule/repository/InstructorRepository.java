package ro.uaic.dbxdrgsl.prefschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.model.Instructor;

import java.util.List;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    // Derived query - find by name
    List<Instructor> findByNameContainingIgnoreCase(String name);
    
    // JPQL query - find instructors with courses
    @Query("SELECT DISTINCT i FROM Instructor i LEFT JOIN FETCH i.courses WHERE SIZE(i.courses) > 0")
    List<Instructor> findInstructorsWithCourses();
    
    // Transactional modifying query - update email
    @Modifying
    @Transactional
    @Query("UPDATE Instructor i SET i.email = :email WHERE i.id = :id")
    int updateEmail(@Param("id") Long id, @Param("email") String email);
}
