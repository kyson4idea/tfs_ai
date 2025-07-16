package com.smy.tfs.framework.security.filter;

import cn.hutool.core.util.StrUtil;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dto.TicketAppDto;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.common.core.domain.entity.SysUser;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.framework.web.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private RedisCache redisCache;

    /**
     * 令牌有效期（默认30分钟）
     */
    @Value("${token.expireTime}")
    private int expireTime;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (checkRequestFromApp(request) && checkAppTokenValid(request)){
            String appId = URL.decode(request.getHeader("appId")) ;
            String userId = URL.decode(request.getHeader("appUserId"));
            String appToken = URL.decode(request.getHeader("appToken"));

            String jsSdkTokenKey = tokenService.getJsSdkTokenKey(appToken);
            LoginUser loginUser = redisCache.getCacheObject(jsSdkTokenKey);
            if (loginUser == null || !StrUtil.equals(loginUser.getAppId(), appId)
                || loginUser.getUser() == null || !StrUtil.equals(loginUser.getUser().getUserName(), userId)) {
                loginUser = new LoginUser();
                TicketAppDto ticketAppDto = ticketAppService.selectTicketAppFullById(appId);
                if (ticketAppDto == null) {
                    log.error(String.format("jssdk调用未找到appId: %s对应的应用配置信息", appId));
                    throw new ServiceException(String.format("未找到appId: %s对应的应用配置信息", appId));
                }

                TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, ticketAppDto.getAccountType());
                if (ticketAccountMapping== null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())){
                    log.error(String.format("jssdk调用未找到userId: %s, userType: %s 对应的用户信息", userId, ticketAppDto.getAccountType()));
                    throw new ServiceException(String.format("未找到userId: %s, userType: %s 对应的用户信息", userId, ticketAppDto.getAccountType()));
                }

                // 手动构建虚拟用户，方便后续处理获取用户信息
                SysUser user = new SysUser();
                user.setUserId(-1L);
                user.setUserName(ticketAccountMapping.getAccountId());
                user.setNickName(ticketAccountMapping.getAccountName());

                loginUser.setUser(user);
                loginUser.setUserId(-1L);
                loginUser.setSameOriginId(ticketAccountMapping.getSameOriginId());
                loginUser.setUserType(ticketAccountMapping.getAccountType());
                loginUser.setAppId(appId);
                redisCache.setCacheObject(jsSdkTokenKey, loginUser, expireTime, TimeUnit.MINUTES);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            String token = tokenService.getToken(request);
            if (StringUtils.isNotNull(token) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
                LoginUser loginUser = tokenService.verifyToken(token);
                loginUser.setTfS(true);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }


        chain.doFilter(request, response);
    }

    // 通过header是否包含指定字段判断是否来自于app端
    private Boolean checkRequestFromApp(HttpServletRequest request){
        String appId = request.getHeader("appId");
        String userId = request.getHeader("appUserId");
        String appToken = request.getHeader("appToken");
        if (StrUtil.hasBlank(appId, userId, appToken)){
            return false;
        }
        return true;
    }

    //暂不处理，默认返回true
    private Boolean checkAppTokenValid(HttpServletRequest request){
        return true;
    }


}
