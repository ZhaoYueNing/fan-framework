package cn.zynworld.fan.core.bean;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public interface BeanConstructor {
    /**
     * 构建bean实例并返回
     */
    Object createBeanInstance(BeanDefinition definition);
}
