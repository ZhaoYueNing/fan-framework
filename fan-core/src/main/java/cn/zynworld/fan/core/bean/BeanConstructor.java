package cn.zynworld.fan.core.bean;

import cn.zynworld.fan.core.config.ConfigReader;
import com.sun.source.doctree.SerialDataTree;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public interface BeanConstructor{
    /**
     * 构建bean实例并返回
     */
    Object createBeanInstance(BeanDefinition definition);

}


