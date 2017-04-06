package com.xaqb.unlock.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by lenovo on 2016/12/1.
 * Json解析，Gosn工具类
 */
public class GsonUtil {
    public static Gson gson = null;

    static {
        if (gson == null) {
//            gson = new Gson();
            gson = new GsonBuilder()
                    .registerTypeAdapter(
                            new TypeToken<TreeMap<String, Object>>() {
                            }.getType(),
                            new JsonDeserializer<TreeMap<String, Object>>() {
                                @Override
                                public TreeMap<String, Object> deserialize(
                                        JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {

                                    TreeMap<String, Object> treeMap = new TreeMap<>();
                                    JsonObject jsonObject = json.getAsJsonObject();
                                    Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                                    for (Map.Entry<String, JsonElement> entry : entrySet) {
                                        treeMap.put(entry.getKey(), entry.getValue());
                                    }
                                    return treeMap;
                                }
                            }).create();
        }
    }

    private GsonUtil() {
    }


    public static Map<String, Object> GsonToMap(String json) {
        return gson.fromJson(json, new TypeToken<TreeMap<String, Object>>() {
        }.getType());
    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String GsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T GsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * 转成list
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> GsonToList(String gsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> GsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <Object> Map<String, Object> GsonToMaps(String gsonString) {
        Map<String, Object> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, Object>>() {
            }.getType());
        }
        return map;
    }

    /**
     * json 转 map
     *
     * @param jsonStr 要转换的json字符串
     * @return
     */
    public static Map<String, Object> JsonToMap(String jsonStr) {
        return JsonToMap(jsonStr, null);
    }

    /**
     * json 转 map
     *
     * @param jsonStr 要转换的json字符串
     * @param result  转换的结果放入位置
     * @return
     */
    public static Map<String, Object> JsonToMap(String jsonStr, Map<String, Object> result) {
        if (jsonStr == null) {
            return null;
        }
        if (result == null) {
            result = new HashMap<String, Object>();
        }
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonStr);
        return JsonToMap(result, "▲▼◆", jsonElement);
    }

    /**
     * json 转 map
     *
     * @param result 要转换的json字符串
     * @param key    key
     * @param value  value
     * @return
     */
    public static Map<String, Object> JsonToMap(Map<String, Object> result, String key, JsonElement value) {
        // 如果key为null 直接报错
        if (key == null) {
            throw new RuntimeException("key值不能为null");
        }
        // 如果value为null,则直接put到map中
        if (value == null) {
            result.put(key, value);
        } else {
            // 如果value为基本数据类型，则放入到map中
            if (value.isJsonPrimitive()) {
                result.put(key, value.getAsString());
            } else if (value.isJsonObject()) {
                // 如果value为JsonObject数据类型，则遍历此JSONObject，进行递归调用本方法
                JsonObject jsonObject = value.getAsJsonObject();
                Iterator<Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, JsonElement> next = iterator.next();
                    result = JsonToMap(result, next.getKey(), next.getValue());
                }
            } else if (value.isJsonArray()) {
                // 如果value为JsonArray数据类型，则遍历此JsonArray，进行递归调用本方法
                JsonArray jsonArray = value.getAsJsonArray();
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    Map<String, Object> tempMap = new HashMap<String, Object>();
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    Iterator<Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<String, JsonElement> next = iterator.next();
                        tempMap = JsonToMap(tempMap, next.getKey(), next.getValue());
                    }
                    list.add(tempMap);
                }
                result.put(key, list);
            }
        }
        // 返回最终结果
        return result;
    }

    /**
     * 根据 json串中的key获取 对应的对象
     *
     * @param jsonStr
     * @param key
     * @return
     */
    public static Object getJsonValue(String jsonStr, String key) {
        Object rulsObj = null;
        Map<?, ?> rulsMap = jsonToMap(jsonStr);
        if (rulsMap != null && rulsMap.size() > 0) {
            rulsObj = rulsMap.get(key);
        }
        return rulsObj;
    }

    /**
     * 将json格式转换成map对象
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

    /**
     * 文件转成字符串
     *
     * @param inputStream 文件流
     * @return
     */
    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}