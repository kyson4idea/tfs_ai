package com.smy.tfs.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class AppAdminDto implements Serializable {

    private static final long serialVersionUID = 7326030376174376387L;

    private String appId;

    private Boolean isAppAdmin;

}
