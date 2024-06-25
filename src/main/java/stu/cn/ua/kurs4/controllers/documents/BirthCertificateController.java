package stu.cn.ua.kurs4.controllers.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import stu.cn.ua.kurs4.model.domain.documents.BirthCertificate;
import stu.cn.ua.kurs4.model.services.documents.BirthCertService;


@RestController
public class BirthCertificateController {
    @Autowired
    private BirthCertService birthCertificateService;


    @PostMapping("/birthcertificate/")
    public BirthCertificate saveBirthCertificate(@RequestBody BirthCertificate birthCertificate){
        return birthCertificateService.save(birthCertificate);
    }

}
