package cn.zynworld.fan.core.exceptions;

/**
 * Created by zhaoyuening on 2019/2/18.
 * 解析xml配置异常
 */
public class FanParseXmlFailException extends Exception{
    public FanParseXmlFailException(String reason) {
        super(reason);
    }
}
