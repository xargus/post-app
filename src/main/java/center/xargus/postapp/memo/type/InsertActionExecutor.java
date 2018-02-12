package center.xargus.postapp.memo.type;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.memo.model.MemoInsertResultModel;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import center.xargus.postapp.utils.TimeFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;

public class InsertActionExecutor implements ActionExecutable {
    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public ApiResultModel execute(MemoDao memoDao, ElasticsearchRepository elasticsearchRepository, String userId, int memoId, String title, String content, Integer start, Integer limit, String time) {
        if (TextUtils.isEmpty(content)) {
            MemoInsertResultModel model = new MemoInsertResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            return model;
        }

        String result = ResultConfig.SUCCESS;
        try {
            memoDao.insert(userId, title, content, TimeFormatUtils.getCurrentTime());
            int id = memoDao.lastInsertId();

            elasticsearchRepository.putMemo(id, userId, title, content);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            result = ResultConfig.INVALID_ID;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            result = ResultConfig.UNKNOWN_ERROR;
        }

        log.info("insert memo, userId : " + userId + ", content : " + content + ", result : " + result);

        MemoInsertResultModel model = new MemoInsertResultModel();
        model.setResult(result);
        return model;
    }
}
