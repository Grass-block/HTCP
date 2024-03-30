package me.gb2022.htcp.remote;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

final class RequestFuture extends CompletableFuture<HttpResponseData> {
    private final String requestRecord;
    private final Map<String, HttpResponseData> requests;
    private final long started = System.currentTimeMillis();

    RequestFuture(String requestRecord, Map<String, HttpResponseData> requests) {
        this.requestRecord = requestRecord;
        this.requests = requests;
    }

    @Override
    public HttpResponseData get() {
        while (!this.requests.containsKey(this.requestRecord)) {
            if (System.currentTimeMillis() - this.started > 4000) {
                return new HttpResponseData(404,new byte[0]);
            }
            Thread.yield();
        }
        HttpResponseData data = this.requests.get(this.requestRecord);
        this.requests.remove(this.requestRecord);
        return data;
    }
}
