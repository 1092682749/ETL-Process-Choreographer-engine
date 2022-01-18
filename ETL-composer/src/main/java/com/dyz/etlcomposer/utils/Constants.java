package com.dyz.etlcomposer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目常量
 */
public final class Constants {
  public static final String BASE_PACKAGE = "com.dyz.dev";//项目基础包名称，根据自己公司的项目修改
  public static final String PCN = "PCN";
  public static final String NMSE = "NMSE";
  public static final String CAR = "CAR";
  public static final String MODEL_PACKAGE = BASE_PACKAGE + ".model";//Model所在包
  public static final String MAPPER_PACKAGE = BASE_PACKAGE + ".dao";//Mapper所在包
  public static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service";//Service所在包
  public static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";//ServiceImpl所在包
  public static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controller";//Controller所在包


  public static final String nodesFormat = "%d_%s_nodes";
  public static final String edgesFormat = "%d_%s_edges";
  public static final String  executeIdFormat = "%s_%s";
  public static final String allDiagram = "diagram_set";
  public static final String idSeq = "diagram_id";

  public static final String MAPPER_INTERFACE_REFERENCE = BASE_PACKAGE + ".utils.Mapper";//Mapper插件基础接口的完全限定名
  public static final String MODULE_NAME = "moduleName";
  public static final String MODULE_FLAG = "SOLVE_CENTER_MODULE";
  public static final String ROLE_FLAG = "SOLVE_CENTER_ROLE";
  public static final String CODE_ERR_PNAME_EXIST = "权限名称";
  public static final String NOTIFY_DELETE = "您的comment: {0} 被管理员删除!id={1}";
  public static final String NOTIFY_COMMENT = "{0} 评论了您的case: {1} ";
  // 新进材料流程id
  public static final String SUPPLIER_PROCESS_ID = "dedc4bcdde374136a684018eec89d3d5";
  // 制程物料变更流程id
  public static final String SPEC_PROCESS_ID = "b9fd02f0faea44eab93f1c6e46a47959";
  // 抱怨系统流程id
  public static final String COMPLAIN_PROCESS_ID = "cae1604ccc194f1ab5077a4a7443f285";
  //其他常量
  public static final String PATH_IMAGE_LISENCE = "src/main/resources/uploads/";
  public static final String PATH_IMAGE_PATH = "../uploads/";
  public static final String PATH_IMAGE_PATH_DB = "uploads/";
  public static final String MYSQL_SQL = " {0} limit {1},{2}";
  /* ~~~~~~~~~~~~~~~~~~~~~返回数据状态 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /*
   * 注意: 添加了全局静态常量之后, 必须在messageMap中添加对应的中文描述
   */
  // 查询成功
//    public static final String CODE_NORMAL = "0";
  public static final Integer SUCCESS = 0;
  public static final Integer FAIL = 400;
  public static final Integer UNAUTHORIZED = 401;
  public static final Integer NOT_FOUND = 404;
  public static final Integer INTERNAL_SERVER_ERROR = 500;

