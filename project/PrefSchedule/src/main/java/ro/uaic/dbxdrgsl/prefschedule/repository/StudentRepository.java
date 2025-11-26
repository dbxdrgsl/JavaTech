package ro.uaic.dbxdrgsl.prefschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.dbxdrgsl.prefschedule.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
