package stu.cn.ua.kurs4.model.services;

import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.model.domain.Department;
import stu.cn.ua.kurs4.repositories.DepartmentRepo;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    private DepartmentRepo departmentRepo;

    public DepartmentService(DepartmentRepo departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    public List<Department> findAll() {
        return departmentRepo.findAll();
    }

    public Optional<Department> findById(Long id) {
        return departmentRepo.findById(id);
    }
}
