package cn.zynworld.fan.core.bean;

import cn.zynworld.fan.common.utils.ListUtils;
import cn.zynworld.fan.common.utils.ObjectUtils;
import cn.zynworld.fan.common.utils.ReflectionUtils;
import cn.zynworld.fan.core.enums.BeanDependentInjectTypeEnum;
import cn.zynworld.fan.core.factory.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/17.
 * 构造bean实例
 */
public class BaseBeanConstructor implements BeanConstructor{
    private BeanFactory beanFactory;

    public BaseBeanConstructor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 构建bean实例
     */
    @Override
    public Object createBeanInstance(BeanDefinition definition) {
        try {
            Object beanInstance = definition.getBeanClass().newInstance();
            // 解决bean的依赖
            handleBeanDependents(beanInstance, definition);
            return beanInstance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 处理bean依赖
     */
    private void handleBeanDependents(Object beanInstance, BeanDefinition definition) {
        List<BeanDependent> beanDependents = definition.getBeanDependents();
        if (ListUtils.isEmpty(beanDependents)) {
            return;
        }

        // 按不同类型解决依赖
        for (BeanDependent dependent : beanDependents) {
            handleBeanDependentByClass(beanInstance, dependent);
            handleBeanDependentByName(beanInstance, dependent);
            handleBeanDependentByValue(beanInstance, dependent);
            handleBeanDependentByProperty(beanInstance, dependent);
        }
    }

    /**
     * 通过属性解决单个依赖
     * @param beanInstance bean实例
     * @param dependent bean依赖信息
     */
    private void handleBeanDependentByProperty(Object beanInstance, BeanDependent dependent) {
        try {
            if (!dependent.getInjectType().equals(BeanDependentInjectTypeEnum.INJECT_TYPE_PROPERTY.getCode())) {
                return;
            }
            // 获取属性值
            String propertyValue = beanFactory.getProperty(dependent.getInjectInfo());
            if (ObjectUtils.isNull(propertyValue)) {
                return;
            }

            // 获取到参数类型
            String fileName = ReflectionUtils.methodNameToFieldName(dependent.getMethodName());
            Class<?> zlass = beanInstance.getClass().getDeclaredField(fileName).getType();

            // 将字符转为基本类
            Object param = ReflectionUtils.stringToBaseType(dependent.getInjectInfo(), zlass);
            if (ObjectUtils.isNull(param)) {
                return;
            }

            // 反射调用方法
            beanInstance.getClass().getMethod(dependent.getMethodName(), param.getClass()).invoke(beanInstance, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过class解决单个依赖
     * @param beanInstance bean实例
     * @param dependent bean依赖信息
     */
    private void handleBeanDependentByClass(Object beanInstance, BeanDependent dependent) {
        try {
            if (!dependent.getInjectType().equals(BeanDependentInjectTypeEnum.INJECT_TYPE_CLASS.getCode())) {
                return;
            }
            Class<?> beanClass = Class.forName(dependent.getInjectInfo());
            // 从beanFactory获取依赖bean
            Object param = beanFactory.getBeanByClass(beanClass);
            if (ObjectUtils.isNotNull(param)) {
                beanInstance.getClass().getMethod(dependent.getMethodName(), param.getClass()).invoke(beanInstance, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过beanName解决单个依赖
     *
     * @param beanInstance  bean实例
     * @param dependent bean依赖信息
     */
    private void handleBeanDependentByName(Object beanInstance, BeanDependent dependent) {
        try {
            if (!dependent.getInjectType().equals(BeanDependentInjectTypeEnum.INJECT_TYPE_NAME.getCode())) {
                return;
            }
            // 从beanFactory获取依赖bean
            Object param = beanFactory.getBeanByName(dependent.getInjectInfo());
            if (ObjectUtils.isNotNull(param)) {
                beanInstance.getClass().getMethod(dependent.getMethodName(), param.getClass()).invoke(beanInstance, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过直接赋值
     *
     * @param beanInstance bean实例
     * @param dependent bean依赖信息
     */
    private void handleBeanDependentByValue(Object beanInstance, BeanDependent dependent) {
        try {
            if (!dependent.getInjectType().equals(BeanDependentInjectTypeEnum.INJECT_TYPE_VALUE.getCode())) {
                return;
            }
            // TODO 当前仅仅支持字符串类型
            Object param = dependent.getInjectInfo();
            if (ObjectUtils.isNotNull(param)) {
                beanInstance.getClass().getMethod(dependent.getMethodName(), param.getClass()).invoke(beanInstance, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
