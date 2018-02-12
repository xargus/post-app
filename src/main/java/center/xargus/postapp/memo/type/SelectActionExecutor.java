package center.xargus.postapp.memo.type;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.memo.model.MemoModel;
import center.xargus.postapp.memo.model.MemoSelectResultModel;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import center.xargus.postapp.utils.TimeFormatUtils;

import java.util.List;

public class SelectActionExecutor implements ActionExecutable {
    @Override
    public ApiResultModel execute(MemoDao memoDao, ElasticsearchRepository elasticsearchRepository, String userId, int memoId, String title, String content, Integer start, Integer limit, String time) {
        if (start == null || limit == null) {
            MemoSelectResultModel model = new MemoSelectResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            return model;
        }

        int startIndex = start;
        int limitIndex = limit;

        List<MemoModel> memoModels = null;
        String result = ResultConfig.SUCCESS;

        if (TextUtils.isEmpty(time)) {
            time = TimeFormatUtils.getCurrentTime();
        }

        int totalLength = 0;
        try {
            if (TextUtils.isEmpty(userId)) {
                memoModels = memoDao.select(startIndex, limitIndex, time);
            } else {
                memoModels = memoDao.selectWithUserId(userId, startIndex, limitIndex, time);
            }

            totalLength = memoDao.totalLength(userId, time);
        } catch (Exception e) {
            e.printStackTrace();
            result = ResultConfig.UNKNOWN_ERROR;
        }

        MemoSelectResultModel model = new MemoSelectResultModel();
        model.setMemoList(memoModels);
        model.setTotalLength(totalLength);
        model.setResult(result);
        return model;
    }
}
