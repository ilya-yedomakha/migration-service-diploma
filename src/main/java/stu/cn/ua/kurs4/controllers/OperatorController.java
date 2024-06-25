package stu.cn.ua.kurs4.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stu.cn.ua.kurs4.model.domain.people.Operator;

import stu.cn.ua.kurs4.model.services.people.OperatorService;

import java.util.List;

@RestController
public class OperatorController {
    @Autowired
    private OperatorService operatorService;

    @GetMapping("operator/{id}")
    public Operator getOperator(@PathVariable Long id){
        return operatorService.findById(id);
    }

    @GetMapping("/operator/getAll")
    public List<Operator> getOperators(){
        return operatorService.findAll();
    }

    @PostMapping("operator/")
    public Operator saveOperator(@RequestBody Operator operator){
        return operatorService.save(operator);
    }
}
