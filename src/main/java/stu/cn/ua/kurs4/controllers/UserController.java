package stu.cn.ua.kurs4.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.controllers.dto.QueueRowDTO;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.model.services.people.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;



    @GetMapping("webcab/user/queues/")
    public List<QueueRowDTO> getRows(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userServiceImpl.findByNumPassport(currentPrincipalPassportNum);
        List<QueueRowDTO> queueRowDTOS = new ArrayList<>();
        queueRowDTOS = userServiceImpl.findQueueRowsDTO(user);
        return queueRowDTOS;
    }

    @DeleteMapping("admin/user/{id}")
    public void deleteUser(@PathVariable Long id){
        userServiceImpl.deleteById(id);
    }

    @PostMapping("admin/user")
    public User addUser(@RequestBody User user){
        return userServiceImpl.saveFromAdmin(user);
    }

    @PutMapping("admin/user")
    public User editUser(@RequestBody User user){
        return userServiceImpl.editFromAdmin(user);
    }
}
