package ro.uaic.dbxdrgsl.prefschedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.dbxdrgsl.prefschedule.model.Pack;
import ro.uaic.dbxdrgsl.prefschedule.repository.PackRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PackService {
    private final PackRepository packRepository;

    public PackService(PackRepository packRepository) {
        this.packRepository = packRepository;
    }

    public Pack save(Pack pack) {
        return packRepository.save(pack);
    }

    public Optional<Pack> findById(Long id) {
        return packRepository.findById(id);
    }

    public List<Pack> findAll() {
        return packRepository.findAll();
    }

    public List<Pack> findByYear(int year) {
        return packRepository.findByYear(year);
    }

    public List<Pack> findByYearAndSemester(int year, int semester) {
        return packRepository.findByYearAndSemester(year, semester);
    }

    public List<Pack> findByYearWithCourses(int year) {
        return packRepository.findByYearWithCourses(year);
    }

    public void deleteById(Long id) {
        packRepository.deleteById(id);
    }
}
