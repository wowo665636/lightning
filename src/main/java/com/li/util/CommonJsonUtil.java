package com.li.util;

/*
 *    Copyright (c) 2001-2010 WoWoTuan Ltd.
 *    All rights reserved
 *
 *    This is unpublished proprietary source code of WoWoTuan Ltd.
 *    The copyright notice above does not evidence any actual
 *    or intended publication of such source code.
 *
 *    NOTICE: UNAUTHORIZED DISTRIBUTION, ADAPTATION OR USE MAY BE
 *    SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 */


import com.google.gson.Gson;

import java.lang.reflect.Type;

public class CommonJsonUtil {
    public static String userObjectToJson(Object model,Class<?> classType){
        
        Gson gson=new Gson();
        
        return gson.toJson(model,classType);
        
    }
    public static Object  jsonToListObject(String json, Type classType){
        
        Gson gson=new Gson();
        
        return gson.fromJson(json,classType);
        
    }
    
    public static Object  jsonToUserObject(String json,Class<?> classType){
        
        Gson gson=new Gson();
        
        return gson.fromJson(json,classType);
        
    }
    
    
}

