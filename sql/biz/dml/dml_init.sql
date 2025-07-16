INSERT INTO tfs.sys_common_data
(id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time)
VALUES(30, 'CommData', 'LIST', '', '', 'SELECT * FROM sys_common_data WHERE 1=1 [AND data_key={data_key}] ORDER BY data_key', '[]', 'admin', '2024-05-17 09:39:00', NULL, NULL);
INSERT INTO tfs.sys_common_data
(id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time)
VALUES(31, 'CommData_DataKey', 'LIST', '', '', 'SELECT distinct data_key FROM sys_common_data', NULL, 'admin', '2024-05-17 09:39:00', NULL, NULL);
INSERT INTO tfs.sys_common_data
(id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time)
VALUES(32, 'CommData_Role', 'LIST', '', '', 'SELECT role_name,role_id FROM sys_role', '[]', 'admin', '2024-05-17 09:39:00', NULL, NULL);
INSERT INTO tfs.sys_common_data
(id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time)
VALUES(33, 'CommData', 'ADD', '', '', 'INSERT INTO sys_common_data(data_key,op_type,`sql`,op_role,create_by,create_time) values({data_key},{op_type},{sql},{op_role},{sys.user_name},NOW())', '[1]', 'admin', '2024-05-17 09:39:00', NULL, NULL);
INSERT INTO tfs.sys_common_data
(id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time)
VALUES(35, 'CommData', 'DELETE', '', '', 'DELETE FROM sys_common_data WHERE id={id}', '[1]', 'admin', '2024-05-17 09:39:00', NULL, NULL);
INSERT INTO tfs.sys_common_data
(id, data_key, op_type, pri_key, auto_sql_table, `sql`, op_role, create_by, create_time, update_by, update_time)
VALUES(37, 'CommData', 'UPDATE', '', '', 'UPDATE sys_common_data SET data_key={data_key},op_type={op_type}[,`sql`={sql}][,op_role={op_role}],update_by={sys.user_name},update_time=NOW() WHERE id={id}', '[1]', 'admin', '2024-05-17 09:39:00', NULL, NULL);



