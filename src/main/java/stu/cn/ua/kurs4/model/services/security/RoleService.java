package stu.cn.ua.kurs4.model.services.security;

import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.model.domain.people.Role;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.model.services.people.UserServiceImpl;
import stu.cn.ua.kurs4.repositories.people.RoleRepo;
import stu.cn.ua.kurs4.repositories.people.UserRepo;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {
    public final RoleRepo roleRepo;

    public final UserRepo userRepo;

    public final UserServiceImpl userService;

    public RoleService(RoleRepo roleRepo, UserRepo userRepo, UserServiceImpl userService) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    public Role save(Role roleToSave){
        Role role = roleRepo.save(roleToSave);
        return role;
    }

    public void deleteById(Long id){
        for (User user: userService.findAll()){
            for (Role role : user.getRoles()){
                if (role.getId().equals(id)){
                    user.getRoles().remove(role);
                    userRepo.save(user);
                }
            }
        }
        roleRepo.deleteById(id);
    }

    public Role findById(Long id){
        return roleRepo.findRoleById(id);
    }

    public Set<Role> findAllRoles(){
        Set<Role> users = new HashSet<>();
        roleRepo.findAll().forEach(users::add);
        return users;
    }
}
