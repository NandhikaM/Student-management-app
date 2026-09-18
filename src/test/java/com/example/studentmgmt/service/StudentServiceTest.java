package com.example.studentmgmt.service;

import com.example.studentmgmt.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService();
    }

    @Test
    void addStudent_shouldStoreStudentWithCorrectFields() {
        Student student = studentService.addStudent("Nandhika", "CSE");

        assertEquals("Nandhika", student.getName());
        assertEquals("CSE", student.getDepartment());
        assertTrue(student.getId() > 0);
    }

    @Test
    void getAllStudents_shouldReturnAllAddedStudents() {
        studentService.addStudent("Alice", "IT");
        studentService.addStudent("Bob", "ECE");

        List<Student> students = studentService.getAllStudents();

        assertEquals(2, students.size());
        assertEquals("Alice", students.get(0).getName());
        assertEquals("Bob", students.get(1).getName());
    }

    @Test
    void getAllStudents_shouldBeEmptyInitially() {
        assertTrue(studentService.getAllStudents().isEmpty());
    }
}
