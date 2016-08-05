package com.buslink.busjie.driver.constant;

/**
 * Created by Administrator on 2015/9/5.
 */
public class JsonName {
    public final static String STATUS="status";
    public final static String DATA="data";
    public final static String MSG="msg";

    public final static String IS_UPDATE_QUOTED="isupdatequoted";//是否可以修改报价  0不可以  1可以	Int

    public final static String CAR_AGE="carage";//车龄
    public final static String DAY="day";//codes中途径点的day
    public final static String  DRIVING_RANGE="drivingrange";
    public final static String TYPE_OF_CAR= "typeofcar";//用车类型 1单程2往返
    public final static String INTENT_BID="intentbid";//意向出价
    public final static String IMG_SUM="imgsum";//行程单照片总数
    public final static String IMG_LST="imglst";//行程单照片集合

    public final static String F_Number="fnumber";//航班号

    public final static String PAY_TYPE="paytype";//支付次数 1一次性支付 2分为两次
    //二次支付新添加字段
    public final static String ONE_PAY_MONEY="onepaymoney";
    public final static String TWO_PAY_MONEY="twopaymoney";
    public final static String PTYPE_TWO="ptype_two";
    public final static String PAY_STATE="paystate";

    public final static String UID="uid";

    public final static String IS_UP_GRADE="isupgrade";
    public final static String IS_QZ_UP_GRADE="isqzupgrade";
    public final static String URL="url";

    public final static String PROCESS="process";
    public final static String OMSG="omsg";
    public final static String STATE="state";
    public final static String IS_REG="isreg";
    public final static String AMSG="amsg";

    public final static String ADD_TIME="addtime";
    public final static String ORDER_LIST="orderlist";
    public final static String START_DATE = "startdate";
    public final static String START_TIME="starttime";
    public final static String START_PROVINCE = "startprovince";
    public final static String START_CITY = "startcity";
    public final static String END_PROVINCE="endprovince";
    public final static String END_CITY="endcity";
    public final static String END_DATE="enddate";
    public final static String END_TIME="endtime";
    public final static String PROVINCE="province";
    public final static String CITY="city";
    public final static String ADDRESS="address";
    public final static String WAYDATE="waydate";
    public final static String WAYSUM="waysum";
    public final static String ORDERSTATE="orderstate";
    public final static String IS_ORDER="isorder";
    public final static String IS_TRIP="istrip";
    public final static String CITY_TWO="citytwo";

    public final static String IMGLST="imglst";
    public final static String IS_CONTROL="iscontrol";



    public final static String ORDERNO="orderno";
    public final static String PTYPE="ptype";
    public final static String TOTAL="total";

    public final static String END_HOUR="endhour";
    public final static String ODAYS="odays";
    public final static String DAYS="days";
    public final static String CAR_TYPE="cartype";
    public final static String START_ADDRESS="startaddress";
    public final static String END_ADDRESS="endaddress";
    public final static String CODES="codes";
    public final static String CARS="cars";
    public final static String ORID="orid";
    public final static String REQ_CARID="reqcarid";
    public final static String ORDER_TYPE="ordertype";
    public final static String MESSAGE="message";
    public final static String TRAIN_NUMBER="trainnumber";
    public final static String F_NUMBER="flightnumber";
    public final static String IS_LIVE="islive";
    public final static String IS_EAT="iseat";
    public final static String CAR_NUMBER="carnumber";
    public final static String DVALUATE="dvaluate";
    public final static String DVALUATE_TIME="dvaluatetime";
    public final static String DSTAR="dstar";
    public final static String PVALUATE="pvaluate";
    public final static String PVALUATETIME="pvaluatetime";
    public final static String PSTAR="pstar";
    public final static String IMG="img";
    public final static String NAME="name";
    public final static String PHONE="phone";
    public final static String COMPANY="company";
    public final static String QUOTED="quoted";
    public final static String CARID="carid";

    public final static String USER_NAME="username";
    public final static String GENDER="gender";
    public final static String ENT_NAME="entname";
    public final static String PATH="path";

    public final static String NICK_NAME="nickname";
    public final static String PRID="prid";

    public final static String TIME="time";

    public final static String PUSH_COUNT="pushcount";

    public final static String DRIVER_LIST="driverlist";
    public final static String CAR_IMG="carimg";
    public final static String IS_QUOTE="isquote";
    public final static String PRICE_ID="priceid";

