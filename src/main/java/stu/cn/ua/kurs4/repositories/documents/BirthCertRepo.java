package stu.cn.ua.kurs4.repositories.documents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.documents.BirthCertificate;
@Repository
public interface BirthCertRepo extends JpaRepository<BirthCertificate,Long> {
}
