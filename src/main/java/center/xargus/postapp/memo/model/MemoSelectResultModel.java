package center.xargus.postapp.memo.model;

import center.xargus.postapp.model.ApiResultModel;

public class MemoSelectResultModel extends ApiResultModel {
    private MemoModel memoModel;

    public MemoModel getMemoModel() {
        return memoModel;
    }

    public void setMemoModel(MemoModel memoModel) {
        this.memoModel = memoModel;
    }
}
