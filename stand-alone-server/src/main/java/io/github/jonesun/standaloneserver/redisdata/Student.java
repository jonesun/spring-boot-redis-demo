package io.github.jonesun.standaloneserver.redisdata;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author jone.sun
 * @date 2021/1/21 17:24
 */
//@RedisHash(value = "Student", timeToLive = 20L)
@RedisHash("Student")
public class Student implements Serializable {

    @Id
    private String id;
    private String name;
    private Gender gender;
    private int grade;

    /**
     * 以秒为单位，失效时间
     */
    @TimeToLive
    private Long time;

    private Date data;

    private LocalDateTime localDateTime;

    private LocalDate localDate;

    private LocalTime localTime;


    public enum Gender {
        MALE, FEMALE
    }


    public Student() {}

    public Student(String id, String name, Gender gender, int grade) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.grade = grade;
        data = new Date();
        localDateTime = LocalDateTime.now();
        localDate = LocalDate.now();
        localTime = LocalTime.now();
    }

    public Student(String id, String name, Gender gender, int grade, Long time) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.grade = grade;
        this.time = time;
        data = new Date();
        localDateTime = LocalDateTime.now();
        localDate = LocalDate.now();
        localTime = LocalTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }


    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", grade=" + grade +
                ", time=" + time +
                ", data=" + data +
                ", localDateTime=" + localDateTime +
                ", localDate=" + localDate +
                ", localTime=" + localTime +
                '}';
    }
}