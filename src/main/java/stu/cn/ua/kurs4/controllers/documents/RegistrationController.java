package stu.cn.ua.kurs4.controllers.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.model.domain.documents.Registration;
import stu.cn.ua.kurs4.model.services.documents.RegistrationService;


@RestController
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;


    @PostMapping("/registration/")
    public Registration saveRegistration(@RequestBody Registration registration){
        return registrationService.save(registration);
    }

}