INSERT INTO tfs.sys_config
(config_id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2024-05-17 09:39:00', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO tfs.sys_config
(config_id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES(2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2024-05-17 09:39:00', '', NULL, '初始化密码 123456');
INSERT INTO tfs.sys_config
(config_id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES(3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2024-05-17 09:39:00', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO tfs.sys_config
(config_id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES(4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'N', 'admin', '2024-05-17 09:39:00', 'admin', '2024-05-21 20:02:41', '是否开启验证码功能（true开启，false关闭）');
INSERT INTO tfs.sys_config
(config_id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES(5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2024-05-17 09:39:00', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO tfs.sys_config
(config_id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES(6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2024-05-17 09:39:00', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');



INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(100, 0, '0', '萨摩耶数字科技', 0, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(101, 100, '0,100', '金融云', 1, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(102, 100, '0,100', '产业云', 2, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(103, 101, '0,100,101', '研发部', 1, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(104, 101, '0,100,101', '运营部', 2, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(105, 101, '0,100,101', '用户增长部', 3, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(106, 101, '0,100,101', '战略合作部', 4, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(107, 101, '0,100,101', '风险管理部', 5, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(108, 102, '0,100,102', '跨境研发部', 1, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);
INSERT INTO tfs.sys_dept
(dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES(109, 102, '0,100,102', '跨境市场部', 2, '', '', '', '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL);


INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '性别男');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '性别女');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '性别未知');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '显示菜单');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '隐藏菜单');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '正常状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '停用状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '正常状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '停用状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '默认分组');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '系统分组');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '系统默认是');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '系统默认否');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '通知');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '公告');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '正常状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '关闭状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '其他操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '新增操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '修改操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '删除操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '授权操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '导出操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '导入操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '强退操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '生成操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '清空操作');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '正常状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '停用状态');
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(100, 1, '单行文本', 'INPUT', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:13:16', 'admin', '2024-04-29 22:11:22', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(101, 2, '多行文本', 'TEXTAREA', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:13:54', 'admin', '2024-04-29 22:40:04', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(102, 3, '数字', 'INPUTNUMBER', 'template_form_item_type', '', NULL, 'N', '0', 'admin', '2024-04-26 19:14:10', 'admin', '2024-04-29 22:40:33', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(103, 4, '单选', 'SELECT', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:14:24', 'admin', '2024-04-29 22:40:50', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(104, 5, '多选', 'SELECTMULTIPLE', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:14:37', 'admin', '2024-04-29 22:41:18', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(105, 6, '日期', 'TIME', 'template_form_item_type', 'Time', NULL, 'N', '0', 'admin', '2024-04-26 19:15:27', 'admin', '2024-04-29 22:41:29', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(106, 7, '日期区间', 'TIMESPAN', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:15:40', 'admin', '2024-04-29 22:41:38', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(107, 8, '图片', 'PICTURE', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:15:53', 'admin', '2024-04-29 22:41:49', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(108, 9, '附件', 'FILE', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:16:04', 'admin', '2024-04-29 22:41:56', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(109, 10, '说明', 'TIP', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:16:35', 'admin', '2024-04-29 22:42:06', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(110, 11, '明细', 'Group', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-26 19:17:16', 'admin', '2024-04-26 19:18:55', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(111, 1, '初始化', 'INIT', 'ticket_data_status', NULL, 'primary', 'N', '1', 'admin', '2024-04-27 10:20:47', 'admin', '2024-05-17 09:34:50', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(112, 2, '草稿中', 'DRAFT', 'ticket_data_status', NULL, 'info', 'N', '1', 'admin', '2024-04-27 10:20:58', 'admin', '2024-05-21 19:47:19', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(113, 3, '审批中', 'APPLYING', 'ticket_data_status', NULL, 'default', 'N', '0', 'admin', '2024-04-27 10:21:06', 'admin', '2024-04-30 14:33:19', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(114, 4, '审批结束', 'APPLY_END', 'ticket_data_status', NULL, 'success', 'N', '0', 'admin', '2024-04-27 10:21:14', 'admin', '2024-04-30 14:31:54', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(115, 5, '审批驳回', 'REJECT', 'ticket_data_status', NULL, 'danger', 'N', '0', 'admin', '2024-04-27 10:21:20', 'admin', '2024-04-30 14:32:15', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(116, 6, '撤回', 'WITHDRAW', 'ticket_data_status', NULL, 'warning', 'N', '0', 'admin', '2024-04-27 10:21:28', 'admin', '2024-04-30 14:32:39', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(117, 1, '初始化', 'INIT', 'ticket_template_status', NULL, 'primary', 'N', '1', 'admin', '2024-04-27 11:19:11', 'admin', '2024-05-21 15:37:38', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(118, 2, '暂停', 'PAUSE', 'ticket_template_status', NULL, 'warning', 'N', '0', 'admin', '2024-04-27 11:19:19', 'admin', '2024-04-30 14:31:28', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(119, 3, '启用中', 'ENABLE', 'ticket_template_status', NULL, 'success', 'N', '0', 'admin', '2024-04-27 11:19:31', 'admin', '2024-04-30 14:31:23', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(120, 4, '作废', 'CANCEL', 'ticket_template_status', NULL, 'danger', 'N', '0', 'admin', '2024-04-27 11:19:39', 'admin', '2024-04-30 14:31:10', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(121, 1, '审批通过', 'PASS', 'approve_deal_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 17:40:53', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(122, 2, '驳回工单', 'REJECT', 'approve_deal_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 17:41:17', 'admin', '2024-04-29 17:41:25', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(123, 0, '驳回到上一个节点', 'REJECT_PRE', 'approve_deal_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 17:41:42', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(124, 0, '会签', 'AND', 'audited_method', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:06:11', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(125, 1, '或签', 'OR', 'audited_method', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:06:29', 'admin', '2024-04-29 22:06:35', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(126, 0, '人工审核', 'BY_USER', 'audited_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:07:53', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(127, 0, '自动通过', 'AUTO_PASS', 'audited_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:08:04', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(128, 0, '自动拒绝', 'AUTO_REJECT', 'audited_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:08:18', 'admin', '2024-04-29 22:08:34', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(129, 0, '初始化', 'INIT', 'event_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:09:51', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(130, 0, '待执行', 'WAIT_EXECUTE', 'event_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:10:03', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(131, 0, '执行成功（终态）', 'EXECUTE_SUCCESS_FINAL', 'event_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:10:29', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(132, 0, '执行失败，待执行', 'EXECUTE_FAILURE_MIDDLE', 'event_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:10:44', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(133, 0, '执行失败', 'EXECUTE_FAILURE', 'event_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:10:55', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(134, 0, 'http', 'HTTP_SERVICE', 'event_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:13:26', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(135, 0, 'https', 'HTTPS_SERVICE', 'event_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:13:43', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(136, 0, 'dubbo', 'DUBBO_SERVICE', 'event_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:13:56', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(137, 0, '执行前', 'BEFORE', 'execute_step', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:15:36', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(139, 0, '通过后执行', 'DONE_PASS', 'execute_step', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:15:59', 'admin', '2024-05-11 16:36:48', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(140, 0, '拒绝后执行', 'DONE_REJECT', 'execute_step', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:16:11', 'admin', '2024-05-11 16:36:55', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(141, 0, '完成后执行', 'FINISH', 'execute_step', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:16:23', 'admin', '2024-05-11 16:37:00', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(142, 0, '指定成员数组', 'APPLY_MEMBER_LIST', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:18:46', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(143, 0, '用户组', 'APPLY_GROUP', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:28:31', 'admin', '2024-05-07 15:28:53', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(144, 0, '申请人上级', 'APPLY_LEADER', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:28:42', 'admin', '2024-05-07 15:43:06', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(145, 0, '申请人', 'APPLY_SELF', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:28:53', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(146, 0, '同意时抄送人-上级', 'CA_LEADER', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:29:09', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(147, 0, '同意时抄送人-用户组', 'CA_GROUP', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:29:24', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(148, 0, '同意时抄送人-指定成员数组', 'CA_MEMBER_LIST', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:29:37', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(149, 0, '同意时抄送人-提交本人', 'CA_SELF', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:29:54', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(150, 0, '结束时抄送人-上级', 'CE_LEADER', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:30:21', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(151, 0, '结束时抄送人-用户组', 'CE_GROUP', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:30:37', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(152, 0, '结束时抄送人-指定成员数组', 'CE_MEMBER_LIST', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:30:51', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(153, 0, '结束时抄送人-指定成员数组', 'CE_MEMBER_LIST', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:31:30', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(154, 0, '结束时抄送人-提交本人', 'CE_SELF', 'executor_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:32:01', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(155, 0, '支持', 'true', 'form_item_advanced_search', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:34:24', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(156, 0, '不支持', 'false', 'form_item_advanced_search', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:34:35', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(157, 0, '等于', 'EQUAL', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:36:17', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(158, 0, '不等于', 'NOT_EQUAL', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:36:27', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(159, 0, '大于', 'GREATER', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:36:54', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(160, 0, '小于', 'LESS', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:37:05', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(161, 0, '大于等于', 'GREATER_EQUAL', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:37:17', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(162, 0, '小于等于', 'LESS_EQUAL', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:37:30', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(163, 0, '必填', 'true', 'form_item_required', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:38:53', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(164, 0, '不必填', 'false', 'form_item_required', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:39:04', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(165, 0, '明细', 'GROUP', 'template_form_item_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:42:32', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(166, 0, '审批初始化', 'APPROVE_INIT', 'node_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:43:49', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(167, 0, '审批通过', 'APPROVE_PASS', 'node_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:44:08', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(168, 0, '审批拒绝', 'APPROVE_REJECT', 'node_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:44:20', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(169, 0, '审批中', 'APPROVING', 'node_status', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:44:32', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(170, 0, '周', 'WEEK', 'ticket_analysis_data_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:52:39', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(171, 0, '天', 'DAY', 'ticket_analysis_data_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:52:51', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(172, 0, '企微', 'WECOM', 'ticket_msg_arrive_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:54:09', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(173, 0, '钉钉', 'DINGTALK', 'ticket_msg_arrive_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:54:20', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(174, 0, '不启用', 'NULL', 'ticket_msg_arrive_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:54:30', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(176, 0, '自动创建', 'APPLY_CREATE', 'ticket_msg_build_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:55:47', 'admin', '2024-05-15 10:09:52', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(177, 0, '手动创建', 'AUDITOR_CREATE', 'ticket_msg_build_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:55:59', 'admin', '2024-05-15 10:10:09', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(178, 0, '不创建', 'CREATE_NONE', 'ticket_msg_build_type', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:56:09', 'admin', '2024-05-15 14:16:22', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(179, 0, '是', 'YES', 'support_ticket_modify', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:57:34', 'admin', '2024-05-14 16:50:25', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(180, 0, '否', 'NO', 'support_ticket_modify', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:57:47', 'admin', '2024-05-14 16:50:31', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(181, 0, '待处理', 'WAITING_HANDLE', 'ticket_status_for_user', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:59:10', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(183, 0, '已处理', 'HANDLED', 'ticket_status_for_user', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:59:37', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(184, 0, '已抄送', 'HAS_CC', 'ticket_status_for_user', NULL, NULL, 'N', '0', 'admin', '2024-04-29 22:59:53', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(186, 0, '选中', 'CHOOSED', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-05-07 10:06:32', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(187, 0, '未选中', 'NO_CHOOSED', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-05-07 10:06:41', 'admin', '2024-05-07 10:07:11', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(188, 0, '包含所有', 'CONTAIN_ALL', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-05-16 14:40:51', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(189, 0, '包含任意', 'CONTAIN_ANY', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-05-16 14:41:06', '', NULL, NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(190, 0, '所有被包含', 'INCLUDE_ALL', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-05-16 14:41:24', 'admin', '2024-05-16 14:54:01', NULL);
INSERT INTO tfs.sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(191, 0, '任意被包含', 'INCLUDE_ANY', 'form_item_compare_type', NULL, NULL, 'N', '0', 'admin', '2024-05-16 14:41:45', 'admin', '2024-05-16 14:54:06', NULL);


INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(1, '用户性别', 'sys_user_sex', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '用户性别列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(2, '菜单状态', 'sys_show_hide', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '菜单状态列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(3, '系统开关', 'sys_normal_disable', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '系统开关列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(4, '任务状态', 'sys_job_status', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '任务状态列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(5, '任务分组', 'sys_job_group', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '任务分组列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(6, '系统是否', 'sys_yes_no', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '系统是否列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(7, '通知类型', 'sys_notice_type', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '通知类型列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(8, '通知状态', 'sys_notice_status', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '通知状态列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(9, '操作类型', 'sys_oper_type', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '操作类型列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(10, '系统状态', 'sys_common_status', '0', 'admin', '2024-04-11 15:09:17', '', NULL, '登录状态列表');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(100, '工单模板表单组件类型', 'template_form_item_type', '0', 'admin', '2024-04-26 19:12:48', 'admin', '2024-04-29 15:39:57', '工单表单中的组件类型：文本、数值、选项、日期等');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(101, '工单数据状态', 'ticket_data_status', '0', 'admin', '2024-04-27 10:19:56', '', NULL, '工单数据状态,需要和代码TicketDataStatusEnum枚举一致');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(102, '工单模版状态', 'ticket_template_status', '0', 'admin', '2024-04-27 11:18:45', 'admin', '2024-04-27 11:18:55', '工单模版状态,需要和代码TicketTemplateStatusEnum枚举一致');
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(103, '审批处理类型', 'approve_deal_type', '0', 'admin', '2024-04-29 17:39:02', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(104, '节点审批方式', 'audited_method', '0', 'admin', '2024-04-29 22:05:19', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(105, '审批类型', 'audited_type', '0', 'admin', '2024-04-29 22:07:20', 'admin', '2024-04-29 22:07:36', NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(106, '事件状态', 'event_status', '0', 'admin', '2024-04-29 22:09:29', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(107, '事件类型', 'event_type', '0', 'admin', '2024-04-29 22:12:06', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(108, '动作执行时机', 'execute_step', '0', 'admin', '2024-04-29 22:15:11', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(109, '执行者类型', 'executor_type', '0', 'admin', '2024-04-29 22:18:24', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(110, '高级搜索', 'form_item_advanced_search', '0', 'admin', '2024-04-29 22:34:06', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(111, '表单项比较类型', 'form_item_compare_type', '0', 'admin', '2024-04-29 22:35:57', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(112, '表单项是否必填', 'form_item_required', '0', 'admin', '2024-04-29 22:38:33', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(113, '流程节点状态', 'node_status', '0', 'admin', '2024-04-29 22:43:18', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(115, '数据分析类型', 'ticket_analysis_data_type', '0', 'admin', '2024-04-29 22:52:13', 'admin', '2024-04-29 22:52:24', NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(116, '消息触达方式', 'ticket_msg_arrive_type', '0', 'admin', '2024-04-29 22:53:52', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(117, '消息创建类型', 'ticket_msg_build_type', '0', 'admin', '2024-04-29 22:55:18', '', NULL, NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(118, '工单支持修改标识', 'support_ticket_modify', '0', 'admin', '2024-04-29 22:57:12', 'admin', '2024-05-14 16:51:05', NULL);
INSERT INTO tfs.sys_dict_type
(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES(119, '对于与人相关的工单状态筛选', 'ticket_status_for_user', '0', 'admin', '2024-04-29 22:58:51', '', NULL, NULL);

INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2024-05-17 09:39:00', '', NULL, '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2024-05-17 09:39:00', '', NULL, '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(''ry'', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2024-05-17 09:39:00', '', NULL, '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(105, '工单每日信息采集', 'DEFAULT', 'tickAnalysisTask.collectTicketDataPrevDay', '0 15 0 * * ?', '1', '1', '0', 'admin', '2024-05-17 11:45:58', '', '2024-05-17 14:56:36', '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(106, '工单每周信息采集', 'DEFAULT', 'tickAnalysisTask.collectTicketDataPrevWeek', '0 15 0 ? * MON', '1', '1', '0', 'admin', '2024-05-17 11:46:15', '', '2024-05-17 14:56:34', '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(107, '每周一发送应用工单卡片信息', 'DEFAULT', 'tickAnalysisTask.sendWeeklyAllTicketSummary', '0 8 9 ? * MON', '1', '1', '0', 'admin', '2024-05-17 11:46:35', '', '2024-05-17 14:56:33', '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(108, '每天发送前一天工单卡片信息', 'DEFAULT', 'tickAnalysisTask.sendDailyAllTicketSummary', '0 0 9 * * ?', '1', '1', '0', 'admin', '2024-05-17 11:47:34', '', '2024-05-17 14:56:31', '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(109, '同步企微userid', 'DEFAULT', 'notificationServiceImpl.matchQwUserId', '12 12 2 * * ?', '1', '1', '0', 'admin', '2024-05-17 11:47:53', '', '2024-05-17 14:56:29', '');
INSERT INTO tfs.sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES(110, '同步账号体系内用户', 'DEFAULT', 'ticketAccountTask.syncTicketRemoteAccount', '0 0 8 * * ?', '1', '1', '0', 'admin', '2024-05-17 14:05:10', 'admin', '2024-05-17 14:56:19', '');


INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1, '系统管理', 0, 5, 'system', 1, '', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2024-05-17 09:38:56', 'admin', '2024-05-17 11:39:04', '系统管理目录');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2, '系统监控', 0, 6, 'monitor', 1, '', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2024-05-17 09:38:56', 'admin', '2024-05-17 11:40:16', '系统监控目录');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(3, '系统工具', 0, 7, 'tool', 1, '', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2024-05-17 09:38:56', 'admin', '2024-05-17 11:40:20', '系统工具目录');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(4, '低代码配置', 0, 8, 'lowcode', 1, '', NULL, '', '', 1, 0, 'M', '0', '0', '', 'component', 'admin', '2024-05-17 09:38:56', 'admin', '2024-05-17 11:40:24', '低代码管理目录');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(100, '用户管理', 1, 1, 'user', 1, '', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2024-05-17 09:38:56', '', NULL, '用户管理菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(101, '角色管理', 1, 2, 'role', 1, '', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2024-05-17 09:38:56', '', NULL, '角色管理菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(102, '菜单管理', 1, 3, 'menu', 1, '', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2024-05-17 09:38:56', '', NULL, '菜单管理菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(103, '部门管理', 1, 4, 'dept', 1, '', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2024-05-17 09:38:56', '', NULL, '部门管理菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(104, '岗位管理', 1, 5, 'post', 1, '', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2024-05-17 09:38:56', '', NULL, '岗位管理菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(105, '字典管理', 1, 6, 'dict', 1, '', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2024-05-17 09:38:56', '', NULL, '字典管理菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(106, '参数设置', 1, 7, 'config', 1, '', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2024-05-17 09:38:56', '', NULL, '参数设置菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(107, '通知公告', 1, 8, 'notice', 1, '', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2024-05-17 09:38:56', '', NULL, '通知公告菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(108, '日志管理', 1, 9, 'log', 1, '', '', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2024-05-17 09:38:56', '', NULL, '日志管理菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(109, '在线用户', 2, 1, 'online', 1, '', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2024-05-17 09:38:56', '', NULL, '在线用户菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(110, '定时任务', 2, 2, 'job', 1, '', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2024-05-17 09:38:56', '', NULL, '定时任务菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(111, '数据监控', 2, 3, 'druid', 1, '', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2024-05-17 09:38:56', '', NULL, '数据监控菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(112, '服务监控', 2, 4, 'server', 1, '', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2024-05-17 09:38:56', '', NULL, '服务监控菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(113, '缓存监控', 2, 5, 'cache', 1, '', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2024-05-17 09:38:56', '', NULL, '缓存监控菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(114, '缓存列表', 2, 6, 'cacheList', 1, '', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2024-05-17 09:38:56', '', NULL, '缓存列表菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(115, '表单构建', 3, 1, 'build', 1, '', 'tool/build/index', '', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2024-05-17 09:38:56', '', NULL, '表单构建菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(116, '代码生成', 3, 2, 'gen', 1, '', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2024-05-17 09:38:56', '', NULL, '代码生成菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(117, '系统接口', 3, 3, 'swagger', 1, '', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2024-05-17 09:38:56', '', NULL, '系统接口菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(118, '页面辅助配置', 4, 1, 'page', 1, '', 'low-code/index', '', '', 1, 0, 'C', '0', '0', 'lowcode:list', 'system', 'admin', '2024-05-17 09:38:56', '', NULL, '页面辅助配置菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(119, '通用数据', 4, 2, 'commonsql', 0, 'CommData', NULL, '', '', 1, 0, 'C', '0', '0', '', 'redis', 'admin', '2024-05-17 09:38:56', '', NULL, '通用Sql数据');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(120, '说明文档', 4, 3, 'https://lt-srm-docker-06.smyjf.cn/ruoyi-lowcode-doc/', 1, '', NULL, '', '', 0, 0, 'C', '0', '0', '', 'documentation', 'admin', '2024-05-17 09:38:56', '', NULL, '低代码组件说明文档');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(500, '操作日志', 108, 1, 'operlog', 1, '', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2024-05-17 09:38:56', '', NULL, '操作日志菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(501, '登录日志', 108, 2, 'logininfor', 1, '', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2024-05-17 09:38:56', '', NULL, '登录日志菜单');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1000, '用户查询', 100, 1, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2024-05-17 09:38:56', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1001, '用户新增', 100, 2, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1002, '用户修改', 100, 3, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1003, '用户删除', 100, 4, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1004, '用户导出', 100, 5, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1005, '用户导入', 100, 6, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1006, '重置密码', 100, 7, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1007, '角色查询', 101, 1, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1008, '角色新增', 101, 2, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1009, '角色修改', 101, 3, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1010, '角色删除', 101, 4, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1011, '角色导出', 101, 5, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1012, '菜单查询', 102, 1, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1013, '菜单新增', 102, 2, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1014, '菜单修改', 102, 3, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1015, '菜单删除', 102, 4, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1016, '部门查询', 103, 1, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1017, '部门新增', 103, 2, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1018, '部门修改', 103, 3, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1019, '部门删除', 103, 4, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1020, '岗位查询', 104, 1, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1021, '岗位新增', 104, 2, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1022, '岗位修改', 104, 3, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1023, '岗位删除', 104, 4, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1024, '岗位导出', 104, 5, '', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1025, '字典查询', 105, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1026, '字典新增', 105, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1027, '字典修改', 105, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1028, '字典删除', 105, 4, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1029, '字典导出', 105, 5, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1030, '参数查询', 106, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1031, '参数新增', 106, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1032, '参数修改', 106, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1033, '参数删除', 106, 4, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1034, '参数导出', 106, 5, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1035, '公告查询', 107, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1036, '公告新增', 107, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1037, '公告修改', 107, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1038, '公告删除', 107, 4, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1039, '操作查询', 500, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1040, '操作删除', 500, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1041, '日志导出', 500, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1042, '登录查询', 501, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1043, '登录删除', 501, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1044, '日志导出', 501, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1045, '账户解锁', 501, 4, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1046, '在线查询', 109, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1047, '批量强退', 109, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1048, '单条强退', 109, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1049, '任务查询', 110, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1050, '任务新增', 110, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1051, '任务修改', 110, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1052, '任务删除', 110, 4, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1053, '状态修改', 110, 5, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1054, '任务导出', 110, 6, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1055, '生成查询', 116, 1, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1056, '生成修改', 116, 2, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1057, '生成删除', 116, 3, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1058, '导入代码', 116, 4, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1059, '预览代码', 116, 5, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(1060, '生成代码', 116, 6, '#', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2024-05-17 09:38:57', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2000, 'Dashboard', 0, 1, 'dashboard', 1, '', 'dashboard/index', NULL, '', 1, 0, 'C', '0', '0', 'dashboard:index', 'dashboard', 'z00740', '2024-04-17 09:15:45', 'admin', '2024-05-17 11:38:52', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2001, '工单系统管理', 0, 2, 'systemManagement', 1, '', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'system', 'admin', '2024-04-19 17:23:13', 'admin', '2024-04-23 09:11:36', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2002, '工单列表', 2001, 1, 'ticket', 1, '', 'system-management/ticket/index', NULL, '', 1, 0, 'C', '0', '0', 'systemManagement:ticket:index', 'build', 'admin', '2024-04-19 17:54:14', 'admin', '2024-04-24 11:13:45', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2003, '账户体系列表', 2001, 2, 'ticketAccount', 1, '', 'system-management/ticket-account/index', NULL, '', 1, 0, 'C', '0', '0', 'systemManagement:ticketAccount:index', 'build', 'admin', '2024-04-19 17:55:18', 'admin', '2024-04-24 11:13:49', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2004, '工单业务管理', 0, 3, 'appManage', 1, '', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'system', 'admin', '2024-04-24 15:29:07', 'admin', '2024-05-13 16:34:55', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2005, '工单模版列表', 2004, 2, 'template-list', 1, '', 'app-management/template-list/index.vue', NULL, '', 1, 0, 'C', '0', '0', 'appManagement:templateList', 'build', 'admin', '2024-04-24 15:37:05', 'admin', '2024-04-24 19:52:15', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2006, '业务详情', 2004, 1, 'app-detail', 1, '', 'app-management/app-detail/index.vue', NULL, '', 1, 0, 'C', '0', '1', 'appManagement:appDetail', 'build', 'admin', '2024-04-24 15:38:04', 'admin', '2024-05-13 16:35:11', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2007, '工单模板配置', 2004, 99, 'template-configure', 1, '', 'app-management/template-configure/index.vue', NULL, '', 1, 0, 'C', '1', '0', 'appManagement:templateConfigure', 'build', 'admin', '2024-04-24 19:41:55', 'admin', '2024-05-13 09:44:54', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2008, '业务用户组列表', 2004, 3, 'executor-group-list', 1, '', 'app-management/executor-group/index.vue', NULL, '', 1, 0, 'C', '0', '0', 'appManagement:executorGroupList', 'build', 'admin', '2024-04-27 14:28:15', 'admin', '2024-05-13 16:35:18', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2009, '业务工单数据列表', 2004, 4, 'appTicketList', 1, '', 'app-management/app-ticket-list/index', NULL, '', 1, 0, 'C', '0', '0', 'appManagement:appTicketList', 'build', 'admin', '2024-04-29 09:49:40', 'admin', '2024-05-13 16:35:23', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2010, '我的工作台', 0, 4, 'workBench', 1, '', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'list', 'admin', '2024-04-29 16:42:18', 'admin', '2024-04-29 16:46:06', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2011, '我申请的工单列表', 2010, 1, 'create-by-me-list', 1, '', 'work-bench/create-by-me-list/index', NULL, '', 1, 0, 'C', '0', '0', 'workBench:createByMeList', 'build', 'admin', '2024-04-29 16:44:33', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2012, '我相关的工单', 2010, 2, 'handle-by-me-list', 1, '', 'work-bench/handle-by-me-list/index', NULL, '', 1, 0, 'C', '0', '0', 'workBench:handleByMeList', 'build', 'admin', '2024-04-29 16:48:15', '', NULL, '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2014, '获取工单模版Id', 2005, 1, '', 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'tfs:tickettemplatefull:get', '#', 'admin', '2024-05-15 19:19:03', 'admin', '2024-05-16 15:11:50', '');
INSERT INTO tfs.sys_menu
(menu_id, menu_name, parent_id, order_num, `path`, is_lowcode, lowcode_cfgid, component, query, custom_params, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2015, '保存工单模版', 2005, 2, '', 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'tfs:tickettemplate:save', '#', 'admin', '2024-05-16 15:12:08', '', NULL, '');



INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(1, 'sysPageConfig', '页面辅助配置', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(2, 'sysPageHistory', '页面配置修改记录', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(3, 'LowCodeUserManage', '用户管理-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(4, 'LowCodeRoleManage', '角色管理-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(5, 'LowCodeMenuManage', '菜单管理-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(6, 'LowCodeDepartManage', '部门管理-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(7, 'ZeroCodePosition', '岗位管理-零代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(8, 'LowCodeDict', '字典管理-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(9, 'LowCodeDictData', '字典管理-数据项-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(10, 'ZeroCodeConfig', '参数设置-零代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(11, 'LowCodeNotice', '通知公告-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(12, 'LowCodeOperationLog', '操作日志-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(13, 'LowCodeLandingLog', '登录日志-低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(14, 'ZeroCodeOnline', '在线用户 - 零代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(15, 'LowCodeJob', '定时任务 - 低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(16, 'LowCodeGen', '代码生成 - 低代码', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(17, 'CommData', '通用数据操作配置', '{
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
}', 'admin', '2024-04-11 15:09:18');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(51, 'autoGen_ActRecord', '操作记录表，数据同步夜莺', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "actType",
        "name": "操作类型，用户操作维度",
        "fieldType": "Select",
        "valueOptions": [],
      },
      {
        "key": "actId",
        "name": "操作内容标识",
        "fieldType": "Text",
        "valueOptions": [],
      },
      {
        "key": "actBy",
        "name": "操作人",
        "fieldType": "Text",
        "valueOptions": [],
      },
      {
        "key": "actContent",
        "name": "操作内容json",
        "fieldType": "Text",
        "valueOptions": [],
      },
      {
        "key": "deleteTime",
        "name": "comment",
        "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
        "fieldType": "Date",
        "valueOptions": [],
      },
    ]
  },
  "tableCardConfig": {
    "api": {
      "list": {
        "url": "/system/record/list",
        "method": "get"
      },
      "rmv": {
        "url": "/system/record",
        "method": "delete"
      }
    },
    "tableColumns": [
      {
        "key": "actType",
        "name": "操作类型，用户操作维度",
        "fieldType": "Select",
        "valueOptions": [],
      },
      {
        "key": "actId",
        "name": "操作内容标识",
        "fieldType": "Text",
        "valueOptions": [],
      },
      {
        "key": "actBy",
        "name": "操作人",
        "fieldType": "Text",
        "valueOptions": [],
      },
      {
        "key": "actContent",
        "name": "操作内容json",
        "fieldType": "Text",
        "valueOptions": [],
      },
      {
        "key": "deleteTime",
        "name": "comment",
        "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
        "fieldType": "Date",
        "valueOptions": [],
      },
      {
        "key": "operator",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text"
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text"
          }
        ],
      }
    ],
    "tableRowKey": "",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增",
        "theme": "primary"
      },
      {
        "key": "TopModifyItem",
        "name": "修改",
        "theme": "primary",
        "relationSelectionMode": "single"
      },
      {
        "key": "BatchDelete",
        "name": "删除",
        "theme": "danger",
        "relationSelectionMode": "multiple"
      }
    ],
    "tableAttrs": {}
  },
  "editDialogConfig": {
    "api": {
      "add": {
        "url": "/system/record",
        "method": "post"
      },
      "mod": {
        "url": "/system/record",
        "method": "put"
      }
    },
    "editFields": [
      {
        "key": "id",
        "name": "comment",
        "fieldType": "Text",
        "valueOptions": [],
        "isModifyHide": 1
      },
      {
        "key": "actType",
        "name": "操作类型，用户操作维度",
        "fieldType": "Select",
        "valueOptions": [],
        "isModifyHide": 0
      },
      {
        "key": "actId",
        "name": "操作内容标识",
        "fieldType": "Text",
        "valueOptions": [],
        "isModifyHide": 0
      },
      {
        "key": "actBy",
        "name": "操作人",
        "fieldType": "Text",
        "valueOptions": [],
        "isModifyHide": 0
      },
      {
        "key": "actContent",
        "name": "操作内容json",
        "fieldType": "Text",
        "valueOptions": [],
        "isModifyHide": 0
      },
      {
        "key": "updateTime",
        "name": "更新时间",
        "fieldType": "Date",
        "valueOptions": [],
        "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
        "isModifyHide": 0
      },
      {
        "key": "updateBy",
        "name": "更新人",
        "fieldType": "Text",
        "valueOptions": [],
        "isModifyHide": 0
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Date",
        "valueOptions": [],
        "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
        "isModifyHide": 1
      },
      {
        "key": "createBy",
        "name": "创建人",
        "fieldType": "Text",
        "valueOptions": [],
        "isModifyHide": 1
      },
      {
        "key": "deleteTime",
        "name": "comment",
        "fieldType": "Date",
        "valueOptions": [],
        "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
        "isModifyHide": 0
      },
    ],
    "rules": {
      "id": [{ "required": true, "message": "comment不能为空", "trigger": "blur" }],
      "actType": [{ "required": true, "message": "操作类型，用户操作维度不能为空", "trigger": "blur" }],
      "actId": [{ "required": true, "message": "操作内容标识不能为空", "trigger": "blur" }],
      "actBy": [{ "required": true, "message": "操作人不能为空", "trigger": "blur" }],
      "actContent": [{ "required": true, "message": "操作内容json不能为空", "trigger": "blur" }],
      "updateTime": [{ "required": true, "message": "更新时间不能为空", "trigger": "blur" }],
      "updateBy": [{ "required": true, "message": "更新人不能为空", "trigger": "blur" }],
      "createTime": [{ "required": true, "message": "创建时间不能为空", "trigger": "blur" }],
      "createBy": [{ "required": true, "message": "创建人不能为空", "trigger": "blur" }],
    }
  },
}', 'admin', '2024-04-11 15:27:08');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(53, 'SystemManagementTicketAccount', '工单系统管理-账户体系列表页面', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "ticketAccountName",
        "name": "账户体系名称",
        "fieldType": "Text"
      },
      {
        "key": "ticketAccountType",
        "name": "账户体系类型",
        "fieldType": "Text"
      }
    ],
    "resetFormQueryRightNow": true,
    "splitNumber": "3"
  },
  "tableCardConfig": {
    "tableColumns": [
      {
        "key": "ticketAccountName",
        "name": "账户体系名称",
        "fieldType": "Text"
      },
      {
        "key": "ticketAccountType",
        "name": "账户体系类型",
        "fieldType": "Text"
      },
      {
        "key": "ticketAccountValue",
        "name": "账户体系配置",
        "fieldType": "Text",
        "maxWidth": "350",
        "attrs": {
          "show-overflow-tooltip": true
        }
      },
      {
        "key": "ticketAccountDescription",
        "name": "账户体系描述",
        "fieldType": "Text",
        "maxWidth": "200",
        "attrs": {
          "show-overflow-tooltip": true
        }
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_normal_disable"
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "ModifyItem",
            "name": "修改",
            "theme": "text",
            "filterMethod": "!row.deleteTime"
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "filterMethod": "!row.deleteTime"
          },
          {
            "key": "SyncConfig",
            "name": "同步配置",
            "theme": "text",
            "filterMethod": "!row.deleteTime"
          },
          {
            "key": "RecentlySyncRecord",
            "name": "最近同步结果",
            "theme": "text",
            "filterMethod": "!row.deleteTime"
          }
        ],
        "attrs": {
          "align": "left",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/systemManagement/systemManagementPage",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/systemManagement/deleteTicketAccountById",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableRowKey": "id",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增账户体系",
        "theme": "primary"
      }
    ]
  },
  "editDialogConfig": {
    "editFields": [
      {
        "key": "ticketAccountName",
        "name": "账户体系名称",
        "fieldType": "Text"
      },
      {
        "key": "ticketAccountType",
        "name": "账户体系类型",
        "fieldType": "Text",
        "isModifyReadOnly": true
      },
      {
        "key": "ticketAccountDescription",
        "name": "账户体系描述",
        "fieldType": "Text",
        "isExclusiveLine": true
      },
      {
        "key": "accountConfigType",
        "name": "账户体系配置",
        "fieldType": "Radio",
        "defaultValue": "dubbo",
        "valueOptions": [
          {
            "label": "dubbo",
            "value": "dubbo"
          }
        ],
        "isExclusiveLine": true
      },
      {
        "key": "interfaceName",
        "name": "接口名称",
        "fieldType": "Text"
      },
      {
        "key": "methodName",
        "name": "方法名称",
        "fieldType": "Text"
      },
      {
        "key": "version",
        "name": "接口版本",
        "fieldType": "Text"
      },
      {
        "key": "group",
        "name": "接口分组",
        "fieldType": "Text"
      }
    ],
    "api": {
      "add": {
        "url": "/systemManagement/insertTicketAccount",
        "method": "post",
        "successCode": 200
      },
      "mod": {
        "url": "/systemManagement/updateTicketAccount",
        "method": "post",
        "successCode": 200
      }
    },
    "rules": {
      "ticketAccountName": [
        {
          "required": true,
          "message": "请输入账户体系名称",
          "trigger": "blur"
        }
      ],
      "ticketAccountType": [
        {
          "required": true,
          "message": "请输入账户体系类型",
          "trigger": "blur"
        }
      ],
      "ticketAccountDescription": [
        {
          "required": true,
          "message": "请输入账户体系描述",
          "trigger": "blur"
        }
      ],
      "accountConfigType": [
        {
          "required": true,
          "message": "请选择账户体系配置类型",
          "trigger": "blur"
        }
      ],
      "interfaceName": [
        {
          "required": true,
          "message": "请输入接口名称",
          "trigger": "blur"
        }
      ],
      "methodName": [
        {
          "required": true,
          "message": "请选择方法名称",
          "trigger": "blur"
        }
      ],
      "version": [],
      "group": []
    },
    "title": "新增账户体系|修改账户体系"
  }
}', 'admin', '2024-04-22 15:17:05');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(54, 'SystemManagementTicket', '工单系统管理-工单列表', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "searchValue",
        "name": "模糊搜索",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入工单id或工单名称"
        }
      },
      {
        "key": "appId",
        "name": "所属业务",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketAppList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "appName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "templateId",
        "name": "工单类型",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketTemplateList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "ticketName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "ticketStatusStr",
        "name": "工单状态",
        "fieldType": "Select",
        "dict": "ticket_data_status",
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "createDateRange",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "createStartTime",
          "createEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "finishDateRange",
        "name": "结单时间",
        "fieldType": "Date",
        "paramKeys": [
          "finishStartTime",
          "finishEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "applyUser",
        "name": "申请人",
        "fieldType": "Text"
      },
      {
        "key": "currentDealUser",
        "name": "受理人",
        "fieldType": "Text"
      }
    ],
    "splitNumber": "3"
  },
  "tableCardConfig": {
    "tableColumns": [
      {
        "key": "id",
        "name": "工单编号",
        "fieldType": "Text"
      },
      {
        "key": "ticketName",
        "name": "工单名称",
        "fieldType": "Text"
      },
      {
        "key": "appName",
        "name": "所属业务",
        "fieldType": "Text"
      },
      {
        "key": "ticketTemplateName",
        "name": "工单类型",
        "fieldType": "Text"
      },
      {
        "key": "ticketStatus",
        "name": "工单状态",
        "fieldType": "Tag",
        "dict": "ticket_data_status"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "ticketFinishTime",
        "name": "结单时间",
        "fieldType": "Text"
      },
      {
        "key": "applyUser",
        "name": "申请人",
        "fieldType": "Text"
      },
      {
        "key": "currentDealUsers",
        "name": "受理人",
        "fieldType": "Text",
        "maxWidth": "300",
        "attrs": {
          "show-overflow-tooltip": true
        }
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "DetailConfig",
            "name": "工单详情",
            "theme": "text"
          }
        ],
        "attrs": {
          "align": "left",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/ticketDataList/queryTicketDataList",
        "method": "get",
        "successCode": 200
      }
    },
    "tableRowKey": "id",
    "selectionMode": "multiple"
  },
  "editDialogConfig": {}
}', 'admin', '2024-04-22 16:47:38');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(55, 'AppManagementTicketTemplateList', '工单业务管理-工单模版列表', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "ticketName",
        "name": "工单模版名称",
        "fieldType": "Text"
      },
      {
        "key": "appId",
        "name": "所属业务",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketAppList?needControl=true",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "appName",
          "apiValueKey": "id"
        },
        "attrs": {
          "filterable": true
        }
      },
      {
        "key": "ticketStatus",
        "name": "模板状态",
        "fieldType": "Select",
        "dict": "ticket_template_status"
      }
    ],
    "splitNumber": "3"
  },
  "tableCardConfig": {
    "api": {
      "list": {
        "url": "/ticketTemplate/selectTicketTemplatePage?needControl=true",
        "method": "get",
        "successCode": 200
      }
    },
    "tableColumns": [
      {
        "key": "ticketName",
        "name": "模版名称",
        "fieldType": "Text"
      },
      {
        "key": "appName",
        "name": "所属业务",
        "fieldType": "Text"
      },
      {
        "key": "ticketStatus",
        "name": "模版状态",
        "fieldType": "Tag",
        "dict": "ticket_template_status"
      },
      {
        "key": "description",
        "name": "模版描述",
        "fieldType": "Text"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "createBy",
        "name": "创建人",
        "fieldType": "Text"
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "EditTemplate",
            "name": "编辑",
            "theme": "text",
            "filterMethod": "row.ticketStatus!=&apos;CANCEL&apos;"
          },
          {
            "key": "ModifyToEnable",
            "name": "启用",
            "theme": "text",
            "filterMethod": "row.ticketStatus!=&apos;CANCEL&apos; && row.ticketStatus!=&apos;ENABLE&apos;"
          },
          {
            "key": "ModifyToPause",
            "name": "暂停",
            "theme": "text",
            "filterMethod": "row.ticketStatus!=&apos;CANCEL&apos; && row.ticketStatus!=&apos;PAUSE&apos; "
          },
          {
            "key": "ModifyToCancel",
            "name": "作废",
            "theme": "text",
            "filterMethod": "row.ticketStatus!=&apos;CANCEL&apos; "
          }
        ],
        "attrs": {
          "align": "left",
          "fixed": "right"
        }
      }
    ],
    "tableRowKey": "id",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "AddTemplate",
        "name": "新增模版",
        "theme": "primary"
      }
    ]
  },
  "editDialogConfig": {}
}', 'admin', '2024-04-27 11:16:44');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(56, 'AppManagementExecutorGroupList', '工单业务管理-业务用户组列表', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "executorGroupName",
        "name": "用户组名称",
        "fieldType": "Text"
      },
      {
        "key": "appId",
        "name": "所属业务",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketAppList?needControl=true",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "appName",
          "apiValueKey": "id"
        },
        "attrs": {
          "filterable": true
        }
      }
    ],
    "splitNumber": "3"
  },
  "tableCardConfig": {
    "tableColumns": [
      {
        "key": "executorGroupName",
        "name": "用户组名称",
        "fieldType": "Text"
      },
      {
        "key": "appName",
        "name": "所属业务",
        "fieldType": "Text"
      },
      {
        "key": "executorGroupDesc",
        "name": "用户组描述",
        "fieldType": "Text"
      },
      {
        "key": "accountInfo",
        "name": "用户组账户信息",
        "fieldType": "Text",
        "maxWidth": "400",
        "attrs": {
          "show-overflow-tooltip": true
        }
      },
      {
        "key": "status",
        "name": "状态",
        "fieldType": "Tag",
        "dict": "sys_normal_disable"
      },
      {
        "key": "createBy",
        "name": "创建人",
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
            "filterMethod": "!row.deleteTime"
          },
          {
            "key": "DeleteItem",
            "name": "删除",
            "theme": "text",
            "filterMethod": "!row.deleteTime"
          }
        ],
        "attrs": {
          "align": "left",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/ticketExecutorGroup/selectTicketExecutorGroupPage",
        "method": "get",
        "successCode": 200
      },
      "rmv": {
        "url": "/ticketExecutorGroup/deleteTicketExecutorGroup",
        "method": "delete",
        "successCode": 200
      }
    },
    "tableRowKey": "id",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "TopAddItem",
        "name": "新增应用用户组",
        "theme": "primary"
      }
    ]
  },
  "editDialogConfig": {
    "api": {
      "mod": {
        "url": "/ticketExecutorGroup/updateTicketExecutorGroup",
        "method": "post",
        "successCode": 200
      },
      "add": {
        "url": "/ticketExecutorGroup/createTicketExecutorGroup",
        "method": "post",
        "successCode": 200
      }
    },
    "editFields": [
      {
        "key": "executorGroupName",
        "name": "用户组名称",
        "fieldType": "Text"
      },
      {
        "key": "appId",
        "name": "所属业务",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketAppList?needControl=true",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "appName",
          "apiValueKey": "id"
        }
      },
      {
        "key": "executorGroupDesc",
        "name": "用户组描述",
        "fieldType": "Text"
      },
      {
        "key": "accountType",
        "name": "账户体系类型",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/dashboard/selectAllAccountList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "ticketAccountName",
          "apiValueKey": "ticketAccountType"
        }
      },
      {
        "key": "accountIdList",
        "name": "用户组账户信息",
        "fieldType": "Select",
        "defaultValue": [],
        "optionsApi": {
          "url": "/ticketExecutorGroup/selectAppRemoteAccountList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "fullUserName",
          "apiValueKey": "userId"
        },
        "linkages": [
          {
            "key": "accountType",
            "dependType": 0
          }
        ],
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        },
        "isExclusiveLine": true
      }
    ],
    "rules": {
      "executorGroupName": [
        {
          "required": true,
          "message": "请输入用户组名称",
          "trigger": "blur"
        }
      ],
      "appId": [
        {
          "required": true,
          "message": "所属业务不能为空",
          "trigger": "blur"
        }
      ],
      "executorGroupDesc": [
        {
          "required": true,
          "message": "请输入用户组描述",
          "trigger": "blur"
        }
      ],
      "accountType": [
        {
          "required": true,
          "message": "账户体系类型不能为空",
          "trigger": "blur"
        }
      ],
      "accountIdList": [
        {
          "required": true,
          "message": "请选用户组账户信息",
          "trigger": "blur"
        }
      ]
    },
    "title": "新增用户组|修改用户组"
  }
}', 'admin', '2024-04-27 14:33:44');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(57, 'autoGen_TicketAccountMappingLack', '用户userid关系缺失表', '{
  "queryCardConfig": {
  "queryFields": [
            {
              "key": "phoneNo",
              "name": "手机号",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "email",
              "name": "邮箱",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "accountId",
              "name": "账户id",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "qwUserId",
              "name": "企业微信userid",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "ddUserId",
              "name": "dingding用户id",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "deleteTime",
              "name": "${comment}",
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "fieldType": "Date",
              "valueOptions": [],
            },
  ]
},
  "tableCardConfig": {
  "api": {
    "list": {
      "url": "/biz/lack/list",
      "method": "get"
    },
    "rmv": {
      "url": "/biz/lack",
      "method": "delete"
    }
  },
  "tableColumns": [
            {
              "key": "phoneNo",
              "name": "手机号",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "email",
              "name": "邮箱",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "accountId",
              "name": "账户id",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "qwUserId",
              "name": "企业微信userid",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "ddUserId",
              "name": "dingding用户id",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "deleteTime",
              "name": "${comment}",
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "fieldType": "Date",
              "valueOptions": [],
            },
    {
      "key": "operator",
      "name": "操作",
      "fieldType": "Operator",
      "btnList": [
        {
          "key": "ModifyItem",
          "name": "修改",
          "theme": "text"
        },
        {
          "key": "DeleteItem",
          "name": "删除",
          "theme": "text"
        }
      ],
    }
  ],
  "tableRowKey": "",
  "selectionMode": "multiple",
  "tableHeaderBtns": [
    {
      "key": "TopAddItem",
      "name": "新增",
      "theme": "primary"
    },
    {
      "key": "TopModifyItem",
      "name": "修改",
      "theme": "primary",
      "relationSelectionMode": "single"
    },
    {
      "key": "BatchDelete",
      "name": "删除",
      "theme": "danger",
      "relationSelectionMode": "multiple"
    }
  ],
  "tableAttrs": {}
},
  "editDialogConfig": {
  "api": {
    "add": {
      "url": "/biz/lack",
      "method": "post"
    },
    "mod": {
      "url": "/biz/lack",
      "method": "put"
    }
  },
  "editFields": [
            {
              "key": "id",
              "name": "${comment}",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 1
            },
            {
              "key": "phoneNo",
              "name": "手机号",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "email",
              "name": "邮箱",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "accountId",
              "name": "账户id",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "qwUserId",
              "name": "企业微信userid",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "ddUserId",
              "name": "dingding用户id",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "createTime",
              "name": "创建时间",
              "fieldType": "Date",
              "valueOptions": [],
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "isModifyHide": 1
            },
            {
              "key": "createBy",
              "name": "创建人",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 1
            },
            {
              "key": "updateTime",
              "name": "最后修改时间",
              "fieldType": "Date",
              "valueOptions": [],
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "isModifyHide": 0
            },
            {
              "key": "updateBy",
              "name": "最后修改人",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "deleteTime",
              "name": "${comment}",
              "fieldType": "Date",
              "valueOptions": [],
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "isModifyHide": 0
            },
  ],
  "rules": {
            "id": [
              {
                "required": true,
                "message": "${comment}不能为空",
                "trigger": "blur"
              }
            ],
            "phoneNo": [
              {
                "required": true,
                "message": "手机号不能为空",
                "trigger": "blur"
              }
            ],
            "email": [
              {
                "required": true,
                "message": "邮箱不能为空",
                "trigger": "blur"
              }
            ],
            "accountId": [
              {
                "required": true,
                "message": "账户id不能为空",
                "trigger": "blur"
              }
            ],
            "qwUserId": [
              {
                "required": true,
                "message": "企业微信userid不能为空",
                "trigger": "blur"
              }
            ],
            "ddUserId": [
              {
                "required": true,
                "message": "dingding用户id不能为空",
                "trigger": "blur"
              }
            ],
            "createTime": [
              {
                "required": true,
                "message": "创建时间不能为空",
                "trigger": "blur"
              }
            ],
            "createBy": [
              {
                "required": true,
                "message": "创建人不能为空",
                "trigger": "blur"
              }
            ],
            "updateTime": [
              {
                "required": true,
                "message": "最后修改时间不能为空",
                "trigger": "blur"
              }
            ],
            "updateBy": [
              {
                "required": true,
                "message": "最后修改人不能为空",
                "trigger": "blur"
              }
            ],
  }
},
}', 'admin', '2024-04-28 16:51:27');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(58, 'AppManagementAppTicketList', '工单业务管理-业务工单数据列表', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "searchValue",
        "name": "模糊搜索",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入工单id或工单名称"
        }
      },
      {
        "key": "appId",
        "name": "所属业务",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketAppList?needControl=true",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "appName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "templateId",
        "name": "工单类型",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketTemplateList?needControl=true",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "ticketName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "ticketStatusStr",
        "name": "工单状态",
        "fieldType": "Select",
        "dict": "ticket_data_status",
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "createDateRange",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "createStartTime",
          "createEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "finishDateRange",
        "name": "结单时间",
        "fieldType": "Date",
        "paramKeys": [
          "finishStartTime",
          "finishEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "applyUser",
        "name": "申请人",
        "fieldType": "Text"
      },
      {
        "key": "currentDealUser",
        "name": "受理人",
        "fieldType": "Text"
      }
    ],
    "splitNumber": "3"
  },
  "tableCardConfig": {
    "tableColumns": [
      {
        "key": "id",
        "name": "工单编号",
        "fieldType": "Text"
      },
      {
        "key": "ticketName",
        "name": "工单名称",
        "fieldType": "Text"
      },
      {
        "key": "appName",
        "name": "所属业务",
        "fieldType": "Text"
      },
      {
        "key": "ticketTemplateName",
        "name": "工单类型",
        "fieldType": "Text"
      },
      {
        "key": "ticketStatus",
        "name": "工单状态",
        "fieldType": "Tag",
        "dict": "ticket_data_status"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "ticketFinishTime",
        "name": "结单时间",
        "fieldType": "Text"
      },
      {
        "key": "applyUser",
        "name": "申请人",
        "fieldType": "Text"
      },
      {
        "key": "currentDealUsers",
        "name": "受理人",
        "fieldType": "Text",
        "maxWidth": "300",
        "attrs": {
          "show-overflow-tooltip": true
        }
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "DetailConfig",
            "name": "工单详情",
            "theme": "text"
          },
          {
            "key": "ReminderTicket",
            "name": "催办",
            "theme": "text",
            "filterMethod": "row.showReminderButton===true"
          },
          {
            "key": "FollowTicket",
            "name": "建群跟单",
            "theme": "text",
            "filterMethod": "row.showFollowButton===true"
          },
          {
            "key": "DispatchTicket",
            "name": "派单",
            "theme": "text",
            "filterMethod": "row.ticketStatus === &apos;APPLYING&apos;"
          }
        ],
        "attrs": {
          "align": "left",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/ticketDataList/queryTicketDataList?needControl=true",
        "method": "get",
        "successCode": 200
      }
    },
    "tableRowKey": "id",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "ReminderTicket",
        "name": "催办",
        "theme": "primary"
      },
      {
        "key": "FollowTicket",
        "name": "建群跟单",
        "theme": "primary"
      }
    ]
  },
  "editDialogConfig": {}
}', 'admin', '2024-04-29 09:52:48');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(59, 'WorkBenchCreateByMeList', '我的工作台-我申请的工单列表', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "searchValue",
        "name": "模糊搜索",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入工单id或工单名称"
        }
      },
      {
        "key": "appId",
        "name": "所属业务",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketAppList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "appName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "templateId",
        "name": "工单类型",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketTemplateList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "ticketName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "ticketStatusStr",
        "name": "工单状态",
        "fieldType": "Select",
        "dict": "ticket_data_status",
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "createDateRange",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "createStartTime",
          "createEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "finishDateRange",
        "name": "结单时间",
        "fieldType": "Date",
        "paramKeys": [
          "finishStartTime",
          "finishEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "currentDealUser",
        "name": "受理人",
        "fieldType": "Text"
      }
    ],
    "splitNumber": "3"
  },
  "tableCardConfig": {
    "tableColumns": [
      {
        "key": "id",
        "name": "工单编号",
        "fieldType": "Text"
      },
      {
        "key": "ticketName",
        "name": "工单名称",
        "fieldType": "Text"
      },
      {
        "key": "appName",
        "name": "所属业务",
        "fieldType": "Text"
      },
      {
        "key": "ticketTemplateName",
        "name": "工单类型",
        "fieldType": "Text"
      },
      {
        "key": "ticketStatus",
        "name": "工单状态",
        "fieldType": "Tag",
        "dict": "ticket_data_status"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "ticketFinishTime",
        "name": "结单时间",
        "fieldType": "Text"
      },
      {
        "key": "applyUser",
        "name": "申请人",
        "fieldType": "Text"
      },
      {
        "key": "currentDealUsers",
        "name": "受理人",
        "fieldType": "Text",
        "maxWidth": "300",
        "attrs": {
          "show-overflow-tooltip": true
        }
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "DetailConfig",
            "name": "工单详情",
            "theme": "text"
          },
          {
            "key": "ReminderTicket",
            "name": "催办",
            "theme": "text",
            "filterMethod": "row.showReminderButton===true"
          },
          {
            "key": "FollowTicket",
            "name": "建群跟单",
            "theme": "text",
            "filterMethod": "row.showFollowButton===true"
          },
          {
            "key": "RevokeTicket",
            "name": "撤销",
            "theme": "text",
            "filterMethod": "row.ticketStatus === &apos;APPLYING&apos;"
          }
        ],
        "attrs": {
          "align": "left",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/ticketDataList/queryTicketDataList?createByMe=true",
        "method": "get",
        "successCode": 200
      }
    },
    "tableRowKey": "id",
    "selectionMode": "multiple",
    "tableHeaderBtns": [
      {
        "key": "ReminderTicket",
        "name": "催办",
        "theme": "primary"
      },
      {
        "key": "FollowTicket",
        "name": "建群跟单",
        "theme": "primary"
      },
      {
        "key": "RevokeTicket",
        "name": "撤销",
        "theme": "primary"
      }
    ]
  },
  "editDialogConfig": {}
}', 'admin', '2024-04-30 09:28:28');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(60, 'WorkBenchHandleByMeList', '我的工作台-我相关的工单', '{
  "queryCardConfig": {
    "queryFields": [
      {
        "key": "searchValue",
        "name": "模糊搜索",
        "fieldType": "Text",
        "attrs": {
          "placeholder": "请输入工单id或工单名称"
        }
      },
      {
        "key": "appId",
        "name": "所属业务",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketAppList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "appName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "templateId",
        "name": "工单类型",
        "fieldType": "Select",
        "optionsApi": {
          "url": "/systemManagement/selectTicketTemplateList",
          "apiMethod": "get",
          "apiKeyPath": "data",
          "apiLabelKey": "ticketName",
          "apiValueKey": "id"
        },
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "ticketStatusStr",
        "name": "工单状态",
        "fieldType": "Select",
        "dict": "ticket_data_status",
        "attrs": {
          "multiple": true,
          "filterable": true,
          "collapse-tags": false
        }
      },
      {
        "key": "createDateRange",
        "name": "创建时间",
        "fieldType": "Date",
        "paramKeys": [
          "createStartTime",
          "createEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "finishDateRange",
        "name": "结单时间",
        "fieldType": "Date",
        "paramKeys": [
          "finishStartTime",
          "finishEndTime"
        ],
        "attrs": {
          "type": "daterange",
          "valueFormat": "yyyy-MM-dd"
        }
      },
      {
        "key": "applyUser",
        "name": "申请人",
        "fieldType": "Text"
      },
      {
        "key": "ticketStatusForUserStr",
        "name": "处理状态",
        "fieldType": "Select",
        "dict": "ticket_status_for_user"
      }
    ],
    "splitNumber": "3"
  },
  "tableCardConfig": {
    "tableColumns": [
      {
        "key": "id",
        "name": "工单编号",
        "fieldType": "Text"
      },
      {
        "key": "ticketName",
        "name": "工单名称",
        "fieldType": "Text"
      },
      {
        "key": "appName",
        "name": "所属业务",
        "fieldType": "Text"
      },
      {
        "key": "ticketTemplateName",
        "name": "工单类型",
        "fieldType": "Text"
      },
      {
        "key": "ticketStatus",
        "name": "工单状态",
        "fieldType": "Tag",
        "dict": "ticket_data_status"
      },
      {
        "key": "createTime",
        "name": "创建时间",
        "fieldType": "Text"
      },
      {
        "key": "ticketFinishTime",
        "name": "结单时间",
        "fieldType": "Text"
      },
      {
        "key": "applyUser",
        "name": "申请人",
        "fieldType": "Text"
      },
      {
        "key": "currentDealUsers",
        "name": "受理人",
        "fieldType": "Text",
        "maxWidth": "300",
        "attrs": {
          "show-overflow-tooltip": true
        }
      },
      {
        "key": "operate",
        "name": "操作",
        "fieldType": "Operator",
        "btnList": [
          {
            "key": "DetailConfig",
            "name": "工单详情",
            "theme": "text"
          }
        ],
        "attrs": {
          "align": "left",
          "fixed": "right"
        }
      }
    ],
    "api": {
      "list": {
        "url": "/ticketDataList/queryTicketDataList?needHandleByMe=true",
        "method": "get",
        "successCode": 200
      }
    },
    "tableRowKey": "id",
    "selectionMode": "multiple"
  },
  "editDialogConfig": {}
}', 'admin', '2024-04-30 09:29:17');
INSERT INTO tfs.sys_page
(page_id, page_key, remark, param_json, create_by, create_time)
VALUES(61, 'autoGen_TicketAnalysisData', '应用维度分析数据', '{
  "queryCardConfig": {
  "queryFields": [
            {
              "key": "appId",
              "name": "应用ID",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "deleteTime",
              "name": "删除时间",
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "fieldType": "Date",
              "valueOptions": [],
            },
            {
              "key": "doingCount",
              "name": "当前审核量",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "applyCountPerDay",
              "name": "每日申请量",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "doneCountPerDay",
              "name": "每日完成量",
              "fieldType": "Text",
              "valueOptions": [],
            },
  ]
},
  "tableCardConfig": {
  "api": {
    "list": {
      "url": "/system/data/list",
      "method": "get"
    },
    "rmv": {
      "url": "/system/data",
      "method": "delete"
    }
  },
  "tableColumns": [
            {
              "key": "appId",
              "name": "应用ID",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "deleteTime",
              "name": "删除时间",
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "fieldType": "Date",
              "valueOptions": [],
            },
            {
              "key": "doingCount",
              "name": "当前审核量",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "applyCountPerDay",
              "name": "每日申请量",
              "fieldType": "Text",
              "valueOptions": [],
            },
            {
              "key": "doneCountPerDay",
              "name": "每日完成量",
              "fieldType": "Text",
              "valueOptions": [],
            },
    {
      "key": "operator",
      "name": "操作",
      "fieldType": "Operator",
      "btnList": [
        {
          "key": "ModifyItem",
          "name": "修改",
          "theme": "text"
        },
        {
          "key": "DeleteItem",
          "name": "删除",
          "theme": "text"
        }
      ],
    }
  ],
  "tableRowKey": "",
  "selectionMode": "multiple",
  "tableHeaderBtns": [
    {
      "key": "TopAddItem",
      "name": "新增",
      "theme": "primary"
    },
    {
      "key": "TopModifyItem",
      "name": "修改",
      "theme": "primary",
      "relationSelectionMode": "single"
    },
    {
      "key": "BatchDelete",
      "name": "删除",
      "theme": "danger",
      "relationSelectionMode": "multiple"
    }
  ],
  "tableAttrs": {}
},
  "editDialogConfig": {
  "api": {
    "add": {
      "url": "/system/data",
      "method": "post"
    },
    "mod": {
      "url": "/system/data",
      "method": "put"
    }
  },
  "editFields": [
            {
              "key": "id",
              "name": "ID",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 1
            },
            {
              "key": "appId",
              "name": "应用ID",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "createTime",
              "name": "创建时间",
              "fieldType": "Date",
              "valueOptions": [],
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "isModifyHide": 1
            },
            {
              "key": "createBy",
              "name": "创建人",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 1
            },
            {
              "key": "updateTime",
              "name": "修改时间",
              "fieldType": "Date",
              "valueOptions": [],
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "isModifyHide": 0
            },
            {
              "key": "updateBy",
              "name": "修改人",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "deleteTime",
              "name": "删除时间",
              "fieldType": "Date",
              "valueOptions": [],
                  "attr": { "type": "datetime", "format": "yyyy-MM-dd HH:mm:ss", "valueFormat": "yyyy-MM-dd HH:mm:ss" },
              "isModifyHide": 0
            },
            {
              "key": "doingCount",
              "name": "当前审核量",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "applyCountPerDay",
              "name": "每日申请量",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
            {
              "key": "doneCountPerDay",
              "name": "每日完成量",
              "fieldType": "Text",
              "valueOptions": [],
              "isModifyHide": 0
            },
  ],
  "rules": {
  }
},
}', '{"accountId":"admin","accountName":"admin","accountType":"ldap"}', '2024-05-15 11:29:53');



INSERT INTO tfs.sys_post
(post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark)
VALUES(1, 'ceo', '董事长', 1, '0', 'admin', '2024-05-17 09:38:56', '', NULL, '');
INSERT INTO tfs.sys_post
(post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark)
VALUES(2, 'se', '项目经理', 2, '0', 'admin', '2024-05-17 09:38:56', '', NULL, '');
INSERT INTO tfs.sys_post
(post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark)
VALUES(3, 'hr', '人力资源', 3, '0', 'admin', '2024-05-17 09:38:56', '', NULL, '');
INSERT INTO tfs.sys_post
(post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark)
VALUES(4, 'user', '普通员工', 4, '0', 'admin', '2024-05-17 09:38:56', '', NULL, '');


INSERT INTO tfs.sys_role
(role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
VALUES(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2024-05-17 09:38:56', '', NULL, '超级管理员');
INSERT INTO tfs.sys_role
(role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
VALUES(102, '普通用户', 'ordinary-user', 2, '1', 1, 0, '0', '0', 'admin', '2024-05-17 11:42:42', 'admin', '2024-05-22 11:27:09', NULL);
INSERT INTO tfs.sys_role
(role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
VALUES(103, '业务管理员', 'app-manager', 3, '1', 1, 0, '0', '0', 'admin', '2024-05-17 11:43:13', 'admin', '2024-05-21 09:55:25', NULL);


INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(102, 2000);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(102, 2010);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(102, 2011);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(102, 2012);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2000);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2004);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2005);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2006);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2007);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2008);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2009);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2010);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2011);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2012);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2014);
INSERT INTO tfs.sys_role_menu
(role_id, menu_id)
VALUES(103, 2015);



INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(1, 103, 'admin', 'admin', '00', '', '', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2024-05-23 15:04:40', 'admin', '2024-05-17 09:38:56', '', '2024-05-23 15:05:40', '管理员');
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(119, NULL, 'tfs_system', 'tfs_system', '00', '', '', '0', '', '$2a$10$6lYzELf607yrhxsgWZBGpOfGipq1.WNdAFkNBX2ENISpHWqdbbWga', '0', '0', '', NULL, 'admin', '2024-05-17 11:43:45', '', NULL, NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(120, NULL, 't01457', '陀中天', '00', '', '18000925060', '0', '', '', '0', '0', '192.168.10.143', '2024-05-23 14:27:38', '', '2024-05-20 14:43:37', 'admin', '2024-05-23 14:28:59', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(121, NULL, 'z01311', '郑吉伟', '00', '', '17727557612', '0', '', '', '0', '0', '192.168.10.85', '2024-05-23 14:05:13', '', '2024-05-20 17:06:42', 'admin', '2024-05-23 14:06:34', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(122, NULL, 'z00740', '张泽东', '00', '', '13368432477', '0', '', '', '0', '2', '192.168.7.52', '2024-05-21 14:43:16', '', '2024-05-21 14:43:16', '', '2024-05-21 14:43:16', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(123, NULL, 'zhengjiwei', 'zjw', '00', '', '', '0', '', '$2a$10$fPSr49emwpVzwbkqEgIYfuK22/dpNduzIZyAaEn/sIt9V1peBbBTm', '0', '0', '', NULL, 'admin', '2024-05-21 15:06:03', '', NULL, NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(124, NULL, 'o02157', 'owen', '00', '', '19532286107', '0', '', '', '0', '2', '192.168.26.21', '2024-05-22 17:18:52', '', '2024-05-21 19:17:21', '', '2024-05-22 17:18:53', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(125, NULL, 'z01140', '邹明', '00', '', '15921594568', '0', '', '', '0', '0', '192.168.22.25', '2024-05-21 21:06:46', '', '2024-05-21 21:06:46', '', '2024-05-21 21:06:46', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(126, NULL, 'k01481', '邝宏辉', '00', '', '18166033883', '0', '', '', '0', '2', '192.168.10.85', '2024-05-22 09:57:38', '', '2024-05-22 09:57:38', '', '2024-05-22 09:57:38', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(127, NULL, 'k01481', '邝宏辉', '00', '', '18166033883', '0', '', '', '0', '2', '192.168.10.85', '2024-05-22 11:13:01', '', '2024-05-22 11:13:01', '', '2024-05-22 11:13:01', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(128, NULL, 'z00740', '张泽东', '00', '', '13368432477', '0', '', '', '0', '0', '192.168.22.25', '2024-05-22 11:28:01', '', '2024-05-22 11:28:01', '', '2024-05-22 11:28:01', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(129, NULL, 'y01081', '杨利', '00', '', '18503060168', '0', '', '', '0', '0', '192.168.12.43', '2024-05-22 17:18:17', '', '2024-05-22 11:29:37', '', '2024-05-22 17:18:17', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(130, NULL, 'k01481', '邝宏辉', '00', '', '18166033883', '0', '', '', '0', '0', '192.168.10.85', '2024-05-23 14:42:51', '', '2024-05-22 11:33:26', '', '2024-05-23 14:44:13', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(131, NULL, 's02155', '施悦', '00', '', '18800273595', '0', '', '', '0', '0', '192.168.26.21', '2024-05-22 16:59:14', '', '2024-05-22 15:10:52', '', '2024-05-22 16:59:14', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(132, NULL, 'o02157', 'owen', '00', '', '19532286107', '0', '', '', '0', '2', '192.168.13.233', '2024-05-23 09:23:50', '', '2024-05-22 17:19:35', '', '2024-05-23 09:23:50', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(133, NULL, 'o02157', 'owen', '00', '', '19532286107', '0', '', '', '0', '0', '192.168.13.233', '2024-05-23 12:02:17', '', '2024-05-23 11:34:00', '', '2024-05-23 12:03:38', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(134, NULL, 'z00684', '张相', '00', '', '13798316015', '0', '', '', '0', '0', '192.168.10.89', '2024-05-23 11:46:58', '', '2024-05-23 11:48:20', '', '2024-05-23 11:48:20', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(135, NULL, 'w01872', '万莹', '00', '', '', '0', '', '', '0', '0', '192.168.22.25', '2024-05-23 11:47:25', '', '2024-05-23 11:48:47', '', '2024-05-23 11:48:47', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(136, NULL, 'l00279', '李杰斌', '00', '', '15013815212', '0', '', '', '0', '0', '192.168.10.159', '2024-05-23 11:48:23', '', '2024-05-23 11:49:44', '', '2024-05-23 11:49:44', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(137, NULL, 'o01129', '欧阳松', '00', '', '17872699903', '0', '', '', '0', '0', '192.168.10.143', '2024-05-23 14:27:58', '', '2024-05-23 11:51:24', '', '2024-05-23 14:29:19', NULL);
INSERT INTO tfs.sys_user
(user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES(138, NULL, 'f01098', '范泽鑫', '00', '', '17329505153', '0', '', '', '0', '0', '192.168.22.25', '2024-05-23 13:58:24', '', '2024-05-23 13:59:45', '', '2024-05-23 13:59:45', NULL);


INSERT INTO tfs.sys_user_post
(user_id, post_id)
VALUES(1, 1);
INSERT INTO tfs.sys_user_post
(user_id, post_id)
VALUES(2, 2);


INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(1, 1);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(1, 101);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(1, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(119, 101);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(119, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(120, 101);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(120, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(120, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(121, 101);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(121, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(121, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(123, 101);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(123, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(123, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(125, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(125, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(128, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(128, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(129, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(129, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(130, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(130, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(131, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(131, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(133, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(133, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(134, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(134, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(135, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(135, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(136, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(136, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(137, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(137, 103);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(138, 102);
INSERT INTO tfs.sys_user_role
(user_id, role_id)
VALUES(138, 103);
