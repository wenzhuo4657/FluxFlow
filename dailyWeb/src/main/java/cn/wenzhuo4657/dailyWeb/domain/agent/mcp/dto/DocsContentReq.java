package cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto;

import java.io.Serializable;

public class DocsContentReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private  String docsId;

    public DocsContentReq(String docsId) {

        docsId = docsId;
    }

    public DocsContentReq() {
    }


    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocsContentReq that = (DocsContentReq) o;
        return docsId != null && docsId.equals(that.docsId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "DocsContentReq{" +
                "docsId='" + docsId + '\'' +
                '}';
    }
}
