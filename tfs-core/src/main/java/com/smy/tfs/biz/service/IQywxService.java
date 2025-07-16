package com.smy.tfs.biz.service;

import com.smy.tfs.api.dto.QywxOAuthUserDto;

public interface IQywxService {

    QywxOAuthUserDto authorize(String code);
}
