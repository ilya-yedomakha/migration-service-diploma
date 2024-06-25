package stu.cn.ua.kurs4.controllers.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.controllers.dto.QueueDepartment;
import stu.cn.ua.kurs4.model.domain.QueueRow;
import stu.cn.ua.kurs4.model.domain.documents.article.Article;
import stu.cn.ua.kurs4.model.domain.documents.article.ArticleType;
import stu.cn.ua.kurs4.model.domain.operations.Operation;
import stu.cn.ua.kurs4.model.domain.people.Role;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.model.services.DepartmentService;
import stu.cn.ua.kurs4.model.services.QueueRowService;
import stu.cn.ua.kurs4.model.services.articles.ArticleService;
import stu.cn.ua.kurs4.model.services.people.UserServiceImpl;
import stu.cn.ua.kurs4.model.services.people.OperatorService;
import stu.cn.ua.kurs4.model.services.security.RoleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
public class ViewController {
    private final UserServiceImpl userServiceImpl;

    private final OperatorService operatorService;
    private final DepartmentService departmentService;
    private final QueueRowService queueRowService;
    private final ArticleService articleService;
    private final RoleService roleService;


    public ViewController(UserServiceImpl userServiceImpl, OperatorService operatorService, DepartmentService departmentService, QueueRowService queueRowService, ArticleService articleService, RoleService roleService) {
        this.userServiceImpl = userServiceImpl;
        this.operatorService = operatorService;
        this.departmentService = departmentService;
        this.queueRowService = queueRowService;
        this.articleService = articleService;
        this.roleService = roleService;
    }

    @RequestMapping("/")
    public String showMain(Model model) {
        return "index";
    }

    @RequestMapping("/webcab/show")
    public String showWebCab(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userServiceImpl.findByNumPassport(currentPrincipalPassportNum);
        model.addAttribute("username", user.getFirstName());
        model.addAttribute("userlastname", user.getLastName());
        model.addAttribute("registrationAllowed", userServiceImpl.ifRegistrationAllowed(user));
        return "webcab";
    }


    @RequestMapping("/webcab/queue/registration/")
    public String queueRegistrationForm(Model model) {
        model.addAttribute("queueRow", new QueueDepartment());
        model.addAttribute("departments", departmentService.findAll());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userServiceImpl.findByNumPassport(currentPrincipalPassportNum);
        model.addAttribute("operations", userServiceImpl.getNewOperations(user));
        return "queueRegistration";
    }

    @RequestMapping("/webcab/queue/registration/calendar/")
    public String showCalendar(@ModelAttribute("queueRow") QueueDepartment queueRow, Model model) {
        model.addAttribute("departmentId", queueRow.getDepartmentId());
        model.addAttribute("phone", queueRow.getPhone());
        model.addAttribute("email", queueRow.getEmail());
        model.addAttribute("operation", queueRow.getOperationId());
        model.addAttribute("queueRow", new QueueDepartment());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        queueRow.setPassNumber(currentPrincipalPassportNum);
        model.addAttribute("conflictTimes", queueRowService.getConflictTimes(queueRow));
        return "calendar";
    }

    @RequestMapping("/webcab/queue/{id}/registration/edit")
    public String queueRegistrationEditForm(@PathVariable Long id, Model model) {

        QueueRow queueRowOld = queueRowService.findById(id);
        QueueDepartment queueRow = new QueueDepartment();
        queueRow.setEmail(queueRowOld.getEmail());
        queueRow.setUser(queueRowOld.getUser());
        queueRow.setPhone(queueRowOld.getPhone());
        queueRow.setPassNumber(queueRowOld.getUser().getNumPassport());
        queueRow.setOperator(queueRowOld.getOperator());
        queueRow.setOperationId(queueRowOld.getOperation().getId());
        queueRow.setDepartmentId(queueRowOld.getOperator().getDepartment().getId());
        model.addAttribute("queueRowOld", queueRowOld);
        model.addAttribute("queueRow", queueRow);
        model.addAttribute("departments", departmentService.findAll());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userServiceImpl.findByNumPassport(currentPrincipalPassportNum);
        List<Operation> operations = new ArrayList<>();
        operations.add(queueRowOld.getOperation());
        operations.addAll(userServiceImpl.getNewOperations(user));
        model.addAttribute("operations", operations);
        return "editQueueRegistration";
    }

