package com.topad.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.topad.R;
import com.topad.TopADApplication;
import com.topad.amap.ToastUtil;
import com.topad.bean.BaseBean;
import com.topad.bean.LoginBean;
import com.topad.net.HttpCallback;
import com.topad.net.http.RequestParams;
import com.topad.util.Constants;
import com.topad.util.Md5;
import com.topad.util.SharedPreferencesUtils;
import com.topad.util.Utils;
import com.topad.view.customviews.TitleView;

/**
 * ${todo}<登录页面>
 *
 * @author lht
 * @data: on 15/11/2 16:35
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String LTAG = LoginActivity.class.getSimpleName();
    // 上下文
    private Context mContext;
    // 顶部布局
    private TitleView mTitleView;
    // 用户名
    private EditText mETUserName;
    // 密码
    private EditText mETPassword;
    // 登录
    private Button mBTLogin;
    // 用户名-手机号
    private String mUserName;
    // 密码
    private String mPassword;

    @Override
    public int setLayoutById() {
        mContext = this;
        return R.layout.activity_login;
    }

    @Override
    public View setLayoutByView() {
        return null;
    }

    @Override
    public void initViews() {
        mTitleView = (TitleView) findViewById(R.id.title);
        mETUserName = (EditText) findViewById(R.id.et_username);
        String phone=getIntent().getStringExtra("phone");
        if(!Utils.isEmpty(phone)){
            mUserName=phone;
            mETUserName.setText(phone);
        }
        mETPassword = (EditText) findViewById(R.id.et_password);
        mBTLogin = (Button) findViewById(R.id.btn_login);

        mBTLogin.setOnClickListener(this);

        // 设置登录按钮
        setNextBtnState(false);
    }

    @Override
    public void initData() {
        showView();
    }

    /**
     * 显示数据
     */
    private void showView() {
        // 设置顶部标题布局
        mTitleView.setTitle("登录");
        mTitleView.setLeftVisiable(false);
        mTitleView.setRightVisiable(false);
        mTitleView.setRightClickListener(new TitleRightOnClickListener(), "注册", 18);

        // 设置手机号
        mETUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String data = getData(mETUserName);
                if (!Utils.isEmpty(data)) {
                    mUserName = data;
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

                String phone = getData(mETUserName);
                String password = getData(mETPassword);

                if (!Utils.isEmpty(phone) && phone.length() > 10
                        && !Utils.isEmpty(password) && password.length() > 5) {
                    setNextBtnState(true);
                } else {
                    setNextBtnState(false);
                }

            }
        });

        // 设置手机号
        mETPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String data = getData(mETPassword);
                if (!Utils.isEmpty(data)) {
                    mPassword = data;
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

                String phone = getData(mETUserName);
                String password = getData(mETPassword);

                if (!Utils.isEmpty(phone) && phone.length() > 10
                        && !Utils.isEmpty(password) && password.length() > 5) {
                    setNextBtnState(true);
                } else {
                    setNextBtnState(false);
                }

            }
        });
    }

    /**
     * 顶部布局--注册按钮事件监听
     */
    public class TitleRightOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // 注册
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.forget_psw:
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
                break;
            // 登录
            case R.id.btn_login:
                TopADApplication.getSelf().logout();
                // 拼接url
                StringBuffer sb = new StringBuffer();
                sb.append(Constants.getCurrUrl()).append(Constants.URL_LOGIN).append("?");
                String url = sb.toString();
                RequestParams rp = new RequestParams();
                rp.add("mobile", mUserName);
                rp.add("pwd", Md5.md5s(mPassword));

                postWithLoading(url, rp, false, new HttpCallback() {
                    @Override
                    public <T> void onModel(int respStatusCode, String respErrorMsg, T t) {
                        LoginBean login = (LoginBean) t;
                        if (login != null) {
                            // 本地缓存token
                            if (!Utils.isEmpty(login.getToken())) {
                                SharedPreferencesUtils.clearCurAccount(mContext);
                                SharedPreferencesUtils.getInstance(mContext, mUserName);
                                SharedPreferencesUtils.put(mContext, SharedPreferencesUtils.KEY_TOKEN, login.getToken());
                            }

                            // 本地存储userid
                            if (!Utils.isEmpty(login.getUserid())) {
                                SharedPreferencesUtils.put(mContext, SharedPreferencesUtils.USER_ID, login.getUserid());
                            }
                            TopADApplication.getSelf().bindUmeng();//绑定友盟
                            // 本地存储mobienumber
                            SharedPreferencesUtils.put(mContext, SharedPreferencesUtils.USER_PHONR, mUserName);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }

                    @Override
                    public void onFailure(BaseBean base) {
                        int status = base.getStatus();// 状态码
                        String msg = base.getMsg();// 错误信息
                        ToastUtil.show(mContext, "status = " + status + "\n"
                                + "msg = " + msg);
                    }
                }, LoginBean.class);


                break;
            default:
                break;
        }
    }


    /**
     * 去除EditText的空格
     *
     * @param et
     * @return
     */
    public String getData(EditText et) {
        String s = et.getText().toString();
        return s.replaceAll(" ", "");
    }

    /**
     * 设置下一步按钮
     *
     * @param flag
     */
    private void setNextBtnState(boolean flag) {
        if (mBTLogin == null)
            return;
        mBTLogin.setEnabled(flag);
        mBTLogin.setClickable(flag);
    }
}
