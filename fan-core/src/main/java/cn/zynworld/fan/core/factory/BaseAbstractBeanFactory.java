package cn.zynworld.fan.core.factory;

import cn.zynworld.fan.common.utils.ListUtils;
import cn.zynworld.fan.common.utils.ObjectUtils;
import cn.zynworld.fan.core.bean.BaseBeanConstructor;
import cn.zynworld.fan.core.bean.BeanConstructor;
import cn.zynworld.fan.core.bean.BeanDefinition;
import cn.zynworld.fan.core.config.BeanDefinitionReader;
import cn.zynworld.fan.core.enums.BeanStatusEnum;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class BaseAbstractBeanFactory implements BeanFactory {

    /**
     * bean 构造器 通过此构建实例
     */
    private BeanConstructor beanConstructor;

    /**
     * 读取 beanDefinition 从配置中
     */
    private List<BeanDefinitionReader> definitionReaderList;

    /**
     * 全部的beanDefinition
     */
    private List<BeanDefinition> beanDefinitionList;

    /**
     * key beanName value BeanDefinition
     */
    private Map<String,BeanDefinition> nameBeanDefinitionMap;

    /**
     * key beanClass value BeanDefinition
     */
    private Map<String, List<BeanDefinition>> classBeanDefinitionMap;

    /**
     * 存储键为beanName的单例实例
     */
    private Map<String,Object> nameBeanInstanceMap;

    /**
     * 存储键为className的单例实例
     */
    private Map<String,Object> classBeanInstanceMap;

    /**
     * @param definitionReaderList 读取配置所用的 BeanDefinitionReader
     */
    public BaseAbstractBeanFactory(List<BeanDefinitionReader> definitionReaderList) {
        this.definitionReaderList = definitionReaderList;
        this.beanConstructor = new BaseBeanConstructor(this);
        refresh();
    }

    /**
     * 通过名字获取beanName
     */
    public Object getBeanByName(String beanName) {
        if (nameBeanInstanceMap.containsKey(beanName)) {
            return nameBeanInstanceMap.get(beanName);
        }

        // 获取bean定义
        BeanDefinition beanDefinition = nameBeanDefinitionMap.get(beanName);
        // 判断是否需要实例化
        if (needCreateInstance(beanDefinition)) {
            return instantiationBean(beanDefinition);
        }
        return null;
    }

    /**
     * 通过类型获取bean
     */
    public <T> T getBeanByClass(Class<T> beanClass) {
        if (classBeanInstanceMap.containsKey(beanClass.getName())) {
            return (T) classBeanInstanceMap.get(beanClass.getName());
        }

        // 获取bean定义
        List<BeanDefinition> beanDefinitionList = classBeanDefinitionMap.get(beanClass.getName());
        if (ListUtils.isEmpty(beanDefinitionList)) {
            return null;
        }
        BeanDefinition definition = beanDefinitionList.get(0);

        // 判断是否需要实例化
        if (needCreateInstance(definition)) {
            return (T) instantiationBean(definition);
        }

        return null;
    }

    /**
     * 刷新BeanFactory
     */
    public void refresh() {
        clearContainer();
        // 从配置文件中读取BeanDefinition
        definitionReaderList.forEach(this::loadBeanDefinition);
        // 将beanDefinition加入对应map中
        beanDefinitionList.forEach(this::addDefinitions);
        // 对所有非懒加载单例实例化
        beanDefinitionList.stream()
                .filter(definition -> definition.getSingleton() && !definition.getLazyLoad())
                .forEach(this::instantiationBean);
    }

    /**
     * 实例化bean
     */
    private Object instantiationBean(BeanDefinition definition) {
        // 已经实例化过无需实例化
        if (definition.getBeanStatus().equals(BeanStatusEnum.BEAN_STATUS_INSTANCED)) {
            return null;
        }

        // 构建bean实例
        Object beanInstance = beanConstructor.createBeanInstance(definition);

        // 如果是单例 存到容器
        if (definition.getSingleton()) {
            // class
            definition.getBeanClassList().forEach(beanClass -> classBeanInstanceMap.put(beanClass.getName(), beanInstance));
            // beanName
            nameBeanInstanceMap.put(definition.getBeanName(), beanInstance);
        }

        return beanInstance;
    }

    /**
     * 从reader中读取bean定义
     * @param reader
     */
    private void loadBeanDefinition(BeanDefinitionReader reader) {
        List<BeanDefinition> definitions = reader.read();
        beanDefinitionList.addAll(definitions);
    }

    /**
     * 将beanDefinition加入到对应map中
     */
    private void addDefinitions(BeanDefinition definition) {
        // TODO 加入重名检测
        String beanName = definition.getBeanName();
        List<Class> beanClassList = definition.getBeanClassList();

        nameBeanDefinitionMap.put(beanName, definition);
        beanClassList.forEach(beanClass -> addDefinitionToClassBeanDefinitionMap(definition, beanClass));
    }

    private void addDefinitionToClassBeanDefinitionMap(BeanDefinition definition,Class beanClass) {
        if (!classBeanDefinitionMap.containsKey(beanClass.getName())) {
            classBeanDefinitionMap.put(beanClass.getName(), new ArrayList<>());
        }

        classBeanDefinitionMap.get(beanClass.getName()).add(definition);
    }

    /**
     * 清空容器
     */
    private void clearContainer() {
        nameBeanDefinitionMap = new HashMap<>();
        classBeanDefinitionMap = new HashMap<>();
        nameBeanInstanceMap = new HashMap<>();
        classBeanInstanceMap = new HashMap<>();
        beanDefinitionList = new ArrayList<>();
    }

    /**
     * 判断是否需要构建实例
     */
    private boolean needCreateInstance(BeanDefinition definition) {
        // 已经实例化的无需实例化
        if (ObjectUtils.isNull(definition) || definition.getBeanStatus().equals(BeanStatusEnum.BEAN_STATUS_INSTANCED.getCode())) {
            return false;
        }
        return true;
    }
}
