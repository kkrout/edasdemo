package com.dong.buddy.mongo.entity;

import com.dong.buddy.advice.AutoIncKey;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable
{

    /**
     * [简要描述]:
     * 
     * @author zhdong
     */
    private static final long serialVersionUID = 1L;

    @Field
    private String createDate;

    @Field
    private String lastUpdateDate;


}
