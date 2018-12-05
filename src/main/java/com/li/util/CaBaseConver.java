package com.li.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdi on 16/12/15.
 */
public class CaBaseConver {

    // TouristSignDTO dto = this.copy(middleVO,TouristSignVO.class,new TouristSignDTO(),TouristSignDTO.class);
    private static final Logger logger = LoggerFactory.getLogger(CaBaseConver.class);
    public static final String px=",";
    /**
     * bean copy
     * @param source 源数据
     * @param clazzs
     * @param target 目标数据
     * @param clazzt
     * @param <T>
     * @return 返回 转换后的数据
     * @throws Exception
     */
    public static   <T> T  copy(Object source ,Class<?> clazzs ,Object target ,Class<?> clazzt) throws Exception{
        BeanCopier beanVo2dto= BeanCopier.create(clazzs, clazzt, false);

        beanVo2dto.copy(source,target,null);

        return (T)target;
    }

    /**
     * api sring 参数转long数组
     * @param param
     * @return
     */
    public static List<Long> converLongList(String param){
        if(StringUtils.isBlank(param)){
            logger.error("param is null");
            return null;
        }
        List<Long> list = new ArrayList<Long>();
        if(param.indexOf(px)==-1){
            if(NumberUtils.isNumber(param)){
                list.add(Long.parseLong(param));
            }

        }else{
            String [] arr = param.split(px);
            for(int i=0;i<arr.length;i++){
                if(!NumberUtils.isNumber( arr[i])){
                    continue;
                }
                list.add(Long.parseLong(arr[i]));
            }
        }
        return list;
    }


    /**
     * 返回值msg 设置
     * @param c
     * @param msg
     */
    public static <T> T setMsg(Class c,String msg){
        try {
            Object obj = c.newInstance();
            c.getMethod("setMsg", String.class).invoke(obj,msg);
            c.getMethod("setSuccess", boolean.class).invoke(obj,false);
            return (T)obj;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }





}
