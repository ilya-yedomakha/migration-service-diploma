package stu.cn.ua.kurs4.model.services.articles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.model.domain.documents.article.Article;
import stu.cn.ua.kurs4.model.domain.documents.article.ArticleType;
import stu.cn.ua.kurs4.repositories.articles.ArticleRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepo articleRepo;

    public ArticleService(ArticleRepo articleRepo) {
        this.articleRepo = articleRepo;
    }

    public Article save(Article article) {
        return articleRepo.save(article);
    }

    public void deleteById(Long id) {
        articleRepo.deleteById(id);
    }

    public List<Article> getAllArticles() {
        return articleRepo.findAll();
    }

    public List<Article> getSpecificArticles(ArticleType articleType) {

        return  articleRepo.findArticlesByArticleType(articleType);
    }

    public Article getArticleById(Long id) {
        return articleRepo.findArticleById(id);
    }

    public List<Article> searchArticles(String query){
        return articleRepo.searchArticles(query);
    }

    public Article edit(Long id, Article article) {
        Article article1 = articleRepo.getById(id);
        if (article1 == null){
            return  null;
        }else {
            article1.setArticleType(article.getArticleType());
            article1.setTitle(article.getTitle());
            article1.setText(article.getText());
            return articleRepo.save(article1);
        }
    }

    public List<Article> searchNewsArticles(String query) {
        List<Article> foundArticles = articleRepo.searchArticles(query);
        List<Article> foundNewsArticles = new ArrayList<>();
        for (Article article : foundArticles){
            if (article.getArticleType().equals(ArticleType.NEWS)){
                foundNewsArticles.add(article);
            }
        }
        return foundNewsArticles;
    }

    public List<Article> searchDocumentInfoArticles(String query) {
        List<Article> foundArticles = articleRepo.searchArticles(query);
        List<Article> foundNewsArticles = new ArrayList<>();
        for (Article article : foundArticles){
            if (article.getArticleType().equals(ArticleType.DOCUMENT_INFO)){
                foundNewsArticles.add(article);
            }
        }
        return foundNewsArticles;
    }

    public List<Article> searchFAQArticles(String query) {
        List<Article> foundArticles = articleRepo.searchArticles(query);
        List<Article> foundNewsArticles = new ArrayList<>();
        for (Article article : foundArticles){
            if (article.getArticleType().equals(ArticleType.FAQ)){
                foundNewsArticles.add(article);
            }
        }
        return foundNewsArticles;
    }

    public Page<Article> findPaginated(Pageable pageable, List<Article> articles) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Article> list;

        if (articles.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, articles.size());
            list = articles.subList(startItem, toIndex);
        }

        Page<Article> bookPage
                = new PageImpl<Article>(list, PageRequest.of(currentPage, pageSize), articles.size());

        return bookPage;
    }
}
