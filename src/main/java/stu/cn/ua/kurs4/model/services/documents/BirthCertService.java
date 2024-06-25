package stu.cn.ua.kurs4.model.services.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.model.domain.documents.BirthCertificate;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.repositories.documents.BirthCertRepo;
import stu.cn.ua.kurs4.repositories.people.UserRepo;

@Service
public class BirthCertService {
    @Autowired
    private final BirthCertRepo birthCertRepo;

    @Autowired
    private final UserRepo userRepo;

    public BirthCertService(BirthCertRepo birthCertRepo, UserRepo userRepo) {
        this.birthCertRepo = birthCertRepo;
        this.userRepo = userRepo;
    }

    public BirthCertificate save(BirthCertificate birthCertificate){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userRepo.findByNumPassport(currentPrincipalPassportNum);
        if (user != null) {
            //звертання до реєстра для перевірки даних надісланих користувачем
            birthCertificate.setUser(user);
            birthCertificate.setIn_registry(true);
            birthCertificate.setActive(true);

            BirthCertificate birthCertificate1 = birthCertRepo.save(birthCertificate);

            user.setBirthCertificate(birthCertificate1);
            userRepo.save(user);
            return birthCertificate1;
        } else return null;
    }

}
