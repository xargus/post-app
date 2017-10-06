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
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, Integer start, Integer limit) {
            if (TextUtils.isEmpty(content)) {
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
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, Integer start, Integer limit) {
            if (memoId == -1 || TextUtils.isEmpty(content)) {
                MemoUpdateResultModel model = new MemoUpdateResultModel();
                model.setResult(ResultConfig.INVALID_PARAMETER);
                return model;
            }

            String result = ResultConfig.SUCCESS;
            try {
                if (userId.equals(memoDao.getUserId(memoId))) {
                    memoDao.update(memoId, content);
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
    }, DELETE {
        @Override
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, Integer start, Integer limit) {
            if (memoId == -1) {
                MemoDeleteResultModel model = new MemoDeleteResultModel();
                model.setResult(ResultConfig.INVALID_PARAMETER);
                return model;
            }

            String result = ResultConfig.SUCCESS;
            try {
                if (userId.equals(memoDao.getUserId(memoId))) {
                    memoDao.delete(memoId);
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
    }, SELECT {
        @Override
        public ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, Integer start, Integer limit) {
            if (start == null || limit == null) {
                MemoSelectResultModel model = new MemoSelectResultModel();
                model.setResult(ResultConfig.INVALID_PARAMETER);
                return model;
            }

            int startIndex = start;
            int limitIndex = limit;

            List<MemoModel> memoModels = null;
            String result = ResultConfig.SUCCESS;

            try {
                if (TextUtils.isEmpty(userId)) {
                    memoModels = memoDao.select(startIndex, limitIndex);
                } else {
                    memoModels = memoDao.select(userId, startIndex, limitIndex);
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

    public abstract ApiResultModel doAction(MemoDao memoDao, String userId, int memoId, String content, Integer start, Integer limit);

    public Logger log = Logger.getLogger(this.getClass());
}