    @RequestMapping("/webcab/queue/{id}/registration/calendar/edit")
    public String showEditCalendar(@PathVariable Long id, @ModelAttribute("queueRow") QueueDepartment queueRow, Model model) {
        model.addAttribute("rowId",id);
        model.addAttribute("departmentId", queueRow.getDepartmentId());
        model.addAttribute("phone", queueRow.getPhone());
        model.addAttribute("email", queueRow.getEmail());
        model.addAttribute("operation", queueRow.getOperationId());
        model.addAttribute("queueRow", new QueueDepartment());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        queueRow.setPassNumber(currentPrincipalPassportNum);
        QueueRow queueRowOld = queueRowService.findById(id);
        List<LocalDateTime> conflictTimes = queueRowService.getConflictTimes(queueRow);
        conflictTimes.remove(queueRowOld.getDateTime());
        model.addAttribute("conflictTimes", conflictTimes);
        return "editCalendar";
    }

    @RequestMapping("webcab/docs")
    public String showDocuments(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userServiceImpl.findByNumPassport(currentPrincipalPassportNum);
        model.addAttribute("documentList", userServiceImpl.getDocumentsByClientId(user.getId()));
        return "documents";
    }

    @RequestMapping("webcab/docs/addBirthCertificate/")
    public String addBirthCertificate(Model model) {
        return "documents/addBirthCertificate";
    }

    @RequestMapping("/webcab/docs/addPassport/")
    public String addPassport(Model model) {
        return "documents/addPassport";
    }

    @RequestMapping("/webcab/docs/addRegistration/")
    public String addRegistration(Model model) {
        return "documents/addRegistration";
    }

    @RequestMapping("/webcab/docs/addVisa/")
    public String addVisa(Model model) {
        return "documents/addVisa";
    }

    @GetMapping("/login")
    public String login() {
        return "security/login";
    }

    @RequestMapping("/accessdenied")
    public String accessDenied() {
        return "security/accessdenied";
    }

