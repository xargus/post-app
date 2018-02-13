package center.xargus.postapp.memo.type;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.memo.model.MemoModel;
import center.xargus.postapp.memo.model.MemoListSelectResultModel;
import center.xargus.postapp.memo.model.MemoSelectResultModel;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import center.xargus.postapp.utils.TimeFormatUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class SelectActionExecutor implements ActionExecutable {
    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public ApiResultModel execute(MemoDao memoDao, ElasticsearchRepository elasticsearchRepository, String userId, int memoId, String title, String content, Integer start, Integer limit, String time) {
        String result = ResultConfig.SUCCESS;

        if (TextUtils.isEmpty(time)) {
            time = TimeFormatUtils.getCurrentTime();
        }

        log.info("memoId " + memoId);
        ApiResultModel resultModel = new ApiResultModel();
        try {
            if (memoId != -1) {
                MemoModel model = memoDao.selectWithId(memoId);

                MemoSelectResultModel memoSelectResultModel = new MemoSelectResultModel();
                memoSelectResultModel.setMemoModel(model);
                resultModel = memoSelectResultModel;
            } else {
                if (start == null || limit == null) {
                    MemoListSelectResultModel model = new MemoListSelectResultModel();
                    model.setResult(ResultConfig.INVALID_PARAMETER);
                    return model;
                }

                int startIndex = start;
                int limitIndex = limit;

                List<MemoModel> memoModels = memoDao.selectWithUserId(userId, startIndex, limitIndex, time);
                int totalLength = memoDao.totalLength(userId, time);

                MemoListSelectResultModel model = new MemoListSelectResultModel();
                model.setMemoList(memoModels);
                model.setTotalLength(totalLength);
                model.setResult(result);
                resultModel = model;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ResultConfig.UNKNOWN_ERROR;
        }

        resultModel.setResult(result);
        return resultModel;
    }
}
