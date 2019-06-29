package com.example.ck.myble.utils;
import com.alibaba.json.JSON;
import com.alibaba.json.JSONArray;
import com.alibaba.json.JSONObject;
import com.alibaba.json.serializer.SerializeFilter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 作者：hzy on 2018/1/17 15:43
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class JsonTool {
    private static boolean DEBUG = false;


    /**
     * @param bean
     * @return String 返回类型
     * @Title: toJson
     * @throws：
     */
    public static String toJson(Object bean) {
        return JSON.toJSONString(bean);
    }

    /**
     * @param bean
     * @return String 返回类型
     * @Title: toJson
     * @throws：
     */
    public static String toJson(Object bean, SerializeFilter profilter) {
        return JSON.toJSONString(bean,profilter);
    }


    /**
     * @param <T>
     * @param json
     * @param classOfT
     * @return T 返回类型
     * @Title: fromJson
     * @Description:
     * @throws：
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }




    /**
     * 集合 转Gson
     *
     * @param json
     * @param t
     * @param <T>
     * @return
     */
//    public static <T> List<T> getListJson(String json, Class<T> t) {
//        return JSON.parseObject(json, new TypeReference<List<T>>() {
//        }.getType());
//    }
    public static <T> ArrayList<T> getListJson(String json, Class<T> t) {
        Type type = new ListParameterizedType(t);
        return JSON.parseObject(json, type);
    }

    public static JSONObject putKeyValue(String json, String key, String value) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        jsonObject.put(key, value);
        return jsonObject;
    }

    private static class ListParameterizedType implements ParameterizedType {
        private Type type;

        private ListParameterizedType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }

        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
        // implement equals method too! (as per javadoc)
    }
//

    /**
     * 获取String value
     *
     * @param result
     * @param key
     * @return
     */
    public static String getString(String result, String key) {
        return JSONObject.parseObject(result).getString(key);
    }

    /**
     * 获取String value
     *
     * @param result
     * @param key
     * @return
     */
    public static Long getLong(String result, String key) {
        return JSONObject.parseObject(result).getLong(key);
    }

    /**
     * 获取 JsonObject
     *
     * @param result
     * @param key
     * @return
     */
    public static JSONObject getJsonObject(String result, String key) {
        return JSONObject.parseObject(result).getJSONObject(key);
    }


    /**
     * 获取 JsonArray
     *
     * @param result
     * @param key
     * @return
     */
    public static JSONArray getJsonArray(String result, String key) {
        return JSONObject.parseObject(result).getJSONArray(key);
    }

    /**
     * 获取int value
     *
     * @param result
     * @param key
     * @return
     */
    public static int getIntValue(String result, String key) {
        return JSONObject.parseObject(result).getIntValue(key);
    }

}
