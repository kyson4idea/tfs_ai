package com.smy.tfs.api.dto.ticket_notify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyMsgDto implements Serializable {
    private static final long serialVersionUID = -5894470949437166818L;
    private String message;
    private String userType;
    private List<String> userIdList;
    
}
