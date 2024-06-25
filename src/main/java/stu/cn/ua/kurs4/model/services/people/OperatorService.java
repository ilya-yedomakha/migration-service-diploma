package stu.cn.ua.kurs4.model.services.people;

import org.springframework.stereotype.Service;
import stu.cn.ua.kurs4.model.domain.people.Operator;
import stu.cn.ua.kurs4.repositories.QueueRowRepo;
import stu.cn.ua.kurs4.repositories.people.OperatorRepo;

import java.util.List;
import java.util.Optional;

@Service
public class OperatorService {


    private final QueueRowRepo queueRowRepo;
    
    private final OperatorRepo operatorRepo;

    public OperatorService(QueueRowRepo queueRowRepo, OperatorRepo operatorRepo) {
        this.queueRowRepo = queueRowRepo;
        this.operatorRepo = operatorRepo;
    }

    public Operator findById(Long id){
        Optional<Operator> operatorOptional = operatorRepo.findById(id);
        return operatorOptional.orElse(null);
    }

    public List<Operator> findAll(){
        List<Operator> operators = operatorRepo.findAll();
        return operatorRepo.findAll();
    }

    public Operator save(Operator operator){

        return operatorRepo.save(operator);
    }

    public void deleteById(Long id){
        operatorRepo.deleteById(id);
    }

    public Operator update(Operator operator, Long id){
        Operator operator1 = operatorRepo.getById(id);
        //operator.set(operator1.get)
        operatorRepo.save(operator1);
        return operator;
    }
}
