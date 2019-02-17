package cn.zynworld.fan.core.bean;

import cn.zynworld.fan.core.enums.BeanStatusEnum;

import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/17.
 * 对bean的定义
 */
public class BeanDefinition {
    /**
     * bean 名称
     */
    private String beanName;

    /**
     * bean的字节码
     */
    private Class beanClass;

    /**
     * bean 是否为单例
     */
    private Boolean singleton;

    /**
     * 是否为懒加载
     */
    private Boolean lazyLoad;

    /**
     * bean 的class 包含父类
     */
    private List<Class> beanClassList;

    /**
     * bean 的注解
     */
    private List<Class> beanAnnotationList;

    /**
     * bean 的所有依赖信息
     */
    private List<BeanDependent> beanDependents;

    /**
     * 状态 {@link BeanStatusEnum#getCode()}
     * 分初始态 和 已实例化
     * 非单例只存在于初始态，单例被实例化后转为实例化状态
     */
    private Integer beanStatus;

    public String getBeanName() {
        return beanName;
    }

    public BeanDefinition setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public BeanDefinition setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
        return this;
    }

    public Integer getBeanStatus() {
        return beanStatus;
    }

    public BeanDefinition setBeanStatus(Integer beanStatus) {
        this.beanStatus = beanStatus;
        return this;
    }

    public Boolean getSingleton() {
        return singleton;
    }

    public BeanDefinition setSingleton(Boolean singleton) {
        this.singleton = singleton;
        return this;
    }

    public Boolean getLazyLoad() {
        return lazyLoad;
    }

    public BeanDefinition setLazyLoad(Boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
        return this;
    }

    public List<Class> getBeanClassList() {
        return beanClassList;
    }

    public BeanDefinition setBeanClassList(List<Class> beanClassList) {
        this.beanClassList = beanClassList;
        return this;
    }

    public List<Class> getBeanAnnotationList() {
        return beanAnnotationList;
    }

    public BeanDefinition setBeanAnnotationList(List<Class> beanAnnotationList) {
        this.beanAnnotationList = beanAnnotationList;
        return this;
    }

    public List<BeanDependent> getBeanDependents() {
        return beanDependents;
    }

    public BeanDefinition setBeanDependents(List<BeanDependent> beanDependents) {
        this.beanDependents = beanDependents;
        return this;
    }
}
