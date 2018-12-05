package com.li;

import com.alibaba.fastjson.JSON;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by wangdi on 17/7/28.
 */
public class MainLightTest {
    public static void main(String[] args) {
       /* TreeMap<String,String> treeMap = new TreeMap<>();
        treeMap.put("a","treea");
        treeMap.put("b","treeb");
        treeMap.put("c","treec");
        System.out.println(JSON.toJSONString(treeMap));*/

        TreeMap<Integer, String> treemap = new TreeMap<Integer, String>();
        SortedMap<Integer, String> treemapincl = new TreeMap<Integer, String>();

        // populating tree map
        treemap.put(2, "two");
        treemap.put(1, "one");
        treemap.put(3, "three");
        treemap.put(6, "six");
        treemap.put(5, "five");

        System.out.println("Getting tail map");
        treemapincl=treemap.tailMap(3);
        System.out.println("Tail map values: "+treemapincl);

        System.out.println("10.16.13.53:9000".indexOf(","));

    }

}
