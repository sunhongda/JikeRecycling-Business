package com.buslink.busjie.driver.constant;

public class Net {

    public final static String HOST="http://123.56.67.27:28081/"; // 测试
   // public final static String HOST = "http://app.busj.cn/"; // 正式

    public final static String BUSLK = HOST+"buslk/";
    public final static String IMGURL = HOST + "carApp";
    public final static String IDENTIFYMESSAGE = HOST + "buslk/getidentifying.htm";
    public final static String DRIVERMANUALLOGIN = HOST + "buslk/drivermanuallogin.htm"; // 登录
   // public final static String USER_GPS = HOST + "buslk/usergps.htm";
   public final static String POSITIONREPORTING=BUSLK +"positionreporting.htm";//位置上报接口
    public final static String FEEDBACK = HOST + "buslk/submitfeedback.htm"; // 意见反馈
    public final static String IMAGEUPFILEAND = HOST + "buslk/imageupfileIos.htm"; // 图片上传
    public final static String SIGNUP = HOST + "buslk/driverregisterdriver.htm"; // 注册
    public final static String HOME = HOST + "buslk/driverhomepage.htm";
    public final static String ORDERCONTROL = HOST + "buslk/ordercontrol.htm"; // 接单控制
    public final static String ORDERLIST = HOST + "buslk/dmyorderlst.htm"; // 订单列表
    public final static String ORDERDETAIL = HOST + "buslk/dmyorderdetail.htm"; // 订单详情
    public final static String ORDERPRICE = HOST + "buslk/orderprice.htm"; // 司机报价
    public final static String TRIPLIST = HOST + "buslk/dmytriplst.htm"; // 行程列表
    public final static String TRIPDETAIL = HOST + "buslk/dmytripdetail.htm"; // 行程详情
    public final static String DRIVERPROFILE = HOST + "buslk/driverregistermsg.htm"; // 司机注册信息
    public final static String UPGRADE = HOST + "buslk/upgradenew.htm";
    public final static String MY_WALLET = HOST+"buslk/mywallet.htm";
    public final static String SET_UP_PWD = HOST + "buslk/setuppwd.htm"; // 设置密码
    public final static String APPLYFOR = HOST + "buslk/applyfor.htm"; // 申请提现
    public final static String BANKLIST = HOST + "buslk/bankcardlst.htm"; // 银行卡列表
    public final static String INCOMELIST = HOST + "buslk/totalrevenuelst.htm"; // 收入明细列表
    public final static String ADDBANK = HOST + "buslk/addbankcard.htm"; // 添加银行卡
    public final static String DELETEBANK = HOST + "buslk/editbankcard.htm"; // 银行卡解绑
    public final static String JUDGEPASSENGER = HOST + "buslk/pasrevaluate.htm"; // 评价乘客
    public final static String VERIFICATION = HOST + "buslk/verificationpwd.htm";
    public final static String APPLY_FOR = HOST + "buslk/applyfor.htm";
    public final static String WITHDRAWLIST = BUSLK + "withdrawalsLst.htm"; // 提现记录
    public final static String ADIMAGESLST = HOST + "buslk/adimageslst.htm";//轮播图
    public final static String ORDER_CONTROL = HOST + "buslk/ordercontrol.htm";//接单控制
    public final static String FRONTEND_ERROR = BUSLK+"frontenderror.htm"; // BUG提交
    public final static String AD = BUSLK + "carfreead.htm";


    public final static String ADLIST = BUSLK + "orderadlist.htm"; // 广告列表
    public final static String DELETE_AD = BUSLK + "delcarfreead.htm";
    public final static String SEARCH = BUSLK + "searchorder.htm"; // 82.搜索
    public final static String DELETE_ORDER = BUSLK + "ddelorder.htm"; // 84.删除订单
    public final static String UPDATE_DRIVER = BUSLK + "updatedriver.htm"; // 85.
    public final static String UPDATE_CAR = BUSLK + "updatecar.htm"; // 86.
    public final static String VIEW_DRIVER = BUSLK + "viewdriver.htm"; //87.
    public final static String VIEW_CAR = BUSLK + "viewcar.htm"; // 88.
    public final static String CAR_IMAGES = BUSLK + "carimages.htm"; // 100.查询所有车辆照片
    public final static String SET_CAR_HEAD_IMG = BUSLK + "setcarheadimg.htm"; // 101.车辆头像切换
    public final static String LOGIN_SERVICE = BUSLK + "loginservice.html";//巴士互联服务协议
    public final static String SERVICE_TERMS = BUSLK + "serviceterms.html";//巴士互联司机服务条款
    public final static String E_COMMERCE = BUSLK + "E-commerce.html";//巴士互联电子商务服务协议

    public final static String CONFIRM_COMPLETION=BUSLK+"confirmcompletion.htm";//确认完成二次支付  version orid
             //拼车
    public final static String CARPOOLING=BUSLK+"carpooling_carseatpublish.htm";//空车发布页
    public final static String CARPOOLING_LIST=BUSLK+"carpooling_carseatpublishlist.htm";//发布列表页
    public final static String CARPOOLING_CARSEATPUBLISHDEL =BUSLK+"carpooling_carseatpublishdel.htm";//司机删除过期的余座广告；
    public final static String CARPOOLING_CARSEATPUBLISHDETAIL =BUSLK+"carpooling_carseatpublishdetail.htm";//司机发布余座信息详情；
    public final static String CARPOOLING_ORDERPAYLIST =BUSLK+"carpooling_orderpaylist.htm";//司机查看订票乘客信息；
    public final static String CARPOOLING_SELLTICKET =BUSLK+"carpooling_sellticket.htm";//司机更换卖票状态； 0.正在售票；1.票已售完；2.停止
                                                                                        // 售票；3. 票已过期; 默认0  票状态，前端只能进行0和2之间切换；

    public final static String CARPOOLING_ISARRIVE=BUSLK+"carpooling_isarrive.htm";//司机给乘客签到；

    public final static String CHARTEREDBUS_CONFIRMATIONORDER=BUSLK+"charteredbus_confirmationorder.htm";//包车  乘客向司机发送用车请求，针对包车订单，司机确认接单；

    public final static String CHARTEREDBUS_DMYORDERDETAIL=BUSLK+"charteredbus_dmyorderdetail.htm";//包车未达成交易的订单详情

    public final static String CHARTEREDBUS_DMYTRIPDETAIL=BUSLK+"charteredbus_dmytripdetail.htm";//包车达成交易的订单详情

    public final static String WEATHER = "http://op.juhe.cn/onebox/weather/query";//天气接口

   public final static String LONINSTATEHANDLE=BUSLK + "loninstatehandle.htm";//退出登录接口



}

























