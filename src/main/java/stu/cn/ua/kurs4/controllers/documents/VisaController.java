package stu.cn.ua.kurs4.controllers.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.model.domain.documents.Visa;
import stu.cn.ua.kurs4.model.services.documents.VisaService;


@RestController
public class VisaController {
    @Autowired
    private VisaService visaService;


    @PostMapping("/visa/")
    public Visa saveVisa(@RequestBody Visa visa){
        return visaService.save(visa);
    }
}
