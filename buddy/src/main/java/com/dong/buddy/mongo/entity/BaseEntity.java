package com.dong.buddy.mongo.entity;

import java.io.Serializable;
import java.util.Date;

public class BaseEntity implements Serializable
{

    /**
     * [简要描述]:
     * 
     * @author zhdong
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private Date createDate;
    private Date lastUpdateDate;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate()
    {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate)
    {
        this.lastUpdateDate = lastUpdateDate;
    }

}
