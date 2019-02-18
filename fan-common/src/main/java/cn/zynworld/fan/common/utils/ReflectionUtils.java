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
    public static String propertyToMethodName(String propertyName) {
        if (StringUtils.isEmpty(propertyName)) {
            return null;
        }

        final String SET_METHOD = "set";
        return SET_METHOD + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
}
