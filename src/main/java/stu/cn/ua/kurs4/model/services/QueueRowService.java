package stu.cn.ua.kurs4.model.services;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.controllers.dto.AdminQueueRowDTO;
import stu.cn.ua.kurs4.controllers.dto.QueueDepartment;
import stu.cn.ua.kurs4.model.domain.QueueRow;
import stu.cn.ua.kurs4.model.domain.operations.Operation;
import stu.cn.ua.kurs4.model.domain.people.Operator;
import stu.cn.ua.kurs4.model.domain.people.User;
import stu.cn.ua.kurs4.repositories.QueueRowRepo;
import stu.cn.ua.kurs4.repositories.operations.OperationRepo;
import stu.cn.ua.kurs4.repositories.people.UserRepo;
import stu.cn.ua.kurs4.repositories.people.OperatorRepo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueueRowService {

    private final QueueRowRepo queueRowRepo;

    private final UserRepo userRepo;

    private final OperatorRepo operatorRepo;
    private final OperationRepo operationRepo;

    private final SendEmailService sendEmailService;

    private final SendSMSService sendSMSService;

    public QueueRowService(QueueRowRepo queueRowRepo, UserRepo userRepo, OperatorRepo operatorRepo, OperationRepo operationRepo, SendEmailService sendEmailService, SendSMSService sendSMSService) {
        this.queueRowRepo = queueRowRepo;
        this.userRepo = userRepo;
        this.operatorRepo = operatorRepo;
        this.operationRepo = operationRepo;
        this.sendEmailService = sendEmailService;
        this.sendSMSService = sendSMSService;
    }


    public QueueRow findById(Long id) {
        Optional<QueueRow> queueRowOptional = queueRowRepo.findById(id);
        return queueRowOptional.orElse(null);
    }

    public List<QueueRow> findAll() {
        List<QueueRow> queueRows = queueRowRepo.findAll();
        return queueRowRepo.findAll();
    }

    public List<LocalDateTime> getConflictTimes(QueueDepartment queueRowDep) {
        Long departmentId = queueRowDep.getDepartmentId();
        QueueRow queueRow = new QueueRow();
        queueRow.setUser(userRepo.findByNumPassport(queueRowDep.getPassNumber()));
        User user = userRepo.findByNumPassport(queueRowDep.getPassNumber());
        queueRow.setDateTime(queueRowDep.getDateTime());
        queueRow.setEmail(queueRowDep.getEmail());
        queueRow.setPhone(queueRowDep.getPhone());
        queueRow.setOperation(operationRepo.findOperationById(queueRowDep.getOperationId()));
        List<Operator> operators_all_dep = operatorRepo.findAll();
        List<Operator> operators = new ArrayList<>();
        Set<LocalDateTime> conflictDateTimes = new HashSet<>();

        for (Operator operator : operators_all_dep) {
            if (operator.getDepartment().getId().equals(departmentId)) {
                operators.add(operator);
            }
        }
        List<QueueRow> queueRows_all = queueRowRepo.findAll();
        List<QueueRow> queueRows = new ArrayList<>();
        for (QueueRow queueRow1 : queueRows_all){
            if (queueRow1.getOperator().getDepartment().getId().equals(departmentId)){
                queueRows.add(queueRow1);
            }
        }

        for (QueueRow queueRow1 : user.getQueues()){
            conflictDateTimes.add(queueRow1.getDateTime());
        }

        for (QueueRow queueRow1 : queueRows){
            int count = 0;
            for (QueueRow queueRow2 : queueRows){
                if (Objects.equals(queueRow1.getDateTime(),queueRow2.getDateTime())){
                    count++;
                }
            }
            if (count == operators.size()) {
                conflictDateTimes.add(queueRow1.getDateTime());
            }
        }

        return new ArrayList<>(conflictDateTimes);
    }


    public QueueRow save(QueueDepartment queueRowDep) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = queueRowDep.getDateTime();

        if (dateTime.isEqual(now) || dateTime.isBefore(now)) {
            return null;
        }

        LocalTime time = dateTime.toLocalTime();
        if (time.isBefore(LocalTime.of(9, 0)) || time.isAfter(LocalTime.of(18, 0))) {
            return null;
        }

        if (time.getMinute() != 0 || time.getSecond() != 0) {
            return null;
        }
        Long departmentId = queueRowDep.getDepartmentId();
        QueueRow queueRow = new QueueRow();
        queueRow.setUser(userRepo.findByNumPassport(queueRowDep.getUser().getNumPassport()));
        queueRow.setDateTime(queueRowDep.getDateTime());
        queueRow.setEmail(queueRowDep.getEmail());
        queueRow.setPhone(queueRowDep.getPhone());
        queueRow.setOperation(operationRepo.findOperationById(queueRowDep.getOperationId()));
        List<Operator> operators_all_dep = operatorRepo.findAll();
        List<Operator> operators = new ArrayList<>();


        for (Operator operator : operators_all_dep) {
            if (operator.getDepartment().getId().equals(departmentId)) {
                operators.add(operator);
            }
        }
        List<Operator> completelyFreeOperators = new ArrayList<>();
        List<Operator> freeOperators = new ArrayList<>();
        List<QueueRow> queueRows_all = queueRowRepo.findAll();
        List<QueueRow> queueRows = new ArrayList<>();
        for (QueueRow queueRow1 : queueRows_all){
            if (queueRow1.getOperator().getDepartment().getId().equals(departmentId)){
                queueRows.add(queueRow1);
            }
        }
        if (Objects.equals(queueRow.getEmail(), "")) {
            queueRow.setEmail(null);
        }

        if (Objects.equals(queueRow.getPhone(), "")) {
            queueRow.setPhone(null);
        }

        for (Operator operator : operators) {
            if (operator.getQueueRows().isEmpty()) {
                completelyFreeOperators.add(operator);
            }
        }
        boolean free = true;
        Duration duration;

        for (QueueRow queueRow1 : queueRows) {
            if (queueRow1.getUser() != null) {
                if (Objects.equals(queueRow1.getUser().getId(), queueRow.getUser().getId())) {
                    for (QueueRow queueRow2 : queueRow1.getUser().getQueues()) {
                        if (Objects.equals(queueRow2.getOperation(), queueRow.getOperation())) {
                            return null;
                        }
                        if (Objects.equals(queueRow2.getDateTime(), queueRow.getDateTime()) && queueRow2.getOperator().getDepartment().getId().equals(departmentId)) {
                            return null;
                        } else if (queueRow2.getDateTime().isAfter(queueRow.getDateTime()) && queueRow2.getOperator().getDepartment().getId().equals(departmentId)) {
                            duration = Duration.between(queueRow.getDateTime(), queueRow2.getDateTime());
                            if (duration.toHours() < 1) {
                                return null;
                            }
                        } else if (queueRow.getDateTime().isAfter(queueRow2.getDateTime()) && queueRow2.getOperator().getDepartment().getId().equals(departmentId)) {
                            duration = Duration.between(queueRow2.getDateTime(), queueRow.getDateTime());
                            if (duration.toHours() < 1) {
                                return null;
                            }
                        }
                    }
                }
            }
        }

        for (QueueRow queueRow1 : queueRows) {
            if (queueRow1.getDateTime().isAfter(queueRow.getDateTime())) {
                duration = Duration.between(queueRow.getDateTime(), queueRow1.getDateTime());
                if (duration.toHours() >= 1) {
                    free = true;
                    if (queueRow1.getOperator() != null) {
                        for (QueueRow queueRow2 : queueRow1.getOperator().getQueueRows()) {
                            if (queueRow2.getDateTime().isAfter(queueRow.getDateTime())) {
                                duration = Duration.between(queueRow.getDateTime(), queueRow2.getDateTime());

                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else if (queueRow.getDateTime().isAfter(queueRow2.getDateTime())) {
                                duration = Duration.between(queueRow2.getDateTime(), queueRow.getDateTime());
                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else {
                                free = false;
                                break;
                            }
                        }
                    }
                    if (free) {
                        freeOperators.add(queueRow1.getOperator());
                    }
                }
            } else if (queueRow.getDateTime().isAfter(queueRow1.getDateTime())) {
                duration = Duration.between(queueRow1.getDateTime(), queueRow.getDateTime());
                if (duration.toHours() >= 1) {
                    if (queueRow1.getOperator() != null) {
                        free = true;
                        for (QueueRow queueRow2 : queueRow1.getOperator().getQueueRows()) {
                            if (queueRow2.getDateTime().isAfter(queueRow.getDateTime())) {
                                duration = Duration.between(queueRow.getDateTime(), queueRow2.getDateTime());

                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else if (queueRow.getDateTime().isAfter(queueRow2.getDateTime())) {
                                duration = Duration.between(queueRow2.getDateTime(), queueRow.getDateTime());
                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else {
                                free = false;
                                break;
                            }
                        }
                    }
                    if (free) {
                        freeOperators.add(queueRow1.getOperator());
                    }
                }
            }

        }

        List<Operator> allOperators = new ArrayList<>();
        allOperators.addAll(completelyFreeOperators);
        allOperators.addAll(freeOperators);
        if (allOperators.isEmpty()) {
            return null;
        }
        int min_queues = Integer.MAX_VALUE;
        Operator minOperator = new Operator();
        for (Operator operator : allOperators){
            if (operator.getQueueRows().size() <= min_queues){
                min_queues = operator.getQueueRows().size();
                minOperator = operator;
            }

        }
        queueRow.setOperator(minOperator);

        if (queueRow.getEmail() != null){
            String body = "Вас записано в чергу на послугу "+queueRow.getOperation().getName() + " до оператора " + queueRow.getOperator().getFirstName() + " " +
                    queueRow.getOperator().getLastName() + " у відділок " + queueRow.getOperator().getDepartment().getNumber() + " міста "+
                    queueRow.getOperator().getDepartment().getCity() + " області " + queueRow.getOperator().getDepartment().getRegion() +
                    " на дату: " + queueRow.getDateTime();
            try {
                sendEmailService.sendMail(queueRow.getEmail(), body,"Черга в міграційній службі");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        if (queueRow.getPhone() != null){
            String body = "Вас записано в чергу на послугу "+queueRow.getOperation().getName() + " до оператора " + queueRow.getOperator().getFirstName() + " " +
                    queueRow.getOperator().getLastName() + " у відділок " + queueRow.getOperator().getDepartment().getNumber() + " міста "+
                    queueRow.getOperator().getDepartment().getCity() + " області " + queueRow.getOperator().getDepartment().getRegion() +
                    " на дату: " + queueRow.getDateTime();
            sendSMSService.sendSMS(queueRow.getPhone(), body);
        }

        return queueRowRepo.save(queueRow);
    }


    public QueueRow edit(Long id, QueueDepartment queueRowDep) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = queueRowDep.getDateTime();

        if (dateTime.isEqual(now) || dateTime.isBefore(now)) {
            return null;
        }

        LocalTime time = dateTime.toLocalTime();
        if (time.isBefore(LocalTime.of(9, 0)) || time.isAfter(LocalTime.of(18, 0))) {
            return null;
        }

        if (time.getMinute() != 0 || time.getSecond() != 0) {
            return null;
        }
        Long departmentId = queueRowDep.getDepartmentId();
        QueueRow queueRow = queueRowRepo.findQueueRowById(id);
        List<QueueRow> queueRows_all = queueRowRepo.findAll();
        queueRows_all.remove(queueRow);

        // deleting current operator and operation
        Operator operator_current = queueRow.getOperator();
        Operation operation_current = queueRow.getOperation();

        operator_current.getQueueRows().remove(queueRow);
        operation_current.getQueueRows().remove(queueRow);

        operatorRepo.save(operator_current);
        operationRepo.save(operation_current);

        queueRow.setOperator(null);
        queueRow.setOperation(operationRepo.findOperationById(queueRowDep.getOperationId()));


        queueRow.setDateTime(queueRowDep.getDateTime());
        queueRow.setEmail(queueRowDep.getEmail());
        queueRow.setPhone(queueRowDep.getPhone());
        queueRow.setOperation(operationRepo.findOperationById(queueRowDep.getOperationId()));



        List<Operator> operators_all_dep = operatorRepo.findAll();
        List<Operator> operators = new ArrayList<>();


        for (Operator operator : operators_all_dep) {
            if (operator.getDepartment().getId().equals(departmentId)) {
                operators.add(operator);
            }
        }
        List<Operator> completelyFreeOperators = new ArrayList<>();
        List<Operator> freeOperators = new ArrayList<>();

        List<QueueRow> queueRows = new ArrayList<>();
        for (QueueRow queueRow1 : queueRows_all){
            if (queueRow1.getOperator().getDepartment().getId().equals(departmentId)){
                queueRows.add(queueRow1);
            }
        }
        if (Objects.equals(queueRow.getEmail(), "")) {
            queueRow.setEmail(null);
        }

        if (Objects.equals(queueRow.getPhone(), "")) {
            queueRow.setPhone(null);
        }

        for (Operator operator : operators) {
            if (operator.getQueueRows().isEmpty()) {
                completelyFreeOperators.add(operator);
            }
        }
        boolean free = true;
        Duration duration;

        for (QueueRow queueRow1 : queueRows) {
            if (queueRow1.getUser() != null) {
                if (Objects.equals(queueRow1.getUser().getId(), queueRow.getUser().getId())) {
                    Set<QueueRow> editQueues = queueRow1.getUser().getQueues();
                    editQueues.remove(queueRow);
                    for (QueueRow queueRow2 : editQueues) {
                        if (Objects.equals(queueRow2.getOperation(), queueRow.getOperation())) {
                            return null;
                        }
                        if (Objects.equals(queueRow2.getDateTime(), queueRow.getDateTime()) && queueRow2.getOperator().getDepartment().getId().equals(departmentId)) {
                            return null;
                        } else if (queueRow2.getDateTime().isAfter(queueRow.getDateTime()) && queueRow2.getOperator().getDepartment().getId().equals(departmentId)) {
                            duration = Duration.between(queueRow.getDateTime(), queueRow2.getDateTime());
                            if (duration.toHours() < 1) {
                                return null;
                            }
                        } else if (queueRow.getDateTime().isAfter(queueRow2.getDateTime()) && queueRow2.getOperator().getDepartment().getId().equals(departmentId)) {
                            duration = Duration.between(queueRow2.getDateTime(), queueRow.getDateTime());
                            if (duration.toHours() < 1) {
                                return null;
                            }
                        }
                    }
                }
            }
        }

        for (QueueRow queueRow1 : queueRows) {
            if (queueRow1.getDateTime().isAfter(queueRow.getDateTime())) {
                duration = Duration.between(queueRow.getDateTime(), queueRow1.getDateTime());
                if (duration.toHours() >= 1) {
                    free = true;
                    if (queueRow1.getOperator() != null) {
                        for (QueueRow queueRow2 : queueRow1.getOperator().getQueueRows()) {
                            if (queueRow2.getDateTime().isAfter(queueRow.getDateTime())) {
                                duration = Duration.between(queueRow.getDateTime(), queueRow2.getDateTime());

                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else if (queueRow.getDateTime().isAfter(queueRow2.getDateTime())) {
                                duration = Duration.between(queueRow2.getDateTime(), queueRow.getDateTime());
                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else {
                                free = false;
                                break;
                            }
                        }
                    }
                    if (free) {
                        freeOperators.add(queueRow1.getOperator());
                    }
                }
            } else if (queueRow.getDateTime().isAfter(queueRow1.getDateTime())) {
                duration = Duration.between(queueRow1.getDateTime(), queueRow.getDateTime());
                if (duration.toHours() >= 1) {
                    if (queueRow1.getOperator() != null) {
                        free = true;
                        for (QueueRow queueRow2 : queueRow1.getOperator().getQueueRows()) {
                            if (queueRow2.getDateTime().isAfter(queueRow.getDateTime())) {
                                duration = Duration.between(queueRow.getDateTime(), queueRow2.getDateTime());

                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else if (queueRow.getDateTime().isAfter(queueRow2.getDateTime())) {
                                duration = Duration.between(queueRow2.getDateTime(), queueRow.getDateTime());
                                if (duration.toHours() < 1) {
                                    free = false;
                                    break;
                                }
                            } else {
                                free = false;
                                break;
                            }
                        }
                    }
                    if (free) {
                        freeOperators.add(queueRow1.getOperator());
                    }
                }
            }

        }

        List<Operator> allOperators = new ArrayList<>();
        allOperators.addAll(completelyFreeOperators);
        allOperators.addAll(freeOperators);
        if (allOperators.isEmpty()) {
            return null;
        }
        int min_queues = Integer.MAX_VALUE;
        Operator minOperator = new Operator();
        for (Operator operator : allOperators){
            if (operator.getQueueRows().size() <= min_queues){
                min_queues = operator.getQueueRows().size();
                minOperator = operator;
            }

        }
        queueRow.setOperator(minOperator);

        if (queueRow.getEmail() != null){
            String body = "Вас записано в чергу на послугу "+queueRow.getOperation().getName() + " до оператора " + queueRow.getOperator().getFirstName() + " " +
                    queueRow.getOperator().getLastName() + " у відділок " + queueRow.getOperator().getDepartment().getNumber() + " міста "+
                    queueRow.getOperator().getDepartment().getCity() + " області " + queueRow.getOperator().getDepartment().getRegion() +
                    " на дату: " + queueRow.getDateTime();
            try {
                sendEmailService.sendMail(queueRow.getEmail(), body,"Черга в міграційній службі");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        if (queueRow.getPhone() != null){
            String body = "Вас записано в чергу на послугу "+queueRow.getOperation().getName() + " до оператора " + queueRow.getOperator().getFirstName() + " " +
                    queueRow.getOperator().getLastName() + " у відділок " + queueRow.getOperator().getDepartment().getNumber() + " міста "+
                    queueRow.getOperator().getDepartment().getCity() + " області " + queueRow.getOperator().getDepartment().getRegion() +
                    " на дату: " + queueRow.getDateTime();
            sendSMSService.sendSMS(queueRow.getPhone(), body);
        }

        return queueRowRepo.save(queueRow);
    }

    public void deleteById(Long id) {
        QueueRow queueRow = queueRowRepo.findById(id).orElse(null);
        if (queueRow == null) {
            return;
        }

        User user = queueRow.getUser();
        if (user != null) {
            user.getQueues().remove(queueRow);
            userRepo.save(user);
        }

        Operator operator = queueRow.getOperator();
        if (operator != null) {
            operator.getQueueRows().remove(queueRow);
            operatorRepo.save(operator);
        }

        Operation operation = queueRow.getOperation();
        if (operation != null) {
            operation.getQueueRows().remove(queueRow);
            operationRepo.save(operation);
        }

        queueRow.setUser(null);
        queueRow.setOperation(null);
        queueRow.setOperator(null);
        queueRowRepo.deleteById(id);
    }

    public List<AdminQueueRowDTO> findAllQueueRowDTOs() {
        List<QueueRow> queueRows = queueRowRepo.findAll();
        List<AdminQueueRowDTO> adminQueueRowDTOS =new ArrayList<>();
        for (QueueRow queueRow : queueRows){
            adminQueueRowDTOS.add(new AdminQueueRowDTO(queueRow.getId(),queueRow.getUser().getNumPassport(), queueRow.getUser().getFirstName(),
                    queueRow.getUser().getLastName(), queueRow.getOperator().getFirstName(),queueRow.getOperator().getLastName(), queueRow.getOperation(), queueRow.getPhone(),
                    queueRow.getEmail(),queueRow.getDateTime(),queueRow.getOperator().getDepartment().getNumber(),queueRow.getOperator().getDepartment().getRegion(),
                    queueRow.getOperator().getDepartment().getCity()));
        }
        return adminQueueRowDTOS;
    }
}
