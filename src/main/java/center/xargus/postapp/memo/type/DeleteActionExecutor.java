package center.xargus.postapp.memo.type;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.memo.model.MemoDeleteResultModel;
import center.xargus.postapp.model.ApiResultModel;
import org.apache.log4j.Logger;

public class DeleteActionExecutor implements ActionExecutable {
    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public ApiResultModel execute(MemoDao memoDao, ElasticsearchRepository elasticsearchRepository, String userId, int memoId, String title, String content, Integer start, Integer limit, String time) {
        if (memoId == -1) {
            MemoDeleteResultModel model = new MemoDeleteResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            return model;
        }

        String result = ResultConfig.SUCCESS;
        try {
            if (userId.equals(memoDao.getUserId(memoId))) {
                memoDao.delete(memoId);

                elasticsearchRepository.deleteMemo(memoId, userId);
            } else {
                result = ResultConfig.WRONG_APPROACH;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ResultConfig.UNKNOWN_ERROR;
        }

        log.info("delete memo, memoId : " + memoId + ", result : " + result);

        MemoDeleteResultModel model = new MemoDeleteResultModel();
        model.setResult(result);
        return model;
    }
}
