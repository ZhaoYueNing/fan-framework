package cn.zynworld.fan.core.entity;

import javax.annotation.Resource;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class Dept {
    private String deptName;
    @Resource(name = "manager")
    private Employee manager;

    public String getDeptName() {
        return deptName;
    }

    public Dept setDeptName(String deptName) {
        this.deptName = deptName;
        return this;
    }

    public Employee getManager() {
        return manager;
    }

    public Dept setManager(Employee manager) {
        this.manager = manager;
        return this;
    }
}
