package cn.zynworld.fan.core.entity;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class Employee {
    private String employeeName;
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
