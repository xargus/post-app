package center.xargus.postapp.memo.controller;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.model.MemoInsertResultModel;
import center.xargus.postapp.memo.type.ActionType;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemoController {
    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private MemoDao memoDao;

    @RequestMapping(value = "/api/memo", method={RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String memo(@RequestParam(value = "action") String action,
                       @RequestParam(value = "userId", required = false) String userId,
                       @RequestParam(value = "memoId", required = false) Integer memoId,
                       @RequestParam(value = "content", required = false) String content,
                       @RequestParam(value = "start", required = false) Integer start,
                       @RequestParam(value = "limit", required = false) Integer limit) {
        if (TextUtils.isEmpty(action) || ActionType.getType(action.toUpperCase()) == null) {
            MemoInsertResultModel model = new MemoInsertResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            return new Gson().toJson(model);
        }

        int id;
        if (memoId == null) {
            id = -1;
        } else {
            id = memoId;
        }

        int startIndex;
        if (start == null) {
            startIndex = -1;
        } else {
            startIndex = start;
        }

        int limitIndex;
        if (limit == null) {
            limitIndex = -1;
        } else {
            limitIndex = limit;
        }

        log.info("action : " + action + ", type : " + ActionType.getType(action.toUpperCase()));

        ApiResultModel model = ActionType.getType(action.toUpperCase()).doAction(memoDao, userId, id, content, startIndex, limitIndex);
        return new Gson().toJson(model);
    }
}
