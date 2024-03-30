package me.gb2022.htcp.remote;

public final class HttpResponseData {
    private final int code;
    private final byte[] data;

    public HttpResponseData(int code, byte[] data) {
        this.code = code;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public int getCode() {
        return code;
    }
}
