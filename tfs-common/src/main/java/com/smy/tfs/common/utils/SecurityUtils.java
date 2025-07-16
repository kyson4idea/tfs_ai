package com.smy.tfs.common.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.smy.tfs.common.constant.HttpStatus;
import com.smy.tfs.common.core.domain.entity.SysUser;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全服务工具类
 *
 * @author ruoyi
 */
@Slf4j
public class SecurityUtils {

    /**
     * 用户ID
     **/
    public static Long getUserId() {
        try {
            return getLoginUser().getUserId();
        } catch (Exception e) {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId() {
        try {
            return getLoginUser().getDeptId();
        } catch (Exception e) {
            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
        }
    }


    public static String getAppId() {
        try {
            return getLoginUser().getAppId();
        } catch (Exception e) {
            throw new ServiceException("获取当前操作所属业务异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static String getAccountUserId() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static String getAccountUserName() {
        try {
            return getLoginUser().getNickName();
        } catch (Exception e) {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static String getAccountUserType() {
        try {
            return getLoginUser().getUserType();
        } catch (Exception e) {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static String getSameOriginIdOrDefault(String defaultValue) {
        try {
            return getLoginUser().getSameOriginId();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 统一用json数组展示
     *
     * @return 不要再微服务里面用这个方法
     */
    public static String getAccountUserInfo() {
        try {
            String userType = getLoginUser().getUserType();
            String userId = getLoginUser().getUsername();
            String sameOriginId = getLoginUser().getSameOriginId();
            String userName = getLoginUser().getNickName();
            JSONObject accountInfo = JSONUtil.createObj();
            accountInfo.putOpt("accountType", userType);
            accountInfo.putOpt("accountId", userId);
            accountInfo.putOpt("accountName", userName);
            accountInfo.putOpt("sameOriginId", sameOriginId);
            return accountInfo.toString();
        } catch (Exception e) {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static Boolean isTfs() {
        try {
            return getLoginUser().isTfS();
        } catch (Exception e) {
            log.error("查询是否工单web端登录进去的操作异常：", e);
            return false;
        }
    }

    /**
     * 统一用json数组展示:都通过源ID控制
     *
     * @return
     */
    @Deprecated
    public static String getAccountUserInfoForSearch() {
        try {
            if (getLoginUser().isTfS()) {
                String sameOriginId = getLoginUser().getSameOriginId();
                return String.format("\"sameOriginId\":\"%s\"", sameOriginId);
            } else {
                String userType = getLoginUser().getUserType();
                String userId = getLoginUser().getUsername();
                return String.format("\"accountType\":\"%s\",\"accountId\":\"%s\"", userType, userId);
            }
        } catch (Exception e) {
            throw new ServiceException("获取用户账户异常" + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * 统一用json数组展示
     *
     * @return
     */
    public static String getOriginUserInfoForSearch() {
        try {
            String sameOriginId = getLoginUser().getSameOriginId();
            return String.format("\"sameOriginId\":\"%s\"", sameOriginId);
        } catch (Exception e) {
            throw new ServiceException("获取用户账户异常" + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    public static boolean isAdmin() {
        Long currentUserId = getUserId();
        return isAdmin(currentUserId);
    }

    /**
     * 手动封装用户到线程
     *
     * @param authentication
     * @param runner
     */
    public static void wrapContext(Authentication authentication, Runnable runner) {
        try {
            SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            runner.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 手动封装用户到线程
     *
     * @param sameOriginId
     * @param accountId
     * @param accountName
     * @param AccountType
     * @param appId
     * @param runner
     */
    public static void wrapContext(String sameOriginId, String accountId, String accountName, String AccountType,
                                   String appId, Runnable runner) {
        try {
            // 手动构建虚拟用户，方便后续处理获取用户信息
            SysUser user = new SysUser();
            user.setUserId(-1L);
            user.setUserName(accountId);
            user.setNickName(accountName);

            LoginUser loginUser = new LoginUser();
            loginUser.setUser(user);
            loginUser.setUserId(-1L);
            loginUser.setUserType(AccountType);
            loginUser.setSameOriginId(sameOriginId);
            loginUser.setAppId(appId);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            runner.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

}
