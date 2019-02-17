package cn.zynworld.fan.core.config;

import cn.zynworld.fan.core.bean.BeanDefinition;

import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class MockBeanDefinitionReader implements BeanDefinitionReader {
    private List<BeanDefinition> definitions;
    public MockBeanDefinitionReader(List<BeanDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public List<BeanDefinition> read() {
        return definitions;
    }
}
