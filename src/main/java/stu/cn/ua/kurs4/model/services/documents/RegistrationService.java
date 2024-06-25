package stu.cn.ua.kurs4.model.services.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import stu.cn.ua.kurs4.model.domain.documents.Registration;
import stu.cn.ua.kurs4.model.domain.people.User;

import stu.cn.ua.kurs4.repositories.documents.RegistrationRepo;
import stu.cn.ua.kurs4.repositories.people.UserRepo;

@Service
public class RegistrationService {
    @Autowired
    private final RegistrationRepo registrationRepo;

    @Autowired
    private final UserRepo userRepo;


    public RegistrationService(RegistrationRepo registrationRepo, UserRepo userRepo) {
        this.registrationRepo = registrationRepo;
        this.userRepo = userRepo;
    }

    public Registration save(Registration registration){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userRepo.findByNumPassport(currentPrincipalPassportNum);


        if (user != null) {
            //звертання до реєстра для перевірки даних надісланих користувачем
            registration.setUser(user);

            registration.setIn_registry(true);
            registration.setActive(true);

            Registration registration1 = registrationRepo.save(registration);

            user.setRegistration(registration1);
            userRepo.save(user);
            return registration1;
        } else return null;
    }
}
