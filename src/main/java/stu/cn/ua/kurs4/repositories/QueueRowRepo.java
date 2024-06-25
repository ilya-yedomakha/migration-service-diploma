package stu.cn.ua.kurs4.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.QueueRow;

@Repository
public interface QueueRowRepo extends JpaRepository<QueueRow, Long> {
    public QueueRow findQueueRowById(Long id);
}
