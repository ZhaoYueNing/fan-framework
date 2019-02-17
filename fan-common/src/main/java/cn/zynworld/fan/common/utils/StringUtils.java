package cn.zynworld.fan.common.utils;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class StringUtils {
    public static boolean isEmpty(String string) {
        if (ObjectUtils.isNull(string) || string.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }
}
