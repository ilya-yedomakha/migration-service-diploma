package stu.cn.ua.kurs4.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import stu.cn.ua.kurs4.controllers.dto.UserRegistrationDto;
import stu.cn.ua.kurs4.model.services.people.UserServiceImpl;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private UserServiceImpl userService;

    public UserRegistrationController(UserServiceImpl userService) {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "security/registration";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        userService.save(registrationDto);
        return "redirect:/registration?success";
    }


    @PostMapping("/foreign")
    public String registerForeignUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        userService.saveForeigner(registrationDto);
        return "redirect:/registration?success";
    }

    @PostMapping("/bankId")
    public String registerBankIdUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        userService.saveBankId(registrationDto);
        return "redirect:/registration?success";
    }
}
