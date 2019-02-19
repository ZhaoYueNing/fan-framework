package cn.zynworld.fan.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/18.
 * 反射工具包
 */
public class ReflectionUtils {

    /**
     * 获取类的所有超类
     */
    public static List<Class> getAllSuperClass(Class zlass) {
        List<Class> classList = new ArrayList<>();
        while (ObjectUtils.isNotNull(zlass)) {
            classList.add(zlass);
            zlass = zlass.getSuperclass();
        }
        return classList;
    }

    /**
     * 获取类的所有接口
     */
    public static List<Class> addInterfaces(Class interfaces) {
        List<Class> classList = new ArrayList<>();
        if (interfaces == null) {
            return classList;
        }
        classList.add(interfaces);
        for (Class zlass : interfaces.getInterfaces()) {
            classList.addAll(addInterfaces(zlass));
        }
        return classList;
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
        if (zlass == String.class) {
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
