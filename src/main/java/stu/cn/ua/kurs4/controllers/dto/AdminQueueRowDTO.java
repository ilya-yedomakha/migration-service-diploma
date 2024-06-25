package stu.cn.ua.kurs4.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.operations.Operation;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminQueueRowDTO {

    private Long id;

    private String pasNum;

    private String username;

    private String userLastName;

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
