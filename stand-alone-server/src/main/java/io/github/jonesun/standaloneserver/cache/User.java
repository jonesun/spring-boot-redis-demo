package io.github.jonesun.standaloneserver.cache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author jone.sun
 * @date 2021/1/22 16:39
 */
public class User implements Serializable {

    private Long id;

    private String username;

    private String password;

    private UserSexEnum sex;

    private String nickName;

    private String address;

//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;

    private BigDecimal money;

    public enum UserSexEnum {
        MAN, WOMAN
    }

    public User() {
    }

    public User(Long id, String username, String password, UserSexEnum sex, String nickName, String address, LocalDate birthday, BigDecimal money) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.nickName = nickName;
        this.address = address;
        this.birthday = birthday;
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserSexEnum getSex() {
        return sex;
    }

    public void setSex(UserSexEnum sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", nickName='" + nickName + '\'' +
                ", address='" + address + '\'' +
                ", birthday=" + birthday +
                ", money=" + money +
                '}';
    }
}
