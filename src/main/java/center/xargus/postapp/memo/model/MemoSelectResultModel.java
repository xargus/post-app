package center.xargus.postapp.memo.model;

import center.xargus.postapp.model.ApiResultModel;

import java.util.List;

public class MemoSelectResultModel extends ApiResultModel {
    private List<MemoModel> memoList;
    private int totalLength;

    public List<MemoModel> getMemoList() {
        return memoList;
    }

    public void setMemoList(List<MemoModel> memoList) {
        this.memoList = memoList;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }
}
