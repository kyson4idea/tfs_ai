package com.smy.tfs.biz.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.biz.bo
 * @Description:
 * @CreateDate 2024/4/24 17:02
 * @UpdateDate 2024/4/24 17:02
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HttpServiceConfig implements Serializable {

    private String url;
    /**
     * GET POST
     */
    private String method;
    private String param;
    private String contentType;
}
