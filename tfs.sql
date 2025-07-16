/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.25-log : Database - tfs
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`tfs` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `tfs`;

/*Table structure for table `act_record` */

CREATE TABLE `act_record` (
  `id` varchar(255) NOT NULL,
  `act_type` varchar(255) NOT NULL COMMENT '操作类型，用户操作维度',
  `act_id` varchar(255) NOT NULL COMMENT '操作内容标识',
  `act_by` varchar(255) NOT NULL COMMENT '操作人',
  `act_content` varchar(5000) NOT NULL COMMENT '操作内容json',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_by` varchar(255) NOT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `delete_time` datetime DEFAULT NULL,
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作记录表，数据同步夜莺';

/*Table structure for table `gen_table` */

CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表';

/*Table structure for table `gen_table_column` */

CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` varchar(64) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表字段';

/*Table structure for table `newtable` */

CREATE TABLE `newtable` (
  `ID` varchar(100) DEFAULT NULL,
  `DeleteTime` date DEFAULT NULL,
  `Data` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `qrtz_blob_triggers` */

CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Blob类型的触发器表';

/*Table structure for table `qrtz_calendars` */

CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日历信息表';

/*Table structure for table `qrtz_cron_triggers` */

CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Cron类型的触发器表';

/*Table structure for table `qrtz_fired_triggers` */

CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint(13) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(13) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(11) NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='已触发的触发器表';

/*Table structure for table `qrtz_job_details` */

CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务详细信息表';

/*Table structure for table `qrtz_locks` */

CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储的悲观锁信息表';

/*Table structure for table `qrtz_paused_trigger_grps` */

CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='暂停的触发器表';

/*Table structure for table `qrtz_scheduler_state` */

CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调度器状态表';

/*Table structure for table `qrtz_simple_triggers` */

CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint(7) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(12) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(10) NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简单触发器的信息表';

/*Table structure for table `qrtz_simprop_triggers` */

CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步机制的行锁表';

/*Table structure for table `qrtz_triggers` */

CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint(13) DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(13) DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(13) NOT NULL COMMENT '开始时间',
  `end_time` bigint(13) DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(2) DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='触发器详细信息表';

/*Table structure for table `rule_execute_flow_202407` */

CREATE TABLE `rule_execute_flow_202407` (
  `id` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `space_code` varchar(255) NOT NULL COMMENT '空间code',
  `project_code` varchar(255) NOT NULL COMMENT '项目code',
  `rule_code` varchar(255) NOT NULL COMMENT '规则code',
  `req_params` text NOT NULL COMMENT '入参',
  `rsp_params` text COMMENT '返回参数',
  `rsp_code` varchar(255) NOT NULL COMMENT '返回状态码：200:表示成功',
  `biz_flow_no` varchar(255) NOT NULL COMMENT '业务流水号',
  `create_by` varchar(765) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_by` varchar(765) NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_biz_flow_no_rsp_code` (`biz_flow_no`,`rsp_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行记录流水表';

/*Table structure for table `sys_common_data` */

CREATE TABLE `sys_common_data` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `data_key` varchar(50) DEFAULT NULL COMMENT '接口数据标识',
  `op_type` varchar(10) DEFAULT NULL COMMENT '操作类型: LIST-列表 | UPDATE-更新 | ADD-新增 | DELETE-删除',
  `pri_key` varchar(20) DEFAULT '' COMMENT '操作单表SQL的唯一键字段名，代码中默认取"id"',
  `auto_sql_table` varchar(20) DEFAULT NULL COMMENT '自动化SQL操作的单表名，代码中默认取data_key',
  `sql` text COMMENT 'SQL配置',
  `op_role` text COMMENT '操作角色',
  `create_by` varchar(10) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(10) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COMMENT='通用接口SQL配置表';

/*Table structure for table `sys_config` */

CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

/*Table structure for table `sys_dept` */

CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

/*Table structure for table `sys_dict_data` */

CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

/*Table structure for table `sys_dict_type` */

CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

/*Table structure for table `sys_job` */

CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度表';

/*Table structure for table `sys_job_log` */

CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度日志表';

/*Table structure for table `sys_logininfor` */

CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=8252 DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';

/*Table structure for table `sys_menu` */

CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `is_lowcode` int(1) DEFAULT '1' COMMENT '是否完全由低代码配置生成的页面',
  `lowcode_cfgid` varchar(100) DEFAULT '' COMMENT '低代码配置标识',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `custom_params` varchar(255) DEFAULT '' COMMENT '自定义菜单配置，json字符串类型',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2019 DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

/*Table structure for table `sys_notice` */

CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

/*Table structure for table `sys_oper_log` */

CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6377 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

/*Table structure for table `sys_page` */

CREATE TABLE `sys_page` (
  `page_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `page_key` varchar(100) NOT NULL COMMENT '页面配置标识',
  `remark` varchar(500) DEFAULT NULL COMMENT '说明',
  `param_json` text COMMENT 'JSON配置',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`page_id`),
  UNIQUE KEY `page_key` (`page_key`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COMMENT='低代码页面配置表';

/*Table structure for table `sys_page_record` */

CREATE TABLE `sys_page_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `page_key` varchar(100) NOT NULL COMMENT '页面配置标识',
  `param_json` text COMMENT 'JSON配置',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_type` int(1) DEFAULT '1' COMMENT '更新类型: 1-新增 | 2-修改 | 3-删除',
  `version` int(4) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=352 DEFAULT CHARSET=utf8mb4 COMMENT='低代码页面配置表';

/*Table structure for table `sys_post` */

CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

/*Table structure for table `sys_role` */

CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

/*Table structure for table `sys_role_dept` */

CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';

/*Table structure for table `sys_role_menu` */

CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

/*Table structure for table `sys_user` */

CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=230 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

/*Table structure for table `sys_user_post` */

CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与岗位关联表';

/*Table structure for table `sys_user_role` */

CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

/*Table structure for table `test1` */

CREATE TABLE `test1` (
  `id` varchar(100) DEFAULT NULL COMMENT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `ticket_account` */

CREATE TABLE `ticket_account` (
  `id` varchar(255) DEFAULT NULL COMMENT 'ID',
  `ticket_account_name` varchar(255) DEFAULT NULL COMMENT '账户名称',
  `ticket_account_type` varchar(255) DEFAULT NULL COMMENT '账户类型',
  `ticket_account_value` varchar(255) DEFAULT NULL COMMENT '账户配置',
  `ticket_account_description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单账户体系表';

/*Table structure for table `ticket_account_mapping` */

CREATE TABLE `ticket_account_mapping` (
  `id` varchar(255) NOT NULL,
  `phone_no` varchar(255) DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `account_id` varchar(255) NOT NULL COMMENT '账户id',
  `qw_user_id` varchar(255) DEFAULT NULL COMMENT '企业微信userid',
  `dd_user_id` varchar(255) DEFAULT NULL COMMENT 'dingding用户id',
  `delete_time` datetime DEFAULT NULL,
  `account_name` varchar(255) NOT NULL COMMENT '账户name',
  `account_type` varchar(255) NOT NULL COMMENT '账户类型',
  `qy_user_name` varchar(255) DEFAULT NULL COMMENT '企业微信username',
  `match_count` int(11) NOT NULL DEFAULT '0' COMMENT '匹配次数',
  `match_result` varchar(100) DEFAULT 'init' COMMENT '匹配结果 init, success, fail, ignore',
  `system_account` tinyint(1) DEFAULT NULL COMMENT '是否系统自带用户',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `dd_user_dept_id` varchar(100) DEFAULT NULL COMMENT 'dingding用户部门id',
  `dd_user_dept_name` varchar(100) DEFAULT NULL COMMENT 'dingding用户部门名称',
  `same_origin_id` varchar(100) DEFAULT NULL COMMENT '同源用户id',
  `superior_id` varchar(100) DEFAULT NULL COMMENT '上级ID',
  `dept_manager_ids` varchar(100) DEFAULT NULL COMMENT '部门负责人ID(s)，多个用逗号分隔',
  `super_dept_id` varchar(100) DEFAULT NULL COMMENT '上级部门ID',
  `dept_name` varchar(100) DEFAULT NULL COMMENT '部门名称',
  `dept_id` varchar(100) DEFAULT NULL COMMENT '部门ID',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  UNIQUE KEY `ticket_account_mapping_account_id_IDX` (`account_id`,`account_type`,`delete_time`) USING BTREE,
  UNIQUE KEY `ticket_account_mapping_account_id_uniq_idx` (`account_id`,`account_type`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户体系映射表';

/*Table structure for table `ticket_account_sync_record` */

CREATE TABLE `ticket_account_sync_record` (
  `id` varchar(255) NOT NULL COMMENT 'ID',
  `ticket_account_id` varchar(255) NOT NULL COMMENT '账户配置id',
  `sync_result` varchar(255) NOT NULL COMMENT '同步结果',
  `sync_result_des` varchar(5000) NOT NULL COMMENT '同步结果描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最近修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最近修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单账户体系同步记录表';

/*Table structure for table `ticket_analysis_data` */

CREATE TABLE `ticket_analysis_data` (
  `id` varchar(255) DEFAULT NULL COMMENT 'ID',
  `app_id` varchar(255) DEFAULT NULL COMMENT '应用ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '修改人',
  `top3_create_by` varchar(255) DEFAULT NULL COMMENT 'Top3 提单人',
  `last_top3_create_by` varchar(255) DEFAULT NULL COMMENT '上个周期（日/周）Top3提单人',
  `create_by_count` int(11) DEFAULT NULL COMMENT '提单人数',
  `last_create_by_count` int(11) DEFAULT NULL COMMENT '上个周期（日/周）提单人数',
  `executor_count` int(11) DEFAULT NULL COMMENT '审批人数',
  `last_executor_count` int(11) DEFAULT NULL COMMENT '上个周期（周/日）审批人数',
  `apply_count` int(11) DEFAULT NULL COMMENT '工单申请量',
  `last_apply_count` int(11) DEFAULT NULL COMMENT '上个周期（周/日）工单申请量',
  `applying_count` int(11) DEFAULT NULL COMMENT '审批中工单量',
  `last_applying_count` int(11) DEFAULT NULL COMMENT '上个周期（周/日）审批中工单量',
  `withdraw_count` int(11) DEFAULT NULL COMMENT '撤回工单量',
  `last_withdraw_count` int(11) DEFAULT NULL COMMENT '上个周期（周/日）撤回工单量',
  `reject_count` int(11) DEFAULT NULL COMMENT '审批拒绝工单量',
  `last_reject_count` int(11) DEFAULT NULL COMMENT '上个周期（周/日）审批拒绝工单量',
  `done_count` int(11) DEFAULT NULL COMMENT '完成工单量',
  `last_done_count` int(11) DEFAULT NULL COMMENT '上个周期（周/日）完成工单量',
  `ticket_avg_efficiency` int(11) DEFAULT NULL COMMENT '工单平均审批时效（s）',
  `last_ticket_avg_efficiency` int(11) DEFAULT NULL COMMENT '上个周期（周/日）工单平均审批时效(s)',
  `category` varchar(64) NOT NULL COMMENT '类别(day/week)',
  `start_date` datetime DEFAULT NULL COMMENT '开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '结束时间',
  UNIQUE KEY `id` (`id`,`delete_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用维度分析数据';

/*Table structure for table `ticket_analysis_data_bak` */

CREATE TABLE `ticket_analysis_data_bak` (
  `id` varchar(255) DEFAULT NULL COMMENT 'ID',
  `app_id` varchar(255) DEFAULT NULL COMMENT '应用ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `doing_count` varchar(255) DEFAULT NULL COMMENT '当前审核量',
  `apply_count_per_day` varchar(255) DEFAULT NULL COMMENT '每日申请量',
  `done_count_per_day` varchar(255) DEFAULT NULL COMMENT '每日完成量',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用维度分析数据';

/*Table structure for table `ticket_app` */

CREATE TABLE `ticket_app` (
  `id` varchar(255) NOT NULL,
  `app_name` varchar(255) NOT NULL COMMENT '应用描述',
  `app_admin_users` text NOT NULL COMMENT '应用管理员（只能域账号）\n[]',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL,
  `app_desc` varchar(255) NOT NULL COMMENT '应用描述',
  `account_type` varchar(255) NOT NULL COMMENT '应用关联账户体系类型',
  `wx_chat_group_id` varchar(100) DEFAULT NULL COMMENT '企微群聊ID',
  `category_enabled` varchar(100) DEFAULT NULL COMMENT '是否开启工单分类 true:开启 false:不开启',
  `extend_enabled` varchar(100) DEFAULT NULL COMMENT '是否开启公共字段',
  `extend_fields` text COMMENT '拓展字段',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';

/*Table structure for table `ticket_category` */

CREATE TABLE `ticket_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL COMMENT '分类code',
  `name` varchar(255) NOT NULL COMMENT '分类名称',
  `superior_code` varchar(255) DEFAULT NULL COMMENT '上级分类code',
  `status` varchar(10) NOT NULL COMMENT '状态：open开启；stop-暂停；del-删除',
  `category_level` varchar(255) NOT NULL COMMENT '分类层级：one一级；two二级；three三',
  `app_id` varchar(255) NOT NULL COMMENT '所属业务',
  `sort` int(11) NOT NULL COMMENT '该节点在当前层级的顺序',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL,
  `template_id` varchar(255) DEFAULT NULL COMMENT '工单模版ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_code` (`code`,`delete_time`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=443 DEFAULT CHARSET=utf8mb4 COMMENT='工单分类表';

/*Table structure for table `ticket_config` */

CREATE TABLE `ticket_config` (
  `id` varchar(100) NOT NULL COMMENT '配置ID',
  `ticket_template_id` varchar(100) NOT NULL COMMENT '工单模版ID',
  `ticket_config_str` varchar(10000) NOT NULL COMMENT '工单配置内容',
  `delete_time` date DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `create_time` date NOT NULL COMMENT '创建时间',
  `update_by` varchar(255) NOT NULL COMMENT '更新人',
  `update_time` date NOT NULL COMMENT '更新时间',
  UNIQUE KEY `ticket_config_id_IDX` (`id`,`delete_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `ticket_data` */

CREATE TABLE `ticket_data` (
  `id` varchar(255) NOT NULL COMMENT '工单ID',
  `template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `app_id` varchar(255) NOT NULL COMMENT '应用ID',
  `ticket_status` varchar(255) NOT NULL COMMENT '工单状态：\n草稿中\n审批中\n审批结束',
  `ticket_name` varchar(255) NOT NULL COMMENT '工单名称',
  `description` varchar(255) NOT NULL COMMENT '说明',
  `current_node_name` varchar(255) DEFAULT NULL COMMENT '最近处理节点名称',
  `current_deal_users` varchar(2048) NOT NULL COMMENT '当前处理人',
  `current_done_users` varchar(5000) NOT NULL COMMENT '处理完成的人',
  `current_cc_users` varchar(4000) DEFAULT NULL COMMENT '所有抄送人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最近修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最近修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `current_node_id` varchar(255) NOT NULL COMMENT '当前处理节点',
  `ticket_finish_time` datetime DEFAULT NULL COMMENT '工单结束时间',
  `beyond_apps` varchar(255) DEFAULT NULL COMMENT '关联的应用',
  `ticket_template_code` varchar(255) DEFAULT NULL COMMENT '工单模板标识',
  `interface_key` varchar(255) DEFAULT NULL COMMENT '接口生成标识',
  `apply_user` varchar(255) DEFAULT NULL,
  `ticket_msg_build_type` varchar(100) DEFAULT NULL COMMENT '消息创建类型',
  `ticket_msg_arrive_type` varchar(100) DEFAULT NULL COMMENT '消息触达方式',
  `ticket_form_change_flag` varchar(100) NOT NULL DEFAULT 'NO' COMMENT '是否支持审批中修改表单',
  `wx_chat_group_id` varchar(100) DEFAULT NULL COMMENT '企微群聊ID',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
  `ticket_aging_flag` varchar(20) DEFAULT NULL COMMENT '支持时效通知',
  `ticket_aging_time` int(11) NOT NULL DEFAULT '0' COMMENT '时效时长(单位小时)',
  `tags` varchar(255) DEFAULT NULL COMMENT '工单标签["",""]',
  `new_tags` text COMMENT '新标签值，格式为对象数组',
  `base_flow` text,
  `ticket_business_key` varchar(100) DEFAULT NULL COMMENT '业务号，保障只有一个审批中的工单',
  `apply_ticket_ways` varchar(20) DEFAULT NULL COMMENT '工单发起方式，为空表示不限制',
  `extend1` varchar(255) DEFAULT NULL,
  `extend2` text COMMENT '拓展字段2',
  `extend3` text COMMENT '拓展字段3',
  `extend4` text COMMENT '拓展字段4',
  `extend5` text COMMENT '拓展字段5',
  `extend6` text COMMENT '拓展字段6',
  `extend7` text COMMENT '拓展字段7',
  `extend8` text COMMENT '拓展字段8',
  `extend9` text COMMENT '拓展字段9',
  `extend10` text COMMENT '拓展字段10',
  PRIMARY KEY (`id`(180)),
  UNIQUE KEY `uniq_ticket_id` (`id`),
  UNIQUE KEY `id` (`id`,`delete_time`),
  KEY `ticket_name_idx` (`ticket_name`),
  KEY `idx_ticket_business_key` (`ticket_business_key`),
  KEY `idx_createtime` (`create_time`),
  KEY `idx_dt_tid_ts_ct` (`delete_time`,`template_id`,`ticket_status`,`create_time`),
  KEY `idx_tid_ct` (`template_id`,`create_time`),
  KEY `idx_ext1` (`extend1`),
  KEY `idx_updatetime` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单数据表';

/*Table structure for table `ticket_executor_group` */

CREATE TABLE `ticket_executor_group` (
  `id` varchar(255) NOT NULL,
  `executor_group_name` varchar(255) NOT NULL COMMENT '应用组名称',
  `account_info` text NOT NULL COMMENT '账户信息\n[\n{account_type:””,account_id:””},\n{account_type:””,account_id:””},\n{account_type:””,account_id:””},\n]',
  `create_time` datetime NOT NULL,
  `create_by` varchar(255) NOT NULL,
  `update_time` datetime NOT NULL,
  `update_by` varchar(255) NOT NULL,
  `delete_time` datetime DEFAULT NULL,
  `executor_group_desc` varchar(255) DEFAULT NULL COMMENT '应用组描述',
  `app_id` varchar(100) DEFAULT NULL COMMENT '所属app_id',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用人员组表';

/*Table structure for table `ticket_flow_data` */

CREATE TABLE `ticket_flow_data` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL COMMENT '工单ID',
  `template_id` varchar(255) NOT NULL COMMENT '流程模版ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `wx_msg_build_type` varchar(255) NOT NULL DEFAULT 'create_none' COMMENT '企微消息创建类型',
  `enable_wx_msg` varchar(255) NOT NULL DEFAULT 'false' COMMENT '是否启用企微消息：true-启用；false-不启用',
  `msg_arrive_type` varchar(255) DEFAULT NULL COMMENT 'WeCom：企微；DingTalk：钉钉',
  `start_cc` varchar(255) DEFAULT NULL COMMENT '开始时抄送（备用）',
  `end_cc` varchar(255) DEFAULT NULL COMMENT '结束时抄送（备用）',
  PRIMARY KEY (`id`(180)),
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `idx_ticket_data_id` (`ticket_data_id`(180))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程数据表';

/*Table structure for table `ticket_flow_event_data` */

CREATE TABLE `ticket_flow_event_data` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL,
  `event_status` varchar(255) NOT NULL COMMENT '0:初始化\n1:待执行\n2:执行中，异步待确认\n3:执行失败，待执行\n20:执行成功（终态）\n30:执行失败（终态）',
  `execute_time` datetime DEFAULT NULL COMMENT '执行时间',
  `ticket_flow_node_data_id` varchar(255) NOT NULL,
  `execute_step` varchar(50) NOT NULL COMMENT 'before:执行前\r\ndoing:执行中\r\ndone:执行后',
  `template_id` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL,
  `event_type` varchar(20) NOT NULL COMMENT '事件类型：dubbo,http',
  `event_config` varchar(255) NOT NULL COMMENT '事件方法',
  `event_tag` varchar(255) NOT NULL COMMENT '事件标识',
  `push_config` varchar(255) DEFAULT NULL COMMENT '推送配置',
  `approve_deal_type` varchar(20) DEFAULT NULL COMMENT '枚举ApproveDealTypeEnum触发类型：APPLY-提交工单；PASS-审批通过；REJECT-驳回工单等',
  `event_tran_data` text COMMENT '事件传输数据',
  PRIMARY KEY (`id`(180)),
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `idx_ticket_data_id` (`ticket_data_id`(180)),
  KEY `idx_flow_node_data_id_dt` (`ticket_flow_node_data_id`,`delete_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程动作数据表';

/*Table structure for table `ticket_flow_event_template` */

CREATE TABLE `ticket_flow_event_template` (
  `id` varchar(255) NOT NULL,
  `ticket_flow_node_template_id` varchar(255) NOT NULL COMMENT '流程模版ID',
  `event_tag` varchar(255) NOT NULL COMMENT '事件标识',
  `execute_step` varchar(50) NOT NULL COMMENT 'before:执行前\r\ndoing:执行中\r\ndone:执行后\r\npass:通过后\r\nreject:拒绝后',
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(255) DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `event_type` varchar(20) NOT NULL COMMENT '事件类型：dubbo,http',
  `event_config` varchar(255) NOT NULL COMMENT '事件方法',
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `push_config` varchar(255) DEFAULT NULL COMMENT '推送配置',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程动作模版表';

/*Table structure for table `ticket_flow_node_action_data` */

CREATE TABLE `ticket_flow_node_action_data` (
  `id` varchar(100) DEFAULT NULL,
  `ticket_data_id` varchar(100) NOT NULL COMMENT '模版ID',
  `template_id` varchar(100) NOT NULL COMMENT '模版ID',
  `ticket_flow_node_data_id` varchar(100) NOT NULL COMMENT '工单流程节点模版ID',
  `action_name` varchar(100) NOT NULL COMMENT '动作名称',
  `action_type` varchar(100) NOT NULL COMMENT '动作类型',
  `action_value` varchar(100) NOT NULL COMMENT '//{approve:pass,update:[{}]}',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_time` varchar(100) DEFAULT NULL COMMENT '删除时间',
  `action_config` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `ticket_flow_node_action_template` */

CREATE TABLE `ticket_flow_node_action_template` (
  `id` varchar(100) DEFAULT NULL,
  `ticket_template_id` varchar(100) NOT NULL COMMENT '工单模版ID',
  `ticket_flow_node_template_id` varchar(100) NOT NULL COMMENT '工单流程节点模版ID',
  `action_name` varchar(100) NOT NULL COMMENT '动作名称',
  `action_type` varchar(100) NOT NULL COMMENT '动作类型',
  `action_value` varchar(100) NOT NULL COMMENT '//{approve:pass,update:[{}]}',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_time` varchar(100) DEFAULT NULL COMMENT '删除时间',
  `action_config` text COMMENT '动作配置'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `ticket_flow_node_approve_detail` */

CREATE TABLE `ticket_flow_node_approve_detail` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL COMMENT '工单ID',
  `ticket_flow_node_data_id` varchar(255) NOT NULL COMMENT '流程节点ID',
  `deal_user_type` varchar(50) NOT NULL COMMENT '处理人ID',
  `deal_user_id` varchar(50) NOT NULL COMMENT '处理人ID',
  `deal_user_name` varchar(50) NOT NULL COMMENT '处理人ID',
  `deal_opinion` varchar(2000) NOT NULL COMMENT '处理意见',
  `deal_type` varchar(30) NOT NULL COMMENT '处理类型/审批结果（审批通过，审批驳回，审批驳回至上一节点）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_by` varchar(255) NOT NULL COMMENT '更新人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `ticket_flow_event_data_id` varchar(255) DEFAULT NULL COMMENT '执行动作数据id',
  `deal_type_callback` varchar(30) DEFAULT NULL COMMENT '执行事件回调处理类型（ACTION_SUCCESS、ACTION_FAILED）',
  `deal_type_description` varchar(50) DEFAULT NULL COMMENT '处理类型描述',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_update_time` (`update_time`) USING BTREE,
  KEY `idx_deal_user_id` (`deal_user_id`) USING BTREE,
  KEY `idx_data_id_node_data_id` (`ticket_data_id`,`ticket_flow_node_data_id`) USING BTREE,
  KEY `idx_ticket_data_id` (`ticket_data_id`(180))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程节点审批明细';

/*Table structure for table `ticket_flow_node_data` */

CREATE TABLE `ticket_flow_node_data` (
  `id` varchar(255) NOT NULL,
  `pre_node_id` varchar(255) NOT NULL COMMENT '上节点ID, first表示开始节点',
  `template_id` varchar(255) NOT NULL COMMENT '模版ID',
  `ticket_data_id` varchar(255) NOT NULL COMMENT '工单数据ID',
  `ticket_flow_data_id` varchar(255) NOT NULL COMMENT '流程数据ID',
  `audited_method` varchar(255) NOT NULL COMMENT '审批方式\n会签\n或签',
  `audited_type` varchar(255) NOT NULL COMMENT '审批类型\n人工审核&自动审核&自动拒绝',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `node_order` int(11) NOT NULL COMMENT '节点顺序',
  `node_status` varchar(255) NOT NULL COMMENT '节点状态',
  `flow_node_rule_type` varchar(255) NOT NULL DEFAULT 'static' COMMENT '节点规则类型：静态规则static 动态规则dynamic',
  `node_name` varchar(255) NOT NULL,
  `node_wx_deal_card_code` varchar(2000) DEFAULT NULL COMMENT '企微审批卡片ID',
  `node_wx_deal_card_message_id` varchar(2000) DEFAULT NULL COMMENT '用来撤回卡片用',
  `modify_field_list` varchar(2000) DEFAULT NULL COMMENT '当前节点可修改的字段',
  `call_back_msg_status` varchar(20) DEFAULT NULL COMMENT '枚举CallBackMsgStatusEnum回调消息状态：SUCCESS_MSG_SENG-发送成功消息；EXCEPTION_MSG_SENG-发送异常消息',
  PRIMARY KEY (`id`(180)),
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `idx_ticket_data_id` (`ticket_data_id`(180))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程节点数据表';

/*Table structure for table `ticket_flow_node_executor_data` */

CREATE TABLE `ticket_flow_node_executor_data` (
  `id` varchar(255) NOT NULL,
  `template_id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL,
  `ticket_flow_node_data_id` varchar(255) NOT NULL,
  `executor_type` varchar(255) NOT NULL COMMENT '上级\n部门负责人\n角色\n用户组\n发起时指定成员\n提交本人',
  `executor_value` varchar(2000) NOT NULL COMMENT '执行人值',
  `create_time` datetime NOT NULL,
  `create_by` varchar(255) NOT NULL,
  `update_time` datetime NOT NULL,
  `update_by` varchar(255) NOT NULL,
  `delete_time` datetime DEFAULT NULL,
  `executor_list` varchar(2000) NOT NULL COMMENT '执行人列表',
  `executor_done_List` varchar(255) NOT NULL COMMENT '完成审批人',
  `group_value` varchar(255) DEFAULT NULL COMMENT '当executor_type为"APPLY_DEPT_POINT"时，组信息：[{"accountType":"-1","accountId":"","accountName":""}]',
  `default_executor_type` varchar(255) DEFAULT NULL COMMENT '审批人为空时，默认的审批人类型',
  `default_executor_value` varchar(2000) DEFAULT NULL COMMENT '审批人为空时，默认的审批人的值',
  PRIMARY KEY (`id`(180)),
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `idx_ticket_data_id` (`ticket_data_id`(180))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程节点执行人数据表';

/*Table structure for table `ticket_flow_node_executor_template` */

CREATE TABLE `ticket_flow_node_executor_template` (
  `id` varchar(255) NOT NULL,
  `ticket_flow_node_template_id` varchar(255) NOT NULL COMMENT '工单流程节点模版ID',
  `executor_type` varchar(255) NOT NULL COMMENT '执行者类型\n上级\n部门负责人\n角色\n用户组\n发起时指定成员\n提交本人\n代办-人\n代办-组',
  `executor_value` varchar(2000) NOT NULL COMMENT '执行者值',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_by` varchar(255) NOT NULL COMMENT '更新人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `default_executor_type` varchar(255) DEFAULT NULL COMMENT '审批人为空时，默认的审批人类型',
  `default_executor_value` varchar(2000) DEFAULT NULL COMMENT '审批人为空时，默认的审批人的值',
  `group_value` varchar(255) DEFAULT NULL COMMENT '当executor_type为"APPLY_DEPT_POINT"时，组信息：[{"accountType":"-1","accountId":"","accountName":""}]',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程节点执行人模版表';

/*Table structure for table `ticket_flow_node_rule_template` */

CREATE TABLE `ticket_flow_node_rule_template` (
  `id` varchar(255) NOT NULL,
  `ticket_flow_node_template_id` varchar(255) NOT NULL COMMENT '流程节点模板ID',
  `rule_info_list` varchar(2000) DEFAULT NULL COMMENT '规则条件',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程节点规则模版表';

/*Table structure for table `ticket_flow_node_sla_template` */

CREATE TABLE `ticket_flow_node_sla_template` (
  `id` varchar(255) NOT NULL,
  `ticket_flow_node_template_id` varchar(255) NOT NULL COMMENT '流程节点模板ID',
  `config_type` varchar(255) NOT NULL COMMENT '配置方式：FLOW_NODE：流程节点，TICKET:工单',
  `config_type_content` varchar(2000) DEFAULT NULL COMMENT '配置方式内容:如config_type是flow_node方式，则内容是{"节点id1","节点id2"}',
  `start_end_time` varchar(255) NOT NULL COMMENT '[00:00:00,23:59:59]',
  `timeout` int(32) NOT NULL COMMENT '用(时间值，单位)表示：例如(30h)',
  `remind_config` text NOT NULL COMMENT '提醒规则：[{"remindType":"before/current/after","timeValue":30m","ticketMsgArriveType":"WECOM","executorType":"APPLY_MEMBER_LIST","executorValue":"[{"accountType":"kefu","accountId":"7025","accountName":"陈红","sameOriginId":"10581"},{"accountType":"kefu","accountId":"6688","accountName":"廖海龙","sameOriginId":"10373"},{"accountType":"kefu","accountId":"4001","accountName":"陈圆圆","sameOriginId":"10580"}]"}]',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单/工单流程节点SLA模版配置表';

/*Table structure for table `ticket_flow_node_template` */

CREATE TABLE `ticket_flow_node_template` (
  `id` varchar(255) NOT NULL,
  `pre_node_id` varchar(255) NOT NULL,
  `ticket_flow_template_id` varchar(255) NOT NULL,
  `audited_method` varchar(255) NOT NULL COMMENT '审批方式\n会签\n或签',
  `audited_type` varchar(255) NOT NULL COMMENT '审批类型\n人工审核&自动审核&自动拒绝',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL,
  `node_order` int(11) NOT NULL COMMENT '节点排序',
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `node_name` varchar(255) NOT NULL COMMENT '节点名称',
  `modify_field_list` varchar(2000) DEFAULT NULL COMMENT '当前节点可修改的字段',
  `default_executor_type` varchar(255) DEFAULT NULL COMMENT '审批人为空时，默认的审批人类型',
  `default_executor_value` varchar(2000) DEFAULT NULL COMMENT '审批人为空时，默认的审批人的值',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程节点模版表';

/*Table structure for table `ticket_flow_template` */

CREATE TABLE `ticket_flow_template` (
  `id` varchar(255) NOT NULL,
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL,
  `start_cc` varchar(255) DEFAULT NULL COMMENT '开始时抄送（备用）',
  `end_cc` varchar(255) DEFAULT NULL COMMENT '结束时抄送（备用）',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程模版表';

/*Table structure for table `ticket_form_data` */

CREATE TABLE `ticket_form_data` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL,
  `template_id` varchar(255) NOT NULL COMMENT '模版ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`(180)),
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `idx_ticket_data_id` (`ticket_data_id`(180))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表单数据表';

/*Table structure for table `ticket_form_item_data` */

CREATE TABLE `ticket_form_item_data` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL COMMENT '工单数据ID',
  `ticket_form_data_id` varchar(255) NOT NULL COMMENT '工单表单数据ID',
  `item_order` int(11) NOT NULL COMMENT '组件排序',
  `item_type` varchar(255) NOT NULL COMMENT '组件类型',
  `item_label` varchar(255) NOT NULL COMMENT '组件名称',
  `item_value` text COMMENT '组件值',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `template_id` varchar(255) NOT NULL COMMENT '模版ID',
  `item_config` varchar(5000) DEFAULT NULL COMMENT '组件配置',
  `item_advanced_search` varchar(255) NOT NULL DEFAULT 'false' COMMENT '是否支持高级搜索：true:支持；false:不支持',
  `item_tips` varchar(255) DEFAULT NULL COMMENT '提示',
  `item_required` varchar(100) DEFAULT 'false' COMMENT '是否必填：true:是支持；false:否',
  `item_parent_id` varchar(255) DEFAULT NULL COMMENT '组件父ID',
  `item_config_ext` text COMMENT '扩展组件配置（备用字段）',
  PRIMARY KEY (`id`(180)),
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `idx_ticket_data_id` (`ticket_data_id`(180)),
  KEY `idx_tfid_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表单组件数据表';

/*Table structure for table `ticket_form_item_data_log` */

CREATE TABLE `ticket_form_item_data_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `ticket_form_item_data_id` varchar(255) NOT NULL COMMENT '工单组件表ID',
  `ticket_data_id` varchar(255) NOT NULL COMMENT '工单数据ID',
  `ticket_form_data_id` varchar(255) NOT NULL COMMENT '工单表单数据ID',
  `item_order` int(11) NOT NULL COMMENT '组件排序',
  `item_type` varchar(255) NOT NULL COMMENT '组件类型',
  `item_label` varchar(255) NOT NULL COMMENT '组件名称',
  `item_value` text COMMENT '组件值',
  `template_id` varchar(255) NOT NULL COMMENT '模版ID',
  `item_config` text COMMENT '组件配置',
  `item_config_ext` text COMMENT '扩展组件配置（备用字段）',
  `item_advanced_search` varchar(255) NOT NULL DEFAULT 'false' COMMENT '是否支持高级搜索：true:支持；false:不支持',
  `item_tips` varchar(255) DEFAULT NULL COMMENT '提示',
  `item_required` varchar(100) DEFAULT 'false' COMMENT '是否必填：true:是支持；false:否',
  `item_parent_id` varchar(255) DEFAULT NULL COMMENT '组件父ID',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `oper_type` varchar(50) NOT NULL COMMENT '日志类型',
  `oper_time` datetime NOT NULL COMMENT '操作时间',
  `oper_id` varchar(255) NOT NULL COMMENT '操作人id',
  `oper_by` varchar(255) NOT NULL COMMENT '操作人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `ticket_form_item_data_ticket_data_id_IDX` (`ticket_data_id`) USING BTREE,
  KEY `ticket_form_item_data_ticket_item_id_IDX` (`ticket_form_item_data_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1268 DEFAULT CHARSET=utf8mb4 COMMENT='工单表单组件数据日志表';

/*Table structure for table `ticket_form_item_id_col_mapping` */

CREATE TABLE `ticket_form_item_id_col_mapping` (
  `id` varchar(255) NOT NULL,
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `form_item_id` varchar(255) NOT NULL COMMENT '组件id',
  `form_item_value_col` varchar(255) NOT NULL COMMENT '表单项id值对应的列名，例如form_item_value1',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='（表单项id和对应的列名映射关系表）';

/*Table structure for table `ticket_form_item_template` */

CREATE TABLE `ticket_form_item_template` (
  `id` varchar(255) NOT NULL,
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `ticket_form_template_id` varchar(255) NOT NULL COMMENT '工单表单模版ID',
  `item_parent_id` varchar(255) NOT NULL COMMENT '父ID',
  `item_order` int(11) NOT NULL COMMENT '组件排序',
  `item_type` varchar(255) NOT NULL COMMENT '组件类型：input:单行文本	textarea:多行文本 inputNumber:数字inputMoney:金额select:单选selectMultiple:多选time日期 timeSpan:日期区间picture:图片file:附件phone:电话group:明细',
  `item_config` text NOT NULL COMMENT '类型配置',
  `item_label` varchar(255) NOT NULL COMMENT '组件标题',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `item_visible_rule` varchar(1000) DEFAULT NULL,
  `item_tips` varchar(255) DEFAULT NULL COMMENT '提示',
  `item_required` varchar(255) NOT NULL DEFAULT 'FALSE' COMMENT '是否必填：TRUE:是支持；FALSE:否',
  `item_advanced_search` varchar(255) NOT NULL DEFAULT 'FALSE' COMMENT '是否支持高级搜索：TRUE:支持；FALSE:不支持',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单组件模版表';

/*Table structure for table `ticket_form_item_values` */

CREATE TABLE `ticket_form_item_values` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL COMMENT '工单ID',
  `form_item_value1` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value2` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value3` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value4` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value5` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value6` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value7` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value8` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value9` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value10` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value11` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value12` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value13` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value14` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value15` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value16` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value17` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value18` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value19` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value20` varchar(255) DEFAULT NULL COMMENT '组件值',
  `current_deal_users` text NOT NULL COMMENT '当前处理人',
  `current_done_users` text NOT NULL COMMENT '处理完成的人',
  `current_cc_users` text NOT NULL COMMENT '所有抄送人',
  `apply_user` varchar(255) DEFAULT NULL,
  `ticket_status` varchar(255) NOT NULL COMMENT '工单状态：\n草稿中\n审批中\n审批结束',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `ticket_name` varchar(255) NOT NULL COMMENT '工单名称',
  `ticket_finish_time` datetime DEFAULT NULL COMMENT '工单结束时间',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
  `form_item_value21` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value22` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value23` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value24` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value25` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value26` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value27` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value28` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value29` varchar(255) DEFAULT NULL COMMENT '组件值',
  `form_item_value30` varchar(255) DEFAULT NULL COMMENT '组件值',
  `wx_chat_group_id` varchar(100) DEFAULT NULL COMMENT '企微群聊ID',
  `ticket_msg_arrive_type` varchar(100) DEFAULT NULL COMMENT '消息触达方式',
  `template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `tags` varchar(255) DEFAULT NULL COMMENT '工单标签["",""]',
  `apply_ticket_ways` varchar(20) DEFAULT NULL COMMENT '工单发起方式，为空表示不限制',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_ticket_data_id` (`ticket_data_id`,`delete_time`),
  KEY `idx_tfiv_updatetime` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='（表单项col和表单项value对应的平铺表）';

/*Table structure for table `ticket_form_template` */

CREATE TABLE `ticket_form_template` (
  `id` varchar(255) NOT NULL,
  `ticket_template_id` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` varchar(255) DEFAULT NULL,
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表单模版表';

/*Table structure for table `ticket_origin_account` */

CREATE TABLE `ticket_origin_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id,数据库自增',
  `phone_no` varchar(255) DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `account_id` varchar(255) NOT NULL COMMENT '账户id',
  `qw_user_id` varchar(255) DEFAULT NULL COMMENT '企业微信userid',
  `dd_user_id` varchar(255) DEFAULT NULL COMMENT 'dingding用户id',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `account_name` varchar(255) NOT NULL COMMENT '账户name',
  `qy_user_name` varchar(255) DEFAULT NULL COMMENT '企业微信username',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `dd_user_dept_id` varchar(100) DEFAULT NULL COMMENT 'dingding用户部门id',
  `dd_user_dept_name` varchar(100) DEFAULT NULL COMMENT 'dingding用户部门名称',
  `same_origin_id` varchar(100) DEFAULT NULL COMMENT '同源用户id:10000自增',
  `match_result` varchar(100) DEFAULT 'init' COMMENT '匹配结果：init success fail',
  `superior_id` varchar(100) DEFAULT NULL COMMENT '上级ID',
  `dept_manager_ids` varchar(100) DEFAULT NULL COMMENT '部门负责人ID(s)，多个用逗号分隔',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11321 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `ticket_sla_config_data` */

CREATE TABLE `ticket_sla_config_data` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL COMMENT '工单数据ID',
  `template_id` varchar(255) NOT NULL COMMENT '模版ID',
  `ticket_sla_data_id` varchar(255) NOT NULL COMMENT '工单sla数据ID',
  `config_type` varchar(255) NOT NULL COMMENT '配置方式：FLOW_NODE：流程节点，TICKET:工单',
  `config_type_content` varchar(2000) DEFAULT NULL COMMENT '配置方式内容:如config_type是FLOW_NODE方式，则内容是"节点id1,节点id2"',
  `start_end_time` varchar(255) NOT NULL COMMENT '[00:00:00,23:59:59]',
  `timeout` varchar(255) NOT NULL COMMENT '用(时间值，单位)表示：例如(30h)',
  `remind_config` text NOT NULL COMMENT '提醒规则：[{"remindType":"before/current/after","timeValue":30m","ticketMsgArriveType":"WECOM","executorType":"APPLY_MEMBER_LIST","executorValue":"[{"accountType":"kefu","accountId":"7025","accountName":"陈红","sameOriginId":"10581"},{"accountType":"kefu","accountId":"6688","accountName":"廖海龙","sameOriginId":"10373"},{"accountType":"kefu","accountId":"4001","accountName":"陈圆圆","sameOriginId":"10580"}]","isReminded":"NO"}]',
  `remind_timing` datetime DEFAULT NULL COMMENT '提醒计时开始时间：工单创建时间/当前节点审批更新时间',
  `remind_status` varchar(255) NOT NULL COMMENT '状态:init-初始化，doing-执行中，done-已完成',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `tscd_td_id_IDX` (`ticket_data_id`) USING BTREE,
  KEY `tscd_rs_rt_IDX` (`remind_status`,`remind_timing`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单sla配置数据表';

/*Table structure for table `ticket_sla_config_template` */

CREATE TABLE `ticket_sla_config_template` (
  `id` varchar(255) NOT NULL,
  `ticket_sla_template_id` varchar(255) NOT NULL COMMENT 'sla模板id',
  `config_type` varchar(255) NOT NULL COMMENT '配置方式：FLOW_NODE：流程节点，TICKET:工单',
  `config_type_content` varchar(2000) DEFAULT NULL COMMENT '配置方式内容:如config_type是flow_node方式，则内容是{"节点id1","节点id2"}',
  `start_end_time` varchar(255) NOT NULL COMMENT '[00:00:00,23:59:59]',
  `timeout` varchar(255) NOT NULL COMMENT '用(时间值，单位)表示：例如(30h)',
  `remind_config` text NOT NULL COMMENT '提醒规则：[{"remindType":"before/current/after","timeValue":30m","ticketMsgArriveType":"WECOM","executorType":"APPLY_MEMBER_LIST","executorValue":"[{"accountType":"kefu","accountId":"7025","accountName":"陈红","sameOriginId":"10581"},{"accountType":"kefu","accountId":"6688","accountName":"廖海龙","sameOriginId":"10373"},{"accountType":"kefu","accountId":"4001","accountName":"陈圆圆","sameOriginId":"10580"}]"}]',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `ticket_template_id` varchar(255) NOT NULL COMMENT '工单模版ID',
  `status` varchar(255) NOT NULL COMMENT '状态',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单sla配置表';

/*Table structure for table `ticket_sla_data` */

CREATE TABLE `ticket_sla_data` (
  `id` varchar(255) NOT NULL,
  `ticket_data_id` varchar(255) NOT NULL,
  `template_id` varchar(255) NOT NULL COMMENT '模版ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tsd_td_id_IDX` (`ticket_data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单sla数据表';

/*Table structure for table `ticket_sla_template` */

CREATE TABLE `ticket_sla_template` (
  `id` varchar(255) NOT NULL,
  `ticket_template_id` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最后修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最后修改人',
  `delete_time` varchar(255) DEFAULT NULL,
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单sla表';

/*Table structure for table `ticket_template` */

CREATE TABLE `ticket_template` (
  `id` varchar(255) NOT NULL COMMENT '工单ID',
  `app_id` varchar(255) NOT NULL COMMENT '应用ID',
  `ticket_status` varchar(255) NOT NULL COMMENT '模版状态：\n草稿\n暂停\n启用中\n作废',
  `ticket_name` varchar(255) NOT NULL COMMENT '工单名称',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '最近修改时间',
  `update_by` varchar(255) NOT NULL COMMENT '最近修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `ticket_template_code` varchar(255) DEFAULT NULL COMMENT '工单模板标识',
  `beyond_apps` varchar(255) DEFAULT NULL COMMENT '关联的应用',
  `interface_key` varchar(255) DEFAULT NULL COMMENT '接口生成标识',
  `ticket_msg_arrive_type` varchar(100) DEFAULT NULL COMMENT '消息触达方式',
  `ticket_form_change_flag` varchar(100) NOT NULL DEFAULT 'NO' COMMENT '是否支持审批中，修改表单',
  `ticket_msg_build_type` varchar(100) NOT NULL DEFAULT 'CREATE_NONE' COMMENT '消息创建方式',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
  `ticket_aging_flag` varchar(20) DEFAULT NULL COMMENT '支持时效通知',
  `ticket_aging_time` int(11) NOT NULL DEFAULT '0' COMMENT '时效时长(单位小时)',
  `ticket_deal_time` int(11) NOT NULL DEFAULT '0' COMMENT '工单处理时效时长(单位小时)',
  `show_dept_name_flag` varchar(20) DEFAULT NULL COMMENT '显示部门名称',
  `apply_ticket_ways` varchar(100) DEFAULT NULL COMMENT '工单发起方式，为空表示不限制',
  UNIQUE KEY `id` (`id`,`delete_time`) USING HASH,
  KEY `ticket_name_idx` (`ticket_name`) USING BTREE,
  KEY `ticket_template_delete_time_IDX` (`delete_time`,`ticket_template_code`) USING BTREE,
  KEY `idx_aid_dt` (`app_id`,`delete_time`),
  KEY `idx_aid_ba_dt` (`app_id`,`beyond_apps`,`delete_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单模版表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
