package ro.uaic.dbxdrgsl.prefschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.uaic.dbxdrgsl.prefschedule.model.Pack;

import java.util.List;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {
    // Derived query - find packs by year and semester
    List<Pack> findByYearAndSemester(int year, int semester);
    
    // JPQL query - find packs with courses
    @Query("SELECT p FROM Pack p LEFT JOIN FETCH p.courses WHERE p.year = :year")
    List<Pack> findByYearWithCourses(@Param("year") int year);
    
    // Derived query - find by year
    List<Pack> findByYear(int year);
}
