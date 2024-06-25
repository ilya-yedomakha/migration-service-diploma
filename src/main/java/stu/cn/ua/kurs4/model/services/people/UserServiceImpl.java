package stu.cn.ua.kurs4.model.services.people;

import com.github.ukrainiantolatin.UkrainianToLatin;
import jakarta.mail.MessagingException;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.controllers.dto.QueueRowDTO;
import stu.cn.ua.kurs4.controllers.dto.UserRegistrationDto;
import stu.cn.ua.kurs4.model.domain.QueueRow;
import stu.cn.ua.kurs4.model.domain.documents.Passport;
import stu.cn.ua.kurs4.model.domain.documents.Visa;
import stu.cn.ua.kurs4.model.domain.documents.superclass.Document;
import stu.cn.ua.kurs4.model.domain.operations.Operation;
import stu.cn.ua.kurs4.model.domain.people.Operator;
import stu.cn.ua.kurs4.model.domain.people.Role;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.model.services.SendEmailService;
import stu.cn.ua.kurs4.model.services.SendSMSService;
import stu.cn.ua.kurs4.repositories.QueueRowRepo;
import stu.cn.ua.kurs4.repositories.documents.*;
import stu.cn.ua.kurs4.repositories.operations.OperationRepo;
import stu.cn.ua.kurs4.repositories.people.OperatorRepo;
import stu.cn.ua.kurs4.repositories.people.RoleRepo;
import stu.cn.ua.kurs4.repositories.people.UserRepo;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final RegistrationRepo registrationRepo;

    private static final Random random = new Random();

    private final PassportRepo passportRepo;

    private final BirthCertRepo birthCertRepo;

    private final VisaRepo visaRepo;

    private final UserRepo userRepository;

    private final QueueRowRepo queueRowRepo;


    private final RoleRepo roleRepository;
    private final OperationRepo operationRepo;

    private final OperatorRepo operatorRepo;

    private final SendEmailService sendEmailService;

    private final SendSMSService sendSMSService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public UserServiceImpl(RegistrationRepo registrationRepo, PassportRepo passportRepo, BirthCertRepo birthCertRepo, VisaRepo visaRepo, UserRepo userRepo, QueueRowRepo queueRowRepo, RoleRepo roleRepository, OperationRepo operationRepo, OperatorRepo operatorRepo, SendEmailService sendEmailService, SendSMSService sendSMSService) {
        this.registrationRepo = registrationRepo;
        this.passportRepo = passportRepo;
        this.birthCertRepo = birthCertRepo;
        this.visaRepo = visaRepo;
        this.userRepository = userRepo;
        this.queueRowRepo = queueRowRepo;

        this.roleRepository = roleRepository;
        this.operationRepo = operationRepo;
        this.operatorRepo = operatorRepo;
        this.sendEmailService = sendEmailService;
        this.sendSMSService = sendSMSService;
    }

    @Override
    public User save(UserRegistrationDto registrationDto) {
        Collection<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findRoleById(1L));
        roles.add(roleRepository.findRoleById(3L));
        User user = new User(registrationDto.getFirstName(), registrationDto.getSecondName(),
                registrationDto.getLastName(), registrationDto.getNumPassport(),
                passwordEncoder.encode(registrationDto.getPassword()), registrationDto.getPassport(), registrationDto.getBirthCertificate(), registrationDto.getVisa(), registrationDto.getRegistration(), registrationDto.getQueues(), roles, true, false);

