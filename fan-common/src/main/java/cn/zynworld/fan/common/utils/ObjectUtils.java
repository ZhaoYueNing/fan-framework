package cn.zynworld.fan.common.utils;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class ObjectUtils {

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean equal(Object obj1, Object obj2) {
        return obj1 == obj2;
    }
}
