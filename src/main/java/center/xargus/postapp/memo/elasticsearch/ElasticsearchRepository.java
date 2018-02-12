package center.xargus.postapp.memo.elasticsearch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchRepository {
    private Logger log = Logger.getLogger(this.getClass());

    private static final String URL = "http://localhost:9200";

    private OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    public void putMemo(int memoId, String userId, String title, String content) throws IOException {
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"),
                new Gson().toJson(new InsertMemoModel(title, content)).toString());

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
        log.info("deleteMemo result : " + result);
    }

    public List<String> searchMemo(String userId, String keyword) throws IOException {
        Request request = new Request.Builder()
                .url(URL + "/memo/" + userId + "/_search?q=content:*"+keyword+"*")
                .build();

        Response response = client.newCall(request).execute();
        String result = new String(response.body().bytes());
        log.info("searchMemo result : " + result);

        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject().getAsJsonObject("hits");
        JsonArray jsonArray = jsonObject.getAsJsonArray("hits");
        List<String> ids = new ArrayList<>();
        for (int i=0; i< jsonArray.size(); i++) {
            String id = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("_id").getAsString();
            ids.add(id);
        }

        return ids;
    }

    private void cacheClear() throws IOException {
        Request request = new Request.Builder()
                .url(URL + "/memo/_cache/clear")
                .build();

        Response response = client.newCall(request).execute();
        String result = new String(response.body().bytes());
        log.info("cache clear result : " + result);
    }

    private class InsertMemoModel {
        public String title;
        public String content;

        public InsertMemoModel(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }
}
