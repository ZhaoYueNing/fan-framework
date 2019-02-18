package cn.zynworld.fan.core.bean;

import cn.zynworld.fan.common.utils.ObjectUtils;
import cn.zynworld.fan.common.utils.ReflectionUtils;
import cn.zynworld.fan.common.utils.StringUtils;
import cn.zynworld.fan.core.enums.BeanDependentInjectTypeEnum;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
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
        Class beanClass = definition.getBeanClass();
        // 得到该类的所有父类及父接口
        List<Class> allSuperClass = ReflectionUtils.getAllSuperClass(beanClass);
        definition.setBeanClassList(allSuperClass);

        // 获取所有标注了Resource注解的字段 转为依赖
        Field[] fields = beanClass.getDeclaredFields();
        List<BeanDependent> dependents = Arrays.stream(fields)
                .filter(field -> ObjectUtils.isNotNull(field.getAnnotation(Resource.class)))
                .map(BeanDefinitionParser::handleResourceAnnotation)
                .collect(Collectors.toList());
        dependents.addAll(definition.getBeanDependents());
        definition.setBeanDependents(dependents);
        // TODO zyn 得到该类的所有注解

    }

    /**
     * 将标注了Resource注解的字段转为依赖
     */
    private static BeanDependent handleResourceAnnotation(Field field) {
        Resource resource = field.getAnnotation(Resource.class);
        BeanDependent dependent = new BeanDependent();
        dependent.setMethodName(ReflectionUtils.propertyToMethodName(field.getName()));
        // 采用beanName的方式注入
        if (StringUtils.isNotEmpty(resource.name())) {
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_NAME.getCode());
            dependent.setInjectInfo(resource.name());
        } else {
            // 采用注解类方式注入
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_CLASS.getCode());
            dependent.setInjectInfo(field.getType().getName());
        }
        return dependent;
    }
}
