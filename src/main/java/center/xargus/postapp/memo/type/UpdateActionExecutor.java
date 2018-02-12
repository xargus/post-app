package center.xargus.postapp.memo.type;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.memo.model.MemoUpdateResultModel;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import center.xargus.postapp.utils.TimeFormatUtils;
import org.apache.log4j.Logger;

public class UpdateActionExecutor implements ActionExecutable {
    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public ApiResultModel execute(MemoDao memoDao, ElasticsearchRepository elasticsearchRepository, String userId, int memoId, String title, String content, Integer start, Integer limit, String time) {
        if (memoId == -1 || TextUtils.isEmpty(content)) {
            MemoUpdateResultModel model = new MemoUpdateResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            return model;
        }

        String result = ResultConfig.SUCCESS;
        try {
            if (userId.equals(memoDao.getUserId(memoId))) {
                memoDao.update(memoId, title, content, TimeFormatUtils.getCurrentTime());

                elasticsearchRepository.putMemo(memoId, userId, title, content);
            } else {
                result = ResultConfig.WRONG_APPROACH;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ResultConfig.UNKNOWN_ERROR;
        }

        log.info("update memo, memoId : " + memoId + ", content : " + content + ", result : " + result);

        MemoUpdateResultModel model = new MemoUpdateResultModel();
        model.setResult(result);
        return model;
    }
}
