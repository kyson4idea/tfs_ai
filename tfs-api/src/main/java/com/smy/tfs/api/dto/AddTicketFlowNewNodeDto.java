package com.smy.tfs.api.dto;

import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.AuditedMethodEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.dto
 * @Description:
 * @CreateDate 2024/5/9 12:02
 * @UpdateDate 2024/5/9 12:02
 */
@Data
public class AddTicketFlowNewNodeDto implements Serializable {
    private static final long serialVersionUID = 3369275654101730880L;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 审批方式
     */
    private AuditedMethodEnum auditedMethod;
    private List<AccountInfo> excutorList;
    private List<AccountInfo> ccList;
}
