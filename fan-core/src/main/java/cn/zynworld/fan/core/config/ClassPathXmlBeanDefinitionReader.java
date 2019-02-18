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
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhaoyuening on 2019/2/18.
 * 类路径内xml配置文件中读取bean配置
 */
public class ClassPathXmlBeanDefinitionReader implements BeanDefinitionReader{

    /**
     * 配置文件地址列表
     */
    private List<String> configLocals;

    private final ClassLoader CLASS_LOADER = this.getClass().getClassLoader();

    /**
     * @param configLocals 配置文件地址
     */
    public ClassPathXmlBeanDefinitionReader(List<String> configLocals) {
        this.configLocals = configLocals;
    }

    @Override
    public List<BeanDefinition> read() {
        return readBeanDefinitionByClassPathXmls();
    }

    private List<BeanDefinition> readBeanDefinitionByClassPathXmls(){
        try {
            List<BeanDefinition> definitionList = new ArrayList<>();
            if (ListUtils.isEmpty(configLocals)) {
                return definitionList;
            }

            // 获取所有文档
            List<Document> documents = getDocuments(configLocals);
            // 处理所有文档 生成基本beanDefinition
            List<BeanDefinition> beanDefinitions = new ArrayList<>();
            for (Document document : documents) {
                List<BeanDefinition> definitions = handleDocument(document);
                beanDefinitions.addAll(definitions);
            }
            // TODO 验证分析所有beanDefinition
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
        final String PROPERTY_ATTRIBUTE_BEAN_NAME = "beanName";
        final String PROPERTY_ATTRIBUTE_VALUE = "value";

        String propertyName = propertyElement.attributeValue(PROPERTY_ATTRIBUTE_PROPERTY_NAME);
        String beanName = propertyElement.attributeValue(PROPERTY_ATTRIBUTE_BEAN_NAME);
        String value = propertyElement.attributeValue(PROPERTY_ATTRIBUTE_VALUE);

        // 配置不合理 value beanName 两种必有其一
        if (StringUtils.isEmpty(beanName) && ObjectUtils.isNull(value)) {
            return null;
        }

        BeanDependent dependent = new BeanDependent();
        // 设置方法名
        dependent.setMethodName(ReflectionUtils.propertyToMethodName(propertyName));

        // beanName 注入的方式
        if (StringUtils.isNotEmpty(beanName)) {
            dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_NAME.getCode());
            dependent.setInjectInfo(beanName);
            return dependent;
        }

        // 直接值注入
        dependent.setInjectInfo(value);
        dependent.setInjectType(BeanDependentInjectTypeEnum.INJECT_TYPE_VALUE.getCode());
        return dependent;
    }


    /**
     * 获取xml的document对象
     */
    private List<Document> getDocuments(List<String> configLocals) throws DocumentException {
        List<Document> documents = new ArrayList<>();
        SAXReader reader = new SAXReader();
        for (String configLocal : configLocals) {
            Document document = reader.read(CLASS_LOADER.getResource(configLocal));
            if (ObjectUtils.isNotNull(document)) documents.add(document);
        }
        return documents;
    }


}
