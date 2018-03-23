package com.jun.sso.sdk.pojo;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.pojo
 * Author:   孙
 * Date:    2018/3/13 14:22
 * Description: //模块目的、功能描述
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.List;

public class BaseResponse {
    private static final ObjectMapper MAPPER = new ObjectMapper();    // 定义jackson对象

    private Integer status;    // 响应业务状态

    private String msg;        // 响应消息

    private Object data;    // 响应中的数据

    private Date timeout;     //超时时间

    public BaseResponse() {
    }

    public BaseResponse(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public BaseResponse(Object data, Date timeout) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
        this.timeout = timeout;
    }

    public static BaseResponse ok(Object data) {
        return new BaseResponse(data);
    }

    public static BaseResponse ok(Object data, Date timeout) {
        return new BaseResponse(data, timeout);
    }

    public static BaseResponse ok() {
        return new BaseResponse(null);
    }

    public static BaseResponse build(Integer status, String msg, Object data) {
        return new BaseResponse(status, msg, data);
    }

    public static BaseResponse build(Integer status, String msg) {
        return new BaseResponse(status, msg, null);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Date getTimeout() {
        return timeout;
    }

    public void setTimeout(Date timeout) {
        this.timeout = timeout;
    }

    /**
     * 将json结果集转化为ITDragonResult对象
     *
     * @param jsonData json数据
     * @param clazz    ITDragonResult中的object类型
     * @return
     */
    public static BaseResponse formatToPojo(String jsonData, Class<?> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, BaseResponse.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (clazz != null) {
                if (data.isObject()) {
                    obj = MAPPER.readValue(data.traverse(), clazz);
                } else if (data.isTextual()) {
                    obj = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 没有object对象的转化
     *
     * @param json
     * @return
     */
    public static BaseResponse format(String json) {
        try {
            return MAPPER.readValue(json, BaseResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object是集合转化
     *
     * @param jsonData json数据
     * @param clazz    集合中的类型
     * @return
     */
    public static BaseResponse formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }
}
