package cn.zynworld.fan.core.bean;

import cn.zynworld.fan.common.utils.ObjectUtils;
import cn.zynworld.fan.common.utils.ReflectionUtils;
import cn.zynworld.fan.common.utils.StringUtils;
import cn.zynworld.fan.core.annotations.Value;
import cn.zynworld.fan.core.enums.BeanDependentInjectLevelEnum;
import cn.zynworld.fan.core.enums.BeanDependentInjectTypeEnum;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by zhaoyuening on 2019/2/18.
 * 对具备基本信息的beanDefinition进行解析及校验
 */
public class BeanDefinitionParser {
    /**
     * 解析具备基础信息的beanDefinition
     */
    public static void parse(BeanDefinition definition) {
        handleDefinitionSuperClassAndInterfaces(definition);
        handleDefinitionDependent(definition);
    }


    /**
     * 解析definition的所有超类及接口
     */
    private static void handleDefinitionSuperClassAndInterfaces(BeanDefinition definition) {
        // 得到该类的所有父类及父接口
        Set<Class> allSuperClass = ReflectionUtils.getAllSuperClass(definition.getBeanClass());
        definition.setBeanSupperClassSet(allSuperClass);
        // 得到类的所有接口及父接口
        Set<Class> allInterfaces = ReflectionUtils.getInterfaces(definition.getBeanClass());
        definition.setBeanSupperInterfaceSet(allInterfaces);
    }

    /**
     * 从字段及方法解析注释并形成依赖
     */
    private static void handleDefinitionDependent(BeanDefinition definition) {
        if (ObjectUtils.isNull(definition.getBeanDependents())) {
            definition.setBeanDependents(new ArrayList<>());
        }

        // 该bean的所有字段及方法
        Field[] fields = definition.getBeanClass().getDeclaredFields();
        Method[] methods = definition.getBeanClass().getDeclaredMethods();

        // 获取所有标注了Resource注解的字段 转为依赖
        List<BeanDependent> fieldResourceDependents = Arrays.stream(fields)
                .filter(field -> ObjectUtils.isNotNull(field.getAnnotation(Resource.class)))
                .map(BeanDefinitionParser::handleResourceAnnotation)
                .collect(Collectors.toList());
        definition.getBeanDependents().addAll(fieldResourceDependents);

        // 获取所有标注了Resource的方法
        List<BeanDependent> methodResourceDependents = Arrays.stream(methods)
                .filter(method -> ObjectUtils.isNotNull(method.getAnnotation(Resource.class)))
                .map(BeanDefinitionParser::handleResourceAnnotation)
                .collect(Collectors.toList());
        definition.getBeanDependents().addAll(methodResourceDependents);

        // 获取所有标注了Value的字段
        List<BeanDependent> fieldValueDependents = Arrays.stream(fields)
                .filter(field -> ObjectUtils.isNotNull(field.getAnnotation(Value.class)) && StringUtils.isNotEmpty(field.getAnnotation(Value.class).value()))
                .map(BeanDefinitionParser::handleValueAnnotation)
                .collect(Collectors.toList());
        definition.getBeanDependents().addAll(fieldValueDependents);

        // 获取所有标注了Value的方法
        List<BeanDependent> methodValueDependents = Arrays.stream(methods)
                .filter(method -> ObjectUtils.isNotNull(method.getAnnotation(Value.class)) && StringUtils.isNotEmpty(method.getAnnotation(Value.class).value()))
                .map(BeanDefinitionParser::handleResourceAnnotation)
                .collect(Collectors.toList());
        definition.getBeanDependents().addAll(methodValueDependents);
    }


    /**
     * 将标注了Resource注解的方法转为依赖
     */
    private static BeanDependent handleResourceAnnotation(Method method) {
        Resource resource = method.getAnnotation(Resource.class);

        BeanDependent dependent = new BeanDependent();
        // 字段级别
        dependent.setInjectLevel(BeanDependentInjectLevelEnum.INJECT_LEVEL_METHOD.getCode());
        dependent.setName(method.getName());
        if (StringUtils.isNotEmpty(resource.name())) {
            // 采用beanName的方式注入
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_NAME.getCode());
            dependent.setInjectInfo(resource.name());
        } else {
            // 采用注解类型方式注入
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_CLASS.getCode());
            dependent.setInjectInfo(method.getReturnType().getName());
        }

        return dependent;
    }

    /**
     * 将标注了Resource注解的字段转为依赖
     */
    private static BeanDependent handleResourceAnnotation(Field field) {
        Resource resource = field.getAnnotation(Resource.class);

        BeanDependent dependent = new BeanDependent();
        // 字段级别
        dependent.setInjectLevel(BeanDependentInjectLevelEnum.INJECT_LEVEL_FIELD.getCode());
        dependent.setName(field.getName());
        if (StringUtils.isNotEmpty(resource.name())) {
            // 采用beanName的方式注入
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_NAME.getCode());
            dependent.setInjectInfo(resource.name());
        } else {
            // 采用注解类型方式注入
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_CLASS.getCode());
            dependent.setInjectInfo(field.getType().getName());
        }

        return dependent;
    }

    /**
     * 将标注了Value注解的字段转为依赖
     */
    private static BeanDependent handleValueAnnotation(Field field) {
        Value value = field.getAnnotation(Value.class);

        BeanDependent dependent = new BeanDependent();

        // 字段级别
        dependent.setInjectLevel(BeanDependentInjectLevelEnum.INJECT_LEVEL_FIELD.getCode());
        dependent.setName(field.getName());
        dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_PROPERTY.getCode());
        dependent.setInjectInfo(value.value());
        return dependent;
    }

    /**
     * 将标注了Value注解的字段转为依赖
     */
    private static BeanDependent handleValueAnnotation(Method method) {
        Value value = method.getAnnotation(Value.class);

        BeanDependent dependent = new BeanDependent();

        // 方法级别
        dependent.setInjectLevel(BeanDependentInjectLevelEnum.INJECT_LEVEL_METHOD.getCode());
        dependent.setName(method.getName());
        dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_PROPERTY.getCode());
        dependent.setInjectInfo(value.value());
        return dependent;
    }
}
