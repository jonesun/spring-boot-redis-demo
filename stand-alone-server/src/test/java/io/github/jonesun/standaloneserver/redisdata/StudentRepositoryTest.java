package io.github.jonesun.standaloneserver.redisdata;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jone.sun
 * @date 2021/1/22 10:09
 */
@DisplayName("简单redis增删改查测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentRepositoryTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    StudentRepository studentRepository;

    static Student student;

    @BeforeAll
    static void init() {
        //在每个单元测试方法执行前执行一遍（只执行一次）
        student = new Student("20210101", "Jone Sun", Student.Gender.MALE, 1);
    }

    @Order(1)
    @DisplayName("保存数据")
    @Test
    void save() {
        Student resultStudent = studentRepository.save(student);
        assertNotNull(resultStudent);
        logger.info(resultStudent.toString());
    }

    @Order(2)
    @DisplayName("查找数据")
    @Test
    void findById() {
        studentRepository.findById(student.getId())
                .ifPresentOrElse(
                        student -> logger.info(student.toString()),
                        () -> logger.error("数据信息不存在，或已过期"));
    }

    @Order(3)
    @DisplayName("更新数据")
    @Test
    void update() {
        student.setName("Richard Watson");
        Student resultStudent = studentRepository.save(student);
        assertAll("测试更新数据",
                () -> assertNotNull(resultStudent),
                () -> assertEquals(student.getName(), resultStudent.getName())
        );
    }

    @Order(4)
    @DisplayName("删除数据")
    @Test
    void delete() {
        studentRepository.deleteById(student.getId());
        studentRepository.findById(student.getId())
                .ifPresentOrElse(
                        student -> logger.info(student.toString()),
                        () -> logger.error("数据信息不存在，或已过期"));
    }

    @Order(5)
    @DisplayName("查找所有")
    @ParameterizedTest
    @MethodSource("studentProvider")
    void findAll(List<Student> studentList) {
        studentRepository.saveAll(studentList);
        studentRepository.findAll().forEach(student1 -> {
            if (student1 == null) {
                logger.error("数据信息不存在，或已过期");
            } else {
                logger.info(student1.toString());
            }
        });
    }

    @Order(6)
    @DisplayName("删除所有")
    @Test
    void deleteAll() {
        studentRepository.deleteAll();
        assertEquals(studentRepository.count(), 0);
    }

    @AfterAll
    static void clean() {
        student = null;
    }


    static Stream<List<Student>> studentProvider() {
        return Stream.of(List.of(new Student(
                "20210102", "John Doe", Student.Gender.MALE, 1),
                new Student(
                        "20210103", "Gareth Houston", Student.Gender.FEMALE, 2),
                new Student(
                        "20210104", "孙 建安", Student.Gender.MALE, 2, 30L)));
    }

//    @ParameterizedTest
//    @ValueSource(ints = {1, 2, 3})
//    @DisplayName("参数化测试")
//    void paramTest(int a) {
//        logger.info("a={}", a);
//        assertTrue(a > 0 && a < 4);
//    }
}