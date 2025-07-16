package com.smy.tfs.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class PluginVisibleDto implements Serializable {
    private static final long serialVersionUID = 5406172030932911259L;
    /**
     * 应用ID
     */
    @NotBlank(message = "应用Id不能为空")
    private String appId;

    /**
     * 用户ID
     */
    @NotBlank(message = "用户Id不能为空")
    private String userId;

}
