package stu.cn.ua.kurs4.model.services.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import stu.cn.ua.kurs4.model.domain.documents.Visa;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.repositories.documents.VisaRepo;
import stu.cn.ua.kurs4.repositories.people.UserRepo;


@Service
public class VisaService {
    @Autowired
    private final VisaRepo visaRepo;

    @Autowired
    private final UserRepo userRepo;

    public VisaService(VisaRepo visaRepo, UserRepo userRepo) {
        this.visaRepo = visaRepo;
        this.userRepo = userRepo;
    }
    public Visa save(Visa visa){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userRepo.findByNumPassport(currentPrincipalPassportNum);


        if (user != null) {
            //звертання до реєстра для перевірки даних надісланих користувачем
            visa.setUser(user);
            visa.setIn_registry(true);
            visa.setActive(true);

            Visa visa1 = visaRepo.save(visa);

            user.setVisa(visa1);
            userRepo.save(user);
            return visa1;
        } else return null;
    }

}
