package yongfa365.jmeter;

import java.util.List;

public class ReportVO {

    /**
     * id用於控制動態id
     */
    private String id;

    /**
     * 用於控制動態id
     */
    private String parentId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 是否成功
     */
    private boolean successful;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 子节点
     */
    private List<ReportVO> childs;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<ReportVO> getChilds() {
        return childs;
    }

    public void setChilds(List<ReportVO> childs) {
        this.childs = childs;
    }

}
