package stu.cn.ua.kurs4.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.controllers.dto.AdminQueueRowDTO;
import stu.cn.ua.kurs4.controllers.dto.QueueDepartment;
import stu.cn.ua.kurs4.controllers.dto.QueueRowDTO;
import stu.cn.ua.kurs4.model.domain.QueueRow;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.model.services.QueueRowService;
import stu.cn.ua.kurs4.model.services.people.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QueueRowController {
    @Autowired
    private QueueRowService queueService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping("queue/{id}")
    public QueueRow getQueueRow(@PathVariable Long id) {
        return queueService.findById(id);
    }

    @GetMapping("/queue/getAll")
    public List<QueueRow> getQueues() {
        return queueService.findAll();
    }

    @PostMapping("/queueRow/")
    public QueueRow saveQueueRow(@RequestBody QueueDepartment queueRow) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalPassportNum = authentication.getName();
        User user = userServiceImpl.findByNumPassport(currentPrincipalPassportNum);
        queueRow.setUser(user);
        return queueService.save(queueRow);
    }

    @PutMapping("/queueRow/{id}")
    public QueueRow editQueueRow(@PathVariable Long id, @RequestBody QueueDepartment queueRow) {
        return queueService.edit(id, queueRow);
    }

    @DeleteMapping("/queueRow/{id}")
    public void deleteQueueRow(@PathVariable Long id) {
        queueService.deleteById(id);
    }

    @GetMapping("/admin/queueRows")
    public List<AdminQueueRowDTO> getRows() {
        List<AdminQueueRowDTO> adminsQueueRowDTOS = new ArrayList<>();
        adminsQueueRowDTOS = queueService.findAllQueueRowDTOs();
        return adminsQueueRowDTOS;
    }
}
