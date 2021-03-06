package com.topad.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.topad.R;
import com.topad.TopADApplication;
import com.topad.amap.ToastUtil;
import com.topad.bean.BaseBean;
import com.topad.bean.GrabSingleBean;
import com.topad.bean.GrabSingleListBean;
import com.topad.bean.SelectProjectBean;
import com.topad.net.HttpCallback;
import com.topad.net.http.RequestParams;
import com.topad.util.Constants;
import com.topad.util.LogUtil;
import com.topad.util.Utils;
import com.topad.view.activity.GrabSingleDetailsActivity;
import com.topad.view.activity.MyWantGrabsingleActivity;
import com.topad.view.customviews.mylist.MyListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * ${todo}<我要抢单>
 *
 * @author lht
 * @data: on 15/10/28 18:32
 */
public class MyWantGrabSingleFragment extends BaseFragment{
	private static final String LTAG = MyWantGrabSingleFragment.class.getSimpleName();
	/** 上下文 **/
	private Context mContext;
	/** 根view布局 **/
	private View mRootView;
	/** listView **/
	private MyListView mListView;
	/** 适配器 **/
	private ListAdapter adapter;
	/** 数据源 **/
	private ArrayList<GrabSingleBean> bankList = new ArrayList<GrabSingleBean>();
	/** 请求页数 **/
	private int page = 1;
	/** 甄选项请求页数 **/
	private int page2 = 0;

	@Override
	public String getFragmentName() {
		return LTAG;
	}

