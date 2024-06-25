package stu.cn.ua.kurs4.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.model.domain.documents.article.Article;
import stu.cn.ua.kurs4.model.services.articles.ArticleService;


@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;


    @PostMapping("/admin/article/")
    public Article saveArticle(@RequestBody Article article){
        return articleService.save(article);
    }

    @PutMapping("/admin/article/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody Article article){
        return articleService.edit(id,article);
    }
    @DeleteMapping("/admin/article/{id}")
    public void deleteArticle(@PathVariable Long id){
        articleService.deleteById(id);
    }
}
