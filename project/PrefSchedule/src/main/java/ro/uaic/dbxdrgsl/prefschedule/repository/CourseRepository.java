package ro.uaic.dbxdrgsl.prefschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.model.Course;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Derived query - find by type
    List<Course> findByType(String type);
    
    // Derived query - find by code
    Course findByCode(String code);
    
    // JPQL query - find courses by instructor name
    @Query("SELECT c FROM Course c JOIN c.instructor i WHERE i.name LIKE %:name%")
    List<Course> findByInstructorNameContaining(@Param("name") String name);
    
    // JPQL query - find optional courses by pack
    @Query("SELECT c FROM Course c WHERE c.type = 'OPTIONAL' AND c.pack.id = :packId")
    List<Course> findOptionalCoursesByPack(@Param("packId") Long packId);
    
    // Transactional modifying query - update course description
    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.description = :description WHERE c.id = :id")
    int updateDescription(@Param("id") Long id, @Param("description") String description);
}
