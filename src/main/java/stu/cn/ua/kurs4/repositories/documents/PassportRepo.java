package stu.cn.ua.kurs4.repositories.documents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.documents.Passport;

@Repository
public interface PassportRepo extends JpaRepository<Passport, Long> {
}
