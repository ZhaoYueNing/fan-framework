package cn.zynworld.fan.core.config;

import cn.zynworld.fan.common.utils.ListUtils;
import cn.zynworld.fan.common.utils.ObjectUtils;
import cn.zynworld.fan.common.utils.ReflectionUtils;
import cn.zynworld.fan.common.utils.StringUtils;
import cn.zynworld.fan.core.bean.BeanDefinition;
import cn.zynworld.fan.core.bean.BeanDefinitionParser;
import cn.zynworld.fan.core.bean.BeanDependent;
import cn.zynworld.fan.core.enums.BeanDependentInjectTypeEnum;
import cn.zynworld.fan.core.enums.BeanStatusEnum;
import cn.zynworld.fan.core.exceptions.FanParseXmlFailException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.util.*;

/**
 * Created by zhaoyuening on 2019/2/18.
 * 类路径内xml配置文件中读取bean配置
 */
public class ClassPathXmlConfigReader implements ConfigReader {

    /**
     * 配置文件地址列表
     */
    private List<String> configLocals;
    /**
     * xml Document
     */
    private List<Document> documents;

    private final ClassLoader CLASS_LOADER = this.getClass().getClassLoader();

    /**
     * @param configLocals 配置文件地址
     */
    public ClassPathXmlConfigReader(List<String> configLocals) {
        this.configLocals = configLocals;
        // 获取所有文档
        this.documents = getDocuments(configLocals);
    }

    @Override
    public List<BeanDefinition> readBeanDefinition() {
        return readBeanDefinitionByClassPathXmls();
    }

    /**
     * 读取所有属性
     */
    @Override
    public Map<String, String> readProperty() {
        final String XPATH_PROPERTY = "/fan/properties/property[@name][@value]";
        final String PROPERTY_NAME = "@name";
        final String PROPERTY_VALUE = "@value";

        // 所有的属性及值
        Map<String, String> propertyMap = new HashMap<>();

        // 遍历获取所有属性
        for (Document document : documents) {
            List<Node> propertys = document.selectNodes(XPATH_PROPERTY);
            for (Node property : propertys) {
                String name = property.valueOf(PROPERTY_NAME);
                String value = property.valueOf(PROPERTY_VALUE);
                propertyMap.put(name, value);
            }
        }

        return propertyMap;
    }

