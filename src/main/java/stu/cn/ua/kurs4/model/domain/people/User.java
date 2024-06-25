package stu.cn.ua.kurs4.model.domain.people;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stu.cn.ua.kurs4.model.domain.QueueRow;
import stu.cn.ua.kurs4.model.domain.documents.*;

import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String secondName;

    private String lastName;

    private String numPassport;

    private String password;

    public User(String firstName, String secondName, String lastName, String numPassport, String password, Passport passport, BirthCertificate birthCertificate, Visa visa, Registration registration, Set<QueueRow> queues, Collection<Role> roles, Boolean citizen, Boolean bankId) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.numPassport = numPassport;
        this.password = password;
        this.passport = passport;
        this.birthCertificate = birthCertificate;
        this.visa = visa;
        this.registration = registration;
        this.queues = queues;
        this.roles = roles;
        this.citizen = citizen;
        this.bankId = bankId;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id", referencedColumnName = "id")
    private Passport passport;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "birthCertificate_id", referencedColumnName = "id")
    private BirthCertificate birthCertificate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "visa_id", referencedColumnName = "id")
    private Visa visa;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "registration_id", referencedColumnName = "id")
    private Registration registration;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<QueueRow> queues;

    private Boolean citizen;
    private Boolean bankId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))

    private Collection<Role> roles;

}
