package stu.cn.ua.kurs4.controllers.dto;

import lombok.Getter;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.QueueRow;
import stu.cn.ua.kurs4.model.domain.documents.BirthCertificate;
import stu.cn.ua.kurs4.model.domain.documents.Passport;
import stu.cn.ua.kurs4.model.domain.documents.Registration;
import stu.cn.ua.kurs4.model.domain.documents.Visa;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserRegistrationDto {
    private String firstName;

    private String secondName;

    private String lastName;

    private String numPassport;

    private String password;

    private Passport passport;

    private BirthCertificate birthCertificate;

    private Visa visa;

    private Registration registration;

    private Set<QueueRow> queues;
    private Boolean citizen;

    private String phone;

    private String email;

    private String RNTRC;

    public UserRegistrationDto(){

    }

    public UserRegistrationDto(String firstName, String secondName, String lastName, String NumPassport, String password, Passport passport, BirthCertificate birthCertificate, Visa visa, Registration registration, Set<QueueRow> queues, Boolean citizen, String email, String phone, String RNTRC) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.numPassport = NumPassport;
        this.password = password;
        this.passport = null;
        this.birthCertificate = null;
        this.visa = null;
        this.registration = null;
        this.queues = new HashSet<>();
        this.citizen = citizen;
        this.email = email;
        this.phone = phone;
        this.RNTRC = RNTRC;
    }
}
