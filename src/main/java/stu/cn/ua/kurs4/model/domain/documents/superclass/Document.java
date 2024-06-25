package stu.cn.ua.kurs4.model.domain.documents.superclass;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String firstName;

    private String secondName;

    private String lastName;

    @Temporal(TemporalType.DATE)
    private Date issueDate;

    private String issueDepartment;

    private boolean in_registry;

    private boolean active;

}
