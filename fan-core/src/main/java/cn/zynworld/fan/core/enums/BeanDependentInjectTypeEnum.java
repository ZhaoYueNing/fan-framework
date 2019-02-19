package cn.zynworld.fan.core.enums;

/**
 * Created by zhaoyuening on 2019/2/17.
 * 依赖注入类型
 */
public enum BeanDependentInjectTypeEnum {

    /**
     * 通过接口或类型注入
     */
    INJECT_TYPE_CLASS(0, "按类型注入"),
    /**
     * 通过bean名称注入
     */
    INJECT_TYPE_NAME(1, "按名称注入"),
    /**
     * 直接传递基本类型及字符串值
     */
    INJECT_TYPE_VALUE(2, "按值注入"),
    /**
     * 按系统属性注入
     */
    INJECT_TYPE_PROPERTY(3, "按属性注入");

    private Integer code;
    private String desc;

    BeanDependentInjectTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
