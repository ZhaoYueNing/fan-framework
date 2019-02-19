package cn.zynworld.fan.core.bean;

import cn.zynworld.fan.common.utils.Constants;
import cn.zynworld.fan.common.utils.ListUtils;
import cn.zynworld.fan.common.utils.ObjectUtils;
import cn.zynworld.fan.common.utils.ReflectionUtils;
import cn.zynworld.fan.core.enums.BeanDependentInjectLevelEnum;
import cn.zynworld.fan.core.enums.BeanDependentInjectTypeEnum;
import cn.zynworld.fan.core.factory.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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


            if (ObjectUtils.isNotNull(propertyValue)) {
                reflectHandle(dependent.getInjectLevel(), dependent.getName(), beanInstance, propertyValue, dependent.getInjectType());
            }
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
                reflectHandle(dependent.getInjectLevel(), dependent.getName(), beanInstance, param, dependent.getInjectType());
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
                reflectHandle(dependent.getInjectLevel(), dependent.getName(), beanInstance, param, dependent.getInjectType());
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
            Object param = dependent.getInjectInfo();
            if (ObjectUtils.isNotNull(param)) {
                reflectHandle(dependent.getInjectLevel(), dependent.getName(), beanInstance, param, dependent.getInjectType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射调用方法或给字段赋值
     *
     * @param levelCode    {@link BeanDependentInjectLevelEnum#getCode()}
     * @param name 字段名或方法名
     * @param beanInstance bean实例
     * @param param 赋值参数
     * @param injectTypeCode {@link BeanDependentInjectTypeEnum#getCode()}
     * TODO task 重写反射调用
     */
    private void reflectHandle(Integer levelCode, String name, Object beanInstance, Object param,Integer injectTypeCode) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // 字段赋值
        if (BeanDependentInjectLevelEnum.INJECT_LEVEL_FIELD.getCode().equals(levelCode)) {
            Field field = beanInstance.getClass().getDeclaredField(name);
            field.setAccessible(Boolean.TRUE);
            // 如果是字符串参数 有可能要转型到字段的类型
            if (param.getClass().equals(String.class)) {
                param = ReflectionUtils.stringToBaseType((String) param, field.getType());
            }
            field.set(beanInstance, param);
            return;
        }

        // 方法赋值 非属性注入 非值注入
        if (BeanDependentInjectLevelEnum.INJECT_LEVEL_METHOD.getCode().equals(levelCode)
                && !BeanDependentInjectTypeEnum.INJECT_TYPE_PROPERTY.getCode().equals(injectTypeCode)
                && !BeanDependentInjectTypeEnum.INJECT_TYPE_VALUE.getCode().equals(injectTypeCode)) {
            beanInstance.getClass().getDeclaredMethod(name, param.getClass()).invoke(beanInstance, param);
            return;
        }

        final HashSet<Class> BASE_TYPE = new HashSet<Class>(Arrays.asList(Integer.class, Float.class, Double.class, String.class, int.class, float.class, double.class));

        // 方法赋值 属性注入 由于属性注入无法知道方法的参数类型故取该方法名的第一个单参数方法
        if (BeanDependentInjectLevelEnum.INJECT_LEVEL_METHOD.getCode().equals(levelCode) && BeanDependentInjectTypeEnum.INJECT_TYPE_PROPERTY.getCode().equals(injectTypeCode)) {
            Method[] methods = beanInstance.getClass().getMethods();
            for (Method method : methods) {
                if (name.equals(method.getName()) && method.getParameterCount() == Constants.ONE && BASE_TYPE.contains(method.getParameterTypes()[Constants.ZERO])) {
                    param = ReflectionUtils.stringToBaseType((String) param, method.getParameterTypes()[Constants.ZERO]);
                    method.invoke(beanInstance, param);
                    break;
                }
            }
            return;
        }

        // 方法赋值 值注入 由于值注入无法知道方法的参数类型故取该方法名的第一个单参数方法
        if (BeanDependentInjectLevelEnum.INJECT_LEVEL_METHOD.getCode().equals(levelCode) && BeanDependentInjectTypeEnum.INJECT_TYPE_VALUE.getCode().equals(injectTypeCode)) {
            Method[] methods = beanInstance.getClass().getMethods();
            for (Method method : methods) {
                if (name.equals(method.getName()) && method.getParameterCount() == Constants.ONE && BASE_TYPE.contains(method.getParameterTypes()[Constants.ZERO])) {
                    param = ReflectionUtils.stringToBaseType((String) param, method.getParameterTypes()[Constants.ZERO]);
                    method.invoke(beanInstance, param);
                    break;
                }
            }
        }
    }
}
