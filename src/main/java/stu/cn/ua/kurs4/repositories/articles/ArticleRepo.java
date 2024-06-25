package stu.cn.ua.kurs4.repositories.articles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stu.cn.ua.kurs4.model.domain.documents.article.Article;
import stu.cn.ua.kurs4.model.domain.documents.article.ArticleType;
import stu.cn.ua.kurs4.model.domain.operations.Operation;

import java.util.List;
import java.util.Set;

@Repository
public interface ArticleRepo extends JpaRepository<Article, Long> {
    Article findArticleById(Long id);
    List<Article> findArticlesByArticleType(ArticleType articleType);

    @Query(value = "SELECT * FROM articles u WHERE u.title like %:o% or u.text like %:o%",
            nativeQuery = true)
    List<Article> searchArticles(@Param("o") String o);
}
