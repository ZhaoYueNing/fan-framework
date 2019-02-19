package cn.zynworld.fan.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhaoyuening on 2019/2/18.
 * 反射工具包
 */
public class ReflectionUtils {

    /**
     * 获取类的所有超类
     */
    public static Set<Class> getAllSuperClass(Class zlass) {
        Set<Class> classSet = new HashSet<>();
        while (ObjectUtils.isNotNull(zlass)) {
            classSet.add(zlass);
            zlass = zlass.getSuperclass();
        }
        return classSet;
    }

    /**
     * 获取类的所有接口
     */
    public static Set<Class> getInterfaces(Class zlass) {
        Set<Class> classSet = new HashSet<>();
        if (zlass == null || classSet.contains(zlass)) {
            return classSet;
        }

        if (zlass.isInterface()) {
            classSet.add(zlass);
        }

        for (Class superClass : zlass.getInterfaces()) {
            classSet.addAll(getInterfaces(superClass));
        }
        return classSet;
    }

    /**
     * 将属性名转为set方法 如name -> setName
     */
    public static String fieldNameToMethodName(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            return null;
        }
        final String SET_METHOD = "set";
        return SET_METHOD + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * 将属性名转为set方法 如name -> setName
     */
    public static String methodNameToFieldName(String methodName) {
        if (StringUtils.isEmpty(methodName) && methodName.length() > 3) {
            return null;
        }
        return  methodName.substring(3,4).toLowerCase() + methodName.substring(4);
    }

    public static <T> T stringToBaseType(String value, Class<T> zlass) {
        if (ObjectUtils.isNull(value)) {
            return null;
        } else if (zlass == String.class) {
            return (T) value;
        } else if (zlass == int.class || zlass == Integer.class) {
            return (T) new Integer(value);
        } else if (zlass == float.class || zlass == Float.class) {
            return (T) new Float(value);
        } else if (zlass == double.class || zlass == Double.class) {
            return (T) new Double(value);
        }
        return null;
    }
}
