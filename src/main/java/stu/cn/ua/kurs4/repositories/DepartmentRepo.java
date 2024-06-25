package stu.cn.ua.kurs4.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.Department;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, Long> {
}
