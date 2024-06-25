package stu.cn.ua.kurs4.repositories.people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.documents.article.Article;
import stu.cn.ua.kurs4.model.domain.documents.article.ArticleType;
import stu.cn.ua.kurs4.model.domain.people.Role;
import stu.cn.ua.kurs4.model.domain.people.User;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByNumPassport(String passport_num);
    User findUserById(Long id);

    @Query(value = "SELECT * FROM users u WHERE u.first_name like %:o% or u.second_name like %:o% or u.last_name like %:o% or u.num_passport like %:o%",
            nativeQuery = true)
    List<User> searchUsers(@Param("o") String o);
}
