package com.galaxy.ishare.http;


public enum HttpCode {
    /**
     * 网络访问成功
     */
    OK(1),
    ERROR(10),
    CANCELLED(6),
    /**
     * 当前无网络
     */
    E_NO_CONNECT(2),
    /**
     * 需要用户登录
     */
    E_NO_REGISTER(3),
    /**
     * 网络连接失败
     */
    E_NET_ACCESS(4),
    /**
     * 网络连接网络超时
     */
    E_NET_TIMEOUT(5),
    /**
     * 请求被取消
     */
    E_DATA_ERROR(7),
    E_SERVICE_ACCESS(8),
    E_RANGE_NOT_SATISFIABLE(9);

    private final int intValue;

    HttpCode(int value) {
        this.intValue = value;
    }

    public int value() {
        return intValue;
    }
}
