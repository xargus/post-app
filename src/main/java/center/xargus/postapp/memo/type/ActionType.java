package center.xargus.postapp.memo.type;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.model.*;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public enum ActionType {
    INSERT {
        @Override
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, int start, int limit) {
            if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(content)) {
                MemoInsertResultModel model = new MemoInsertResultModel();
                model.setResult(ResultConfig.INVALID_PARAMETER);
                return model;
            }

            String result = ResultConfig.SUCCESS;
            try {
                memoDao.insert(userId, content);
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
    }, UPDATE {
        @Override
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, int start, int limit) {
            if (memoId == -1 || TextUtils.isEmpty(content)) {
                MemoUpdateResultModel model = new MemoUpdateResultModel();
                model.setResult(ResultConfig.INVALID_PARAMETER);
                return model;
            }

            String result = ResultConfig.SUCCESS;
            try {
                memoDao.update(memoId, content);
            } catch (Exception e) {
                e.printStackTrace();
                result = ResultConfig.UNKNOWN_ERROR;
            }

            log.info("update memo, memoId : " + memoId + ", content : " + content + ", result : " + result);

            MemoUpdateResultModel model = new MemoUpdateResultModel();
            model.setResult(result);
            return model;
        }
    }, DELETE {
        @Override
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, int start, int limit) {
            if (memoId == -1) {
                MemoDeleteResultModel model = new MemoDeleteResultModel();
                model.setResult(ResultConfig.INVALID_PARAMETER);
                return model;
            }

            String result = ResultConfig.SUCCESS;
            try {
                memoDao.delete(memoId);
            } catch (Exception e) {
                e.printStackTrace();
                result = ResultConfig.UNKNOWN_ERROR;
            }

            log.info("delete memo, memoId : " + memoId + ", result : " + result);

            MemoDeleteResultModel model = new MemoDeleteResultModel();
            model.setResult(result);
            return model;
        }
    }, SELECT {
        @Override
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, int start, int limit) {
            if (start == -1 || limit == -1) {
                MemoSelectResultModel model = new MemoSelectResultModel();
                model.setResult(ResultConfig.INVALID_PARAMETER);
                return model;
            }

            List<MemoModel> memoModels = null;
            String result = ResultConfig.SUCCESS;

            try {
                if (TextUtils.isEmpty(userId)) {
                    memoModels = memoDao.select(start, limit);
                } else {
                    memoModels = memoDao.select(userId, start, limit);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = ResultConfig.UNKNOWN_ERROR;
            }

            MemoSelectResultModel model = new MemoSelectResultModel();
            model.setMemoList(memoModels);
            model.setResult(result);
            return model;
        }
    };

    public static ActionType getType(String type) {
        ActionType actionType = null;
        try {
            actionType = ActionType.valueOf(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actionType;
    }

    public abstract ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, int start, int limit);

    public Logger log = Logger.getLogger(this.getClass());
}