	/**
	 * ***生命周期******
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == mRootView) {
			mRootView = getLayoutInflater(savedInstanceState).inflate(R.layout.fargment_grab_single, null);
		}
		return mRootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		initView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initView() {
		mListView = (MyListView) mRootView.findViewById(R.id.listview);

		// 设置listview可以加载、刷新
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		// 设置适配器
		adapter = new ListAdapter();

		// listview单击
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent(mContext, GrabSingleDetailsActivity.class);
				intent.putExtra("state", "0");// 0我要提交
				intent.putExtra("data_details", bankList.get(position-1));
				startActivity(intent);
			}
		});

		// 设置回调函数
		mListView.setMyListViewListener(new MyListView.IMyListViewListener() {

			@Override
			public void onRefresh() {
				bankList.clear();
				page = 1;
				if(page2 != 0){
					page2 = 1;
				}
				setData();
			}

			@Override
			public void onLoadMore() {
				page ++;
				if(page2 != 0){
					page2 ++;
				}
				setData();
			}
		});
	}

	@Override
	public void setVisible(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		LogUtil.d("是否显示：" + isVisibleToUser + "    isNeedRefresh:" + isNeedRefresh);
		if (isVisibleToUser && isNeedRefresh) {
			isNeedRefresh = false;
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		initData();
		mListView.setAdapter(adapter);
	}

	/**
	 * 请求数据
	 */
	public void initData() {
		setData();
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ListAdapter() {
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bankList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return bankList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate((R.layout.fargment_grab_single_item), null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.im_icon);
				holder.name = (TextView) convertView .findViewById(R.id.tv_name);
				holder.price = (TextView) convertView .findViewById(R.id.tv_price);
				holder.state = (TextView) convertView .findViewById(R.id.tv_state);
				holder.content = (TextView) convertView .findViewById(R.id.tv_content);
				holder.time = (TextView) convertView .findViewById(R.id.tv_time);
				holder.countdown = (TextView) convertView .findViewById(R.id.tv_countdown);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(bankList.get(position).getTitle());
			SpannableStringBuilder ssb = new SpannableStringBuilder("￥" + bankList.get(position).getBudget());
			holder.price.setText(ssb.toString());

			// 托管
			holder.state.setVisibility(View.GONE);
			if (!Utils.isEmpty(bankList.get(position).getIspay())){
				if("1".equals(bankList.get(position).getIspay())){
					holder.state.setVisibility(View.VISIBLE);
				}else{
					holder.state.setVisibility(View.GONE);
				}
			}

			holder.content.setText(bankList.get(position).getDetail());
			String[] sourceStrArray = bankList.get(position).getEnddate().split(" ");
			holder.time.setText(sourceStrArray[0]);

			// 当前时间
			SimpleDateFormat dataformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datestr = dataformat.format(new Date());

			try {
				holder.countdown.setText("还有" + Utils.daysBetween(bankList.get(position).getAdddate(), datestr) + "天到期");
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return convertView;
		}

		class ViewHolder {
			ImageView icon;
			TextView name;
			TextView state;
			TextView price;
			TextView content;
			TextView time;
			TextView countdown;
		}
	}

	/**
	 * 设置数据
	 */
	public void setData() {
		MyWantGrabsingleActivity activity = (MyWantGrabsingleActivity) getActivity();
		// 有筛选项
		if(activity != null && activity.getSelectProjectBean() != null){
			SelectProjectBean bean = activity.getSelectProjectBean();
			if(page2 == 0){
				page2 = Integer.parseInt(bean.getPage());
			}
			setSelectProjectData(bean.getIspay(), bean.getPaytype(), bean.getType1(), bean.getType2());
		}
		// 没有筛选项时
		else{
			// 拼接url
			StringBuffer sb = new StringBuffer();
			sb.append(Constants.getCurrUrl()).append(Constants.URL_NEED_GETLIST).append("?");
			String url = sb.toString();
			RequestParams rp=new RequestParams();
			rp.add("userid", TopADApplication.getSelf().getUserId());
			rp.add("type1", "0"); // 当是我的数据默认为0
			rp.add("type2", "0");// 当是我的数据默认为0
			rp.add("isselfpost", "0"); // 是否是自己发布的
			rp.add("isqd", "1"); // 我要抢单该值为1
			rp.add("page", page + "");
			rp.add("province", TopADApplication.getSelf().getProvice());
			postWithLoading(url, rp, false, new HttpCallback() {
				@Override
				public <T> void onModel(int respStatusCode, String respErrorMsg, T t) {
					GrabSingleListBean bean = (GrabSingleListBean) t;
					if (bean != null && bean.data.size()!= 0) {

						if(bankList != null && bankList.size() > 0){
							bankList.clear();
						}
						for(int i = 0; i < bean.data.size(); i++){
							bankList.add(bean.data.get(i));
							adapter.notifyDataSetChanged();
						}
					}
					mListView.stopRefresh();

					if(bankList == null || bankList.size() == 0){
						mListView.setPullLoadEnable(false);
					}else{
						mListView.setPullLoadEnable(true);
					}
				}

				@Override
				public void onFailure(BaseBean base) {
					int status = base.getStatus();// 状态码
					String msg = base.getMsg();// 错误信息
					ToastUtil.show(mContext, "status = " + status + "\n"
							+ "msg = " + msg);
				}
			}, GrabSingleListBean.class);
		}
	}

	/**
	 * 有筛选项使用
	 */
	private void setSelectProjectData(String ispay, String paytype, String type1, String type2) {
		// 拼接url
		StringBuffer sb = new StringBuffer();
		sb.append(Constants.getCurrUrl()).append(Constants.URL_NEDD_SEARCH).append("?");
		String url = sb.toString();
		RequestParams rp = new RequestParams();
		rp.add("userid", TopADApplication.getSelf().getUserId());
		rp.add("ispay", ispay);
		rp.add("paytype", paytype);
		rp.add("type1", type1);
		rp.add("type2", type2);
		rp.add("page", page2 + "");

		postWithLoading(url, rp, false, new HttpCallback() {
			@Override
			public <T> void onModel(int respStatusCode, String respErrorMsg, T t) {
				if(bankList != null && bankList.size() > 0){
					bankList.clear();
					adapter.notifyDataSetChanged();
				}

				GrabSingleListBean bean = (GrabSingleListBean) t;
				if (bean != null && bean.data.size()!= 0) {
					for(int i = 0; i < bean.data.size(); i++){
						bankList.add(bean.data.get(i));
						adapter.notifyDataSetChanged();
					}
				}
				mListView.stopRefresh();

				if(bean.data == null || bean.data.size() == 0){
					mListView.setPullLoadEnable(false);
					ToastUtil.show(mContext, "没有您想要的结果");
				}else{
					mListView.setPullLoadEnable(true);
				}

			}

			@Override
			public void onFailure(BaseBean base) {
				int status = base.getStatus();// 状态码
				String msg = base.getMsg();// 错误信息
				ToastUtil.show(mContext, "status = " + status + "\n"
						+ "msg = " + msg);
			}
		}, GrabSingleListBean.class);
	}
}