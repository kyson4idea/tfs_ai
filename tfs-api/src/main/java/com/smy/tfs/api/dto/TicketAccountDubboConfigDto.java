package com.smy.tfs.api.dto;

import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class TicketAccountDubboConfigDto implements Serializable {

    private static final long serialVersionUID = 2701876865109648414L;
    @NotBlank(message = "账户体系配置类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String accountConfigType;

    private String interfaceName;
    private String methodName;
    private String version;
    private String group;
}
