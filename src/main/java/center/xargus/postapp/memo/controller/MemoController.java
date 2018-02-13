package center.xargus.postapp.memo.controller;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.memo.type.ActionType;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import center.xargus.postapp.utils.TimeFormatUtils;
import com.google.gson.Gson;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.Callable;

@CrossOrigin(origins = {"http://localhost:3000", "https://post.xargus.center"})
@Controller
public class MemoController {
    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private ElasticsearchRepository elasticsearchRepository;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initialize() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("memo.sql"));
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }

    @RequestMapping(value = "/api/memo", method={RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Callable<String> memo(@RequestParam(value = "action") final String action,
                                @RequestParam(value = "memoId", required = false) final Integer memoId,
                                 @RequestParam(value = "title", required = false) final String title,
                                @RequestParam(value = "content", required = false) final String content,
                                @RequestParam(value = "start", required = false) final Integer start,
                                @RequestParam(value = "limit", required = false) final Integer limit,
                                @RequestParam(value = "userId") final String userId,
                                @RequestParam(value = "accessToken") String accessToken,
                                 @RequestParam(value = "time", required = false) final String time) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (TextUtils.isEmpty(action) || ActionType.getType(action.toUpperCase()) == null || TextUtils.isEmpty(userId)) {
                    ApiResultModel model = new ApiResultModel();
                    model.setResult(ResultConfig.INVALID_PARAMETER);
                    return new Gson().toJson(model);
                }

                if (!TextUtils.isEmpty(time) && !TimeFormatUtils.validateFormat(time, TimeFormatUtils.DATE_FORMAT)) {
                    ApiResultModel model = new ApiResultModel();
                    model.setResult(ResultConfig.INVALID_TIME_FORMAT);
                    return new Gson().toJson(model);
                }

                int id;
                if (memoId == null) {
                    id = -1;
                } else {
                    id = memoId;
                }

                log.info("action : " + action + ", type : " + ActionType.getType(action.toUpperCase()) + ", userId : " + userId + ", memoId : " + memoId);

                ApiResultModel model = ActionType
                        .getType(action.toUpperCase())
                        .getActionExecutor()
                        .execute(sqlSession.getMapper(MemoDao.class), elasticsearchRepository, userId, id, title, content, start, limit, time);
                return new Gson().toJson(model);
            }
        };
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
