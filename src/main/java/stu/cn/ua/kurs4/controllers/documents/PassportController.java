package stu.cn.ua.kurs4.controllers.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.model.domain.documents.Passport;
import stu.cn.ua.kurs4.model.services.documents.PassportService;


@RestController
public class PassportController {
    @Autowired
    private PassportService passportService;


    @PostMapping("/passport/")
    public Passport savePassport(@RequestBody Passport passport){
        return passportService.save(passport);
    }

}
