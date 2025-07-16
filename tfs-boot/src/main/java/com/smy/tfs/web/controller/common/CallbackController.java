package com.smy.tfs.web.controller.common;

import com.smy.framework.core.config.Property;
import com.smy.tfs.api.dto.QywxOAuthUserDto;
import com.smy.tfs.biz.service.IQywxService;
import com.smy.tfs.common.constant.Constants;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.framework.web.service.SysLoginService;
import com.smy.tfs.quartz.task.TicketCallBackRetryTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/callback")
public class CallbackController {

    @Resource
    private IQywxService qywxService;
    @Resource
    private SysLoginService loginService;

    @GetMapping("/authorize")
    @ResponseBody
    public AjaxResult authorize(@RequestParam("code") String code, HttpServletResponse response) {
        log.info("开始企业微信免密登录，方法入参企微标识code={}",code);
        String httpCookieDomain = Property.getProperty("http.cookie.domain");
        AjaxResult ajax = AjaxResult.success();
        QywxOAuthUserDto qywxOAuth = qywxService.authorize(code);
        if (qywxOAuth == null || qywxOAuth.getUserId() == null) {
            log.error("企业微信免密登录失败，原因：根据访问Token查找用户信息为空");
            return AjaxResult.error("企业微信免密登录失败，根据访问Token查找用户信息为空");
        }
        String token = loginService.loginByQywxUserId(qywxOAuth.getUserId());
        ajax.put(Constants.TOKEN, token);

        Cookie tokenC = new Cookie("smy_union_user_token", token);
        if (StringUtils.isNotEmpty(httpCookieDomain)){
            tokenC.setDomain(httpCookieDomain);
        }
        tokenC.setPath("/");
        tokenC.setMaxAge(-1);
        response.addCookie(tokenC);

        log.info("企业微信免密登录成功，token={}",token);
        return ajax;
    }

    @GetMapping("/qywechat")
    public void qyWechatCallback(@RequestParam("code") String code, @RequestParam("redirect") String redirect, @RequestParam("state") String state,
                                 HttpServletResponse response) throws IOException {
        response.sendRedirect(redirect + "?code=" + code + "&state=tmp" + state);
    }

    @Resource
    TicketCallBackRetryTask ticketCallBackRetryTask;
    @GetMapping("/callBackRetry")
    public void callBackRetry() {
        ticketCallBackRetryTask.callBackRetry();
    }

}
