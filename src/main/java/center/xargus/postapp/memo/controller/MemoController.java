package center.xargus.postapp.memo.controller;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.type.ActionType;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "https://post.xargus.center"})
@Controller
public class MemoController {
    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private MemoDao memoDao;

    @RequestMapping(value = "/api/memo", method={RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String memo(@RequestParam(value = "action") String action,
                       @RequestParam(value = "memoId", required = false) Integer memoId,
                       @RequestParam(value = "content", required = false) String content,
                       @RequestParam(value = "start", required = false) Integer start,
                       @RequestParam(value = "limit", required = false) Integer limit,
                       @RequestParam(value = "userId") String userId,
                       @RequestParam(value = "accessToken") String accessToken) {
        if (TextUtils.isEmpty(action) || ActionType.getType(action.toUpperCase()) == null || TextUtils.isEmpty(userId)) {
            ApiResultModel model = new ApiResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            return new Gson().toJson(model);
        }

        int id;
        if (memoId == null) {
            id = -1;
        } else {
            id = memoId;
        }

        log.info("action : " + action + ", type : " + ActionType.getType(action.toUpperCase()) + ", userId : " + userId);

        ApiResultModel model = ActionType.getType(action.toUpperCase()).doAction(memoDao, userId, id, content, start, limit);
        return new Gson().toJson(model);
    }

//    @RequestMapping(value = "/api/memoAll", method={RequestMethod.GET, RequestMethod.POST})
//    @ResponseBody
//    public String memoAll(@RequestParam(value = "start", required = false) Integer start,
//                          @RequestParam(value = "limit", required = false) Integer limit) {
//
//        ApiResultModel model = ActionType.SELECT.doAction(memoDao, null, -1, null, start, limit);
//        return new Gson().toJson(model);
//    }
}
