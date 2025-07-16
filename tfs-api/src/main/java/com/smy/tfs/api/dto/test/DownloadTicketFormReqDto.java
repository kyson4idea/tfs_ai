package com.smy.tfs.api.dto.test;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class DownloadTicketFormReqDto implements Serializable {
    private static final long serialVersionUID = -8600370793351208768L;

    private String startTime;

    private String endTime;

    private String userName;

}
