package io.github.jonesun.sentinel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jonesun.sentinel.redisdata.Student;
import io.github.jonesun.sentinel.redisdata.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jone.sun
 * @date 2021/2/4 15:11
 */
@RestController
public class StudentController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    StudentRepository studentRepository;

    @GetMapping("sava")
    public String save() throws JsonProcessingException {
        Student student = new Student("20210101", "Jone Sun123", Student.Gender.MALE, 1);
        Student resultStudent = studentRepository.save(student);
        logger.info(resultStudent.toString());
        return new ObjectMapper().writeValueAsString(resultStudent);
    }
}