//        data from registers
        Date dateOfBirth = generateDateOfBirth();
        Date issueDate = generateIssueDate(dateOfBirth);
        Date expireDate = generateExpireDate(issueDate);
        String issueDepartment = generateRandomDepartment();
        String placeOfBirth = generateRandomPlaceOfBirth();

        Passport passport = new Passport();
        passport.setNumber(user.getNumPassport());
        passport.setFirstName(user.getFirstName());
        passport.setSecondName(user.getFirstName());
        passport.setLastName(user.getLastName());
        passport.setDateOfBirth(dateOfBirth);
        passport.setIssueDate(issueDate);
        passport.setExpireDate(expireDate);
        passport.setIssueDepartment(issueDepartment);
        passport.setPlaceOfBirth(placeOfBirth);
        passport.setActive(true);
        passport.setIn_registry(true);
        Random random = new Random();

        String[] sexes = {"M", "F"};
        int index = random.nextInt(sexes.length);

        String selectedSex = sexes[index];
        passport.setSex(selectedSex);
        passport.setCitizenship("UKR");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = formatter.format(dateOfBirth);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }

        String randomString = sb.toString();
        passport.setRecord_no(formattedDate + "-" + randomString);
        passport.setRNTRC(registrationDto.getRNTRC());
        userRepository.save(user);
        passportRepo.save(passport);

        passport.setUser(user);
        passportRepo.save(passport);
        user.setPassport(passport);
        return userRepository.save(user);
    }

    public User saveForeigner(UserRegistrationDto registrationDto) {
        Collection<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findRoleById(1L));
        String passwordRand = generatePassayPassword();
        User user = new User(registrationDto.getFirstName(), registrationDto.getSecondName(),
                registrationDto.getLastName(), registrationDto.getNumPassport(),
                passwordEncoder.encode(passwordRand), registrationDto.getPassport(), registrationDto.getBirthCertificate(), registrationDto.getVisa(), registrationDto.getRegistration(), registrationDto.getQueues(), roles, false, false);

        if (Objects.equals(registrationDto.getEmail(), "")) {
            registrationDto.setEmail(null);
        }

        if (Objects.equals(registrationDto.getPhone(), "")) {
            registrationDto.setPhone(null);
        }

        if (registrationDto.getEmail() != null) {
            String body = "Вас зареєстровано на веб-сайті міграційної служби. Для входу в особистий кабінет, введіть номер свого документу, що надає тимчасове" +
                    "право перебувати на території країни і даний пароль: " + passwordRand;
            try {
                sendEmailService.sendMail(registrationDto.getEmail(), body, "Реєстрація на сайті міграційної служби");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        if (registrationDto.getPhone() != null) {
            String body = "Вас зареєстровано на веб-сайті міграційної служби. Для входу в особистий кабінет, введіть номер свого документу, що надає тимчасове" +
                    "право перебувати на території країни і даний пароль: " + passwordRand;
            sendSMSService.sendSMS(registrationDto.getPhone(), body);
        }

        //        data from registers

        Date dateOfBirth = generateDateOfBirth();
        Date issueDate = generateIssueDate(dateOfBirth);
        Date expireDate = generateExpireDate(issueDate);
        String issueDepartment = generateRandomDepartment();
        String nationality = generateRandomNationality();

        Visa passport = new Visa();

        passport.setNumber(user.getNumPassport());
        passport.setFirstName(user.getFirstName());
        passport.setSecondName(user.getFirstName());
        passport.setLastName(user.getLastName());
        passport.setDateOfBirth(dateOfBirth);
        passport.setIssueDate(issueDate);
        passport.setExpireDate(expireDate);
        passport.setIssueDepartment(issueDepartment);
        passport.setActive(true);
        passport.setIn_registry(true);
        Random random = new Random();

        String[] sexes = {"M", "F"};
        int index = random.nextInt(sexes.length);

        String selectedSex = sexes[index];
        passport.setSex(selectedSex);
        passport.setNationality(nationality);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 14; i++) {
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }

        String randomString = sb.toString();
        passport.setControlNumber(randomString);
        userRepository.save(user);
        visaRepo.save(passport);

        passport.setUser(user);
        visaRepo.save(passport);
        user.setVisa(passport);
        return userRepository.save(user);
    }

    public User saveBankId(UserRegistrationDto registrationDto) {
        Collection<Role> roles = new ArrayList<>();
        String password = "BankIdPassword";
        String[] firstNames = {"Alexander", "Irina", "Victoria", "Michael", "Julia", "Andrew", "Elena", "Dmitry", "Anastasia", "Vladislav"};
        String[] secondNames = {"Alexander", "Irina", "Victoria", "Michael", "Julia", "Andrew", "Олегович", "Дмитрович", "Дмитрівна", "Іллівна"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
        Random random = new Random();

        String firstName = firstNames[random.nextInt(firstNames.length)];
        String secondName = secondNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        roles.add(roleRepository.findRoleById(1L));
        roles.add(roleRepository.findRoleById(3L));
        User user = new User(firstName, secondName,
                lastName, registrationDto.getNumPassport(),
                passwordEncoder.encode(password), registrationDto.getPassport(), registrationDto.getBirthCertificate(), registrationDto.getVisa(), registrationDto.getRegistration(), registrationDto.getQueues(), roles, true, true);

        //        data from registers
        Date dateOfBirth = generateDateOfBirth();
        Date issueDate = generateIssueDate(dateOfBirth);
        Date expireDate = generateExpireDate(issueDate);
        String issueDepartment = generateRandomDepartment();
        String placeOfBirth = generateRandomPlaceOfBirth();

        Passport passport = new Passport();

        passport.setNumber(user.getNumPassport());
        passport.setFirstName(firstName);
        passport.setSecondName(secondName);
        passport.setLastName(lastName);
        passport.setDateOfBirth(dateOfBirth);
        passport.setIssueDate(issueDate);
        passport.setExpireDate(expireDate);
        passport.setIssueDepartment(issueDepartment);
        passport.setPlaceOfBirth(placeOfBirth);
        passport.setActive(true);
        passport.setIn_registry(true);

        String[] sexes = {"M", "F"};
        int index = random.nextInt(sexes.length);

        String selectedSex = sexes[index];
        passport.setSex(selectedSex);
        passport.setCitizenship("UKR");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = formatter.format(dateOfBirth);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }

        String randomString = sb.toString();
        passport.setRecord_no(formattedDate + "-" + randomString);
        userRepository.save(user);
        passportRepo.save(passport);

        passport.setUser(user);
        passportRepo.save(passport);
        user.setPassport(passport);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByNumPassport(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getNumPassport(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        User usr = userRepository.findUserById(id);
        Set<QueueRow> queueRows = usr.getQueues();
        for (QueueRow queueRow : queueRows) {
            Operator operator = queueRow.getOperator();
            operator.getQueueRows().remove(queueRow);
            Operation operation = queueRow.getOperation();
            operation.getQueueRows().remove(queueRow);
            queueRow.setOperator(null);
            queueRow.setUser(null);
            queueRow.setOperation(null);


            queueRowRepo.save(queueRow);
            operationRepo.save(operation);
            operatorRepo.save(operator);
            queueRowRepo.delete(queueRow);
        }

        usr.getQueues().clear();
        usr.getRoles().clear();
        userRepository.save(usr);
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }


    public User saveFromAdmin(User userToSave) {
        User user;

        boolean b = false;
        for (User user2 : findAll()) {
            if (user2.getNumPassport().equals(userToSave.getNumPassport())) {
                b = true;
                break;
            }
        }

        if (userToSave.getPassword() == null || userToSave.getPassword().equals("") || userToSave.getNumPassport() == null || userToSave.getFirstName() == null
                || userToSave.getLastName() == null || userToSave.getSecondName() == null || userToSave.getNumPassport().equals("") || userToSave.getFirstName().equals("")
                || userToSave.getLastName().equals("") || userToSave.getSecondName().equals("") || b) {
            return null;
        }
        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
        Collection<Role> roles = new ArrayList<>();
        Collection<Role> roles_from = userToSave.getRoles();
        for (Role role : roles_from) {
            roles.add(roleRepository.findRoleById(role.getId()));
        }
        userToSave.setRoles(roles);
        user = userRepository.save(userToSave);

        return user;
    }

    public User findByNumPassport(String email) {
        return userRepository.findByNumPassport(email);
    }

    public List<QueueRowDTO> findQueueRowsDTO(User user) {
//        System.out.println(UkrainianToLatin.generateLat());
        List<QueueRowDTO> queueRowDTOS = new ArrayList<>();
        for (QueueRow queueRow : user.getQueues()) {
            QueueRowDTO queueRowDTO = new QueueRowDTO(queueRow.getId(), queueRow.getOperator().getFirstName(), queueRow.getOperator().getLastName(), queueRow.getOperation(), queueRow.getPhone(), queueRow.getEmail(), queueRow.getDateTime(), queueRow.getOperator().getDepartment().getNumber(), queueRow.getOperator().getDepartment().getRegion(), queueRow.getOperator().getDepartment().getCity());
            queueRowDTOS.add(queueRowDTO);
        }
        return queueRowDTOS;
    }

    public List<Operation> getNewOperations(User user) {
        Set<QueueRow> queueRows = user.getQueues();
        List<Operation> operations = operationRepo.findAll();
        Set<Operation> usedOperations = new HashSet<>();
        for (QueueRow queueRow : queueRows) {
            usedOperations.add(queueRow.getOperation());
        }

        operations.removeAll(usedOperations);

        return new ArrayList<>(operations);
    }


    public User findById(Long id) {
        Optional<User> clientOptional = userRepository.findById(id);
        return clientOptional.orElse(null);
    }


    public List<Document> getDocumentsByClientId(Long id) {
        User user = userRepository.getById(id);

        List<Document> documents = new ArrayList<>();
        documents.add(user.getBirthCertificate());
        documents.add(user.getPassport());
        documents.add(user.getRegistration());
        documents.add(user.getVisa());


        return documents;
    }

    public User editFromAdmin(User user) {
        User foundUser = userRepository.findUserById(user.getId());
        if (user.getPassword() != null) {
            foundUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            foundUser.getRoles().clear();
            Set<Role> findRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role findRole = roleRepository.findRoleById(role.getId());
                findRoles.add(findRole);
            }
            foundUser.setRoles(findRoles);
            foundUser.setCitizen(false);
            for (Role role : findRoles){
                if (role.equals(roleRepository.findRoleById(3L))){
                    foundUser.setCitizen(true);
                }
            }

        }


        return userRepository.save(foundUser);
    }


    public String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "error";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        String password = gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }

    public Boolean ifRegistrationAllowed(User user) {
        int count = 0;
        for (QueueRow queueRow : user.getQueues()) {
            if (queueRow.getOperation() != null) {
                count++;
            }
        }
        List<Operation> operations = operationRepo.findAll();

        return operations.size() > count;
    }

    public List<User> findUsersByRoleId(Long id) {
        Role role = roleRepository.findRoleById(id);
        List<User> userList = userRepository.findAll();
        List<User> resultUserList = new ArrayList<>();
        for (User user : userList) {
            for (Role role1 : user.getRoles()) {
                if (role1.equals(role)) {
                    resultUserList.add(user);
                }
            }
        }
        return resultUserList;
    }


    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    public List<User> searchUsersWithRoles(String query, Long roleId) {
        Role role = roleRepository.findRoleById(roleId);
        List<User> userList = userRepository.searchUsers(query);
        List<User> resultUserList = new ArrayList<>();
        for (User user : userList) {
            for (Role role1 : user.getRoles()) {
                if (role1.equals(role)) {
                    resultUserList.add(user);
                }
            }
        }
        return resultUserList;
    }


    public Page<User> findPaginated(Pageable pageable, List<User> users) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<User> list;

        if (users.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            list = users.subList(startItem, toIndex);
        }

        Page<User> bookPage
                = new PageImpl<User>(list, PageRequest.of(currentPage, pageSize), users.size());

        return bookPage;
    }


    // методи, що не будуть використовуватись у фінальній версії вебсайту, виконують функцію заміни даних, що надходитимуть із закритих державних реєстрів
    private static Date generateDateOfBirth() {
        Calendar calendar = new GregorianCalendar();
        int year = randBetween(1950, 2005);
        int dayOfYear = randBetween(1, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return calendar.getTime();
    }

    private static Date generateIssueDate(Date dateOfBirth) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.YEAR, 13);

        int minIssueYear = calendar.get(Calendar.YEAR);
        calendar.setTime(new Date());
        int maxIssueYear = calendar.get(Calendar.YEAR);

        int year = randBetween(minIssueYear, maxIssueYear);
        int dayOfYear = randBetween(1, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return calendar.getTime();
    }

    private static Date generateExpireDate(Date issueDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(issueDate);
        calendar.add(Calendar.YEAR, 4);

        int minExpireYear = calendar.get(Calendar.YEAR);
        int maxExpireYear = minExpireYear + 10;

        int year = randBetween(minExpireYear, maxExpireYear);
        int dayOfYear = randBetween(1, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return calendar.getTime();
    }

    private static String generateRandomDepartment() {
        String[] departments = {"1111", "2222", "3333", "4444"};
        return departments[random.nextInt(departments.length)];
    }

    private static String generateRandomPlaceOfBirth() {
        String[] places = {"City X", "City Y", "City Z", "City W"};
        return places[random.nextInt(places.length)];
    }

    private static String generateRandomNationality() {
        String[] places = {"UKR", "US", "UK", "PL"};
        return places[random.nextInt(places.length)];
    }

    private static int randBetween(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }


}
