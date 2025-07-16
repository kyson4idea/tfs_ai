package com.smy.tfs.web.controller.system;

import com.smy.framework.core.config.Property;
import com.smy.tfs.common.constant.Constants;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.entity.SysMenu;
import com.smy.tfs.common.core.domain.entity.SysUser;
import com.smy.tfs.common.core.domain.model.LoginBody;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.utils.RsaUtils;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.framework.web.service.SysLoginService;
import com.smy.tfs.framework.web.service.SysPermissionService;
import com.smy.tfs.system.service.ISysMenuService;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class SysLoginController {
    private static final Logger log = LoggerFactory.getLogger(SysLoginController.class);

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Value("${sys.password.encode.open:true}")
    private Boolean encodePwd;


    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody, HttpServletResponse response) {
        String httpCookieDomain = Property.getProperty("http.cookie.domain");
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotEmpty(loginBody.getPassword()) && encodePwd) {
            try {
                loginBody.setPassword(RsaUtils.decryptByPrivateKey(loginBody.getPassword()));
            } catch (Exception e) {
                log.error("用户密码解密失败", e);
            }
        }
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), Boolean.TRUE.equals(loginBody.getUseLdapLogin()));
        ajax.put(Constants.TOKEN, token);

        Cookie tokenC = new Cookie("smy_union_user_token", token);
        if (StringUtils.isNotEmpty(httpCookieDomain)){
            tokenC.setDomain(httpCookieDomain);
        }
        tokenC.setPath("/");
        tokenC.setMaxAge(-1);
        response.addCookie(tokenC);

        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("sameOriginId", loginUser.getSameOriginId());
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
