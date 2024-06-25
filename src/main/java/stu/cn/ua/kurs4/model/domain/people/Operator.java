package stu.cn.ua.kurs4.model.domain.people;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.Department;
import stu.cn.ua.kurs4.model.domain.QueueRow;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "operators")
public class Operator{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "operator")
    @JsonIgnore
    private Set<QueueRow> queueRows;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