  //0 -1000内的编码不允许动 自定义请使用1000外的编码
  // 查询失败
  public static final Integer CODE_ERR_UNKNOWN = 100;
  public static final Integer CODE_ERR_PARAM = 101;
  public static final Integer CODE_ERR_ACCOUNT_EXIST = 103;
  public static final Integer CODE_ERR_ACCOUNT_NO_EXIST = 104;
  public static final Integer CODE_ERR_PARAM_ID = 102;
  public static final Integer CODE_ERR_IMAGE_NAME = 150;
  public static final Integer CODE_ERR_WAYBILL_UNKNOWN = 151;
  public static final Integer CODE_ERR_SMS_INVALID = 152;
  public static final Integer CODE_ERR_SOURCEING_NOT_EXIST = 153;
  public static final Integer CODE_ERR_DRIVER_CAR_STATUS = 154;
  public static final Integer CODE_ERR_DRIVER_NOT_SHORT_TERM = 155;
  public static final Integer CODE_ERR_OLD_PASSWORD_ERR = 156;
  public static final Integer CODE_ERR_NOT_BANK_CARD = 157;
  public static final Integer CODE_ERR_SOURCEING_STATUS_ERR = 158;
  public static final Integer CODE_ERR_BANK_CARD_EXIST = 159;
  public static final Integer CODE_ERR_USER_ERR_STATUS = 160;
  public static final Integer CODE_ERR_COMPANY_NAME_ERR = 161;
  public static final Integer CODE_ERR_STATEMENT_COMPANY_ERR = 162;
  public static final Integer CODE_ERR_DRIVER_CARDETAIL_STATUS = 163;
  public static final Integer CODE_ERR_PASSWORD_ERR = 164;
  public static final Integer CODE_ERR_ENTITY_IS_NULL = 165;
  public static final Integer CODE_ERR_SOURCING_INFO_ERR = 167;
  public static final Integer CODE_ERR_STATEMENT_COMPANY_NOT_EXIST = 168;
  public static final Integer CODE_ERR_CHAINS_NOT_EXIST = 169;
  public static final Integer CODE_ERR_ADDRESS_NOT_EXIST_STATEMENTCOMPANY = 170;
  public static final Integer CODE_SELECT_DRIVER_FULL = 171;
  public static final Integer CODE_NOT_SELECT_DRIVER = 172;
  public static final Integer CODE_NOT_DRIVER_SELECT = 173;
  public static final Integer CODE_WAYBILL_SUCCESS = 174;

  //运营端角色id
  public static final String CODE_ADMIN_ROLE_ID = "BQO1aXuV2vLPdKMVLCfWE8MIkD9Mhyks";
  public static final String CODE_WEIXIU_ROLE_ID = "oLOAla8QHsgGsfpgwek47Mgyfgmvxpwn";
  public static final String CODE_YOUQI_ROLE_ID = "lXguLQr30LO6pcYcFe9snkjbqN4lA9HT";
  public static final String CODE_QIPEI_ROLE_ID = "SqWRSbOHelXa6DwBi9uEMAPaWKCsAy9S";
  public static final String CODE_COMPONY_ROLE_ID = "TuXdtaapxOxPZ3ZO9U298mA7A9kmrcwH";
  public static final String CODE_STATEMENT_ROLE_ID = "MrJtT4LpiarIckNIlxsCFk7XK4rnIOPs";
  //融云配置
//    public static final String RONGYUN_ACCOUNT_SID = "8a216da85d158d1b015d3600770b0dd3";
//    public static final String RONGYUN_ACCOUNT_TOKEN = "84c65dd3d110441e9df9be6828d64615";
//    public static final String RONGYUN_APP_ID = "8a216da85d158d1b015d3600774a0dd7";
  public static final String RONGYUN_ACCOUNT_SID = "8aaf07085f333ef6015f38a0fea00456";
  public static final String RONGYUN_ACCOUNT_TOKEN = "7c4929a688374ec2aa598d98f5a0f2ee";
  public static final String RONGYUN_APP_ID = "8aaf07085f333ef6015f38a0fee8045a";


  //后台运营端消息提示
  public static final Integer CODE_ERR_AREA_REPEAT = 1000;    //区域已存在
  public static final Integer CODE_ERR_TYPE_NULL = 1001;    //类型不允许为空
  public static final Integer CODE_ERR_USER_NAME = 1002;    //用户名或密码不能为空
  public static final Integer CODE_ERR_NAME_NULL = 1003;    //单位名称不允许为空
  public static final Integer CODE_ERR_UNIT_ADDRESS = 1004;    //单位地址已存在
  public static final Integer CODE_ERR_PHONE_NUMBER = 1005;    //手机号不能为空
  public static final Integer CODE_ERR_PASSWORD = 1006;    //密码不能为空
  public static final Integer CODE_ERR_QUESTION_TITLE = 1007;    //问题标题已存在
  public static final Integer CODE_ERR_RNAME_NULL = 1008;    //角色名不能为空


