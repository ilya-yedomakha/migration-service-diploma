package stu.cn.ua.kurs4.model.domain.operations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.QueueRow;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "operations")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "operation")
    @JsonIgnore
    private Set<QueueRow> queueRows;
}
