package stu.cn.ua.kurs4.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.people.Operator;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    private Float x_coordinate;

    private Float y_coordinate;

    private String region;

    private String city;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private Set<Operator> operators;
}
