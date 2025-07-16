package com.smy.tfs.api.dto.dynamic;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TicketDealUser implements Serializable {

    private static final long serialVersionUID = -1052875911125801806L;

    private String userType;

    private String userId;

    private String userName;
}
