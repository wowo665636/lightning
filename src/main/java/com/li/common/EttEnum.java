package com.li.common;


import java.util.HashMap;
import java.util.Map;

/**
 * ett 枚举类
 */
public enum EttEnum {
    NA("na", "空广告"),
    V("v","加载"),
    AV("av", "展示"),
    CLICK("click", "点击"),
    TEL("tel", "电话"),
    VP("vp", "视频相关"),
    CLOSE("close", "关闭"),
    TO("to", "超时"),
    E("e", "错误"),

            ;

    private static final Map<String, EttEnum> codeMap = new HashMap<String, EttEnum>((int)(EttEnum.values().length/0.75)+1);

    static{
        for(EttEnum ettenum: values()){
            codeMap.put(ettenum.getCode(), ettenum);
        }
    }



    /**
     * 根据code获取枚举值
     * @param code 编码
     * @return 对应的枚举
     */
    public  EttEnum valueOfCode(String code){
        return codeMap.get(code);
    }

    private String code; // 编码
    private String desc; // 描述

     EttEnum( String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
