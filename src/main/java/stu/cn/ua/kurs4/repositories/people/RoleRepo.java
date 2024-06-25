package stu.cn.ua.kurs4.repositories.people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.people.Role;
@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findRoleById(Long id);
}
