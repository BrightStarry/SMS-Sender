/* 平台信息表 */
CREATE TABLE platform(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(16) NOT NULL COMMENT '平台名字',
  token VARCHAR(128) DEFAULT '' COMMENT '令牌',
  callback_url VARCHAR(128) DEFAULT '' COMMENT '回调url',
  status TINYINT DEFAULT 1 COMMENT '状态： 0：停止授权；1：启用授权',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY(id),
  KEY idx_token(token),
  KEY idx_name(name)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '平台信息表';

/*短信通道信息表*/
CREATE TABLE channel(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(32) DEFAULT '' COMMENT '通道名',
  sort TINYINT DEFAULT 0 COMMENT '排序,优先级,0-255',
  cache_name VARCHAR(32) DEFAULT '' COMMENT '缓存key,用于缓存',
  key_name VARCHAR(32) DEFAULT '' COMMENT 'key,用于spring容器,',
  type TINYINT NOT NULL COMMENT '通道类型,对同一个ip的接口调用为同一类型',
  support_operator TINYINT DEFAULT 0 COMMENT '支持的运营商. 0:未知;1:移动;2:联通;3.电信',
  max_group_number SMALLINT DEFAULT 1 COMMENT '最大群发数(每次最多发送号码数).如果不支持,则为1',
  max_connect TINYINT DEFAULT 1 COMMENT '最大连接数,针对socket',
  max_concurrent Integer NOT NULL COMMENT '最大并发数,针对所有,类型相同,并发肯定一样',
  is_cmpp TINYINT NOT NULL COMMENT  '是否为cmpp. 0:否; 1:是',

  a_key VARCHAR(32) DEFAULT '' COMMENT '连接用的字符a',
  b_key VARCHAR(32) DEFAULT '' COMMENT '连接用的字符b',
  c_key VARCHAR(32) DEFAULT '' COMMENT '连接用的字符c',
  d_key VARCHAR(32) DEFAULT '' COMMENT '连接用的字符d',

  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY (id),
  KEY idx_name(name)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '短信通道信息表';

INSERT INTO channel(name,is_cmpp, max_connect,max_concurrent,sort ,type,key_name, cache_name,support_operator,a_key,b_key,c_key,d_key)
    VALUES
      ('掌游_移动',0,1,100,1,0,'zhangYou','zhangYouYD',1,'10010317','710317','asdfg123456ghjjjjjkh',''),
      ('宽信_移动',0,1,100,2,1,'kuanXin','kuanXinYD',1,'387568','84f26c091438461bb01fcd021da1c197','',''),
      ('宽信_联通',0,1,100,4,1,'kuanXin','kuanXinDX',3,'387568','84f26c091438461bb01fcd021da1c197','',''),
      ('宽信_CMPP',1,1,200,5,2,'CMPP','kuanXinCMPP',1,'387843','387843','zuma#387843','10689082'),
      ('群正_移动',0,1,100,6,3,'qunZheng','qunZhengYD',1,'hzzmkjyzm','YBpFJzkc2q170501','',''),
      ('筑望_CMPP',1,1,300,7,4,'CMPP','zhuWangCMPP',1,'944027','944027','SVPOUXJLYD','1069026427'),
      ('畅想_移动',0,1,100,8,5,'changXiang','changXiangYD',1,'zmkj','zmkj','',''),
      ('创蓝_移动',0,1,100,9,6,'chuangLan','chuangLanYD',1,'M8671404','vHEZ1bywo','',''),
      ('铭锋_移动',0,1,100,10,7,'mingFeng','mingFengYD',1,'zmkjhy','zmkjhy01','','')
      ;


/*系统用户表*/
CREATE TABLE user(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  username VARCHAR(16) NOT NULL COMMENT '用户名',
  password VARCHAR(16) NOT NULL COMMENT '密码',

  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY (id),
  KEY idx_username(username)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '短信通道信息表';

INSERT INTO user (username, password) VALUES
  ('zuma', '123456');


/*字典表*/
CREATE TABLE dict(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  module VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN' COMMENT '所属模块',
  type VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN' COMMENT '类型',
  name VARCHAR(32) NOT NULL COMMENT 'key',
  value VARCHAR(128) NOT NULL COMMENT 'value',
  remark VARCHAR(32) DEFAULT '' COMMENT '备注',

  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY (id),
  KEY idx_module(module),
  KEY idx_type(type),
  KEY idx_name(name)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '字典表';

INSERT INTO dict(remark,module, type, name, value)
    VALUES
      /*通用*/
      ('电信手机号正则','common','common','DIANXIN_PHONE_NUMBER_REGEXP','(^(133|153|173|177|(18[0|1|9]))\\d{8}$)|(^1(349|700)\\d{7}$)'),
      ('移动手机号正则','common','common','YIDONG_PHONE_NUMBER_REGEXP','(^((13[4-9])|147|(15[0|1|2|7|8|9])|178|(18[2|3|4|7|8]))\\d{8}$)|(^1705\\d{7}$)'),
      ('联通手机号正则','common','common','LIANTONG_PHONE_NUMBER_REGEXP','(^((13[0-2])|145|(15[5|6])|17[5|6]|(18[5|6]))\\d{8}$)|(^1709\\d{7}$)'),
      ('手机号数组分隔符','common','common','PHONES_SEPARATOR',','),
      ('短信内容分隔符','common','common','SMS_MESSAGE_SEPARATOR','!&'),
      ('默认每页条数','common','common','PAGE_SIZE','5'),

#       ('号码源每块号码数','common','common','NUMBER_SOURCE_BLOCK_NUMBER','3000'),
#       ('号码源分块前缀(前缀+id)','common','common','NUMBER_SOURCE_PRE','NUMBER_SOURCE_'),
#       ('号码组保存前缀(前缀+id)','common','common','NUMBER_GROUP_PRE','NUMBER_GROUP_PRE_'),
      /*运行配置*/

      ('发送任务结束处理器-等待超时时间','run','run','SEND_TASK_END_HANDLER_WAIT_TIMEOUT','3'),
      ('socket连接重试次数','run','run','SOCKET_CONNECT_MAX_RETRY_NUMBER','3'),
      ('socket心跳检测频率','run','run','SOCKET_ACTIVE_TEST_SECOND','180'),
      ('socket默认每秒发送次数','run','run','SOCKET_DEFAULT_QPS','1000');





/*短信上行记录表*/
CREATE TABLE sms_up_record(
  id BIGINT AUTO_INCREMENT COMMENT '记录id',
  channel_id BIGINT DEFAULT 0 COMMENT '通道id',
  channel_name VARCHAR(16) DEFAULT '' COMMENT '通道名',
  phone CHAR(11)  DEFAULT '' COMMENT '手机号',
  content VARCHAR(255) DEFAULT '' COMMENT '短信内容',
  request_body VARCHAR(512) DEFAULT '' COMMENT '请求对象json字符',
  up_time DATETIME DEFAULT 0 COMMENT '上行时间',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY(id),
  KEY idx_phone(phone),
  KEY idx_channel_id(channel_id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '短信上行记录表';

/*短信发送记录表,每调用接口一次,生成一条记录*/
CREATE TABLE sms_send_record(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  platform_id BIGINT DEFAULT 0 COMMENT '平台id,当接口调用发送时',
  platform_name VARCHAR(16) DEFAULT '' COMMENT '平台名,当接口调用发送时',
  send_task_id BIGINT DEFAULT 0 COMMENT '任务id,,当用户操作发送时',
  channel_id BIGINT DEFAULT 0 COMMENT '通道id',
  channel_name VARCHAR(16) DEFAULT 0 COMMENT '通道名',
  phones VARCHAR(4096) NOT NULL COMMENT '发送手机号',

  request_body VARCHAR(1024) DEFAULT '' COMMENT '调用者请求对象json字符',
  other_id VARCHAR(32) DEFAULT '' COMMENT '其他id,一般为接口返回的该次调用唯一标识',

  sync_time TIMESTAMP DEFAULT '0000-00-00 00:00:00' COMMENT '同步回调时间',
  sync_result_body VARCHAR(1024) DEFAULT '' COMMENT '同步返回对象json字符',

  async_time TIMESTAMP DEFAULT '0000-00-00 00:00:00' COMMENT '异步回调时间',
  async_result_body VARCHAR(1024) DEFAULT  '' COMMENT '异步返回对象json字符',

  error_info VARCHAR(128) COMMENT '异常信息,如果有的话',

  status TINYINT DEFAULT 0 COMMENT '状态. 0:默认;1:同步成功;2:异步成功;-1:同步失败;-2:异步失败',

  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间,也是发送时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (id),
  KEY idx_platform_id(platform_id),
  KEY idx_task_id(send_task_id),
  KEY idx_channel_id(channel_id),
  KEY idx_other_id(other_id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '短信发送记录表,每调用接口一次,生成一条记录';

/*发送任务表*/
CREATE TABLE send_task_record(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(32) NOT NULL COMMENT '任务名',
  remark VARCHAR(64) DEFAULT '' COMMENT '备注',
  user_id BIGINT NOT NULL COMMENT '创建任务的用户id',
  channel_id BIGINT DEFAULT 0 COMMENT '通道id',
  channel_name VARCHAR(16) DEFAULT 0 COMMENT '通道名',
  number_group_id BIGINT NOT NULL COMMENT '号码组id',
  number_group_name VARCHAR(32) NOT NULL COMMENT '号码组名字',
  sms_content_id BIGINT NOT NULL COMMENT '短信内容id',
  sms_content_name VARCHAR(32) NOT NULL COMMENT '短信内容名',
  content VARCHAR(256) NOT NULL COMMENT '发送内容',
  thread_count TINYINT DEFAULT 2 COMMENT '开启线程数',
  is_shard TINYINT DEFAULT 0 COMMENT '是否分片处理,每小时处理一部分',

  expect_start_time TIMESTAMP DEFAULT current_timestamp COMMENT '期望发送时间',
  expect_end_time TIMESTAMP NOT NULL DEFAULT 0 COMMENT '期望结束时间',
  real_start_time TIMESTAMP NOT NULL DEFAULT current_timestamp COMMENT '实际发送时间',
  real_end_time TIMESTAMP NOT NULL DEFAULT 0 COMMENT  '实际结束时间',
  total_time SMALLINT DEFAULT 0 COMMENT '总用时(秒)',

  status TINYINT DEFAULT 0 COMMENT '状态. 0:等待中;1:运行中;2:成功;-1:失败;-2:中断',
  number_num SMALLINT NOT NULL COMMENT '号码总数',
  success_num SMALLINT DEFAULT 0 COMMENT '成功数,异步成功',
  failed_num SMALLINT DEFAULT 0 COMMENT '失败数,异步失败',
  un_response SMALLINT DEFAULT 0 COMMENT '未响应数',
#   async_un_response SMALLINT DEFAULT 0 COMMENT '异步未响应数',
  used_num SMALLINT DEFAULT 0 COMMENT '已操作总数(可能被中断导致后续号码未操作)',

  error_number_path VARCHAR(128) DEFAULT '' COMMENT '失败号码路径,可供下载',
  error_info VARCHAR(256) DEFAULT '' COMMENT '失败信息,可能为空',

  is_delete TINYINT DEFAULT 0 COMMENT '是否被删除. 0:否;1:是',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY (id),
  KEY idx_user_id(user_id),
  KEY idx_number_group_id(number_group_id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '发送任务表';

/*号码源数据表*/
CREATE TABLE number_source (
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(32) NOT NULL COMMENT '名字',
  remark VARCHAR(128) DEFAULT '' COMMENT '备注',
  number_count INT NOT NULL COMMENT '号码总数',
  grouped_count INT DEFAULT 0 COMMENT '已分组的号码总数',
  status TINYINT DEFAULT 0 COMMENT '状态. 0:未分组; 1:已分组',

  is_delete TINYINT DEFAULT 0 COMMENT '是否被删除. 0:否;1:是',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY (id)

)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '号码源数据表';

/*号码组数据表*/
CREATE TABLE number_group (
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(32) NOT NULL COMMENT '名字',
  remark VARCHAR(128) DEFAULT '' COMMENT '备注',
  type_id BIGINT NOT NULL COMMENT '号码组类别id',
  type_name VARCHAR(32) NOT NULL COMMENT '号码组类别名',
  number_source_id BIGINT COMMENT '号码源id',
  number_source_name VARCHAR(32) COMMENT '号码源名称',
  number_count INT NOT NULL COMMENT '号码总数',
  group_mode TINYINT DEFAULT 0 COMMENT '分组模式. 0:顺序;1:随机;2:手动',

#   is_delete TINYINT DEFAULT 0 COMMENT '是否被删除. 0:否;1:是',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  PRIMARY KEY (id),
  KEY idx_type_id(type_id),
  KEY idx_number_source_id(number_source_id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '号码组数据表';


/*号码组类别表*/
CREATE TABLE number_group_type(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(32) NOT NULL COMMENT '名字',
  remark VARCHAR(128) DEFAULT '' COMMENT '备注',

  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '号码组类别表';

/*短信内容表*/
CREATE TABLE sms_content(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(32) NOT NULL COMMENT '名字',
  content VARCHAR(256) NOT NULL COMMENT '话术内容',
  remark VARCHAR(128) DEFAULT '' COMMENT '备注',

  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT = '短信内容表';

/*spring security 记住我功能表格*/
create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null);
