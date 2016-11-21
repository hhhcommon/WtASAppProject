package com.woting.ui.mine.myupload.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.mine.myupload.MyUploadActivity;
import com.woting.ui.mine.myupload.adapter.MyUploadListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传的专辑列表
 * Created by Administrator on 2016/11/19.
 */
public class MyUploadSequFragment extends Fragment implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private Context context;
    private MyUploadListAdapter adapter;
    private List<RankInfo> subList;
    private List<String> delList;
    private List<RankInfo> newList = new ArrayList<>();
    private List<RankInfo> checkList = new ArrayList<>();

    private View rootView;
    private Dialog dialog;
    private XListView mListView;

    private String tag = "UPLOAD_SEQU_FRAGMENT_VOLLEY_REQUEST_CANCEL_TAG";
    private int page = 1;
    private int refreshType = 1;// == 1 刷新  == 2 加载更多
    private int pageSizeNum;// 获取列表页码
    private boolean isPullLoadEnable;
    private boolean isCancelRequest;
    private boolean isAll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_upload, container, false);
            initListView();
        }
        return rootView;
    }

    // 初始化控件
    private void initListView() {
        mListView = (XListView) rootView.findViewById(R.id.list_view);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);

        dialog = DialogUtils.Dialogph(context, "loading....");
        sendRequest();
    }

    // 发送网络请求
    private void sendRequest() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if(dialog != null) dialog.dismiss();
            ToastUtils.show_allways(context, "网络连接失败，请检查网络连接!");
            if(refreshType == 1) {
                mListView.stopRefresh();
            } else {
                mListView.stopLoadMore();
            }
            return ;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "SEQU");
            jsonObject.put("Page", String.valueOf(page));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 获取用户上传的专辑列表  目前没有接口  测试获取的是我喜欢的专辑
        VolleyRequest.RequestPost(GlobalConfig.getFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if(isCancelRequest) return ;
                if(subList != null) subList.clear();
                page++;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.w("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType != null && ReturnType.equals("1001")) {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        subList = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<RankInfo>>() {}.getType());

                        try {
                            String allCountString = arg1.getString("AllCount");
                            String pageSizeString = arg1.getString("PageSize");
                            if (allCountString != null && !allCountString.equals("") && pageSizeString != null && !pageSizeString.equals("")) {
                                int allCountInt = Integer.valueOf(allCountString);
                                int pageSizeInt = Integer.valueOf(pageSizeString);
                                if(pageSizeInt < 10 || allCountInt < 10){
                                    isPullLoadEnable = false;
                                }else{
                                    isPullLoadEnable = true;
                                    if (allCountInt % pageSizeInt == 0) {
                                        pageSizeNum = allCountInt / pageSizeInt;
                                    } else {
                                        pageSizeNum = allCountInt / pageSizeInt + 1;
                                    }
                                }
                                mListView.setPullLoadEnable(isPullLoadEnable);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (refreshType == 1) {
                            newList.clear();
                        }
                        newList.addAll(subList);
                        if (adapter == null) {
                            mListView.setAdapter(adapter = new MyUploadListAdapter(context, newList));
                        } else {
                            adapter.setList(newList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 无论何种返回值，都需要终止掉上拉刷新及下拉加载的滚动状态
                if (refreshType == 1) {
                    mListView.stopRefresh();
                } else {
                    mListView.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 设置点选框显示与隐藏
    public boolean setCheckVisible(boolean isVisible) {
        if(newList != null && newList.size() > 0) {
            adapter.setVisible(isVisible);
            if(isVisible) {
                mListView.setPullRefreshEnable(false);
                mListView.setPullLoadEnable(false);
            } else {
                mListView.setPullRefreshEnable(true);
                mListView.setPullLoadEnable(isPullLoadEnable);
                checkList.clear();
            }
            return true;
        } else {
            ToastUtils.show_allways(context, "当前页没有数据可编辑!");
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position <= 0) {
            return ;
        }
        if(((MyUploadActivity)context).getEditState()) {
            int checkType = newList.get(position - 1).getChecktype();
            if(checkType == 0) {
                newList.get(position - 1).setChecktype(1);
            } else {
                newList.get(position - 1).setChecktype(0);
            }
            adapter.setList(newList);
            ifAll();
        } else {
            Intent intent = new Intent(context, AlbumActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", "recommend");
            bundle.putSerializable("list", newList.get(position - 1));
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }
    }

    // 判断是否全选
    private void ifAll() {
        for(int i=0; i<newList.size(); i++) {
            if(newList.get(i).getChecktype() == 1 && !checkList.contains(newList.get(i))) {
                checkList.add(newList.get(i));
            } else if(newList.get(i).getChecktype() == 0 && checkList.contains(newList.get(i))) {
                checkList.remove(newList.get(i));
            }
        }
        if(checkList.size() == newList.size()){
            Intent intentAll = new Intent();
            intentAll.setAction(BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_ALL);
            context.sendBroadcast(intentAll);
            isAll = true;
        }else{
            if(isAll) {
                Intent intentNoCheck = new Intent();
                intentNoCheck.setAction(BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_NO);
                context.sendBroadcast(intentNoCheck);
                isAll = false;
            }
        }
    }

    // 设置状态  checkType == 1 全选  OR  checkType == 0 非全选
    public void allSelect(int checkType) {
        for(int i=0; i<newList.size(); i++) {
            newList.get(i).setChecktype(checkType);
        }
        ifAll();
        adapter.setList(newList);
    }

    // 删除
    public void delItem() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在删除...");
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getChecktype() == 1) {
                    if (delList == null) {
                        delList = new ArrayList<>();
                    }
                    String type = newList.get(i).getMediaType();
                    String contentId = newList.get(i).getContentId();
                    delList.add(type + "::" + contentId);
                }
            }
            sendDeleteItemRequest();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
        }
    }

    // 删除单条喜欢
    protected void sendDeleteItemRequest() {
        JSONObject jsonObject =VolleyRequest.getJsonObject(context);
        try {
            String s = delList.toString();
            jsonObject.put("DelInfos", s.substring(1, s.length() - 1).replaceAll(" ", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.delFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
            private String returnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if(dialog != null) dialog.dismiss();
                if(isCancelRequest) return ;
                delList.clear();
                try {
                    returnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (returnType != null && returnType.equals("1001")) {
                    for(int i=0; i<newList.size(); i++) {
                        if(newList.get(i).getChecktype() == 1) {
                            newList.remove(i);
                        }
                    }
                    checkList.clear();
                    adapter.setVisible(false);
                } else {
                    ToastUtils.show_allways(context, "删除失败，请检查网络或稍后重试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                delList.clear();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == 1){
                    getActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshType = 1;
        page = 1;
        sendRequest();
    }

    @Override
    public void onLoadMore() {
        if (page <= pageSizeNum) {
            refreshType = 2;
            sendRequest();
        } else {
            mListView.stopLoadMore();
            mListView.setPullLoadEnable(false);
        }
    }

    @Override
    public void onDestroyView() {
        super .onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
