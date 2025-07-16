package com.smy.tfs.framework.security.handle;

import cn.hutool.json.JSONUtil;
import com.smy.framework.core.config.Property;
import com.smy.tfs.common.constant.HttpStatus;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.ServletUtils;
import com.smy.tfs.common.utils.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 *
 * @author ruoyi
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        AjaxResult ajaxResult = AjaxResult.error(code, msg);
        if (ServletUtils.isEncryptData(request)) {
            ServletUtils.renderEncryptString(response, ajaxResult);
        } else {
            ServletUtils.renderString(response, JSONUtil.toJsonStr(ajaxResult));
        }

        // 设置Cookie的最大年龄为0=删除
        String httpCookieDomain = Property.getProperty("http.cookie.domain");
        Cookie tokenC = new Cookie("smy_union_user_token", "");
        if (StringUtils.isNotEmpty(httpCookieDomain)){
            tokenC.setDomain(httpCookieDomain);
        }
        tokenC.setPath("/");
        tokenC.setMaxAge(0);
        response.addCookie(tokenC);
    }
}
