package stu.cn.ua.kurs4.controllers.dto;


import lombok.Getter;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.model.domain.people.Operator;

import java.time.LocalDateTime;

@Getter
@Setter
public class QueueDepartment {

    private Operator operator;

    private User user;

    private Long operationId;

    private String phone;

    private String email;

    private LocalDateTime dateTime;

    private Long departmentId;

    private String passNumber;
}