    private List<BeanDefinition> readBeanDefinitionByClassPathXmls(){
        try {
            List<BeanDefinition> definitionList = new ArrayList<>();
            if (ListUtils.isEmpty(configLocals)) {
                return definitionList;
            }


            // 处理所有文档 生成基本beanDefinition
            List<BeanDefinition> beanDefinitions = new ArrayList<>();
            for (Document document : documents) {
                List<BeanDefinition> definitions = handleDocument(document);
                beanDefinitions.addAll(definitions);
            }

            // 对所有beanDefinition 进行解析
            beanDefinitions.forEach(BeanDefinitionParser::parse);
            return beanDefinitions;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 处理单个xml文档
     */
    private List<BeanDefinition> handleDocument(Document document) throws FanParseXmlFailException, ClassNotFoundException {
        final String ROOT_ELEMENT_NAME = "fan";
        final String BEANS_ELEMENT_NAME = "beans";

        List<BeanDefinition> beanDefinitionList = new ArrayList<>();

        Element rootElement = document.getRootElement();
        if (!ROOT_ELEMENT_NAME.equals(rootElement.getName())) {
            throw new FanParseXmlFailException("Failed to parse the XML file");
        }

        for (Iterator<Element> elementIterator = rootElement.elementIterator(); elementIterator.hasNext(); ) {
            Element element = elementIterator.next();
            if (BEANS_ELEMENT_NAME.equals(element.getName())) {
                List<BeanDefinition> definitions = handleBeansElement(element);
                beanDefinitionList.addAll(definitions);
            }
        }

        return beanDefinitionList;
    }

    /**
     * 处理beans元素
     */
    private List<BeanDefinition> handleBeansElement(Element element) throws ClassNotFoundException {
        final String BEAN_ELEMENT_NAME = "bean";

        List<BeanDefinition> beanDefinitionList = new ArrayList<>();

        for (Iterator<Element> elementIterator = element.elementIterator(); elementIterator.hasNext();) {
            Element beanElement = elementIterator.next();
            if (BEAN_ELEMENT_NAME.equals(beanElement.getName())) {
                BeanDefinition beanDefinition = handleBeanElement(beanElement);
                if (ObjectUtils.isNotNull(beanDefinition)) beanDefinitionList.add(beanDefinition);
            }
        }

        return beanDefinitionList;
    }

    private BeanDefinition handleBeanElement(Element beanElement) throws ClassNotFoundException {
        final String PROPERTY_ELEMENT_NAME = "property";
        final String BEAN_ATTRIBUTE_BEAN_NAME = "beanName";
        final String BEAN_ATTRIBUTE_BEAN_CLASS = "beanClass";
        final String BEAN_ATTRIBUTE_SINGLETON = "singleton";
        final String BEAN_ATTRIBUTE_LAZYLOAD = "lazyLoad";

        BeanDefinition definition = new BeanDefinition();

        String beanClass = beanElement.attributeValue(BEAN_ATTRIBUTE_BEAN_CLASS);
        String beanName = beanElement.attributeValue(BEAN_ATTRIBUTE_BEAN_NAME,beanClass);
        String singletion = beanElement.attributeValue(BEAN_ATTRIBUTE_SINGLETON,Boolean.TRUE.toString());
        String lazyload = beanElement.attributeValue(BEAN_ATTRIBUTE_LAZYLOAD, Boolean.FALSE.toString());

        if (StringUtils.isEmpty(beanClass)) {
            return null;
        }

        // bean的依赖
        List<BeanDependent> dependents = new ArrayList<>();

        // 属性参数
        for (Iterator<Element> elementIterator = beanElement.elementIterator(); elementIterator.hasNext(); ) {
            Element element = elementIterator.next();
            if (element.getName().equals(PROPERTY_ELEMENT_NAME)) {
                BeanDependent dependent = handleBeanPropertyElement(element);
                if (ObjectUtils.isNotNull(dependent)) dependents.add(dependent);
            }
        }

        definition.setBeanClass(Class.forName(beanClass));
        definition.setBeanName(beanName);
        definition.setLazyLoad(Boolean.parseBoolean(lazyload));
        definition.setSingleton(Boolean.parseBoolean(singletion));
        definition.setBeanDependents(dependents);
        definition.setBeanStatus(BeanStatusEnum.BEAN_STATUS_INIT.getCode());

        return definition;
    }

    private BeanDependent handleBeanPropertyElement(Element propertyElement) {
        final String PROPERTY_ATTRIBUTE_PROPERTY_NAME = "propertyName";
        // bean 注入
        final String PROPERTY_ATTRIBUTE_BEAN_REF = "beanRef";
        // 属性注入
        final String PROPERTY_ATTRIBUTE_PROPERTY_REF = "propertyRef";
        // 按值直接注入
        final String PROPERTY_ATTRIBUTE_VALUE = "value";

        String propertyName = propertyElement.attributeValue(PROPERTY_ATTRIBUTE_PROPERTY_NAME);
        String beanRef = propertyElement.attributeValue(PROPERTY_ATTRIBUTE_BEAN_REF);
        String propertyRef = propertyElement.attributeValue(PROPERTY_ATTRIBUTE_PROPERTY_REF);
        String value = propertyElement.attributeValue(PROPERTY_ATTRIBUTE_VALUE);


        BeanDependent dependent = new BeanDependent();
        // 设置方法名
        dependent.setMethodName(ReflectionUtils.fieldNameToMethodName(propertyName));

        if (StringUtils.isNotEmpty(beanRef)) {
            // beanRef 注入的方式
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_NAME.getCode());
            dependent.setInjectInfo(beanRef);
            return dependent;
        } else if (StringUtils.isNotEmpty(propertyRef)) {
            // property 注入
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_PROPERTY.getCode());
            dependent.setInjectInfo(beanRef);
            return dependent;
        } else if (ObjectUtils.isNotNull(value)) {
            // value 注入
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_VALUE.getCode());
            dependent.setInjectInfo(value);
            return dependent;
        }

        // 都不符合
        return null;
    }


    /**
     * 获取xml的document对象
     */
    private List<Document> getDocuments(List<String> configLocals) {
        try {
            List<Document> documents = new ArrayList<>();
            SAXReader reader = new SAXReader();
            for (String configLocal : configLocals) {
                Document document = reader.read(CLASS_LOADER.getResource(configLocal));
                if (ObjectUtils.isNotNull(document)) documents.add(document);
            }
            return documents;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
