package center.xargus.postapp.memo.type;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.memo.model.MemoModel;
import center.xargus.postapp.memo.model.MemoListSelectResultModel;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActionExecutor implements ActionExecutable {
    @Override
    public ApiResultModel execute(MemoDao memoDao, ElasticsearchRepository elasticsearchRepository, String userId, int memoId, String title, String content, Integer start, Integer limit, String time) {
        if (TextUtils.isEmpty(content)) {
            MemoListSelectResultModel model = new MemoListSelectResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            return model;
        }

        String result = ResultConfig.SUCCESS;
        List<MemoModel> models = new ArrayList<>();
        try {
            List<String> ids = elasticsearchRepository.searchMemo(userId, content);
            if (ids != null && ids.size() > 0) {
                models = memoDao.selectWithIds(ids);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = ResultConfig.UNKNOWN_ERROR;
        }

        MemoListSelectResultModel model = new MemoListSelectResultModel();
        model.setResult(result);
        model.setMemoList(models);
        model.setTotalLength(models.size());
        return model;
    }
}
