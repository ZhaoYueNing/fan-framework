package cn.zynworld.fan.core.entity;

import cn.zynworld.fan.core.annotations.Value;

import javax.annotation.Resource;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class Employee {
    private String employeeName;
    // 注入属性参数
    @Value("managerAge")
    private Integer age;

    public String getEmployeeName() {
        return employeeName;
    }

    public Employee setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Employee setAge(Integer age) {
        this.age = age;
        return this;
    }
}
