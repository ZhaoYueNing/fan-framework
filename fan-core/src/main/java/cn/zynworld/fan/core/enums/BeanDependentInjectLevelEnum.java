package cn.zynworld.fan.core.enums;

/**
 * Created by zhaoyuening on 2019/2/17.
 * 依赖注入级别
 */
public enum BeanDependentInjectLevelEnum {

    INJECT_LEVEL_FIELD(0,"字段注入"),
    INJECT_LEVEL_METHOD(1, "方法注入"),
    ;

    private Integer code;
    private String desc;

    BeanDependentInjectLevelEnum(Integer code, String desc) {
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
