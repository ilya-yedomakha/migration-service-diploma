package stu.cn.ua.kurs4.model.domain.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.documents.superclass.Document;
import stu.cn.ua.kurs4.model.domain.people.User;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "visa")
public class Visa extends Document {

    private String number;

    private String nationality;

    private String sex;

    private String passportNumber;

    private String controlNumber;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Temporal(TemporalType.DATE)
    private Date expireDate;

    @JsonIgnore
    @OneToOne(mappedBy = "visa")
    private User user;
}
