package com.smy.tfs.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import java.io.Serializable;

@Getter @Setter
public class ReqParam implements Serializable {
    private static final long serialVersionUID = -2574404476879732760L;

    public ReqParam(){

    }
    public ReqParam(String id){
        this.id = id;
    }
    public ReqParam(String id,String idType){
        this.id = id;
        this.idType = idType;
    }
    @RequestParam(value = "id", example = "1", description = "应用id")
    private String id;

    @RequestParam(value = "id类型", example = "1", description = "应用id类型")
    private String idType;

    @RequestParam(value = "publicNet", example = "false", description = "是否公网环境")
    private String publicNet;
}