    public final static String SEAT="seat";
    public final static String DRIVING="driving";
    public final static String SCORE="score";
    public final static String DRIVER_IMG="driverimg";

    public final static String TN="tn";

    public final static String START="start";
    public final static String VALUATE="valuate";

    public final static String IS_SET_UPPWD="issetuppwd";
    public final static String BANK_CARD_SUM="bankcardsum";
    public final static String MONEY_SUM="moneysum";
    public final static String IS_CASH="iscash";
    public final static String PID="pid";

    public final static String PWD="pwd";
    public final static String MONEY="money";
    public final static String BID="bid";
    public final static String BANK="bank";
    public final static String ACCOUNT="account";
    public final static String PUSHID = "pushid";

    public final static String CAR_HEAD = "carhead";
    public final static String CAR_IMG_TWO = "carimgtwo";
    public final static String CAR_IMG_THREE= "carimgthree";
    public final static String CAR_IMG_FOUR= "carimgfour";
    public final static String DID= "did";
    public final static String RESOURCE_TYPE= "resourcetype";
    public final static String MTYPE="mtype";

    public final static String LON="lon";
    public final static String LAT="lat";

    public final static String SLON="slon";
    public final static String SLAT="slat";
    public final static String ELON="elon";
    public final static String ELAT="elat";
    //拼车
    public final static String ORDERREQUIRESTATE="orderrequirestate";
   // public final static String PID="pid";余座广告
   public final static String PRICE="price";//拼车价格

   // public final static String TOTAL="total";要卖的座位数
    public final static String SEATTOTAL="seattotal";//要卖的座位数
    public final static String DURATION="duration";//预计到达时间
    public final static String ISARRIVE="isarrive";//司机检票0 未检票（乘客未到）；1已检票（乘客已到）; 默认0
    public final static String OID="oid";//乘客需求id(carpooling_order表主键id)
    public final static String ISEXIST="isexist";//isexist	切换票状态功能是否存在  0不存在  1 存在  备注：isexist=0  按钮为灰色点击不调接口
    public final static String PUSHNUMBER="pushnumber";//司机发布的消息编号
    public final static String ISDEL="isdel"; //isdel  是否可删除 0不可删除  1可删除 司机删除过期的余座广告，或着离出发时间已经相差1个星期的广告；
    public final static String ISCARPOOLING="iscarpooling";	//拼车单是否有新消息 0无1有


    //包车
    public final static String CDAY_TYPE="cdaytype";//包车日类型 1半日租 2全日租
    public final static String DISTANCE="distance";//全日100公里；半日50公里
    //  public final static String TIME="time";//全日8小时；半日4小时；
    // public final static String PRICE="price";//商务全日800元/辆;商务半日400元/辆
    // public final static String CARTYPE="cartype";//车类型1.商务    2.15座大巴   3.20座中巴   4.39座中巴   5.51座中巴  6.55座中巴
    public final static String CAR_SUM="carsum";//车辆需求    几辆
    public final static String PRICE_SUM="pricesum";//总价    priceone*N辆车
    public final static String PRICE_ONE="priceone";//单价    pricesum/ carsum= priceone元/辆
    public final static String CAR_NAME="carname";//车类型名称
    public final static String CAR_ITYPEPE_IMG="caritypeimg";//车类型图片
    public final static String CCAR_TYPE="ccartype";////车类型1.7座商务    2.15座大巴   3.20座中巴   4.39座中巴   5.51座中巴  6.55座中巴

   // public final static String REFUNDMONEY="refundmoney";//退单金额(乘客收到的退款资金)
    public final static String DRIVERPROFIT="driverprofit";//退单金额(司机收到的赔偿金)

    //天气用到的参数
    public static final String CITY_NAME = "cityname";//城市名称
    public static final String KEY = "key";//密钥
    public static final String WEATHER_KEY="860d7ac5ba973608f50c8118f8b9a916";
    public static final String REASON = "reason";//理由
    public static final String RESULT = "result";//结果
    public static final String REAL_TIME = "realtime";//真实时间
    public static final String WEATHER = "weather";//天气
    public static final String INFO = "info";//信息
    public static final String TEMPERATURE = "temperature";//温度

//评价
public static final String STAR="star";//星级别0,  1,2,3,4,5

    //退出登录用到
    public static final String PAGESTAGE = "pagestate";//1.直接进入软件界面 2退出软件  3进入登录界面
    public static final String OPERATIONTYPE="operationtype";//1进入软件    2退出软件

}
