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
@Table(name = "registration")
public class Registration extends Document {

    private String number;

    private String address;

    @Temporal(TemporalType.DATE)
    private Date expireDate;

    @JsonIgnore
    @OneToOne(mappedBy = "registration")
    private User user;
}
