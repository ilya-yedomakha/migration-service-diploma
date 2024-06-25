package stu.cn.ua.kurs4.model.services.people;


import org.springframework.security.core.userdetails.UserDetailsService;
import stu.cn.ua.kurs4.controllers.dto.UserRegistrationDto;
import stu.cn.ua.kurs4.model.domain.people.User;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
}
