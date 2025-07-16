package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 操作记录表，数据同步夜莺
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("act_record")
public class ActRecord extends TfsBaseEntity implements Serializable {

    private static final long serialVersionUID = 3538563572786679303L;

    private String id;

    /**
     * 操作类型，用户操作维度
     */
    private String actType;

    /**
     * 操作内容标识
     */
    private String actId;

    /**
     * 操作人
     */
    private String actBy;

    /**
     * 操作内容json
     */
    private String actContent;

}
