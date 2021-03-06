package com.topad.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.topad.R;
import com.topad.TopADApplication;
import com.topad.bean.AdServiceCaseListBean;
import com.topad.util.Constants;
import com.topad.util.Utils;
import com.topad.view.customviews.CycleImageLayout;
import com.topad.view.customviews.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * ${todo}<案例 >
 *
 * @author lht
 * @data: on 15/10/27 14:40
 */
public class ADSCaseActivity extends BaseActivity implements View.OnClickListener,
        CycleImageLayout.ImageCycleViewListener {
    private static final String LTAG = ADSCaseActivity.class.getSimpleName();
    /** 上下文 **/
    private Context mContext;
    /** 顶部布局 **/
    private TitleView mTitleView;
    /** 内容 **/
    private TextView mContent;
    /** 轮播banner **/
    private CycleImageLayout mBannerView;

    /** 编辑-产品案例数据元 **/
    private AdServiceCaseListBean mAdCaseListBean;
    /** 案例位置 **/
    private int position;

    /** 数据 **/
    private ArrayList<String> imageUrlList = new ArrayList<String>();

    @Override
    public int setLayoutById() {
        mContext = this;
        return R.layout.activity_ads_case;
    }

    @Override
    public View setLayoutByView() {
        return null;
    }

    @Override
    public void initViews() {
        mTitleView = (TitleView) findViewById(R.id.title);
        mBannerView = (CycleImageLayout) findViewById(R.id.ad_view);
        mContent = (TextView) findViewById(R.id.tv_case_introduce_content);
        imageUrlList = new ArrayList<String>();
    }

    @Override
    public void initData() {
        // 接收数据
        Intent intent = getIntent();
        if (intent != null) {
            mAdCaseListBean = (AdServiceCaseListBean) intent.getSerializableExtra("data_case");
            position = intent.getIntExtra("position", 0);
        }

        showView();
    }

    /**
     * 显示数据
     */
    private void showView() {
        // 设置顶部标题布局
        mTitleView.setTitle("案例详情");
        mTitleView.setLeftClickListener(new TitleLeftOnClickListener());

        mBannerView.setImageResources(setData(), null, this);
        // 内容
        if(!Utils.isEmpty(mAdCaseListBean.data.get(position).getIntro())){
            mContent.setText(mAdCaseListBean.data.get(position).getIntro());
        }
    }

    @Override
    public void displayImage(String imageURL, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageURL, imageView, TopADApplication.getSelf().getImageLoaderOption(),
                new ImageLoadingListener(){
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
    }

    @Override
    public void onImageClick(int position, View imageView) {

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

    public ArrayList<String> setData(){
        if(mAdCaseListBean != null && mAdCaseListBean.data.size()>0){
            if(!Utils.isEmpty(mAdCaseListBean.data.get(position).getImgs())){
                String[] aa = mAdCaseListBean.data.get(position).getImgs().split("\\|");
                for(int i = 0; i < aa.length; i++){
                    imageUrlList.add(0, Constants.getCurrUrl() + Constants.CASE_IMAGE_URL_HEADER + aa[i]);
                }
            }
        }
        return imageUrlList;
    }
}
