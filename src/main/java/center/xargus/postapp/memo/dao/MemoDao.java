package center.xargus.postapp.memo.dao;

import center.xargus.postapp.memo.model.MemoModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemoDao {
    void insert(@Param("user_id") String userId, @Param("content") String content, @Param("updateDate") String updateDate);
    void update(@Param("_id") int memoId, @Param("content") String content, @Param("updateDate") String updateDate);
    void delete(@Param("_id") int memoId);
    List<MemoModel> select(@Param("start") int start, @Param("limit") int limit, @Param("time") String time);
    List<MemoModel> selectWithUserId(@Param("user_id") String userId, @Param("start") int start, @Param("limit") int limit, @Param("time") String time);
    int totalLength(@Param("user_id") String userId, @Param("time") String time);
    List<MemoModel> selectWithIds(@Param("ids") List<String> ids);
    int lastInsertId();
    String getUserId(@Param("_id") int memoId);
}
