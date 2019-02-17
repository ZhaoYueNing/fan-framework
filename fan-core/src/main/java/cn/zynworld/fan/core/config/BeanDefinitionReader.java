package cn.zynworld.fan.core.config;

import cn.zynworld.fan.core.bean.BeanDefinition;

import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/17.
 * 从配置文件中读取bean定义
 */
public interface BeanDefinitionReader {
    /**
     * 从配置中读取出bean定义
     */
    List<BeanDefinition> read();
}
