package com.topad.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.topad.R;
import com.topad.TopADApplication;
import com.topad.amap.ToastUtil;
import com.topad.bean.AdDetailsBean;
import com.topad.bean.BaseBean;
import com.topad.bean.GrabSingleBean;
import com.topad.bean.GrabSingleListBean;
import com.topad.net.HttpCallback;
import com.topad.net.http.RequestParams;
import com.topad.util.Constants;
import com.topad.util.Utils;
import com.topad.view.customviews.MyGridView;
import com.topad.view.customviews.TitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ${todo}<我的抢单详情页>
 *      state－0我要提交，1抢单成功，2未选择抢单人，3项目已取消，4已选择其他
 * @author lht
 * @data: on 15/10/28 16:32
 */
public class GrabSingleDetailsActivity extends BaseActivity implements View.OnClickListener{
    private static final String LTAG = GrabSingleDetailsActivity.class.getSimpleName();
    /** 上下文 **/
    private Context mContext;
    /** 顶部布局 **/
    private TitleView mTitleView;
    /** 案例 **/
    private MyGridView mGridView;
    /** 名称 **/
    private TextView mName;
    /** 状态 **/
    private TextView mTVState;
    /** 价钱 **/
    private TextView mMoney;
    /** 内容 **/
    private TextView mContent;
    /** 地址 **/
    private TextView mAddress;
    /** 类型 **/
    private TextView mTVType;
    /** 时间 **/
    private TextView mTVTime;

    /** 我要提交 **/
    private RelativeLayout mLYDetails;
    /** 详细内容 **/
    private TextView mDetailsContent;
    /** 提交 **/
    private Button mSubmit;
    /** 数据源 **/
    private ArrayList<HashMap<String,String>> list;

    /** 抢单成功 **/
    private LinearLayout mLYSuccess;
    /** 申诉 **/
    private LinearLayout mLYAppeal;
    /** 未选择抢单人 **/
    private LinearLayout mLYNotSelect;
    /** 未选择抢单人数 **/
    private TextView mNotSelectNum;
    /** 项目已取消 **/
    private LinearLayout mLYCancel;
    /** 已选择其他 **/
    private LinearLayout mLYOther;

    /** 状态 **/
    private String state;
    /** 数据元 **/
    private GrabSingleBean grabSingleBean;

    @Override
    public int setLayoutById() {
        mContext = this;
        return R.layout.activity_grab_single_details;
    }

    @Override
    public View setLayoutByView() {
        return null;
    }

    @Override
    public void initViews() {
        mTitleView = (TitleView) findViewById(R.id.title);

        mName = (TextView) findViewById(R.id.tv_name);
        mTVState = (TextView) findViewById(R.id.tv_state);
        mMoney = (TextView) findViewById(R.id.tv_price);
        mContent = (TextView) findViewById(R.id.tv_content);
        mAddress = (TextView) findViewById(R.id.tv_address);
        mTVType = (TextView) findViewById(R.id.tv_type);
        mTVTime = (TextView) findViewById(R.id.tv_time);

        // 我要提交
        mLYDetails = (RelativeLayout) findViewById(R.id.ly_details);
        mGridView = (MyGridView) findViewById(R.id.gv_in);
        mDetailsContent = (TextView) findViewById(R.id.tv_need_details_content);
        mSubmit = (Button) findViewById(R.id.btn_submit);

        // 抢单成功
        mLYSuccess = (LinearLayout) findViewById(R.id.ly_success);
        mLYAppeal = (LinearLayout) findViewById(R.id.ly_appeal);

        // 未选择抢单人
        mLYNotSelect = (LinearLayout) findViewById(R.id.ly_not_select);
        mNotSelectNum = (TextView) findViewById(R.id.tv_not_select_num);

        // 项目已取消
        mLYCancel = (LinearLayout) findViewById(R.id.ly_cancel);
        // 已选择其他
        mLYOther = (LinearLayout) findViewById(R.id.ly_other);

        mSubmit.setOnClickListener(this);
        mLYAppeal.setOnClickListener(this);
    }

    @Override
    public void initData() {
        // 接收数据
        Intent intent = getIntent();
        if (intent != null) {
            grabSingleBean = (GrabSingleBean) intent.getSerializableExtra("data_details");
            state = intent.getStringExtra("state");
        }

        showView();
    }

