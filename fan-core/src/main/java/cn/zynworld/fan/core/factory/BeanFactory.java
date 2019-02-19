package cn.zynworld.fan.core.factory;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public interface BeanFactory {
    /**
     * 通过name获取bean
     */
    Object getBeanByName(String beanName);

    /**
     * 通过类型获取bean
     */
    <T> T getBeanByClass(Class<T> beanClass);

    /**
     * 获取系统属性
     * @param name 系统属性名
     */
    String getProperty(String name);

    /**
     * 刷新BeanFactory
     */
    void refresh();
}
