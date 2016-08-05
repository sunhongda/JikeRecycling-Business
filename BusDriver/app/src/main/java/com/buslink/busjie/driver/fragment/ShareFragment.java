package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.ThirdParty;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareFragment extends LevelTwoFragment {

    private UMSocialService mController;

    @Override
    public String getTitle() {
        return "分享有奖";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_share;
    }

    @Override
    protected void initView() {
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();

        // TODO 微信
        UMWXHandler wxHandler1 = new UMWXHandler(getActivity(), ThirdParty.WXAppID,ThirdParty.WXAppSecret);
        wxHandler1.setTargetUrl("http://www.busbond.com/");
        wxHandler1.addToSocialSDK();
        UMWXHandler wxHandler2 = new UMWXHandler(getActivity(), ThirdParty.WXAppID,ThirdParty.WXAppSecret);
        wxHandler2.setTargetUrl("http://www.busbond.com/");
        wxHandler2.setToCircle(true);
        wxHandler2.addToSocialSDK();
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), "1104850122","0W5Fp33UKgqGEwwS");
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("巴士互联，你我同行");
        qzone.setTargetUrl("http://www.busbond.com/");
        qzone.setTitle("巴士互联，你我同行");
        qzone.setShareMedia(new UMImage(getActivity(),R.mipmap.logo));
        mController.setShareMedia(qzone);
        qZoneSsoHandler.addToSocialSDK();
        RenrenSsoHandler renrenSsoHandler=new RenrenSsoHandler(getActivity(),"201874","8401c0964f04a72a14c812d6132fcef","3bf66e42db1e4fa9829b955cc300b737");
        mController.getConfig().setSsoHandler(renrenSsoHandler);
        renrenSsoHandler.addToSocialSDK();

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),"1104850122", "0W5Fp33UKgqGEwwS");
        qqSsoHandler.setTargetUrl("http://www.busbond.com/");
        qqSsoHandler.addToSocialSDK();
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent("巴士互联，你我同行");
        qqShareContent.setTitle("巴士互联，你我同行");
        qqShareContent.setShareMedia(new UMImage(getActivity(), R.mipmap.logo));
        qqShareContent.setTargetUrl("http://www.busbond.com/");
        mController.setShareMedia(qqShareContent);
        SmsShareContent sms = new SmsShareContent();
        mController.setShareMedia(sms);
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
        MailShareContent mail = new MailShareContent(new UMImage(getActivity(), R.mipmap.logo));
        mail.setTitle("巴士互联，你我同行");
        mail.setShareContent("巴士互联，你我同行 \n" +
                "http://www.busbond.com/");
        // 设置tencent分享内容
        mController.setShareMedia(mail);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,
                SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA,SHARE_MEDIA.EMAIL);
        mController.setShareContent("巴士互联，你我同行 \nhttp://www.busbond.com/");
        mController.setShareMedia(new UMImage(getActivity(),R.mipmap.logo));
    }

    @OnClick(R.id.bt) void shareTwo(){
        mActivity.startFragment(BackActivity.class, TwoShareFragment.class);
    }

    @OnClick(R.id.bt_1) void share() {
        mController.openShare(mActivity, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