    @RequestMapping("/articles/")
    public String showAllArticles(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

        model.addAttribute("articleType", "all");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.getAllArticles());
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "articles/articles";
    }

    @GetMapping("/articles/search")
    public String searchArticles(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        model.addAttribute("articleType", "all");
        model.addAttribute("query", query);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.searchArticles(query));
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("articleType", "all");
        return "articles/articles";
    }

    @RequestMapping("/articles/News")
    public String showNEWSArticles(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        model.addAttribute("articleType", "NEWS");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.getSpecificArticles(ArticleType.NEWS));
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "articles/articles";
    }

    @GetMapping("/articles/News/search")
    public String searchNewsArticles(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        model.addAttribute("query", query);
        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.searchNewsArticles(query));
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("articleType", "NEWS");
        return "articles/articles";
    }

    @RequestMapping("/articles/documentInfo")
    public String showDocINFArticles(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        model.addAttribute("articleType", "DOCUMENT_INFO");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.getSpecificArticles(ArticleType.DOCUMENT_INFO));
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "articles/articles";
    }

    @GetMapping("/articles/documentInfo/search")
    public String searchDocArticles(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        model.addAttribute("query", query);
        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.searchDocumentInfoArticles(query));
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("articleType", "DOCUMENT_INFO");
        return "articles/articles";
    }

    @RequestMapping("/articles/FAQ")
    public String showFAQ(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        model.addAttribute("articleType", "FAQ");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.getSpecificArticles(ArticleType.FAQ));
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "articles/articles";
    }

    @GetMapping("/articles/FAQ/search")
    public String searchFAQArticles(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        model.addAttribute("query", query);
        Page<Article> articlePage = articleService.findPaginated(PageRequest.of(currentPage -1,pageSize),articleService.searchFAQArticles(query));
        model.addAttribute("articlePage", articlePage);

        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("articleType", "FAQ");
        return "articles/articles";
    }

    @RequestMapping("/admin/createArticle")
    public String createArticle(Model model) {
        List<ArticleType> articleTypes = new ArrayList<>();
        articleTypes.add(ArticleType.FAQ);
        articleTypes.add(ArticleType.NEWS);
        articleTypes.add(ArticleType.DOCUMENT_INFO);
        model.addAttribute("articleTypes", articleTypes);
        return "articles/admin/createArticle";
    }

    @GetMapping("/article/{id}")
    public String showArticle(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.getArticleById(id));
        return "articles/article";
    }

    @RequestMapping("admin/users/")
    public String showUsersForAdmin(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){

        model.addAttribute("role", "all");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.findAll());
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }

    @RequestMapping("admin/users/search")
    public String searchUsersForAdmin(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        model.addAttribute("query", query);
        model.addAttribute("role", "all");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.searchUsers(query));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }

    @RequestMapping("admin/users/admins")
    public String showUsersAdminsForAdmin(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){

        model.addAttribute("role", "admin");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.findUsersByRoleId(2L));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }


    @RequestMapping("admin/users/admins/search")
    public String searchAdminUsersForAdmin(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        model.addAttribute("query", query);
        model.addAttribute("role", "admin");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.searchUsersWithRoles(query,2L));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }


    @RequestMapping("admin/users/users")
    public String showUsersUsersForAdmin(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){

        model.addAttribute("role", "user");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.findUsersByRoleId(1L));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }

    @RequestMapping("admin/users/users/search")
    public String searchUserUsersForAdmin(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        model.addAttribute("query", query);
        model.addAttribute("role", "user");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.searchUsersWithRoles(query,1L));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }

    @RequestMapping("admin/users/citizens")
    public String showCitizensUsersForAdmin(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){

        model.addAttribute("role", "citizen");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.findUsersByRoleId(3L));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }

    @RequestMapping("admin/users/citizens/search")
    public String searchCitizenUsersForAdmin(@RequestParam("query") String query, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        model.addAttribute("query", query);
        model.addAttribute("role", "citizen");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<User> userPage = userServiceImpl.findPaginated(PageRequest.of(currentPage -1,pageSize),userServiceImpl.searchUsersWithRoles(query,3L));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/admin/users";
    }



    @RequestMapping("/admin/users/createUser/")
    public String createUser(Model model) {
        Set<Role> roles  = roleService.findAllRoles();
        model.addAttribute("roles", roles);
        return "users/admin/addUser";
    }

    @RequestMapping("/admin/user/{id}/editUser/changeRoles")
    public String editUserRoles(@PathVariable Long id, Model model) {
        Set<Role> roles  = roleService.findAllRoles();
        User user = userServiceImpl.findById(id);
        model.addAttribute("userId", id);
        model.addAttribute("currentRoles", user.getRoles());
        model.addAttribute("roles", roles);
        Boolean b = user.getCitizen();
        if (b == null){
            b = false;
        }
        model.addAttribute("citizenship",b);
        return "users/admin/editUserRoles";
    }

    @RequestMapping("/admin/user/{id}/editUser/changePassword")
    public String editUserPassword(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "users/admin/editUserPassword";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userServiceImpl.findById(id);
        model.addAttribute("user",user);
        model.addAttribute("currentRoles", user.getRoles());
        model.addAttribute("documentList", userServiceImpl.getDocumentsByClientId(user.getId()));
        return "users/admin/user";
    }



    @GetMapping("/admin/article/editForm/{id}")
    public String showEditArticle(@PathVariable Long id, Model model) {
        List<ArticleType> articleTypes = new ArrayList<>();
        articleTypes.add(ArticleType.FAQ);
        articleTypes.add(ArticleType.NEWS);
        articleTypes.add(ArticleType.DOCUMENT_INFO);
        model.addAttribute("article", articleService.getArticleById(id));
        model.addAttribute("articleTypes", articleTypes);
        return "articles/admin/editArticle";
    }

    @GetMapping("/admin/queueRows/show")
    public String showAdminRows(){
        return "admin/queueRows";
    }
}
