package cn.zynworld.fan.core.config;

import cn.zynworld.fan.core.bean.BeanDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaoyuening on 2019/2/17.
 */
public class MockConfigReader implements ConfigReader {
    private List<BeanDefinition> definitions;
    public MockConfigReader(List<BeanDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public List<BeanDefinition> readBeanDefinition() {
        return definitions;
    }

    @Override
    public Map<String, String> readProperty() {
        return new HashMap<>();
    }
}