    public void showView() {
        // 设置title
        mTitleView.setTitle(grabSingleBean.getTitle());
        mTitleView.setLeftClickListener(new TitleLeftOnClickListener());

        if("1".equals(grabSingleBean.getIspay())){
            mTVState.setVisibility(View.VISIBLE);
        }else{
            mTVState.setVisibility(View.GONE);
        }

        if("0".equals(state)){// 我要提交
            mLYDetails.setVisibility(View.VISIBLE);
            mLYSuccess.setVisibility(View.GONE);
            mLYAppeal.setVisibility(View.GONE);
            mLYNotSelect.setVisibility(View.GONE);
            mLYCancel.setVisibility(View.GONE);
            mLYOther.setVisibility(View.GONE);
        }else if("1".equals(state)){// 抢单成功
            mLYDetails.setVisibility(View.GONE);
            mLYSuccess.setVisibility(View.VISIBLE);
            mLYAppeal.setVisibility(View.VISIBLE);
            mLYNotSelect.setVisibility(View.GONE);
            mLYCancel.setVisibility(View.GONE);
            mLYOther.setVisibility(View.GONE);
        }else if("2".equals(state)){// 未选择抢单人
            mLYDetails.setVisibility(View.GONE);
            mLYSuccess.setVisibility(View.GONE);
            mLYAppeal.setVisibility(View.GONE);
            mLYNotSelect.setVisibility(View.VISIBLE);
            mLYCancel.setVisibility(View.GONE);
            mLYOther.setVisibility(View.GONE);
        }else if("3".equals(state)){// 项目已取消
            mLYDetails.setVisibility(View.GONE);
            mLYSuccess.setVisibility(View.GONE);
            mLYAppeal.setVisibility(View.GONE);
            mLYNotSelect.setVisibility(View.GONE);
            mLYCancel.setVisibility(View.VISIBLE);
            mLYOther.setVisibility(View.GONE);
        }else if("4".equals(state)){// 已选择其他
            mLYDetails.setVisibility(View.GONE);
            mLYSuccess.setVisibility(View.GONE);
            mLYAppeal.setVisibility(View.GONE);
            mLYNotSelect.setVisibility(View.GONE);
            mLYCancel.setVisibility(View.GONE);
            mLYOther.setVisibility(View.VISIBLE);
        }

        // 名字
        if(!Utils.isEmpty(grabSingleBean.getTitle())){
            mName.setText(grabSingleBean.getCompanyname());
        }

        // 价格
        if(!Utils.isEmpty(grabSingleBean.getBudget())){
            SpannableStringBuilder ssb = new SpannableStringBuilder("￥" + grabSingleBean.getBudget());
            mMoney.setText(ssb.toString());
        }

        // 介绍
        if(!Utils.isEmpty(grabSingleBean.getDetail())){
            mContent.setText(grabSingleBean.getDetail());
        }

        // 地址
        if(!Utils.isEmpty(grabSingleBean.getAddress())){
            mAddress.setText(grabSingleBean.getAddress());
        }

        // 类别
        if(!Utils.isEmpty(grabSingleBean.getType1())
                && !Utils.isEmpty(grabSingleBean.getType2())){
            SpannableStringBuilder ssb = new SpannableStringBuilder("类型：" + grabSingleBean.getType1() + "-" + grabSingleBean.getType2());
            mTVType.setText(ssb.toString());
        }

        // 时间
        if(!Utils.isEmpty(grabSingleBean.getAdddate())){
            String[] sourceStrArray = grabSingleBean.getAdddate().split(" ");
            mTVTime.setText(sourceStrArray[0]);
        }

        //为GridView设置适配器
        mGridView.setAdapter(new MyAdapter(this, setData()));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            // 提交
            case R.id.btn_submit:
                // 拼接url
                StringBuffer sb = new StringBuffer();
                sb.append(Constants.getCurrUrl()).append(Constants.URL_NEED_GETIT).append("?");
                String url = sb.toString();
                RequestParams rp=new RequestParams();
                rp.add("needid", grabSingleBean.getId());
                rp.add("userid", TopADApplication.getSelf().getUserId());
                rp.add("token", TopADApplication.getSelf().getToken());
                postWithLoading(url, rp, false, new HttpCallback() {
                    @Override
                    public <T> void onModel(int respStatusCode, String respErrorMsg, T t) {
                        BaseBean bean = (BaseBean) t;
                        if (bean != null ) {
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(BaseBean base) {
                        int status = base.getStatus();// 状态码
                        String msg = base.getMsg();// 错误信息
//                        ToastUtil.show(mContext, "status = " + status + "\n"
//                                + "msg = " + msg);
                    }
                }, BaseBean.class);

                break;

            // 申诉
            case R.id.ly_appeal:
                //跳转到html5 页面
                WebViewActivity.LaunchSelf(mContext, Constants.URL_SHEN_SU, "");

                break;
            default:
                break;
        }
    }

    /**
     * 顶部布局--左按钮事件监听
     */
    public class TitleLeftOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }

    }

    //自定义适配器
    class MyAdapter extends BaseAdapter {
        //上下文对象
        private Context context;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

        MyAdapter(Context context, ArrayList<HashMap<String,String>> list) {
            this.context = context;
            this.list = list;
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                //根据布局文件获取View返回值
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_grab_single_details_item_layout, null);
            }
            ImageView imageview = (ImageView) convertView.findViewById(R.id.icon);
            TextView name = (TextView) convertView.findViewById(R.id.tv_name);

            imageview.setImageResource(Integer.parseInt(list.get(position).get("icon")));
            name.setText( list.get(position).get("name"));
            return convertView;
        }
    }


    /**
     * 设置数据
     */
    private ArrayList<HashMap<String,String>> setData() {
        list = new ArrayList<HashMap<String,String>>();

        // 实名认证
        if(!Utils.isEmpty(grabSingleBean.getNeedTName())){
            HashMap<String, String> map =  new HashMap<String,String>();
            map.put("icon", String.valueOf(R.drawable.shiming_normal));
            if("0".equals(grabSingleBean.getNeedTName())){
                map.put("icon", String.valueOf(R.drawable.shiming));
            }else{
                map.put("icon", String.valueOf(R.drawable.shiming_normal));
            }
            map.put("name", "实名认证");
            list.add(map);
        }

        // 手机认证
        if(!Utils.isEmpty(grabSingleBean.getNeedSafemoney())){
            HashMap<String, String> map2 =  new HashMap<String,String>();
            if("0".equals(grabSingleBean.getNeedSafemoney())){
                map2.put("icon", String.valueOf(R.drawable.shoujirenzheng));
            }else{
                map2.put("icon", String.valueOf(R.drawable.shoujirenzheng_normal));
            }
            map2.put("name", "保证金");
            list.add(map2);
        }

        // 保证完成
        if(!Utils.isEmpty(grabSingleBean.getNeedFinish())){
            HashMap<String, String> map3 =  new HashMap<String,String>();
            if("0".equals(grabSingleBean.getNeedFinish())){
                map3.put("icon", String.valueOf(R.drawable.baozhengwancheng));
            }else{
                map3.put("icon", String.valueOf(R.drawable.baozhengwancheng_normal));
            }
            map3.put("name", "保证完成");
            list.add(map3);
        }

        // 保证原创
        if(!Utils.isEmpty(grabSingleBean.getNeedSelf())){
            HashMap<String, String> map4 =  new HashMap<String,String>();
            if("0".equals(grabSingleBean.getNeedSelf())){
                map4.put("icon", String.valueOf(R.drawable.yuanchuang));
            }else{
                map4.put("icon", String.valueOf(R.drawable.yuanchuang_normal));
            }
            map4.put("name", "保证原创");
            list.add(map4);
        }

        // 保证维护
        if(!Utils.isEmpty(grabSingleBean.getNeedRepair())){
            HashMap<String, String> map5 =  new HashMap<String,String>();
            if("0".equals(grabSingleBean.getNeedRepair())){
                map5.put("icon", String.valueOf(R.drawable.weihu));
            }else{
                map5.put("icon", String.valueOf(R.drawable.weihu_normal));
            }
            map5.put("name", "保证维护");
            list.add(map5);
        }

        return list;
    }
}