  public static Map<Integer, String> messageMap = new HashMap<Integer, String>() {
    private static final long serialVersionUID = 769847890431L;

    {
      // 查询成功
//            put(CODE_NORMAL, "操作成功");
      put(SUCCESS, "成功");
      put(FAIL, "失败");
      put(UNAUTHORIZED, "未认证（签名错误）");
      put(NOT_FOUND, "接口不存在");
      put(INTERNAL_SERVER_ERROR, "系统繁忙");

      // 查询失败
      put(CODE_ERR_UNKNOWN, "系统繁忙");
      put(CODE_ERR_PARAM, "参数异常");
      put(CODE_ERR_PARAM_ID, "参数异常: 缺少id");
      put(CODE_ERR_IMAGE_NAME, "图片拓展名异常");
      put(CODE_ERR_WAYBILL_UNKNOWN, "未知运单");
      put(CODE_ERR_SMS_INVALID, "验证码不正确");
      put(CODE_ERR_ACCOUNT_EXIST, "账号已存在");
      put(CODE_ERR_ACCOUNT_NO_EXIST, "账号不存在");

      //后台运营端消息提示
      put(CODE_ERR_AREA_REPEAT, "该区域已存在");
      put(CODE_ERR_TYPE_NULL, "类型不允许为空");
      put(CODE_ERR_USER_NAME, "用户名或密码不能为空");
      put(CODE_ERR_PHONE_NUMBER, "手机号不能为空");
      put(CODE_ERR_PASSWORD, "密码不能为空");
      put(CODE_ERR_NAME_NULL, "单位名称不允许为空");
      put(CODE_ERR_UNIT_ADDRESS, "单位地址已存在");
      put(CODE_ERR_SOURCEING_NOT_EXIST, "货源不存在");
      put(CODE_ERR_DRIVER_CAR_STATUS, "您的信息未通过审核");
      put(CODE_ERR_DRIVER_CARDETAIL_STATUS, "车辆信息未完善");
      put(CODE_ERR_DRIVER_NOT_SHORT_TERM, "您在当前区域未短期租赁承运公司");
      put(CODE_ERR_OLD_PASSWORD_ERR, "旧密码不正确");
      put(CODE_ERR_PASSWORD_ERR, "密码不正确");
      put(CODE_ERR_NOT_BANK_CARD, "未绑定银行卡信息");
      put(CODE_ERR_SOURCEING_STATUS_ERR, "货源已被抢完或者货源已取消");
      put(CODE_ERR_BANK_CARD_EXIST, "已绑定银行卡");
      put(CODE_ERR_USER_ERR_STATUS, "用户信息未审核");
      put(CODE_ERR_COMPANY_NAME_ERR, "您所属的运输公司不存在或未审核通过");
      put(CODE_ERR_STATEMENT_COMPANY_ERR, "货源可挂靠的结算公司不存在");
      put(CODE_ERR_QUESTION_TITLE, "问题标题已存在");

      put(CODE_ERR_ENTITY_IS_NULL, "查询结果为空");
      put(CODE_ERR_SOURCING_INFO_ERR, "货源信息错误");


      put(CODE_ERR_STATEMENT_COMPANY_NOT_EXIST, "结算公司不存在");
      put(CODE_ERR_CHAINS_NOT_EXIST, "连锁店不存在");
      put(CODE_ERR_ADDRESS_NOT_EXIST_STATEMENTCOMPANY, "所属区域没有结算公司");
      put(CODE_SELECT_DRIVER_FULL, "货源需要的车辆已经选择够");
      put(CODE_NOT_SELECT_DRIVER, "您还没有确认过司机");
      put(CODE_NOT_DRIVER_SELECT, "暂无司机报价");
      put(CODE_WAYBILL_SUCCESS, "该运单已完成交易");
      put(CODE_ERR_RNAME_NULL, "角色名不能为空");
    }
  };


}
