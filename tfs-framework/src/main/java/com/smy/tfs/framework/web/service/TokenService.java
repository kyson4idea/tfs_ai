package com.smy.tfs.framework.web.service;

import cn.hutool.json.JSONUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.constant.Constants;
import com.smy.tfs.common.core.domain.entity.SysUser;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.ServletUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.ip.AddressUtils;
import com.smy.tfs.common.utils.ip.IpUtils;
import com.smy.tfs.system.service.ISysUserService;
import com.smy.uls.dto.RpcInvokeResponse;
import com.smy.uls.dto.UnifiedLoginUserInfo;
import com.smy.uls.service.UnifiedLoginService;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 *
 * @author ruoyi
 */
@Component
public class TokenService {
    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;
    /**
     * 令牌自定义标识
     */
    @Value("${token.header}")
    private String header;
    /**
     * 令牌秘钥
     */
    @Value("${token.secret}")
    private String secret;
    /**
     * 令牌有效期（默认30分钟）
     */
    @Value("${token.expireTime}")
    private int expireTime;
    @Autowired
    private RedisCache redisCache;

    @Resource
    private UnifiedLoginService unifiedLoginService;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private ISysUserService userService;
    @Resource
    private SysPermissionService permissionService;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        String userKey = getTokenKey(token);
        return redisCache.getCacheObject(userKey);
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        RpcInvokeResponse<String> loginResp = unifiedLoginService.doUnifiedLogin(loginUser.getUsername(), TfsBaseConstant.TFS_SYSTEM_ACCOUNT_TYPE, TfsBaseConstant.TFS_SYSTEM_APP_TYPE);
        if (!loginResp.isSuccess()) {
            throw new ServiceException(loginResp.getMessage());
        }
        String token = loginResp.getData();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        return token;
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param token
     * @return 令牌
     */
    public LoginUser verifyToken(String token) {
        RpcInvokeResponse<UnifiedLoginUserInfo> loginResp = unifiedLoginService.getUnifiedLoginInfo(token, TfsBaseConstant.defaultUserType, TfsBaseConstant.TFS_SYSTEM_APP_TYPE);
        if (!loginResp.isSuccess()) {
            throw new ServiceException(loginResp.getMessage());
        }

        UnifiedLoginUserInfo unifiedLoginUserInfo = JSONUtil.toBean(JSONUtil.parseObj(loginResp.getData()), UnifiedLoginUserInfo.class);

        String userKey = getTokenKey(token);
        LoginUser currentLoginUser = redisCache.getCacheObject(userKey);
        if (StringUtils.isNotNull(currentLoginUser)) {
            refreshToken(currentLoginUser);
        } else {
            String currentLoginAccountId = unifiedLoginUserInfo.getCurrentLoginAccountId();
            TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(currentLoginAccountId, "ldap");

            // 查询数据库生成一个user,并放入缓存
            SysUser user = userService.selectUserByUserName(ticketAccountMapping.getAccountId());
            currentLoginUser = new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
            currentLoginUser.setUser(user);
            currentLoginUser.setSameOriginId(ticketAccountMapping.getSameOriginId());
            currentLoginUser.setUserType(ticketAccountMapping.getAccountType());
            currentLoginUser.setAppId("");

            currentLoginUser.setToken(token);
            refreshToken(currentLoginUser);
        }
        return currentLoginUser;
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }

    public String getJsSdkTokenKey(String jsToken) {
        return CacheConstants.LOGIN_JSSDK_TOKEN_KEY + jsToken;
    }
}
