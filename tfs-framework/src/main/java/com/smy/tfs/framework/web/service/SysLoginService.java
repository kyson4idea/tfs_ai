package com.smy.tfs.framework.web.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.constant.Constants;
import com.smy.tfs.common.constant.HttpStatus;
import com.smy.tfs.common.constant.UserConstants;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.entity.SysUser;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.exception.user.*;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.MessageUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.ip.IpUtils;
import com.smy.tfs.framework.manager.AsyncManager;
import com.smy.tfs.framework.manager.factory.AsyncFactory;
import com.smy.tfs.framework.security.context.AuthenticationContextHolder;
import com.smy.tfs.framework.tool.LdapUtil;
import com.smy.tfs.system.service.ISysConfigService;
import com.smy.tfs.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import scala.runtime.StringFormat;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Resource
    private ISysUserService sysUserService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysPermissionService permissionService;

    @Value("${ldap.server:ldap://192.168.20.23:389}")
    private String ldapServer;

    @Value("${tfs.ordinary.user.role.id}")
    private String ordinaryUserRoleId;



    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid, boolean useLdapLogin) {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password);
        // 用户验证
        Authentication authentication = null;
        JSONObject ldapUsrInf = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);

            if (useLdapLogin) {
                ldapUsrInf = LdapUtil.adLogin(username, password, ldapServer);
                if (HttpStatus.SUCCESS != ldapUsrInf.getIntValue(LdapUtil.RET_CODE)) {
                    throw new BadCredentialsException("LDAP鉴权失败");
                }
            } else {
                // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
                authentication = authenticationManager.authenticate(authenticationToken);
            }
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));

        LoginUser loginUser = null;
        // ldap用户初次登录则在用户表中生成用户
        if (useLdapLogin) {
            String chnName = ldapUsrInf.getString("chnName");
            String mobile = ldapUsrInf.getString("mobile");
            String email = ldapUsrInf.getString("email");

            SysUser user = userService.selectUserByUserName(username);
            if (null == user) {
                user = new SysUser();
                user.setUserName(username);
                user.setNickName(chnName);
                user.setRoleId((long) 1);
                user.setPhonenumber(mobile);
                user.setEmail(email);

                userService.insertUser(user);
                // 新增用户角色关联 默认分配一个普通用户角色
                sysUserService.initSystemUserRole(user.getUserId(), Collections.singletonList(Long.parseLong(ordinaryUserRoleId)));

                user = userService.selectUserByUserName(username);
            }
            loginUser = new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
            loginUser.setUser(user);
        } else {
            loginUser = (LoginUser) authentication.getPrincipal();
        }

        if ("admin".equals(loginUser.getUsername())){
            loginUser.setSameOriginId(TfsBaseConstant.defaultOriginId);
        } else {
            TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(loginUser.getUsername(), loginUser.getUserType());
            if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())){
                throw new ServiceException("未找到账号映射关系,请联系系统管理员先进行账号映射");
            }
            loginUser.setSameOriginId(ticketAccountMapping.getSameOriginId());
        }

        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }


    public String loginByQywxUserId(String qywxUserId) {
        LoginUser loginUser = null;
        // ldap用户初次登录则在用户表中生成用户

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountByTypeAndQywxUserId(TfsBaseConstant.defaultUserType, qywxUserId);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getAccountId())) {
            log.error("企业微信免密登录失败，原因：根据企业微信用户id未找到账户信息，请联系管理员同步");
            throw new ServiceException("根据企业微信用户id未找到账户信息，请联系管理员同步");
        }
        SysUser user = userService.selectUserByUserName(ticketAccountMapping.getAccountId());
        if (user == null){
            user = new SysUser();
            user.setUserName(ticketAccountMapping.getAccountId());
            user.setNickName(ticketAccountMapping.getAccountName());
            user.setRoleId((long) 1);
            user.setPhonenumber(ticketAccountMapping.getPhoneNo());
            user.setEmail(ticketAccountMapping.getEmail());

            userService.insertUser(user);
            // 新增用户角色关联 默认分配一个普通用户角色
            sysUserService.initSystemUserRole(user.getUserId(), Collections.singletonList(Long.parseLong(ordinaryUserRoleId)));
            user = userService.selectUserByUserName(ticketAccountMapping.getAccountId());
        }

        loginUser = new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
        loginUser.setUser(user);
        loginUser.setSameOriginId(ticketAccountMapping.getSameOriginId());

        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    public String loginByLdapUserId(String ldapUserId) {
        // ldap用户初次登录则在用户表中生成用户
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(ldapUserId, TfsBaseConstant.defaultUserType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getAccountId())) {
            log.error("根据用户id:{}未找到账户信息，请联系管理员同步", ldapUserId);
            throw new ServiceException(String.format("根据用户id:%s未找到账户信息，请联系管理员同步", ldapUserId));
        }
        SysUser user = userService.selectUserByUserName(ticketAccountMapping.getAccountId());
        if (user == null){
            user = new SysUser();
            user.setUserName(ticketAccountMapping.getAccountId());
            user.setNickName(ticketAccountMapping.getAccountName());
            user.setRoleId((long) 1);
            user.setPhonenumber(ticketAccountMapping.getPhoneNo());
            user.setEmail(ticketAccountMapping.getEmail());

            userService.insertUser(user);
            // 新增用户角色关联 默认分配一个普通用户角色
            sysUserService.initSystemUserRole(user.getUserId(), Collections.singletonList(Long.parseLong(ordinaryUserRoleId)));
            user = userService.selectUserByUserName(ticketAccountMapping.getAccountId());
        }

        LoginUser loginUser = new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
        loginUser.setUser(user);
        loginUser.setSameOriginId(ticketAccountMapping.getSameOriginId());

        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }


    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            redisCache.deleteObject(verifyKey);
            if (captcha == null) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            if (!code.equalsIgnoreCase(captcha)) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     *
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
