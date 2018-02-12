package center.xargus.postapp.memo.type;

public enum ActionType {
    INSERT {
        @Override
        public ActionExecutable getActionExecutor() {
            return new InsertActionExecutor();
        }
    }, UPDATE {
        @Override
        public ActionExecutable getActionExecutor() {
            return new UpdateActionExecutor();
        }
    }, DELETE {
        @Override
        public ActionExecutable getActionExecutor() {
            return new DeleteActionExecutor();
        }
    }, SELECT {
        @Override
        public ActionExecutable getActionExecutor() {
            return new SelectActionExecutor();
        }
    }, SEARCH {
        @Override
        public ActionExecutable getActionExecutor() {
            return new SearchActionExecutor();
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

    public abstract ActionExecutable getActionExecutor();
}
