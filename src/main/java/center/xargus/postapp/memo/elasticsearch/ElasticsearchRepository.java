package center.xargus.postapp.memo.elasticsearch;

import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElasticsearchRepository {
    private Logger log = Logger.getLogger(this.getClass());

    private static final String URL = "http://localhost:9200";

    private OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    public void putMemo(int memoId, String userId, String content) throws IOException {
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"),
                new Gson().toJson(new InsertMemoModel(content)).toString());

        Request request = new Request.Builder()
                .url(URL + "/memo/" + userId + "/" + String.valueOf(memoId))
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String result = new String(response.body().bytes());
        log.info("putMemo result : " + result);
    }

    public void deleteMemo(int memoId, String userId) throws IOException {
        Request request = new Request.Builder()
                .url(URL + "/memo/" + userId + "/" + String.valueOf(memoId))
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        String result = new String(response.body().bytes());
        log.info("putMemo result : " + result);
    }

    public void searchMemo(String userId, String keyword) throws IOException {
        Request request = new Request.Builder()
                .url(URL + "/memo/" + userId + "/_search?q=content:*"+keyword+"*")
                .build();

        Response response = client.newCall(request).execute();
        String result = new String(response.body().bytes());
        log.info("putMemo result : " + result);
    }

    private class InsertMemoModel {
        public String content;

        public InsertMemoModel(String content) {
            this.content = content;
        }
    }
}
