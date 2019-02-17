package cn.zynworld.fan.core.enums;

/**
 * Created by zhaoyuening on 2019/2/17.
 * 依赖注入类型
 */
public enum BeanStatusEnum {
    BEAN_STATUS_INIT(0, "初始态"),
    BEAN_STATUS_INSTANCED(1, "被实例化"),
    ;

    private Integer code;
    private String desc;

    BeanStatusEnum(Integer code, String desc) {
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
