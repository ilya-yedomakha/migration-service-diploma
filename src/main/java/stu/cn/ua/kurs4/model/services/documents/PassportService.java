package stu.cn.ua.kurs4.model.services.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.model.domain.documents.Passport;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.repositories.documents.PassportRepo;
import stu.cn.ua.kurs4.repositories.people.UserRepo;

@Service
public class PassportService {
    @Autowired
    private final PassportRepo passportRepo;

    @Autowired
    private final UserRepo userRepo;

    public PassportService(PassportRepo passportRepo, UserRepo userRepo) {
        this.passportRepo = passportRepo;
        this.userRepo = userRepo;
    }

    public Passport save(Passport passport){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userRepo.findByNumPassport(currentPrincipalPassportNum);

        if (user != null) {
            passport.setUser(user);
            passport.setIn_registry(true);
            passport.setActive(true);


            Passport passport1 = passportRepo.save(passport);

            user.setPassport(passport1);
            userRepo.save(user);
            return passport1;
        } else return null;
    }
}
