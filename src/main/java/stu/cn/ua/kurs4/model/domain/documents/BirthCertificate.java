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
@Table(name = "birthCertificates")
public class BirthCertificate extends Document {

    private String number;

    private String sex;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    private String placeOfBirth;

    @JsonIgnore
    @OneToOne(mappedBy = "birthCertificate")
    private User user;
}
