package cn.zynworld.fan.core.config;

import cn.zynworld.fan.core.bean.BeanDefinition;

import java.util.List;
import java.util.Map;

/**
 * Created by zhaoyuening on 2019/2/17.
 * 从配置文件中读取bean定义
 */
public interface ConfigReader {
    /**
     * 从配置中读取出bean定义
     */
    List<BeanDefinition> readBeanDefinition();

    /**
     * 从配置中读取属性
     */
    Map<String, String> readProperty();
}
