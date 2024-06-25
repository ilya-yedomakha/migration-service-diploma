package stu.cn.ua.kurs4.model.domain.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.documents.superclass.Document;
import stu.cn.ua.kurs4.model.domain.people.User;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "passport")
public class Passport extends Document {

    private String number;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    private String placeOfBirth;

    private String citizenship;

    private String sex;

    private String record_no;


    @JsonProperty("RNTRC")
    private String RNTRC;


    @Temporal(TemporalType.DATE)
    private Date expireDate;

    @JsonIgnore
    @OneToOne(mappedBy = "passport")
    private User user;
}
