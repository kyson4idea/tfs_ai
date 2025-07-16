-- ----------------------------
-- 1、部门表
-- ----------------------------
drop table if exists sys_dept;
create table sys_dept (
  dept_id           bigint(20)      not null auto_increment    comment '部门id',
  parent_id         bigint(20)      default 0                  comment '父部门id',
  ancestors         varchar(50)     default ''                 comment '祖级列表',
  dept_name         varchar(30)     default ''                 comment '部门名称',
  order_num         int(4)          default 0                  comment '显示顺序',
  leader            varchar(20)     default null               comment '负责人',
  phone             varchar(11)     default null               comment '联系电话',
  email             varchar(50)     default null               comment '邮箱',
  status            char(1)         default '0'                comment '部门状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (dept_id)
) engine=innodb auto_increment=200 comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------
insert into sys_dept values(100,  0,   '0',          '萨摩耶数字科技',   0, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(101,  100, '0,100',      '金融云', 1, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(102,  100, '0,100',      '产业云', 2, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(103,  101, '0,100,101',  '研发部',   1, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(104,  101, '0,100,101',  '运营部',   2, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(105,  101, '0,100,101',  '用户增长部',   3, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(106,  101, '0,100,101',  '战略合作部',   4, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(107,  101, '0,100,101',  '风险管理部',   5, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(108,  102, '0,100,102',  '跨境研发部',   1, '', '', '', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(109,  102, '0,100,102',  '跨境市场部',   2, '', '', '', '0', '0', 'admin', sysdate(), '', null);


-- ----------------------------
-- 2、用户信息表
-- ----------------------------
drop table if exists sys_user;
create table sys_user (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  dept_id           bigint(20)      default null               comment '部门ID',
  user_name         varchar(30)     not null                   comment '用户账号',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  user_type         varchar(2)      default '00'               comment '用户类型（00系统用户）',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phonenumber       varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            varchar(100)    default ''                 comment '头像地址',
  password          varchar(100)    default ''                 comment '密码',
  status            char(1)         default '0'                comment '帐号状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (user_id)
) engine=innodb auto_increment=100 comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1,  103, 'admin', 'admin', '00', '', '', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), 'admin', sysdate(), '', null, '管理员');

-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
drop table if exists sys_post;
create table sys_post
(
  post_id       bigint(20)      not null auto_increment    comment '岗位ID',
  post_code     varchar(64)     not null                   comment '岗位编码',
  post_name     varchar(50)     not null                   comment '岗位名称',
  post_sort     int(4)          not null                   comment '显示顺序',
  status        char(1)         not null                   comment '状态（0正常 1停用）',
  create_by     varchar(64)     default ''                 comment '创建者',
  create_time   datetime                                   comment '创建时间',
  update_by     varchar(64)     default ''			       comment '更新者',
  update_time   datetime                                   comment '更新时间',
  remark        varchar(500)    default null               comment '备注',
  primary key (post_id)
) engine=innodb comment = '岗位信息表';

-- ----------------------------
-- 初始化-岗位信息表数据
-- ----------------------------
insert into sys_post values(1, 'ceo',  '董事长',    1, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(2, 'se',   '项目经理',  2, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(3, 'hr',   '人力资源',  3, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(4, 'user', '普通员工',  4, '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 4、角色信息表
-- ----------------------------
drop table if exists sys_role;
create table sys_role (
  role_id              bigint(20)      not null auto_increment    comment '角色ID',
  role_name            varchar(30)     not null                   comment '角色名称',
  role_key             varchar(100)    not null                   comment '角色权限字符串',
  role_sort            int(4)          not null                   comment '显示顺序',
  data_scope           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  menu_check_strictly  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
  dept_check_strictly  tinyint(1)      default 1                  comment '部门树选择项是否关联显示',
  status               char(1)         not null                   comment '角色状态（0正常 1停用）',
  del_flag             char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by            varchar(64)     default ''                 comment '创建者',
  create_time          datetime                                   comment '创建时间',
  update_by            varchar(64)     default ''                 comment '更新者',
  update_time          datetime                                   comment '更新时间',
  remark               varchar(500)    default null               comment '备注',
  primary key (role_id)
) engine=innodb auto_increment=100 comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values('1', '超级管理员',  'admin',  1, 1, 1, 1, '0', '0', 'admin', sysdate(), '', null, '超级管理员');
insert into sys_role values('2', '普通角色',    'common', 2, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '普通角色');


-- ----------------------------
-- 5、菜单权限表
-- ----------------------------
drop table if exists sys_menu;
create table sys_menu (
  menu_id           bigint(20)      not null auto_increment    comment '菜单ID',
  menu_name         varchar(50)     not null                   comment '菜单名称',
  parent_id         bigint(20)      default 0                  comment '父菜单ID',
  order_num         int(4)          default 0                  comment '显示顺序',
  path              varchar(200)    default ''                 comment '路由地址',
  is_lowcode        int(1)          default 1                  comment '是否完全由低代码配置生成的页面',
  lowcode_cfgid     varchar(100)    default ''                 comment '低代码配置标识',
  component         varchar(255)    default null               comment '组件路径',
  query             varchar(255)    default null               comment '路由参数',
  custom_params     varchar(255)    default ''                 comment '自定义菜单配置，json字符串类型',
  is_frame          int(1)          default 1                  comment '是否为外链（0是 1否）',
  is_cache          int(1)          default 0                  comment '是否缓存（0缓存 1不缓存）',
  menu_type         char(1)         default ''                 comment '菜单类型（M目录 C菜单 F按钮）',
  visible           char(1)         default 0                  comment '菜单状态（0显示 1隐藏）',
  status            char(1)         default 0                  comment '菜单状态（0正常 1停用）',
  perms             varchar(100)    default null               comment '权限标识',
  icon              varchar(100)    default '#'                comment '菜单图标',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default ''                 comment '备注',
  primary key (menu_id)
) engine=innodb auto_increment=2000 comment = '菜单权限表';

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into sys_menu values('1', '系统管理', '0', '1', 'system',           1, '', null, '', '', 1, 0, 'M', '0', '0', '', 'system',   'admin', sysdate(), '', null, '系统管理目录');
insert into sys_menu values('2', '系统监控', '0', '2', 'monitor',          1, '', null, '', '', 1, 0, 'M', '0', '0', '', 'monitor',  'admin', sysdate(), '', null, '系统监控目录');
insert into sys_menu values('3', '系统工具', '0', '3', 'tool',             1, '', null, '', '', 1, 0, 'M', '0', '0', '', 'tool',     'admin', sysdate(), '', null, '系统工具目录');
insert into sys_menu values('4', '低代码配置', '0', '4', 'lowcode',        1, '', null, '', '', 1, 0, 'M', '0', '0', '', 'component', 'admin', sysdate(), '', null, '低代码管理目录');
-- 二级菜单
insert into sys_menu values('100',  '用户管理', '1',   '1', 'user',        1, '', 'system/user/index',        '', '', 1, 0, 'C', '0', '0', 'system:user:list',        'user',          'admin', sysdate(), '', null, '用户管理菜单');
insert into sys_menu values('101',  '角色管理', '1',   '2', 'role',        1, '', 'system/role/index',        '', '', 1, 0, 'C', '0', '0', 'system:role:list',        'peoples',       'admin', sysdate(), '', null, '角色管理菜单');
insert into sys_menu values('102',  '菜单管理', '1',   '3', 'menu',        1, '', 'system/menu/index',        '', '', 1, 0, 'C', '0', '0', 'system:menu:list',        'tree-table',    'admin', sysdate(), '', null, '菜单管理菜单');
insert into sys_menu values('103',  '部门管理', '1',   '4', 'dept',        1, '', 'system/dept/index',        '', '', 1, 0, 'C', '0', '0', 'system:dept:list',        'tree',          'admin', sysdate(), '', null, '部门管理菜单');
insert into sys_menu values('104',  '岗位管理', '1',   '5', 'post',        1, '', 'system/post/index',        '', '', 1, 0, 'C', '0', '0', 'system:post:list',        'post',          'admin', sysdate(), '', null, '岗位管理菜单');
insert into sys_menu values('105',  '字典管理', '1',   '6', 'dict',        1, '', 'system/dict/index',        '', '', 1, 0, 'C', '0', '0', 'system:dict:list',        'dict',          'admin', sysdate(), '', null, '字典管理菜单');
insert into sys_menu values('106',  '参数设置', '1',   '7', 'config',      1, '', 'system/config/index',      '', '', 1, 0, 'C', '0', '0', 'system:config:list',      'edit',          'admin', sysdate(), '', null, '参数设置菜单');
insert into sys_menu values('107',  '通知公告', '1',   '8', 'notice',      1, '', 'system/notice/index',      '', '', 1, 0, 'C', '0', '0', 'system:notice:list',      'message',       'admin', sysdate(), '', null, '通知公告菜单');
insert into sys_menu values('108',  '日志管理', '1',   '9', 'log',         1, '', '',                         '', '', 1, 0, 'M', '0', '0', '',                        'log',           'admin', sysdate(), '', null, '日志管理菜单');
insert into sys_menu values('109',  '在线用户', '2',   '1', 'online',      1, '', 'monitor/online/index',     '', '', 1, 0, 'C', '0', '0', 'monitor:online:list',     'online',        'admin', sysdate(), '', null, '在线用户菜单');
insert into sys_menu values('110',  '定时任务', '2',   '2', 'job',         1, '', 'monitor/job/index',        '', '', 1, 0, 'C', '0', '0', 'monitor:job:list',        'job',           'admin', sysdate(), '', null, '定时任务菜单');
insert into sys_menu values('111',  '数据监控', '2',   '3', 'druid',       1, '', 'monitor/druid/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list',      'druid',         'admin', sysdate(), '', null, '数据监控菜单');
insert into sys_menu values('112',  '服务监控', '2',   '4', 'server',      1, '', 'monitor/server/index',     '', '', 1, 0, 'C', '0', '0', 'monitor:server:list',     'server',        'admin', sysdate(), '', null, '服务监控菜单');
insert into sys_menu values('113',  '缓存监控', '2',   '5', 'cache',       1, '', 'monitor/cache/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',      'redis',         'admin', sysdate(), '', null, '缓存监控菜单');
insert into sys_menu values('114',  '缓存列表', '2',   '6', 'cacheList',   1, '', 'monitor/cache/list',       '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',      'redis-list',    'admin', sysdate(), '', null, '缓存列表菜单');
insert into sys_menu values('115',  '表单构建', '3',   '1', 'build',       1, '', 'tool/build/index',         '', '', 1, 0, 'C', '0', '0', 'tool:build:list',         'build',         'admin', sysdate(), '', null, '表单构建菜单');
insert into sys_menu values('116',  '代码生成', '3',   '2', 'gen',         1, '', 'tool/gen/index',           '', '', 1, 0, 'C', '0', '0', 'tool:gen:list',           'code',          'admin', sysdate(), '', null, '代码生成菜单');
insert into sys_menu values('117',  '系统接口', '3',   '3', 'swagger',     1, '', 'tool/swagger/index',       '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list',       'swagger',       'admin', sysdate(), '', null, '系统接口菜单');
insert into sys_menu values('118',  '页面辅助配置', '4', '1', 'page',       1, '',  'low-code/index',          '', '', 1, 0, 'C', '0', '0', 'lowcode:list',            'system',        'admin', sysdate(), '', null, '页面辅助配置菜单');
insert into sys_menu values('119',  '通用数据',  '4', '2', 'commonsql',    0, 'CommData', null,               '', '', 1, 0, 'C', '0', '0', '',                        'redis',         'admin', sysdate(), '', null, '通用Sql数据');
insert into sys_menu values('120',  '说明文档',  '4', '3', 'https://lt-srm-docker-06.smyjf.cn/ruoyi-lowcode-doc/', 1, '', null, '', '', 0, 0, 'C', '0', '0', '',      'documentation', 'admin', sysdate(), '', null, '低代码组件说明文档');

-- 三级菜单
insert into sys_menu values('500', '操作日志',            '108', '1', 'operlog',    1, '',                    'monitor/operlog/index',    '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',    'form',          'admin', sysdate(), '', null, '操作日志菜单');
insert into sys_menu values('501', '登录日志',            '108', '2', 'logininfor', 1, '',                    'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor',    'admin', sysdate(), '', null, '登录日志菜单');
-- 用户管理按钮
insert into sys_menu values('1000', '用户查询', '100', '1',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1001', '用户新增', '100', '2',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1002', '用户修改', '100', '3',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1003', '用户删除', '100', '4',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1004', '用户导出', '100', '5',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1005', '用户导入', '100', '6',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1006', '重置密码', '100', '7',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd',       '#', 'admin', sysdate(), '', null, '');
-- 角色管理按钮
insert into sys_menu values('1007', '角色查询', '101', '1',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1008', '角色新增', '101', '2',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1009', '角色修改', '101', '3',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1010', '角色删除', '101', '4',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1011', '角色导出', '101', '5',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export',         '#', 'admin', sysdate(), '', null, '');
-- 菜单管理按钮
insert into sys_menu values('1012', '菜单查询', '102', '1',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1013', '菜单新增', '102', '2',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1014', '菜单修改', '102', '3',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1015', '菜单删除', '102', '4',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove',         '#', 'admin', sysdate(), '', null, '');
-- 部门管理按钮
insert into sys_menu values('1016', '部门查询', '103', '1',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1017', '部门新增', '103', '2',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1018', '部门修改', '103', '3',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1019', '部门删除', '103', '4',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove',         '#', 'admin', sysdate(), '', null, '');
-- 岗位管理按钮
insert into sys_menu values('1020', '岗位查询', '104', '1',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1021', '岗位新增', '104', '2',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1022', '岗位修改', '104', '3',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1023', '岗位删除', '104', '4',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1024', '岗位导出', '104', '5',  '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export',         '#', 'admin', sysdate(), '', null, '');
-- 字典管理按钮
insert into sys_menu values('1025', '字典查询', '105', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1026', '字典新增', '105', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1027', '字典修改', '105', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1028', '字典删除', '105', '4', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1029', '字典导出', '105', '5', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export',         '#', 'admin', sysdate(), '', null, '');
-- 参数设置按钮
insert into sys_menu values('1030', '参数查询', '106', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1031', '参数新增', '106', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1032', '参数修改', '106', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1033', '参数删除', '106', '4', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1034', '参数导出', '106', '5', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export',       '#', 'admin', sysdate(), '', null, '');
-- 通知公告按钮
insert into sys_menu values('1035', '公告查询', '107', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1036', '公告新增', '107', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1037', '公告修改', '107', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1038', '公告删除', '107', '4', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove',       '#', 'admin', sysdate(), '', null, '');
-- 操作日志按钮
insert into sys_menu values('1039', '操作查询', '500', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1040', '操作删除', '500', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1041', '日志导出', '500', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export',     '#', 'admin', sysdate(), '', null, '');
-- 登录日志按钮
insert into sys_menu values('1042', '登录查询', '501', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1043', '登录删除', '501', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1044', '日志导出', '501', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1045', '账户解锁', '501', '4', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock',  '#', 'admin', sysdate(), '', null, '');
-- 在线用户按钮
insert into sys_menu values('1046', '在线查询', '109', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1047', '批量强退', '109', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1048', '单条强退', '109', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', sysdate(), '', null, '');
-- 定时任务按钮
insert into sys_menu values('1049', '任务查询', '110', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1050', '任务新增', '110', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1051', '任务修改', '110', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1052', '任务删除', '110', '4', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1053', '状态修改', '110', '5', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1054', '任务导出', '110', '6', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export',         '#', 'admin', sysdate(), '', null, '');
-- 代码生成按钮
insert into sys_menu values('1055', '生成查询', '116', '1', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query',             '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1056', '生成修改', '116', '2', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit',              '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1057', '生成删除', '116', '3', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1058', '导入代码', '116', '4', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1059', '预览代码', '116', '5', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1060', '生成代码', '116', '6', '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code',              '#', 'admin', sysdate(), '', null, '');




-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
drop table if exists sys_user_role;
create table sys_user_role (
  user_id   bigint(20) not null comment '用户ID',
  role_id   bigint(20) not null comment '角色ID',
  primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('2', '2');


-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
drop table if exists sys_role_menu;
create table sys_role_menu (
  role_id   bigint(20) not null comment '角色ID',
  menu_id   bigint(20) not null comment '菜单ID',
  primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('2', '1');
insert into sys_role_menu values ('2', '2');
insert into sys_role_menu values ('2', '3');
insert into sys_role_menu values ('2', '4');
insert into sys_role_menu values ('2', '100');
insert into sys_role_menu values ('2', '101');
insert into sys_role_menu values ('2', '102');
insert into sys_role_menu values ('2', '103');
insert into sys_role_menu values ('2', '104');
insert into sys_role_menu values ('2', '105');
insert into sys_role_menu values ('2', '106');
insert into sys_role_menu values ('2', '107');
insert into sys_role_menu values ('2', '108');
insert into sys_role_menu values ('2', '109');
insert into sys_role_menu values ('2', '110');
insert into sys_role_menu values ('2', '111');
insert into sys_role_menu values ('2', '112');
insert into sys_role_menu values ('2', '113');
insert into sys_role_menu values ('2', '114');
insert into sys_role_menu values ('2', '115');
insert into sys_role_menu values ('2', '116');
insert into sys_role_menu values ('2', '117');
insert into sys_role_menu values ('2', '500');
insert into sys_role_menu values ('2', '501');
insert into sys_role_menu values ('2', '1000');
insert into sys_role_menu values ('2', '1001');
insert into sys_role_menu values ('2', '1002');
insert into sys_role_menu values ('2', '1003');
insert into sys_role_menu values ('2', '1004');
insert into sys_role_menu values ('2', '1005');
insert into sys_role_menu values ('2', '1006');
insert into sys_role_menu values ('2', '1007');
insert into sys_role_menu values ('2', '1008');
insert into sys_role_menu values ('2', '1009');
insert into sys_role_menu values ('2', '1010');
insert into sys_role_menu values ('2', '1011');
insert into sys_role_menu values ('2', '1012');
insert into sys_role_menu values ('2', '1013');
insert into sys_role_menu values ('2', '1014');
insert into sys_role_menu values ('2', '1015');
insert into sys_role_menu values ('2', '1016');
insert into sys_role_menu values ('2', '1017');
insert into sys_role_menu values ('2', '1018');
insert into sys_role_menu values ('2', '1019');
insert into sys_role_menu values ('2', '1020');
insert into sys_role_menu values ('2', '1021');
insert into sys_role_menu values ('2', '1022');
insert into sys_role_menu values ('2', '1023');
insert into sys_role_menu values ('2', '1024');
insert into sys_role_menu values ('2', '1025');
insert into sys_role_menu values ('2', '1026');
insert into sys_role_menu values ('2', '1027');
insert into sys_role_menu values ('2', '1028');
insert into sys_role_menu values ('2', '1029');
insert into sys_role_menu values ('2', '1030');
insert into sys_role_menu values ('2', '1031');
insert into sys_role_menu values ('2', '1032');
insert into sys_role_menu values ('2', '1033');
insert into sys_role_menu values ('2', '1034');
insert into sys_role_menu values ('2', '1035');
insert into sys_role_menu values ('2', '1036');
insert into sys_role_menu values ('2', '1037');
insert into sys_role_menu values ('2', '1038');
insert into sys_role_menu values ('2', '1039');
insert into sys_role_menu values ('2', '1040');
insert into sys_role_menu values ('2', '1041');
insert into sys_role_menu values ('2', '1042');
insert into sys_role_menu values ('2', '1043');
insert into sys_role_menu values ('2', '1044');
insert into sys_role_menu values ('2', '1045');
insert into sys_role_menu values ('2', '1046');
insert into sys_role_menu values ('2', '1047');
insert into sys_role_menu values ('2', '1048');
insert into sys_role_menu values ('2', '1049');
insert into sys_role_menu values ('2', '1050');
insert into sys_role_menu values ('2', '1051');
insert into sys_role_menu values ('2', '1052');
insert into sys_role_menu values ('2', '1053');
insert into sys_role_menu values ('2', '1054');
insert into sys_role_menu values ('2', '1055');
insert into sys_role_menu values ('2', '1056');
insert into sys_role_menu values ('2', '1057');
insert into sys_role_menu values ('2', '1058');
insert into sys_role_menu values ('2', '1059');
insert into sys_role_menu values ('2', '1060');

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
drop table if exists sys_role_dept;
create table sys_role_dept (
  role_id   bigint(20) not null comment '角色ID',
  dept_id   bigint(20) not null comment '部门ID',
  primary key(role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';

-- ----------------------------
-- 初始化-角色和部门关联表数据
-- ----------------------------
insert into sys_role_dept values ('2', '100');
insert into sys_role_dept values ('2', '101');
insert into sys_role_dept values ('2', '105');


-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
drop table if exists sys_user_post;
create table sys_user_post
(
  user_id   bigint(20) not null comment '用户ID',
  post_id   bigint(20) not null comment '岗位ID',
  primary key (user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';

-- ----------------------------
-- 初始化-用户与岗位关联表数据
-- ----------------------------
insert into sys_user_post values ('1', '1');
insert into sys_user_post values ('2', '2');


-- ----------------------------
-- 10、操作日志记录
-- ----------------------------
drop table if exists sys_oper_log;
create table sys_oper_log (
  oper_id           bigint(20)      not null auto_increment    comment '日志主键',
  title             varchar(50)     default ''                 comment '模块标题',
  business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
  method            varchar(100)    default ''                 comment '方法名称',
  request_method    varchar(10)     default ''                 comment '请求方式',
  operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
  oper_name         varchar(50)     default ''                 comment '操作人员',
  dept_name         varchar(50)     default ''                 comment '部门名称',
  oper_url          varchar(255)    default ''                 comment '请求URL',
  oper_ip           varchar(128)    default ''                 comment '主机地址',
  oper_location     varchar(255)    default ''                 comment '操作地点',
  oper_param        varchar(2000)   default ''                 comment '请求参数',
  json_result       varchar(2000)   default ''                 comment '返回参数',
  status            int(1)          default 0                  comment '操作状态（0正常 1异常）',
  error_msg         varchar(2000)   default ''                 comment '错误消息',
  oper_time         datetime                                   comment '操作时间',
  cost_time         bigint(20)      default 0                  comment '消耗时间',
  primary key (oper_id),
  key idx_sys_oper_log_bt (business_type),
  key idx_sys_oper_log_s  (status),
  key idx_sys_oper_log_ot (oper_time)
) engine=innodb auto_increment=100 comment = '操作日志记录';


-- ----------------------------
-- 11、字典类型表
-- ----------------------------
drop table if exists sys_dict_type;
create table sys_dict_type
(
  dict_id          bigint(20)      not null auto_increment    comment '字典主键',
  dict_name        varchar(100)    default ''                 comment '字典名称',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_id),
  unique (dict_type)
) engine=innodb auto_increment=100 comment = '字典类型表';

insert into sys_dict_type values(1,  '用户性别', 'sys_user_sex',        '0', 'admin', sysdate(), '', null, '用户性别列表');
insert into sys_dict_type values(2,  '菜单状态', 'sys_show_hide',       '0', 'admin', sysdate(), '', null, '菜单状态列表');
insert into sys_dict_type values(3,  '系统开关', 'sys_normal_disable',  '0', 'admin', sysdate(), '', null, '系统开关列表');
insert into sys_dict_type values(4,  '任务状态', 'sys_job_status',      '0', 'admin', sysdate(), '', null, '任务状态列表');
insert into sys_dict_type values(5,  '任务分组', 'sys_job_group',       '0', 'admin', sysdate(), '', null, '任务分组列表');
insert into sys_dict_type values(6,  '系统是否', 'sys_yes_no',          '0', 'admin', sysdate(), '', null, '系统是否列表');
insert into sys_dict_type values(7,  '通知类型', 'sys_notice_type',     '0', 'admin', sysdate(), '', null, '通知类型列表');
insert into sys_dict_type values(8,  '通知状态', 'sys_notice_status',   '0', 'admin', sysdate(), '', null, '通知状态列表');
insert into sys_dict_type values(9,  '操作类型', 'sys_oper_type',       '0', 'admin', sysdate(), '', null, '操作类型列表');
insert into sys_dict_type values(10, '系统状态', 'sys_common_status',   '0', 'admin', sysdate(), '', null, '登录状态列表');


-- ----------------------------
-- 12、字典数据表
-- ----------------------------
drop table if exists sys_dict_data;
create table sys_dict_data
(
  dict_code        bigint(20)      not null auto_increment    comment '字典编码',
  dict_sort        int(4)          default 0                  comment '字典排序',
  dict_label       varchar(100)    default ''                 comment '字典标签',
  dict_value       varchar(100)    default ''                 comment '字典键值',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  css_class        varchar(100)    default null               comment '样式属性（其他样式扩展）',
  list_class       varchar(100)    default null               comment '表格回显样式',
  is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_code)
) engine=innodb auto_increment=100 comment = '字典数据表';

insert into sys_dict_data values(1,  1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', '0', 'admin', sysdate(), '', null, '性别男');
insert into sys_dict_data values(2,  2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', '0', 'admin', sysdate(), '', null, '性别女');
insert into sys_dict_data values(3,  3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', '0', 'admin', sysdate(), '', null, '性别未知');
insert into sys_dict_data values(4,  1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '显示菜单');
insert into sys_dict_data values(5,  2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '隐藏菜单');
insert into sys_dict_data values(6,  1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(7,  2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');
insert into sys_dict_data values(8,  1,  '正常',     '0',       'sys_job_status',      '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(9,  2,  '暂停',     '1',       'sys_job_status',      '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');
insert into sys_dict_data values(10, 1,  '默认',     'DEFAULT', 'sys_job_group',       '',   '',        'Y', '0', 'admin', sysdate(), '', null, '默认分组');
insert into sys_dict_data values(11, 2,  '系统',     'SYSTEM',  'sys_job_group',       '',   '',        'N', '0', 'admin', sysdate(), '', null, '系统分组');
insert into sys_dict_data values(12, 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '系统默认是');
insert into sys_dict_data values(13, 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '系统默认否');
insert into sys_dict_data values(14, 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', '0', 'admin', sysdate(), '', null, '通知');
insert into sys_dict_data values(15, 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', '0', 'admin', sysdate(), '', null, '公告');
insert into sys_dict_data values(16, 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(17, 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '关闭状态');
insert into sys_dict_data values(18, 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '其他操作');
insert into sys_dict_data values(19, 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '新增操作');
insert into sys_dict_data values(20, 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '修改操作');
insert into sys_dict_data values(21, 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '删除操作');
insert into sys_dict_data values(22, 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', '0', 'admin', sysdate(), '', null, '授权操作');
insert into sys_dict_data values(23, 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '导出操作');
insert into sys_dict_data values(24, 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '导入操作');
insert into sys_dict_data values(25, 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '强退操作');
insert into sys_dict_data values(26, 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '生成操作');
insert into sys_dict_data values(27, 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '清空操作');
insert into sys_dict_data values(28, 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(29, 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');


-- ----------------------------
-- 13、参数配置表
-- ----------------------------
drop table if exists sys_config;
create table sys_config (
  config_id         int(5)          not null auto_increment    comment '参数主键',
  config_name       varchar(100)    default ''                 comment '参数名称',
  config_key        varchar(100)    default ''                 comment '参数键名',
  config_value      varchar(500)    default ''                 comment '参数键值',
  config_type       char(1)         default 'N'                comment '系统内置（Y是 N否）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (config_id)
) engine=innodb auto_increment=100 comment = '参数配置表';

insert into sys_config values(1, '主框架页-默认皮肤样式名称',     'sys.index.skinName',            'skin-blue',     'Y', 'admin', sysdate(), '', null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow' );
insert into sys_config values(2, '用户管理-账号初始密码',         'sys.user.initPassword',         '123456',        'Y', 'admin', sysdate(), '', null, '初始化密码 123456' );
insert into sys_config values(3, '主框架页-侧边栏主题',           'sys.index.sideTheme',           'theme-dark',    'Y', 'admin', sysdate(), '', null, '深色主题theme-dark，浅色主题theme-light' );
insert into sys_config values(4, '账号自助-验证码开关',           'sys.account.captchaEnabled',    'true',          'Y', 'admin', sysdate(), '', null, '是否开启验证码功能（true开启，false关闭）');
insert into sys_config values(5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser',      'false',         'Y', 'admin', sysdate(), '', null, '是否开启注册用户功能（true开启，false关闭）');
insert into sys_config values(6, '用户登录-黑名单列表',           'sys.login.blackIPList',         '',              'Y', 'admin', sysdate(), '', null, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');


-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
drop table if exists sys_logininfor;
create table sys_logininfor (
  info_id        bigint(20)     not null auto_increment   comment '访问ID',
  user_name      varchar(50)    default ''                comment '用户账号',
  ipaddr         varchar(128)   default ''                comment '登录IP地址',
  login_location varchar(255)   default ''                comment '登录地点',
  browser        varchar(50)    default ''                comment '浏览器类型',
  os             varchar(50)    default ''                comment '操作系统',
  status         char(1)        default '0'               comment '登录状态（0成功 1失败）',
  msg            varchar(255)   default ''                comment '提示消息',
  login_time     datetime                                 comment '访问时间',
  primary key (info_id),
  key idx_sys_logininfor_s  (status),
  key idx_sys_logininfor_lt (login_time)
) engine=innodb auto_increment=100 comment = '系统访问记录';


-- ----------------------------
-- 15、定时任务调度表
-- ----------------------------
drop table if exists sys_job;
create table sys_job (
  job_id              bigint(20)    not null auto_increment    comment '任务ID',
  job_name            varchar(64)   default ''                 comment '任务名称',
  job_group           varchar(64)   default 'DEFAULT'          comment '任务组名',
  invoke_target       varchar(500)  not null                   comment '调用目标字符串',
  cron_expression     varchar(255)  default ''                 comment 'cron执行表达式',
  misfire_policy      varchar(20)   default '3'                comment '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  concurrent          char(1)       default '1'                comment '是否并发执行（0允许 1禁止）',
  status              char(1)       default '0'                comment '状态（0正常 1暂停）',
  create_by           varchar(64)   default ''                 comment '创建者',
  create_time         datetime                                 comment '创建时间',
  update_by           varchar(64)   default ''                 comment '更新者',
  update_time         datetime                                 comment '更新时间',
  remark              varchar(500)  default ''                 comment '备注信息',
  primary key (job_id, job_name, job_group)
) engine=innodb auto_increment=100 comment = '定时任务调度表';

insert into sys_job values(1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams',        '0/10 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');
insert into sys_job values(2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')',  '0/15 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');
insert into sys_job values(3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)',  '0/20 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 16、定时任务调度日志表
-- ----------------------------
drop table if exists sys_job_log;
create table sys_job_log (
  job_log_id          bigint(20)     not null auto_increment    comment '任务日志ID',
  job_name            varchar(64)    not null                   comment '任务名称',
  job_group           varchar(64)    not null                   comment '任务组名',
  invoke_target       varchar(500)   not null                   comment '调用目标字符串',
  job_message         varchar(500)                              comment '日志信息',
  status              char(1)        default '0'                comment '执行状态（0正常 1失败）',
  exception_info      varchar(2000)  default ''                 comment '异常信息',
  create_time         datetime                                  comment '创建时间',
  primary key (job_log_id)
) engine=innodb comment = '定时任务调度日志表';


-- ----------------------------
-- 17、通知公告表
-- ----------------------------
drop table if exists sys_notice;
create table sys_notice (
  notice_id         int(4)          not null auto_increment    comment '公告ID',
  notice_title      varchar(50)     not null                   comment '公告标题',
  notice_type       char(1)         not null                   comment '公告类型（1通知 2公告）',
  notice_content    longblob        default null               comment '公告内容',
  status            char(1)         default '0'                comment '公告状态（0正常 1关闭）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(255)    default null               comment '备注',
  primary key (notice_id)
) engine=innodb auto_increment=10 comment = '通知公告表';

-- ----------------------------
-- 初始化-公告信息表数据
-- ----------------------------
insert into sys_notice values('1', '温馨提醒：2018-07-01 若依新版本发布啦', '2', '新版本内容', '0', 'admin', sysdate(), '', null, '管理员');
insert into sys_notice values('2', '维护通知：2018-07-01 若依系统凌晨维护', '1', '维护内容',   '0', 'admin', sysdate(), '', null, '管理员');


-- ----------------------------
-- 18、代码生成业务表
-- ----------------------------
drop table if exists gen_table;
create table gen_table (
  table_id          bigint(20)      not null auto_increment    comment '编号',
  table_name        varchar(200)    default ''                 comment '表名称',
  table_comment     varchar(500)    default ''                 comment '表描述',
  sub_table_name    varchar(64)     default null               comment '关联子表的表名',
  sub_table_fk_name varchar(64)     default null               comment '子表关联的外键名',
  class_name        varchar(100)    default ''                 comment '实体类名称',
  tpl_category      varchar(200)    default 'crud'             comment '使用的模板（crud单表操作 tree树表操作）',
  package_name      varchar(100)                               comment '生成包路径',
  module_name       varchar(30)                                comment '生成模块名',
  business_name     varchar(30)                                comment '生成业务名',
  function_name     varchar(50)                                comment '生成功能名',
  function_author   varchar(50)                                comment '生成功能作者',
  gen_type          char(1)         default '0'                comment '生成代码方式（0zip压缩包 1自定义路径）',
  gen_path          varchar(200)    default '/'                comment '生成路径（不填默认项目路径）',
  options           varchar(1000)                              comment '其它生成选项',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (table_id)
) engine=innodb auto_increment=1 comment = '代码生成业务表';


-- ----------------------------
-- 19、代码生成业务表字段
-- ----------------------------
drop table if exists gen_table_column;
create table gen_table_column (
  column_id         bigint(20)      not null auto_increment    comment '编号',
  table_id          varchar(64)                                comment '归属表编号',
  column_name       varchar(200)                               comment '列名称',
  column_comment    varchar(500)                               comment '列描述',
  column_type       varchar(100)                               comment '列类型',
  java_type         varchar(500)                               comment 'JAVA类型',
  java_field        varchar(200)                               comment 'JAVA字段名',
  is_pk             char(1)                                    comment '是否主键（1是）',
  is_increment      char(1)                                    comment '是否自增（1是）',
  is_required       char(1)                                    comment '是否必填（1是）',
  is_insert         char(1)                                    comment '是否为插入字段（1是）',
  is_edit           char(1)                                    comment '是否编辑字段（1是）',
  is_list           char(1)                                    comment '是否列表字段（1是）',
  is_query          char(1)                                    comment '是否查询字段（1是）',
  query_type        varchar(200)    default 'EQ'               comment '查询方式（等于、不等于、大于、小于、范围）',
  html_type         varchar(200)                               comment '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  dict_type         varchar(200)    default ''                 comment '字典类型',
  sort              int                                        comment '排序',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (column_id)
) engine=innodb auto_increment=1 comment = '代码生成业务表字段';

-- ----------------------------
-- 20、通用接口SQL配置表
-- ----------------------------
drop table if exists sys_common_data;
create table sys_common_data (
  id              int(10) unsigned  not null auto_increment,
  data_key        varchar(50)       default null              comment '接口数据标识',
  op_type         varchar(10)       default null              comment '操作类型: LIST-列表 | UPDATE-更新 | ADD-新增 | DELETE-删除',
  pri_key         varchar(20)       default ''                comment '操作单表SQL的唯一键字段名，代码中默认取"id"',
  auto_sql_table  varchar(20)       default null              comment '自动化SQL操作的单表名，代码中默认取data_key',
  `sql`           text                                        comment 'SQL配置',
  op_role         text                                        comment '操作角色',
  create_by       varchar(10)       default null              comment '创建者',
  create_time     datetime          default null              comment '创建时间',
  update_by       varchar(10)       default null              comment '更新者',
  update_time     datetime          default null              comment '更新时间',
  primary key (id)
) engine=InnoDB auto_increment=1 comment='通用接口SQL配置表';

INSERT INTO sys_common_data (id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time) VALUES(30, 'CommData',          'LIST',   '', '', 'SELECT * FROM sys_common_data WHERE 1=1 [AND data_key={data_key}] ORDER BY data_key', '[]', 'admin', sysdate(), NULL, NULL);
INSERT INTO sys_common_data (id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time) VALUES(31, 'CommData_DataKey',  'LIST',   '', '', 'SELECT distinct data_key FROM sys_common_data', NULL, 'admin', sysdate(), NULL, NULL);
INSERT INTO sys_common_data (id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time) VALUES(32, 'CommData_Role',     'LIST',   '', '', 'SELECT role_name,role_id FROM sys_role', '[]', 'admin', sysdate(), NULL, NULL);
INSERT INTO sys_common_data (id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time) VALUES(33, 'CommData',          'ADD',    '', '', 'INSERT INTO sys_common_data(data_key,op_type,`sql`,op_role,create_by,create_time) values({data_key},{op_type},{sql},{op_role},{sys.user_name},NOW())', '[1]', 'admin', sysdate(), NULL, NULL);
INSERT INTO sys_common_data (id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time) VALUES(35, 'CommData',          'DELETE', '', '', 'DELETE FROM sys_common_data WHERE id={id}', '[1]', 'admin', sysdate(), NULL, NULL);
INSERT INTO sys_common_data (id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time) VALUES(37, 'CommData',          'UPDATE', '', '', 'UPDATE sys_common_data SET data_key={data_key},op_type={op_type}[,`sql`={sql}][,op_role={op_role}],update_by={sys.user_name},update_time=NOW() WHERE id={id}', '[1]', 'admin', sysdate(), NULL, NULL);


-- ----------------------------
-- 21、低代码页面配置表
-- ----------------------------
drop table if exists sys_page;
create table sys_page (
  page_id           bigint(20)        not null auto_increment,
  page_key          varchar(100)      not null                  comment '页面配置标识',
  remark            varchar(500)      default null              comment '说明',
  param_json        text                                        comment 'JSON配置',
  create_by         varchar(64)       default ''                comment '创建者',
  create_time       datetime                                    comment '创建时间',
  primary key (page_id),
  unique (page_key)
) engine=InnoDB auto_increment=51 comment='低代码页面配置表';

INSERT INTO sys_page VALUES(1, 'sysPageConfig', '页面辅助配置', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "pageKey",
        "name": "页面标识",
        "fieldType": "Text"
      },
      {
        "key": "remark",
        "name": "页面说明",
        "fieldType": "Text"
      },
      {
        "key": "createBy",
        "name": "创建者",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "params[beginTime]",
          "params[endTime]"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      }
    ]
  },
  "tableCardConfig": {
    "selectionMode": "multiple",
    "tableRowKey": "pageId",
    "api": {
      "list": {
        "url": "/system/page/list",
        "method": "get",
        "successCode": 200,
        "axiosOptions": {
          "timeout": 20000
        }
      },
      "rmv": {
        "url": "/system/page",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableColumns": [
      {
        "key": "pageId",
        "name": "配置ID",
        "fieldType": "Number",
        "disabled": true
      },
      {
        "key": "pageKey",
        "name": "页面标识",
        "fieldType": "Text"
      },
      {
        "key": "remark",
        "name": "页面说明",
        "fieldType": "Text"
      },
      {
        "key": "createBy",
        "name": "创建者",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "DetailConfig",
            "name": "详情",
            "theme": "text"
          },
          {
            "key": "ModifyConfig",
            "name": "修改",
            "theme": "text",
            "filterMethod": "[''sysPageConfig'', ''sysPageHistory'', ''ZeroCodeConfig'', ''LowCodeNotice'', ''LowCodeOperationLog'', ''LowCodeDict'', ''LowCodeLandingLog'', ''LowCodeRoleManage'', ''LowCodeUserManage'', ''ZeroCodePosition'', ''LowCodeDepartManage'', ''LowCodeMenuManage'', ''LowCodeDictData'', ''ZeroCodeOnline''].indexOf(row.pageKey) &lt; 0"
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "filterMethod": "[''sysPageConfig'', ''sysPageHistory'', ''ZeroCodeConfig'', ''LowCodeNotice'', ''LowCodeOperationLog'', ''LowCodeDict'', ''LowCodeLandingLog'', ''LowCodeRoleManage'', ''LowCodeUserManage'', ''ZeroCodePosition'', ''LowCodeDepartManage'', ''LowCodeMenuManage'', ''LowCodeDictData'', ''ZeroCodeOnline''].indexOf(row.pageKey) &lt; 0"
          },
          {
            "key": "ModifyHistory",
            "name": "历史",
            "theme": "text"
          }
        ]
      }
    ],
    "tableHeaderBtns": [
      {
        "key": "AddConfig",
        "name": "新增",
        "theme": "primary"
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "relationSelectionMode": "multiple"
      }
    ],
    "disableSelectionRow": "function (row) {        if (row.pageId === 1 || row.pageId === 2) {          return true        }        return false;      }",
    "tableAttrs": {},
    "needTransformDataRows": false,
    "tableSize": "medium",
    "defaultSelectionValue": [],
    "pageType": "page"
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "method": "post",
        "url": "/system/page"
      },
      "mod": {
        "method": "put",
        "url": "/system/page"
      }
    },
    "editFields": [
      {
        "key": "pageKey",
        "name": "页面标识",
        "fieldType": "Text"
      },
      {
        "key": "remark",
        "name": "页面说明",
        "fieldType": "Text"
      },
      {
        "key": "paramJson",
        "name": "配置内容",
        "fieldType": "TextArea"
      }
    ],
    "rules": {
      "pageKey": [
        {
          "message": "请输入页面标识",
          "required": true,
          "trigger": "blur"
        }
      ],
      "paramJson": [
        {
          "message": "请输入页面配置内容",
          "required": true,
          "trigger": "blur"
        }
      ]
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(2, 'sysPageHistory', '页面配置修改记录', '{
  "tableCardConfig": {
    "api": {
      "list": {
        "url": "/system/page/record/list",
        "method": "get"
      }
    },
    "tableColumns": [
      {
        "key": "recordId",
        "name": "记录编号",
        "fieldType": "Number"
      },
      {
        "key": "pageKey",
        "name": "页面配置标识",
        "fieldType": "Text"
      },
      {
        "key": "updateTime",
        "name": "更新时间",
        "fieldType": "Text"
      },
      {
        "key": "updateType",
        "name": "更新类型",
        "fieldType": "Tag",
        "dict": "sys_oper_type"
      },
      {
        "key": "updateBy",
        "name": "更新者",
        "fieldType": "Text"
      },
      {
        "key": "version",
        "name": "版本",
        "fieldType": "Number"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "CompareItem",
            "name": "对比",
            "theme": "text"
          },
          {
            "key": "RollBack",
            "name": "回滚",
            "theme": "text"
          }
        ]
      }
    ],
    "tableRowKey": "recordId"
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(3, 'LowCodeUserManage', '用户管理-低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "userName",
        "name": "用户名称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入用户名称"
        }
      },
      {
        "key": "phonenumber",
        "name": "手机号码",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入手机号码"
        }
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_normal_disable",
        "attrs": {
          "placeholder": "用户状态"
        }
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "params[beginTime]",
          "params[endTime]"
        ],
        "attrs": {
          "type": "daterange",
          "value-format": "yyyy-MM-dd"
        }
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "userId",
    "selectionMode": "multiple",
    "api": {
      "list": {
        "url": "/system/user/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/user",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "/system/user/export",
        "method": "post",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:user:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "success",
        "relationSelectionMode": "single",
        "icon": "el-icon-edit",
        "permission": [
          "system:user:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "relationSelectionMode": "multiple",
        "icon": "el-icon-delete",
        "permission": [
          "system:user:remove"
        ]
      },
      {
        "key": "$$UploadData",
        "name": "导入",
        "theme": "info",
        "icon": "el-icon-upload2",
        "permission": [
          "system:user:import"
        ],
        "uploadConfig": {
          "title": "用户导入",
          "uploadTips": [
            "仅允许导入xls、xlsx格式文件。"
          ],
          "showDownloadFile": true,
          "showUpdateExistDataChoose": true,
          "downloadFileShowName": "下载模板",
          "downloadFileLink": "system/user/importTemplate",
          "uploadUrl": "/system/user/importData",
          "apiExtendKey": [
            "updateData:updateSupport"
          ]
        }
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "filename": "user",
        "icon": "el-icon-download",
        "permission": [
          "system:user:export"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "userId",
        "name": "用户编号",
        "fieldType": "Text"
      },
      {
        "key": "userName",
        "name": "用户名称",
        "fieldType": "Text"
      },
      {
        "key": "nickName",
        "name": "用户昵称",
        "fieldType": "Text"
      },
      {
        "key": "dept",
        "name": "部门",
        "fieldType": "Slot"
      },
      {
        "key": "phonenumber",
        "name": "手机号码",
        "fieldType": "Number"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Switch",
        "attrs": {
          "active-value": "0",
          "inactive-value": "1"
        }
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "attrs": {
          "align": "center",
          "fixed": "right"
        },
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:user:edit"
            ],
            "filterMethod": "row.userId !== 1"
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "system:user:remove"
            ],
            "filterMethod": "row.userId !== 1"
          },
          {
            "key": "MoreOp",
            "name": "更多",
            "theme": "text",
            "icon": "el-icon-d-arrow-right",
            "filterMethod": "row.userId !== 1",
            "children": [
              {
                "key": "ResetPassword",
                "name": "重置密码",
                "icon": "el-icon-key",
                "theme": "text",
                "permission": [
                  "system:user:resetPwd"
                ]
              },
              {
                "key": "AssignRole",
                "name": "分配角色",
                "icon": "el-icon-circle-check",
                "theme": "text",
                "permission": [
                  "system:user:edit"
                ]
              }
            ]
          }
        ]
      }
    ]
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/user",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/user",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "nickName",
        "name": "用户昵称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入用户昵称",
          "maxlength": 30
        }
      },
      {
        "key": "deptId",
        "name": "归属部门",
        "fieldType": "Slot"
      },
      {
        "key": "phonenumber",
        "name": "手机号码",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入手机号码",
          "maxlength": 11
        }
      },
      {
        "key": "email",
        "name": "邮箱",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入邮箱",
          "maxlength": 50
        }
      },
      {
        "key": "userName",
        "name": "用户名称",
        "fieldType": "Text",
        "isModifyHide": true,
        "attrs": {
          "placeholder": "请输入用户名称",
          "maxlength": 30
        }
      },
      {
        "key": "password",
        "name": "用户密码",
        "fieldType": "Password",
        "defaultValue": "",
        "attrs": {
          "showPassword": true,
          "placeholder": "请输入用户密码",
          "maxlength": 20,
          "autoComplete": "new-password"
        },
        "isModifyHide": true
      },
      {
        "key": "sex",
        "name": "用户性别",
        "fieldType": "Select",
        "dict": "sys_user_sex",
        "attrs": {
          "placeholder": "请选择性别"
        },
        "isExclusiveLine": false
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Radio",
        "dict": "sys_normal_disable",
        "defaultValue": "0"
      },
      {
        "key": "postIds",
        "name": "岗位",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/system/user/",
          "apiMethod": "get",
          "apiKeyPath": "posts",
          "apiLabelKey": "postName",
          "apiValueKey": "postId"
        },
        "attrs": {
          "multiple": true,
          "placeholder": "请选择岗位"
        }
      },
      {
        "key": "roleIds",
        "name": "角色",
        "fieldType": "Select",
        "valueOptions": [],
        "optionsApi": {
          "url": "/system/user/",
          "apiMethod": "get",
          "apiKeyPath": "roles",
          "apiLabelKey": "roleName",
          "apiValueKey": "roleId"
        },
        "attrs": {
          "multiple": true,
          "placeholder": "请选择角色"
        }
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "TextArea",
        "attrs": {
          "placeholder": "请输入内容",
          "rows": 5
        },
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "userName": [
        {
          "required": true,
          "message": "用户名称不能为空",
          "trigger": "blur"
        },
        {
          "min": 2,
          "max": 20,
          "message": "用户名称长度必须介于 2 和 20 之间",
          "trigger": "blur"
        }
      ],
      "nickName": [
        {
          "required": true,
          "message": "用户昵称不能为空",
          "trigger": "blur"
        }
      ],
      "password": [
        {
          "required": true,
          "message": "用户密码不能为空",
          "trigger": "blur"
        },
        {
          "min": 5,
          "max": 20,
          "message": "用户密码长度必须介于 5 和 20 之间",
          "trigger": "blur"
        }
      ],
      "email": [
        {
          "type": "email",
          "message": "请输入正确的邮箱地址",
          "trigger": "blur"
        }
      ],
      "phonenumber": [
        {
          "pattern": /^1[3|4|5|6|7|8|9][0-9]\\d{8}$/,
          "message": "请输入正确的手机号码",
          "trigger": "blur"
        }
      ]
    },
    "modifyApiDealKeys": [
      "*"
    ]
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(4, 'LowCodeRoleManage', '角色管理-低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "roleName",
        "name": "角色名称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入角色名称"
        }
      },
      {
        "key": "roleKey",
        "name": "权限字符",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入权限字符"
        }
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_normal_disable",
        "attrs": {
          "placeholder": "角色状态"
        }
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "params[beginTime]",
          "params[endTime]"
        ],
        "attrs": {
          "type": "daterange",
          "value-format": "yyyy-MM-dd"
        }
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "roleId",
    "selectionMode": "multiple",
    "api": {
      "list": {
        "url": "/system/role/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/role",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "system/role/export",
        "method": "post",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:role:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "success",
        "relationSelectionMode": "single",
        "icon": "el-icon-edit",
        "permission": [
          "system:role:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "relationSelectionMode": "multiple",
        "icon": "el-icon-delete",
        "permission": [
          "system:role:remove"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "role_",
        "permission": [
          "system:role:export"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "roleId",
        "name": "角色编号",
        "fieldType": "Number"
      },
      {
        "key": "roleName",
        "name": "角色名称",
        "fieldType": "Text"
      },
      {
        "key": "roleKey",
        "name": "权限字符",
        "fieldType": "Text"
      },
      {
        "key": "roleSort",
        "name": "显示顺序",
        "fieldType": "Number"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Switch",
        "attrs": {
          "active-value": "0",
          "inactive-value": "1"
        }
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "attrs": {
          "align": "center",
          "fixed": "right"
        },
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "filterMethod": "row.roleId !== 1",
            "icon": "el-icon-edit",
            "permission": [
              "system:role:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "filterMethod": "row.roleId !== 1",
            "icon": "el-icon-delete",
            "permission": [
              "system:role:remove"
            ]
          },
          {
            "key": "MoreOp",
            "name": "更多",
            "theme": "text",
            "icon": "el-icon-d-arrow-right",
            "children": [
              {
                "key": "DataPermissions",
                "name": "数据权限",
                "icon": "el-icon-key",
                "theme": "text",
                "permission": [
                  "system:role:edit"
                ]
              },
              {
                "key": "AssignUser",
                "name": "分配用户",
                "icon": "el-icon-user",
                "theme": "text",
                "permission": [
                  "system:role:edit"
                ]
              }
            ],
            "filterMethod": "row.roleId !== 1"
          }
        ]
      }
    ]
  },
  "editDialogConfig": {
    "modifyApiDealKeys": [
      "*"
    ],
    "api": {
      "add": {
        "url": "/system/role",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/role",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "roleName",
        "name": "角色名称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入角色名称"
        },
        "isExclusiveLine": true
      },
      {
        "key": "roleKey",
        "name": "权限字符",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入权限字符",
          "tips": "控制器中定义的权限字符，如：@PreAuthorize(````@ss.hasRole(''admin'')````)"
        },
        "isExclusiveLine": true
      },
      {
        "key": "roleSort",
        "name": "角色顺序",
        "fieldType": "Number",
        "defaultValue": "0",
        "isExclusiveLine": true
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Radio",
        "dict": "sys_normal_disable",
        "defaultValue": "0",
        "isExclusiveLine": true
      },
      {
        "key": "menuIds",
        "name": "菜单权限",
        "fieldType": "Slot",
        "isExclusiveLine": true
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "TextArea",
        "attrs": {
          "placeholder": "请输入内容",
          "rows": 5
        },
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "roleName": [
        {
          "required": true,
          "message": "角色名称不能为空",
          "trigger": "blur"
        }
      ],
      "roleKey": [
        {
          "required": true,
          "message": "权限字符不能为空",
          "trigger": "blur"
        }
      ],
      "roleSort": [
        {
          "required": true,
          "message": "角色顺序不能为空",
          "trigger": "blur"
        }
      ]
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(5, 'LowCodeMenuManage', '菜单管理-低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "menuName",
        "name": "菜单名称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入菜单名称"
        }
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_normal_disable",
        "attrs": {
          "placeholder": "请选择菜单状态"
        }
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "menuId",
    "tableAttrs": {
      "defaultExpandAll": false
    },
    "api": {
      "list": {
        "url": "/system/menu/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/menu",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:menu:add"
        ]
      },
      {
        "key": "FoldOrExpand",
        "name": "展开/折叠",
        "theme": "info",
        "icon": "el-icon-sort"
      }
    ],
    "tableColumns": [
      {
        "key": "menuName",
        "name": "菜单名称",
        "fieldType": "Text",
        "additionalWidth": "40"
      },
      {
        "key": "icon",
        "name": "图标",
        "fieldType": "SvgIcon"
      },
      {
        "key": "orderNum",
        "name": "排序",
        "fieldType": "Text"
      },
      {
        "key": "perms",
        "name": "权限标识",
        "fieldType": "Text"
      },
      {
        "key": "component",
        "name": "组件路径",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_normal_disable"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "编辑",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:menu:edit"
            ]
          },
          {
            "key": "AddItem",
            "name": "新增",
            "theme": "text",
            "icon": "el-icon-plus",
            "permission": [
              "system:menu:add"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "system:menu:remove"
            ]
          }
        ],
        "attrs": {
          "fixed": "right",
          "align": "center"
        }
      }
    ]
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/menu",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/menu",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "parentId",
        "name": "上级菜单",
        "fieldType": "Slot",
        "isExclusiveLine": true
      },
      {
        "key": "menuType",
        "name": "菜单类型",
        "fieldType": "Radio",
        "valueOptions": [
          {
            "label": "目录",
            "value": "M"
          },
          {
            "label": "菜单",
            "value": "C"
          },
          {
            "label": "按钮",
            "value": "F"
          }
        ],
        "isLabelValueOption": true,
        "defaultValue": "M",
        "isExclusiveLine": true
      },
      {
        "key": "icon",
        "name": "菜单图标",
        "fieldType": "Slot",
        "isExclusiveLine": true,
        "relations": [
          {
            "key": "menuType",
            "value": [
              "M",
              "C"
            ]
          }
        ]
      },
      {
        "key": "menuName",
        "name": "菜单名称",
        "fieldType": "Text"
      },
      {
        "key": "orderNum",
        "name": "显示排序",
        "fieldType": "Number"
      },
      {
        "key": "isFrame",
        "name": "是否外链",
        "fieldType": "Radio",
        "valueOptions": [
          {
            "label": "是",
            "value": "0"
          },
          {
            "label": "否",
            "value": "1"
          }
        ],
        "isLabelValueOption": true,
        "defaultValue": "1",
        "tips": "选择是外链则路由地址需要以````http(s)://````开头",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "M",
              "C"
            ]
          }
        ]
      },
      {
        "key": "path",
        "name": "路由地址",
        "fieldType": "Text",
        "tips": "访问的路由地址，如：````user````，如外网地址需内链访问则以````http(s)://````开头",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "M",
              "C"
            ]
          }
        ]
      },
      {
        "key": "isLowCode",
        "name": "完全低代码",
        "fieldType": "Radio",
        "valueOptions": [
          {
            "label": "是",
            "value": "0"
          },
          {
            "label": "否",
            "value": "1"
          }
        ],
        "relations": [
          {
            "key": "menuType",
            "value": [
              "C"
            ]
          }
        ],
        "isLabelValueOption": true,
        "defaultValue": "1"
      },
      {
        "key": "lowCodeCfgId",
        "name": "低代码配置",
        "fieldType": "Text",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "C"
            ]
          },
          {
            "key": "isLowCode",
            "value": [
              "0"
            ]
          }
        ]
      },
      {
        "key": "component",
        "name": "组件路径",
        "fieldType": "Text",
        "tips": "访问的组件路径，如：````system/user/index````，默认在````views````目录下",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "C"
            ]
          },
          {
            "key": "isLowCode",
            "value": [
              "1"
            ]
          }
        ]
      },
      {
        "key": "perms",
        "name": "权限字符",
        "fieldType": "Text",
        "tips": "控制器中定义的权限字符，如：@PreAuthorize(````@ss.hasPermi(''system:user:list'')````)",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "F",
              "C"
            ]
          }
        ]
      },
      {
        "key": "query",
        "name": "路由参数",
        "fieldType": "Text",
        "tips": "访问路由的默认传递参数，如：````{&quot;id&quot;: 1, &quot;name&quot;: &quot;ry&quot;}````",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "C"
            ]
          }
        ]
      },
      {
        "key": "isCache",
        "name": "是否缓存",
        "fieldType": "Radio",
        "valueOptions": [
          {
            "label": "缓存",
            "value": "0"
          },
          {
            "label": "不缓存",
            "value": "1"
          }
        ],
        "isLabelValueOption": true,
        "defaultValue": "0",
        "tips": "选择是则会被````keep-alive````缓存，需要匹配组件的````name````和地址保持一致",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "C"
            ]
          }
        ]
      },
      {
        "key": "visible",
        "name": "显示状态",
        "fieldType": "Radio",
        "dict": "sys_show_hide",
        "isLabelValueOption": true,
        "defaultValue": "0",
        "tips": "选择隐藏则路由将不会出现在侧边栏，但仍然可以访问",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "M",
              "C"
            ]
          }
        ]
      },
      {
        "key": "status",
        "name": "菜单状态",
        "fieldType": "Radio",
        "dict": "sys_normal_disable",
        "isLabelValueOption": true,
        "defaultValue": "0",
        "tips": "选择停用则路由将不会出现在侧边栏，也不能被访问",
        "relations": [
          {
            "key": "menuType",
            "value": [
              "M",
              "C"
            ]
          }
        ]
      },
      {
        "key": "customParams",
        "name": "自定义配置",
        "fieldType": "JsonText",
        "attrs": {
          "outputType": "text"
        },
        "relations": [
          {
            "key": "menuType",
            "value": [
              "M",
              "C"
            ]
          }
        ],
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "parentId": [],
      "menuType": [],
      "icon": [],
      "menuName": [
        {
          "required": true,
          "message": "菜单名称不能为空",
          "trigger": "blur"
        }
      ],
      "orderNum": [
        {
          "required": true,
          "message": "菜单顺序不能为空",
          "trigger": "blur"
        }
      ],
      "isFrame": [],
      "path": [
        {
          "required": true,
          "message": "路由地址不能为空",
          "trigger": "blur"
        }
      ],
      "isLowCode": [],
      "lowCodeCfgId": [
        {
          "required": true,
          "message": "页面配置标识不能为空",
          "trigger": "blur"
        }
      ],
      "component": [],
      "perms": [],
      "query": [],
      "isCache": [],
      "visible": [],
      "status": [],
      "customParams": []
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(6, 'LowCodeDepartManage', '部门管理-低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "deptName",
        "name": "部门名称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入部门名称"
        }
      },
      {
        "key": "delFlag",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_normal_disable",
        "attrs": {
          "placeholder": "部门状态"
        }
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "deptId",
    "tableAttrs": {
      "default-expand-all": true
    },
    "api": {
      "list": {
        "url": "/system/dept/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/dept",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus"
      },
      {
        "key": "FoldOrExpand",
        "name": "展开/折叠",
        "theme": "info",
        "icon": "el-icon-sort"
      }
    ],
    "tableColumns": [
      {
        "key": "deptName",
        "name": "部门名称",
        "fieldType": "Text"
      },
      {
        "key": "orderNum",
        "name": "排序",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_normal_disable"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:dept:edit"
            ]
          },
          {
            "key": "AddItem",
            "name": "新增",
            "theme": "text",
            "icon": "el-icon-plus",
            "permission": [
              "system:dept:add"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "filterMethod": "row.parentId !== 0",
            "permission": [
              "system:dept:remove"
            ]
          }
        ],
        "attrs": {
          "fixed": "right",
          "align": "center"
        }
      }
    ]
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/dept",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/dept",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "parentId",
        "name": "上级部门",
        "fieldType": "Slot",
        "isExclusiveLine": true
      },
      {
        "key": "deptName",
        "name": "部门名称",
        "fieldType": "Text"
      },
      {
        "key": "orderNum",
        "name": "显示排序",
        "fieldType": "Number"
      },
      {
        "key": "leader",
        "name": "负责人",
        "fieldType": "Text",
        "attrs": {
          "maxlength": 20
        }
      },
      {
        "key": "phone",
        "name": "联系电话",
        "fieldType": "Text",
        "attrs": {
          "maxlength": 11
        }
      },
      {
        "key": "email",
        "name": "邮箱",
        "fieldType": "Text",
        "attrs": {
          "maxlength": 50
        }
      },
      {
        "key": "status",
        "name": "部门状态",
        "fieldType": "Radio",
        "dict": "sys_normal_disable",
        "isLabelValueOption": true,
        "defaultValue": "0"
      }
    ],
    "rules": {
      "parentId": [
        {
          "required": true,
          "message": "上级部门不能为空",
          "trigger": "blur"
        }
      ],
      "deptName": [
        {
          "required": true,
          "message": "部门名称不能为空",
          "trigger": "blur"
        }
      ],
      "orderNum": [
        {
          "required": true,
          "message": "显示排序不能为空",
          "trigger": "blur"
        }
      ],
      "email": [
        {
          "type": "email",
          "message": "请输入正确的邮箱地址",
          "trigger": "blur"
        }
      ],
      "phone": [
        {
          "pattern": /^1[3|4|5|6|7|8|9][0-9]\\d{8}$/,
          "message": "请输入正确的手机号码",
          "trigger": "blur"
        }
      ]
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(7, 'ZeroCodePosition', '岗位管理-零代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "postCode",
        "name": "岗位编码",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入岗位编码"
        }
      },
      {
        "key": "postName",
        "name": "岗位名称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入岗位名称"
        }
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_normal_disable"
      }
    ]
  },
  "tableCardConfig": {
    "api": {
      "list": {
        "url": "/system/post/list",
        "method": "get",
        "successCode": 200
      },
      "export": {
        "url": "/system/post/export",
        "method": "post",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/post",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:post:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "primary",
        "icon": "el-icon-edit",
        "relationSelectionMode": "single",
        "permission": [
          "system:post:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "system:post:remove"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "post",
        "permission": [
          "system:post:export"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "postId",
        "name": "岗位编号",
        "fieldType": "Text"
      },
      {
        "key": "postCode",
        "name": "岗位编码",
        "fieldType": "Text"
      },
      {
        "key": "postName",
        "name": "岗位名称",
        "fieldType": "Text"
      },
      {
        "key": "postSort",
        "name": "岗位排序",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_normal_disable"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:post:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "system:post:remove"
            ]
          }
        ],
        "attrs": {
          "align": "center",
          "fixed": "right"
        }
      }
    ],
    "tableRowKey": "postId",
    "selectionMode": "multiple"
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/post",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/post",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "postName",
        "name": "岗位名称",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "postCode",
        "name": "岗位编码",
        "fieldType": "Text"
      },
      {
        "key": "postSort",
        "name": "岗位顺序",
        "fieldType": "Number",
        "isExclusiveLine": true
      },
      {
        "key": "status",
        "name": "岗位状态",
        "fieldType": "Radio",
        "dict": "sys_normal_disable",
        "isExclusiveLine": true
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "TextArea",
        "attrs": {
          "rows": 5
        },
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "postName": [
        {
          "required": true,
          "message": "岗位名称不能为空",
          "trigger": "blur"
        }
      ],
      "postCode": [
        {
          "required": true,
          "message": "岗位编码不能为空",
          "trigger": "blur"
        }
      ],
      "postSort": [
        {
          "required": true,
          "message": "岗位顺序不能为空",
          "trigger": "blur"
        }
      ]
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(8, 'LowCodeDict', '字典管理-低代码', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "dictName",
        "name": "字典名称",
        "fieldType": "Text"
      },
      {
        "key": "dictType",
        "name": "字典类型",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_normal_disable"
      },
      {
        "key": "dateRange",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "params[beginTime]",
          "params[endTime]"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      }
    ],
    "resetFormQueryRightNow": true
  },
  "tableCardConfig": {
    "tableRowKey": "dictId",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:dict:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "primary",
        "icon": "el-icon-edit",
        "relationSelectionMode": "single",
        "permission": [
          "system:dict:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "system:dict:remove"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "dict",
        "permission": [
          "system:dict:export"
        ]
      },
      {
        "key": "RefreshCache",
        "name": "刷新缓存",
        "theme": "warning",
        "icon": "el-icon-refresh",
        "permission": [
          "system:dict:remove"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "dictId",
        "name": "字典编号",
        "fieldType": "Text"
      },
      {
        "key": "dictName",
        "name": "字典名称",
        "fieldType": "Text"
      },
      {
        "key": "dictType",
        "name": "字典类型",
        "fieldType": "Link",
        "linkToKey": "$slot"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_normal_disable"
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:dict:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "system:dict:remove"
            ]
          }
        ]
      }
    ],
    "api": {
      "list": {
        "url": "/system/dict/type/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/dict/type",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "system/dict/type/export",
        "method": "post",
        "successCode": 200
      }
    }
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/dict/type",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/dict/type",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "dictName",
        "name": "字典名称",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "dictType",
        "name": "字典类型",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Radio",
        "valueOptions": [],
        "dict": "sys_normal_disable",
        "defaultValue": "0",
        "isExclusiveLine": true
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "TextArea",
        "attrs": {
          "rows": 5
        },
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "dictName": [
        {
          "required": true,
          "message": "字典名称不能为空",
          "trigger": "blur"
        }
      ],
      "dictType": [
        {
          "required": true,
          "message": "字典类型不能为空",
          "trigger": "blur"
        }
      ]
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(9, 'LowCodeDictData', '字典管理-数据项-低代码', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "dictType",
        "name": "字典名称",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/system/dict/type/optionselect",
          "apiLabelKey": "dictName",
          "apiValueKey": "dictType"
        },
        "attrs": {
          "clearable": false
        }
      },
      {
        "key": "dictLabel",
        "name": "字典标签",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_normal_disable"
      }
    ],
    "resetFormQueryRightNow": true
  },
  "tableCardConfig": {
    "tableRowKey": "dictCode",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:dict:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "primary",
        "icon": "el-icon-edit",
        "relationSelectionMode": "single",
        "permission": [
          "system:dict:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "system:dict:remove"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "dictdata",
        "permission": [
          "system:dict:export"
        ]
      },
      {
        "key": "DataClose",
        "name": "关闭",
        "theme": "warning",
        "icon": "el-icon-close",
        "permission": [
          "system:dict:remove"
        ],
        "attrs": {
          "plain": true
        }
      }
    ],
    "tableColumns": [
      {
        "key": "dictCode",
        "name": "字典编码",
        "fieldType": "Text"
      },
      {
        "key": "dictLabel",
        "replaceKey": "dictLabelClass",
        "name": "字典标签",
        "fieldType": "Tag"
      },
      {
        "key": "dictValue",
        "name": "字典键值",
        "fieldType": "Text"
      },
      {
        "key": "dictSort",
        "name": "字典排序",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_normal_disable"
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:dict:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "system:dict:remove"
            ]
          }
        ],
        "attrs": {
          "align": "center",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/system/dict/data/list",
        "method": "get",
        "successCode": 200,
        "rspAdapter": "function defaultRspAdaptor(rsp) {\\n        // 处理响应结果\\n        var rows = rsp.rows || [];\\n  rows.forEach(function(item) {\\n    item.dictLabelClass = [{\\n      type: item.listClass,\\n        name: item.dictLabel\\n    }]\\n  })\\n  return rsp;\\n}"
      },
      "rmv": {
        "url": "/system/dict/data",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "/system/dict/data/export",
        "method": "post",
        "successCode": 200
      }
    }
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/dict/data",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/dict/data",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "dictType",
        "name": "字典类型",
        "fieldType": "Text",
        "attrs": {
          "disabled": true
        },
        "isExclusiveLine": true
      },
      {
        "key": "dictLabel",
        "name": "数据标签",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "dictValue",
        "name": "数据键值",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "cssClass",
        "name": "样式属性",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "dictSort",
        "name": "显示排序",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "listClass",
        "name": "回显样式",
        "fieldType": "Select",
        "valueOptions": [
          {
            "value": "default",
            "label": "默认"
          },
          {
            "value": "primary",
            "label": "主要"
          },
          {
            "value": "success",
            "label": "成功"
          },
          {
            "value": "info",
            "label": "信息"
          },
          {
            "value": "warning",
            "label": "警告"
          },
          {
            "value": "danger",
            "label": "危险"
          }
        ],
        "isExclusiveLine": true
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Radio",
        "dict": "sys_normal_disable",
        "defaultValue": "0",
        "isExclusiveLine": true
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "TextArea",
        "attrs": {
          "rows": 5
        },
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "postName": [
        {
          "required": true,
          "message": "岗位名称不能为空",
          "trigger": "blur"
        }
      ],
      "postCode": [
        {
          "required": true,
          "message": "岗位编码不能为空",
          "trigger": "blur"
        }
      ],
      "postSort": [
        {
          "required": true,
          "message": "岗位顺序不能为空",
          "trigger": "blur"
        }
      ]
    },
    "modifyApiDealKeys": [
      "*"
    ]
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(10, 'ZeroCodeConfig', '参数设置-零代码', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "configName",
        "name": "参数名称",
        "fieldType": "Text"
      },
      {
        "key": "configKey",
        "name": "参数键名",
        "fieldType": "Text"
      },
      {
        "key": "configType",
        "name": "系统内置",
        "fieldType": "Select",
        "dict": "sys_yes_no"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "params[beginTime]",
          "params[endTime]"
        ],
        "attrs": {
          "type": "daterange",
          "value-format": "yyyy-MM-dd"
        }
      }
    ],
    "resetFormQueryRightNow": true
  },
  "tableCardConfig": {
    "tableRowKey": "configId",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:config:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "success",
        "relationSelectionMode": "single",
        "icon": "el-icon-edit",
        "permission": [
          "system:config:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "relationSelectionMode": "multiple",
        "icon": "el-icon-delete",
        "permission": [
          "system:config:remove"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "config",
        "permission": [
          "system:config:export"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "configId",
        "name": "参数主键",
        "fieldType": "Text"
      },
      {
        "key": "configName",
        "name": "参数名称",
        "fieldType": "Text"
      },
      {
        "key": "configKey",
        "name": "参数键名",
        "fieldType": "Text"
      },
      {
        "key": "configValue",
        "name": "参数键值",
        "fieldType": "Text"
      },
      {
        "key": "configType",
        "name": "系统内置",
        "fieldType": "Tag",
        "dict": "sys_yes_no"
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "attrs": {
          "align": "center",
          "fixed": "right"
        },
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:config:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "system:config:remove"
            ]
          }
        ]
      }
    ],
    "api": {
      "list": {
        "url": "/system/config/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/config",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "/system/config/export",
        "method": "post",
        "successCode": 200
      }
    }
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/config",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/config",
        "method": "put",
        "successCode": 200
      }
    },
    "modifyApiDealKeys": [],
    "editFields": [
      {
        "key": "configName",
        "name": "参数名称",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "configKey",
        "name": "参数键名",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "configValue",
        "name": "参数键值",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "configType",
        "name": "系统内置",
        "fieldType": "Radio",
        "dict": "sys_yes_no",
        "defaultValue": "Y",
        "isExclusiveLine": true
      },
      {
        "key": "remark",
        "name": "备注",
        "fieldType": "TextArea",
        "attrs": {
          "rows": 5
        },
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "configName": [
        {
          "required": true,
          "message": "参数名称不能为空",
          "trigger": "blur"
        }
      ],
      "configKey": [
        {
          "required": true,
          "message": "参数键名不能为空",
          "trigger": "blur"
        }
      ],
      "configValue": [
        {
          "required": true,
          "message": "参数键值不能为空",
          "trigger": "blur"
        }
      ]
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(11, 'LowCodeNotice', '通知公告-低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "noticeTitle",
        "name": "公告标题",
        "fieldType": "Text"
      },
      {
        "key": "createBy",
        "name": "操作人员",
        "fieldType": "Text"
      },
      {
        "key": "noticeType",
        "name": "类型",
        "fieldType": "Select",
        "dict": "sys_notice_type"
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "noticeId",
    "selectionMode": "multiple",
    "api": {
      "list": {
        "url": "/system/notice/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/system/notice",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "system:notice:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "primary",
        "icon": "el-icon-edit",
        "relationSelectionMode": "single",
        "permission": [
          "system:notice:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "system:notice:remove"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "noticeId",
        "name": "序号",
        "fieldType": "Text"
      },
      {
        "key": "noticeTitle",
        "name": "公告标题",
        "fieldType": "Text"
      },
      {
        "key": "noticeType",
        "name": "公告类型",
        "fieldType": "Tag",
        "dict": "sys_notice_type"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_notice_status"
      },
      {
        "key": "createBy",
        "name": "创建者",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "attrs": {
          "align": "center",
          "fixed": "right"
        },
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "system:notice:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "system:notice:remove"
            ]
          }
        ]
      }
    ]
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/notice",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/system/notice",
        "method": "put",
        "successCode": 200
      }
    },
    "modifyApiDealKeys": [],
    "editFields": [
      {
        "key": "noticeTitle",
        "name": "公告标题",
        "fieldType": "Text"
      },
      {
        "key": "noticeType",
        "name": "公告类型",
        "fieldType": "Select",
        "dict": "sys_notice_type"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Radio",
        "dict": "sys_notice_status",
        "isExclusiveLine": true
      },
      {
        "key": "noticeContent",
        "name": "内容",
        "fieldType": "Slot",
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "noticeTitle": [
        {
          "required": true,
          "message": "公告标题不能为空",
          "trigger": "blur"
        }
      ],
      "noticeType": [
        {
          "required": true,
          "message": "公告类型不能为空",
          "trigger": "change"
        }
      ]
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(12, 'LowCodeOperationLog', '操作日志-低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "title",
        "name": "系统模块",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入系统模块"
        }
      },
      {
        "key": "operName",
        "name": "操作人员",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入操作人员"
        }
      },
      {
        "key": "businessType",
        "name": "类型",
        "fieldType": "Select",
        "dict": "sys_oper_type",
        "attrs": {
          "placeholder": "请选择操作类型"
        }
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_common_status",
        "attrs": {
          "placeholder": "请选择操作状态"
        }
      },
      {
        "key": "dateRange",
        "name": "操作时间",
        "fieldType": "Date",
        "paramKeys": [
          "params[beginTime]",
          "params[endTime]"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "operId",
    "selectionMode": "multiple",
    "tableAttrs": {
      "defaultSort": {
        "prop": "operTime",
        "order": "descending"
      }
    },
    "api": {
      "list": {
        "url": "/monitor/operlog/list",
        "method": "get",
        "successCode": 200,
        "reqAdapter": "function defaultReqAdaptor(req) {\\n  // 处理请求参数\\n  if (req.prop) {\\n    var map = {};\\n    Object.keys(req).forEach(function(key) {\\n      if (key === ''prop'') {\\n        map.orderByColumn = req[key];\\n      } else if (key === ''order'') {\\n        map.isAsc = req[key];\\n      } else {\\n        map[key] = req[key];\\n      }\\n    })\\n    return map;\\n  }\\n  return req;\\n}"
      },
      "rmv": {
        "url": "/monitor/operlog",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "monitor/operlog/export",
        "method": "get",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "monitor:operlog:remove"
        ]
      },
      {
        "key": "clearAll",
        "name": "清空",
        "theme": "danger",
        "icon": "el-icon-delete",
        "permission": [
          "monitor:operlog:remove"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "operlog",
        "permission": [
          "monitor:operlog:export"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "operId",
        "name": "日志编号",
        "fieldType": "Text"
      },
      {
        "key": "title",
        "name": "系统模块",
        "fieldType": "Text"
      },
      {
        "key": "businessType",
        "name": "操作类型",
        "fieldType": "Tag",
        "dict": "sys_oper_type"
      },
      {
        "key": "requestMethod",
        "name": "请求方式",
        "fieldType": "Text"
      },
      {
        "key": "operName",
        "name": "操作人员",
        "fieldType": "Text",
        "additionalWidth": "12",
        "attrs": {
          "sortable": "custom",
          "sort-orders": [
            "descending",
            "ascending"
          ]
        }
      },
      {
        "key": "operIp",
        "name": "操作地址",
        "fieldType": "Text"
      },
      {
        "key": "operLocation",
        "name": "操作地点",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "操作状态",
        "fieldType": "Tag",
        "dict": "sys_common_status"
      },
      {
        "key": "operTime",
        "name": "操作日期",
        "fieldType": "Text",
        "additionalWidth": "12",
        "attrs": {
          "sortable": "custom",
          "sort-orders": [
            "descending",
            "ascending"
          ]
        }
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "attrs": {
          "fixed": "right",
          "align": "center"
        },
        "btnList": [
          {
            "key": "DetailItem",
            "name": "详情",
            "theme": "text",
            "icon": "el-icon-view",
            "permission": [
              "monitor:operlog:query"
            ]
          }
        ]
      }
    ]
  },
  "editDialogConfig": {
    "splitNumber": "2",
    "editFields": [
      {
        "key": "title",
        "name": "操作模块",
        "fieldType": "Text"
      },
      {
        "key": "operName",
        "name": "登录信息",
        "fieldType": "Slot"
      },
      {
        "key": "operUrl",
        "name": "请求地址",
        "fieldType": "Text"
      },
      {
        "key": "requestMethod",
        "name": "请求方式",
        "fieldType": "Text"
      },
      {
        "key": "method",
        "name": "操作方法",
        "fieldType": "Text",
        "isExclusiveLine": 1
      },
      {
        "key": "operParam",
        "name": "请求参数",
        "fieldType": "TextArea",
        "isExclusiveLine": 1
      },
      {
        "key": "jsonResult",
        "name": "返回参数",
        "fieldType": "Text",
        "isExclusiveLine": 1
      },
      {
        "key": "status",
        "name": "操作状态",
        "fieldType": "Select",
        "dict": "sys_common_status"
      },
      {
        "key": "operTime",
        "name": "操作时间",
        "fieldType": "Text"
      },
      {
        "key": "errorMsg",
        "name": "异常信息",
        "fieldType": "Text",
        "relations": [
          {
            "key": "status",
            "value": [
              1,
              "1"
            ]
          }
        ],
        "isExclusiveLine": 1
      }
    ],
    "rules": {
      "title": [],
      "operName": [],
      "operUrl": [],
      "requestMethod": [],
      "method": [],
      "operParam": [],
      "jsonResult": [],
      "status": [],
      "operTime": [],
      "errorMsg": []
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(13, 'LowCodeLandingLog', '登录日志-低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "ipaddr",
        "name": "登录地址",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入登录地址"
        }
      },
      {
        "key": "userName",
        "name": "用户名称",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入用户名称"
        }
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Select",
        "dict": "sys_common_status",
        "attrs": {
          "placeholder": "请选择登录状态"
        }
      },
      {
        "key": "dateRange",
        "name": "登录时间",
        "fieldType": "Date",
        "paramKeys": [
          "params[beginTime]",
          "params[endTime]"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "infoId",
    "selectionMode": "multiple",
    "tableAttrs": {
      "defaultSort": {
        "prop": "loginTime",
        "order": "descending"
      }
    },
    "api": {
      "list": {
        "url": "/monitor/logininfor/list",
        "method": "get",
        "successCode": 200,
        "reqAdapter": "function defaultReqAdaptor(req) {\\n  // 处理请求参数\\n  if (req.prop) {\\n    var map = {};\\n    Object.keys(req).forEach(function(key) {\\n      if (key === ''prop'') {\\n        map.orderByColumn = req[key];\\n      } else if (key === ''order'') {\\n        map.isAsc = req[key];\\n      } else {\\n        map[key] = req[key];\\n      }\\n    })\\n    return map;\\n  }\\n  return req;\\n}"
      },
      "rmv": {
        "url": "/monitor/logininfor",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "monitor/logininfor/export",
        "method": "get",
        "successCode": 200
      }
    },
    "tableHeaderBtns": [
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "monitor:logininfor:remove"
        ]
      },
      {
        "key": "clearAll",
        "name": "清空",
        "theme": "danger",
        "icon": "el-icon-delete",
        "permission": [
          "monitor:logininfor:remove"
        ]
      },
      {
        "key": "Unlock",
        "name": "解锁",
        "theme": "primary",
        "icon": "el-icon-unlock",
        "relationSelectionMode": "single",
        "permission": [
          "monitor:logininfor:unlock"
        ]
      },
      {
        "key": "ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "logininfr",
        "permission": [
          "monitor:logininfor:export"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "infoId",
        "name": "访问编号",
        "fieldType": "Text"
      },
      {
        "key": "userName",
        "name": "用户名称",
        "fieldType": "Text",
        "additionalWidth": "12",
        "attrs": {
          "sortable": "custom",
          "sort-orders": [
            "descending",
            "ascending"
          ]
        }
      },
      {
        "key": "ipaddr",
        "name": "登录地址",
        "fieldType": "Text"
      },
      {
        "key": "loginLocation",
        "name": "登录地点",
        "fieldType": "Text",
        "maxWidth": 320,
        "attrs": {
          "showOverflowTooltip": true
        }
      },
      {
        "key": "browser",
        "name": "浏览器",
        "fieldType": "Text"
      },
      {
        "key": "os",
        "name": "操作系统",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "登录状态",
        "fieldType": "Tag",
        "dict": "sys_common_status"
      },
      {
        "key": "msg",
        "name": "操作信息",
        "fieldType": "Text"
      },
      {
        "key": "loginTime",
        "name": "登录日期",
        "fieldType": "Text",
        "additionalWidth": "12",
        "attrs": {
          "sortable": "custom",
          "sort-orders": [
            "descending",
            "ascending"
          ]
        }
      }
    ]
  },
  "editDialogConfig": {}
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(14, 'ZeroCodeOnline', '在线用户 - 零代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "ipaddr",
        "name": "登录地址",
        "fieldType": "Text"
      },
      {
        "key": "userName",
        "name": "用户名称",
        "fieldType": "Text"
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "tokenId",
    "selectionMode": "none",
    "api": {
      "list": {
        "url": "/monitor/online/list",
        "method": "get",
        "successCode": 200,
        "rspAdapter": "function defaultRspAdaptor(rsp, req) {\\n  // 处理响应结果\\n  var rows = rsp.rows || [];\\n  rows.forEach(function(item, index) {\\n    item.sn = index + 1 + req.pageSize * (req.pageNum - 1);\\n  })\\n  return rsp;\\n}"
      },
      "rmv": {
        "url": "/monitor/online",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableColumns": [
      {
        "key": "sn",
        "name": "序号",
        "fieldType": "Text"
      },
      {
        "key": "tokenId",
        "name": "会话编号",
        "fieldType": "Text"
      },
      {
        "key": "userName",
        "name": "登录名称",
        "fieldType": "Text"
      },
      {
        "key": "deptName",
        "name": "部门名称",
        "fieldType": "Text"
      },
      {
        "key": "ipaddr",
        "name": "主机",
        "fieldType": "Text"
      },
      {
        "key": "loginLocation",
        "name": "登录地点",
        "fieldType": "Text"
      },
      {
        "key": "browser",
        "name": "浏览器",
        "fieldType": "Text"
      },
      {
        "key": "os",
        "name": "操作系统",
        "fieldType": "Text"
      },
      {
        "key": "loginTime",
        "name": "登录时间",
        "fieldType": "Text",
        "timeFormat": "{y}-{m}-{d} {h}:{i}:{s}"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "DeleteItem",
            "name": "强退",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "monitor:online:forceLogout"
            ]
          }
        ],
        "attrs": {
          "align": "center",
          "fixed": "right"
        }
      }
    ]
  },
  "editDialogConfig": {}
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(15, 'LowCodeJob', '定时任务 - 低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "jobName",
        "name": "任务名称",
        "fieldType": "Text"
      },
      {
        "key": "jobGroup",
        "name": "任务组名",
        "fieldType": "Select",
        "dict": "sys_job_group"
      },
      {
        "key": "status",
        "name": "任务状态",
        "fieldType": "Select",
        "dict": "sys_job_status"
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "jobId",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary",
        "icon": "el-icon-plus",
        "permission": [
          "monitor:job:add"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "primary",
        "icon": "el-icon-edit",
        "relationSelectionMode": "single",
        "permission": [
          "monitor:job:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "monitor:job:remove"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "导出",
        "theme": "warning",
        "icon": "el-icon-download",
        "filename": "job",
        "permission": [
          "monitor:job:export"
        ]
      },
      {
        "key": "$$ExportData",
        "name": "日志",
        "theme": "info",
        "icon": "el-icon-s-operation",
        "permission": [
          "monitor:job:query"
        ],
        "attrs": {
          "plain": true
        }
      }
    ],
    "tableColumns": [
      {
        "key": "jobId",
        "name": "任务编号",
        "fieldType": "Text"
      },
      {
        "key": "jobName",
        "name": "任务名称",
        "fieldType": "Text"
      },
      {
        "key": "jobGroup",
        "name": "任务组名",
        "fieldType": "Tag",
        "dict": "sys_job_group"
      },
      {
        "key": "invokeTarget",
        "name": "调用目标字符串",
        "fieldType": "Text"
      },
      {
        "key": "cronExpression",
        "name": "cron执行表达式",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Switch",
        "attrs": {
          "active-value": "0",
          "inactive-value": "1"
        }
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "monitor:job:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "monitor:job:remove"
            ]
          },
          {
            "key": "More",
            "name": "更多",
            "theme": "text",
            "icon": "el-icon-d-arrow-right",
            "permission": [
              "monitor:job:changeStatus",
              "monitor:job:query"
            ],
            "children": [
              {
                "key": "HandleRunOne",
                "name": "执行一次",
                "theme": "text",
                "icon": "el-icon-caret-right",
                "permission": [
                  "monitor:job:changeStatus"
                ]
              },
              {
                "key": "HandleView",
                "name": "任务详细",
                "theme": "text",
                "icon": "el-icon-view",
                "permission": [
                  "monitor:job:query"
                ]
              },
              {
                "key": "HandleJobLog",
                "name": "调度日志",
                "theme": "text",
                "icon": "el-icon-s-operation",
                "permission": [
                  "monitor:job:query"
                ]
              }
            ]
          }
        ],
        "attrs": {
          "align": "center",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/monitor/job/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/monitor/job",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "/monitor/job/export",
        "method": "post",
        "successCode": 200
      }
    }
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/monitor/job",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/monitor/job",
        "method": "put",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "jobName",
        "name": "任务名称",
        "fieldType": "Text"
      },
      {
        "key": "jobGroup",
        "name": "任务分组",
        "fieldType": "Select",
        "dict": "sys_job_group"
      },
      {
        "key": "invokeTarget",
        "name": "调用方法",
        "fieldType": "Text",
        "tips": "Bean调用示例：ryTask.ryParams(''ry'')Class类调用示例：RyTask.ryParams(''ry'')参数说明：支持字符串，布尔类型，长整型，浮点型，整型",
        "isExclusiveLine": true
      },
      {
        "key": "cronExpression",
        "name": "cron表达式",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "misfirePolicy",
        "name": "执行策略",
        "fieldType": "Radio",
        "defaultValue": "1",
        "valueOptions": [
          {
            "label": "立即执行",
            "value": "1"
          },
          {
            "label": "执行一次",
            "value": "2"
          },
          {
            "label": "放弃执行",
            "value": "3"
          }
        ],
        "isExclusiveLine": true
      },
      {
        "key": "concurrent",
        "name": "是否并发",
        "fieldType": "Radio",
        "defaultValue": "1",
        "valueOptions": [
          {
            "value": "0",
            "label": "允许"
          },
          {
            "value": "1",
            "label": "禁止"
          }
        ]
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Radio",
        "dict": "sys_job_status",
        "defaultValue": "0"
      }
    ],
    "rules": {
      "jobName": [
        {
          "required": true,
          "message": "任务名称不能为空",
          "trigger": "blur"
        }
      ],
      "invokeTarget": [
        {
          "required": true,
          "message": "调用目标字符串不能为空",
          "trigger": "blur"
        }
      ],
      "cronExpression": [
        {
          "required": true,
          "message": "cron执行表达式不能为空",
          "trigger": "blur"
        }
      ]
    },
    "modifyApiDealKeys": [
      "*"
    ]
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(16, 'LowCodeGen', '代码生成 - 低代码', '{
  "queryCardConfig": {
    "resetFormQueryRightNow": true,
    "queryFields": [
      {
        "key": "tableName",
        "name": "表名称",
        "fieldType": "Select"
      },
      {
        "key": "tableComment",
        "name": "表描述",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "创建时间",
        "fieldType": "Select",
        "dict": "sys_normal_disable"
      }
    ]
  },
  "tableCardConfig": {
    "tableRowKey": "tableId",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "GenTable",
        "name": "生成",
        "theme": "primary",
        "icon": "el-icon-download",
        "permission": [
          "tool:gen:code"
        ]
      },
      {
        "key": "TopModifyItem",
        "name": "导入",
        "theme": "primary",
        "icon": "el-icon-edit",
        "permission": [
          "tool:gen:import"
        ]
      },
      {
        "key": "ModifyItem",
        "name": "修改",
        "theme": "success",
        "icon": "el-icon-edit",
        "relationSelectionMode": "single",
        "permission": [
          "tool:gen:edit"
        ]
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "icon": "el-icon-delete",
        "relationSelectionMode": "multiple",
        "permission": [
          "tool:gen:remove"
        ]
      }
    ],
    "tableColumns": [
      {
        "key": "sn",
        "name": "序号",
        "fieldType": "Text"
      },
      {
        "key": "tableName",
        "name": "表名称",
        "fieldType": "Text"
      },
      {
        "key": "dictValue",
        "name": "表描述",
        "fieldType": "Text"
      },
      {
        "key": "tableComment",
        "name": "字典排序",
        "fieldType": "Text"
      },
      {
        "key": "className",
        "name": "实体",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "updateTime",
        "name": "更新时间",
        "fieldType": "Text"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "PreviewItem",
            "name": "预览",
            "theme": "text",
            "icon": "el-icon-view",
            "permission": [
              "tool:gen:preview"
            ]
          },
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "icon": "el-icon-edit",
            "permission": [
              "tool:gen:edit"
            ]
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "icon": "el-icon-delete",
            "permission": [
              "tool:gen:remove"
            ]
          },
          {
            "key": "SynchDbItem",
            "name": "同步",
            "theme": "text",
            "icon": "el-icon-refresh",
            "permission": [
              "tool:gen:edit"
            ]
          },
          {
            "key": "$$ExportData",
            "name": "生成代码",
            "theme": "text",
            "icon": "el-icon-download",
            "permission": [
              "tool:gen:code"
            ]
          }
        ],
        "attrs": {
          "align": "center",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/tool/gen/list",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/tool/gen",
        "method": "delete",
        "successCode": 200
      },
      "export": {
        "url": "/tool/gen/export",
        "method": "post",
        "successCode": 200
      }
    }
  }
}', 'admin', sysdate());
INSERT INTO sys_page VALUES(17, 'CommData', '通用数据操作配置', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "data_key",
        "name": "数据对象",
        "fieldType": "Select",
        "optionsApi": {
          "type": "api",
          "url": "/common/data/list/CommData_DataKey",
          "apiLabelKey": "data_key",
          "apiValueKey": "data_key",
          "apiKeyPath": "rows",
          "apiMethod": "get"
        }
      }
    ]
  },
  "tableCardConfig": {
    "tableColumns": [
      {
        "key": "data_key",
        "name": "数据对象",
        "fieldType": "Text"
      },
      {
        "key": "op_type",
        "name": "操作类型"
      },
      {
        "key": "op_role",
        "name": "操作角色",
        "fieldType": "Text"
      },
      {
        "key": "create_by",
        "name": "创建者"
      },
      {
        "key": "create_time",
        "name": "创建时间"
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "编辑",
            "theme": "text"
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text"
          }
        ]
      }
    ],
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary"
      }
    ],
    "tableRowKey": "id",
    "selectionMode": "none",
    "needTransformDataRows": false,
    "tableSize": "medium",
    "defaultSelectionValue": [],
    "pageType": "page",
    "tableAttrs": {}
  },
  "editDialogConfig": {
    "editFields": [
      {
        "key": "data_key",
        "name": "数据对象",
        "fieldType": "Text"
      },
      {
        "key": "op_type",
        "name": "操作类型",
        "fieldType": "Select",
        "valueOptions": [
          {
            "label": "LIST",
            "value": "LIST"
          },
          {
            "label": "UPDATE",
            "value": "UPDATE"
          },
          {
            "label": "ADD",
            "value": "ADD"
          },
          {
            "label": "DELETE",
            "value": "DELETE"
          }
        ]
      },
      {
        "key": "op_role",
        "name": "操作角色",
        "fieldType": "Select",
        "valueOptions": [],
        "optionsApi": {
          "type": "api",
          "url": "/common/data/list/CommData_Role",
          "apiLabelKey": "role_name",
          "apiValueKey": "role_id",
          "apiKeyPath": "rows",
          "apiMethod": "get"
        },
        "attrs": {
          "multiple": true,
          "outputType": "text"
        }
      },
      {
        "key": "sql",
        "name": "SQL脚本",
        "fieldType": "JsonText",
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "data_key": [],
      "op_type": [],
      "op_role": [],
      "sql": []
    }
  }
}', 'admin', sysdate());

-- ----------------------------
-- 22、低代码页面配置修改记录表
-- ----------------------------
drop table if exists sys_page_record;
create table sys_page_record (
  record_id         bigint(20)            not null auto_increment,
  page_key          varchar(100)          not null                  comment '页面配置标识',
  param_json        text                                            comment 'JSON配置',
  update_by         varchar(64)           default ''                comment '更新者',
  update_time       datetime                                        comment '更新时间',
  update_type       int(1)                default 1                 comment '更新类型: 1-新增 | 2-修改 | 3-删除',
  version           int(4)                default 0                 comment '版本号',
  primary key (record_id)
) engine=InnoDB auto_increment=1 comment='低代码页面配置表';