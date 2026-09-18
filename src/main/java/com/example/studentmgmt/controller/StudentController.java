package com.example.studentmgmt.controller;

import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Add a student: POST /students?name=John&department=CSE
    @PostMapping
    public Student addStudent(@RequestParam String name, @RequestParam String department) {
        return studentService.addStudent(name, department);
    }

    // View all students: GET /students
    @GetMapping
    public List<Student> getStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
