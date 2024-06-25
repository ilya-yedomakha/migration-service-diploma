package stu.cn.ua.kurs4.repositories.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.operations.Operation;

@Repository
public interface OperationRepo extends JpaRepository<Operation, Long> {
    Operation findOperationById(Long id);
}
