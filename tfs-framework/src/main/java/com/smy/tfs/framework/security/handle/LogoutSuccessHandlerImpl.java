package com.smy.tfs.framework.security.handle;

import cn.hutool.json.JSONUtil;
import com.smy.framework.core.config.Property;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.utils.ServletUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.framework.web.service.TokenService;
import com.smy.uls.service.UnifiedLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;
    @Resource
    private UnifiedLoginService unifiedLoginService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String httpCookieDomain = Property.getProperty("http.cookie.domain");
        // 删除用户缓存记录
        String token = tokenService.getToken(request);
        unifiedLoginService.doUnifiedLogout(token);

        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            tokenService.delLoginUser(loginUser.getToken());
        }

        // 设置Cookie的最大年龄为0=删除
        Cookie tokenC = new Cookie("smy_union_user_token", "");
        if (StringUtils.isNotEmpty(httpCookieDomain)){
            tokenC.setDomain(httpCookieDomain);
        }
        tokenC.setPath("/");
        tokenC.setMaxAge(0);
        response.addCookie(tokenC);

        AjaxResult ajaxResult = AjaxResult.success("退出成功");
        if (ServletUtils.isEncryptData(request)) {
            ServletUtils.renderEncryptString(response, ajaxResult);
        } else {
            ServletUtils.renderString(response, JSONUtil.toJsonStr(ajaxResult));
        }
    }
}
