package stu.cn.ua.kurs4.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.operations.Operation;
import stu.cn.ua.kurs4.model.domain.people.Operator;
import stu.cn.ua.kurs4.model.domain.people.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class QueueRowDTO {

    private Long id;

    private String operatorFirstName;

    private String operatorLastName;

    private Operation operation;

    private String phone;

    private String email;

    private LocalDateTime dateTime;

    private Integer departmentNumber;

    private String region;

    private String city;
}
