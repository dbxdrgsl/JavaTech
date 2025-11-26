package ro.uaic.dbxdrgsl.prefschedule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ro.uaic.dbxdrgsl.prefschedule.service.StudentService;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class StudentUIController {
    private final StudentService service;

    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", service.findAll());
        return "students";
    }
}
