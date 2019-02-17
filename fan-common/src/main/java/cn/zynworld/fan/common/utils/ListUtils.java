package cn.zynworld.fan.common.utils;

import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class ListUtils {
    public static boolean isEmpty(List list) {
        if (ObjectUtils.isNull(list) || list.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }
}
