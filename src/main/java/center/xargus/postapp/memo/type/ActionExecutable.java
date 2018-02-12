package center.xargus.postapp.memo.type;

import center.xargus.postapp.memo.dao.MemoDao;
import center.xargus.postapp.memo.elasticsearch.ElasticsearchRepository;
import center.xargus.postapp.model.ApiResultModel;

public interface ActionExecutable {
    ApiResultModel execute(MemoDao memoDao,
                           ElasticsearchRepository elasticsearchRepository,
                           String userId,
                           int memoId,
                           String title,
                           String content,
                           Integer start,
                           Integer limit,
                           String time);
}
