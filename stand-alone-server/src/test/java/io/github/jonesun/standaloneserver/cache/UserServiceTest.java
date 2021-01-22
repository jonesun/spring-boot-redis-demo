package io.github.jonesun.standaloneserver.cache;

import io.github.jonesun.standaloneserver.redisdata.Student;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jone.sun
 * @date 2021/1/22 17:46
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class UserServiceTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    static User user;

    @BeforeAll
    static void init() {
        //在每个单元测试方法执行前执行一遍（只执行一次）
        user = new User(123L, "admin", "123456", User.UserSexEnum.MAN, "管理员", "中国", LocalDate.now(), BigDecimal.valueOf(10000000.99));
    }

    @Order(1)
    @Test
    void saveUser() {
        logger.info("保存: {}", user);
        userService.saveUser(user);
    }

    @Order(2)
    @Test
    void findUser() {
        User resultUser = userService.findUser(user.getId());
        logger.info("获取: {}", resultUser);
    }

    @Order(3)
    @Test
    void updateUser() {
        user.setUsername("admin123");
        userService.updateUser(user);
    }

    @Order(4)
//    @Disabled
    @Test
    void deleteById() {
        userService.deleteById(user.getId());
    }

    @Order(5)
//    @Disabled
    @Test
    void clear() {
        userService.clear();
    }
}