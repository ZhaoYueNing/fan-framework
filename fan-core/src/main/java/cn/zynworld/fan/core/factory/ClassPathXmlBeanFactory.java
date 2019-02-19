package cn.zynworld.fan.core.factory;

import cn.zynworld.fan.core.config.ClassPathXmlConfigReader;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/18.
 */
public class ClassPathXmlBeanFactory extends BaseAbstractBeanFactory {
    /**
     * @param configLocals 配置文件地址
     */
    public ClassPathXmlBeanFactory(List<String> configLocals) {
        super(Collections.singletonList(new ClassPathXmlConfigReader(configLocals)));
    }
}
