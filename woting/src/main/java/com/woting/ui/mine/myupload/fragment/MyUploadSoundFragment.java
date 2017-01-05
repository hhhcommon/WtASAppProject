package com.woting.ui.mine.myupload.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.myupload.MyUploadActivity;
import com.woting.ui.mine.myupload.adapter.MyUploadListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传的声音列表
 * Created by Administrator on 2016/11/19.
 */
public class MyUploadSoundFragment extends Fragment implements AdapterView.OnItemClickListener, TipView.WhiteViewClick {
    private Context context;
    private MyUploadListAdapter adapter;
    private SearchPlayerHistoryDao dbDao;
    //    private List<String> delList;
    private List<RankInfo> newList = new ArrayList<>();
    private List<RankInfo> checkList = new ArrayList<>();

    private View rootView;
    private Dialog dialog;
    private ListView mListView;
    private TipView tipView;// 没有网络、没有数据提示

    private String tag = "UPLOAD_SEQU_FRAGMENT_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private boolean isAll;

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialogph(context, "获取数据中....");
        sendRequest();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        dbDao = new SearchPlayerHistoryDao(context);// 初始化数据库
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_upload, container, false);
            initView();
        }
        return rootView;
    }

    // 初始化控件
    private void initView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);

        dialog = DialogUtils.Dialogph(context, "数据加载中....");
        sendRequest();
    }

    // 发送网络请求
    private void sendRequest() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if (dialog != null) dialog.dismiss();
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceId", PhoneMessage.imei);
            jsonObject.put("PCDType", GlobalConfig.PCDType);
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("UserId", CommonUtils.getUserId(context));
            jsonObject.put("FlagFlow", "2");
            jsonObject.put("ChannelId", "0");
            jsonObject.put("SeqMediaId", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 获取用户上传的声音列表  目前没有接口  测试获取的是我喜欢的声音
        VolleyRequest.RequestPost(GlobalConfig.getMediaList, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.w("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType != null && ReturnType.equals("1001")) {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        newList = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<RankInfo>>() {}.getType());
                        if (adapter == null) {
                            mListView.setAdapter(adapter = new MyUploadListAdapter(context, newList));
                        } else {
                            adapter.setList(newList);
                        }
                        tipView.setVisibility(View.GONE);
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有发过内容\n快去上传自己的内容吧");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    // 设置点选框显示与隐藏
    public boolean setCheckVisible(boolean isVisible) {
        if (newList != null && newList.size() > 0) {
            adapter.setVisible(isVisible);
            if (!isVisible) checkList.clear();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position <= 0) {
            return;
        }
        if (((MyUploadActivity) context).getEditState()) {
            int checkType = newList.get(position).getChecktype();
            if (checkType == 0) {
                newList.get(position).setChecktype(1);
            } else {
                newList.get(position).setChecktype(0);
            }
            adapter.setList(newList);
            ifAll();
        } else {
            String MediaType = newList.get(position).getMediaType();
            if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                String playername = newList.get(position).getContentName();
                String playerimage = newList.get(position).getContentImg();
                String playerurl = newList.get(position).getContentPlay();
                String playerurI = newList.get(position).getContentURI();
                String playermediatype = newList.get(position).getMediaType();
                String playcontentshareurl = newList.get(position).getContentShareURL();
                String plaplayeralltime = "0";
                String playerintime = "0";
                String playercontentdesc = newList.get(position).getCurrentContent();
                String playernum = newList.get(position).getWatchPlayerNum();
                String playerzantype = "0";
                String playerfrom = newList.get(position).getContentPub();
                String playerfromid = "";
                String playerfromurl = "";
                String playeraddtime = Long.toString(System.currentTimeMillis());
                String bjuserid = CommonUtils.getUserId(context);
                String ContentFavorite = newList.get(position).getContentFavorite();
                String ContentId = newList.get(position).getContentId();
                String localurl = newList.get(position).getLocalurl();

                String sequName = newList.get(position).getSequName();
                String sequId = newList.get(position).getSequId();
                String sequDesc = newList.get(position).getSequDesc();
                String sequImg = newList.get(position).getSequImg();

                String ContentPlayType = newList.get(position).getContentPlayType();

                // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                PlayerHistory history = new PlayerHistory(
                        playername, playerimage, playerurl, playerurI, playermediatype,
                        plaplayeralltime, playerintime, playercontentdesc, playernum,
                        playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                        ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg, ContentPlayType);
                dbDao.deleteHistory(playerurl);
                dbDao.addHistory(history);

                if (PlayerFragment.context != null) {
                    MainActivity.change();
                    HomeActivity.UpdateViewPager();
                    Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("text", newList.get(position).getContentName());
                    push.putExtras(bundle1);
                    context.sendBroadcast(push);
                    getActivity().finish();
                } else {
                    SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                    et.putString(StringConstant.PLAYHISTORYENTER, "true");
                    et.putString(StringConstant.PLAYHISTORYENTERNEWS, newList.get(position).getContentName());
                    if (!et.commit()) Log.w("commit", "数据 commit 失败!");
                    MainActivity.change();
                    HomeActivity.UpdateViewPager();
                    getActivity().finish();
                }
            }
        }
    }

    // 判断是否全选
    private void ifAll() {
        for (int i = 0; i < newList.size(); i++) {
            if (newList.get(i).getChecktype() == 1 && !checkList.contains(newList.get(i))) {
                checkList.add(newList.get(i));
            } else if (newList.get(i).getChecktype() == 0 && checkList.contains(newList.get(i))) {
                checkList.remove(newList.get(i));
            }
        }
        if (checkList.size() == newList.size()) {
            Intent intentAll = new Intent();
            intentAll.setAction(BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_ALL);
            context.sendBroadcast(intentAll);
            isAll = true;
        } else {
            if (isAll) {
                Intent intentNoCheck = new Intent();
                intentNoCheck.setAction(BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_NO);
                context.sendBroadcast(intentNoCheck);
                isAll = false;
            }
        }
    }

    // 设置状态  checkType == 1 全选  OR  checkType == 0 非全选
    public void allSelect(int checkType) {
        for (int i = 0; i < newList.size(); i++) {
            newList.get(i).setChecktype(checkType);
        }
        ifAll();
        adapter.setList(newList);
    }

    // 删除
    public void delItem() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在删除...");
            StringBuilder builder = new StringBuilder();
            String contentSeqId = null;
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getChecktype() == 1) {
//                    if (delList == null) {
//                        delList = new ArrayList<>();
//                    }
                    builder.append(newList.get(i).getContentId() + ",");
//                    contentId = newList.get(i).getContentId();
                    contentSeqId = newList.get(i).getContentSeqId();
//                    delList.add(contentId);
                }
            }
            String contentId = builder.toString();
            sendDeleteItemRequest(contentId.substring(0, contentId.length() - 1), contentSeqId);
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
        }
    }

    // 删除单条喜欢
    protected void sendDeleteItemRequest(String contentId, String contentSeqId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceId", PhoneMessage.imei);
            jsonObject.put("PCDType", GlobalConfig.PCDType);
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("UserId", CommonUtils.getUserId(context));
            jsonObject.put("ContentId", contentId);
            jsonObject.put("SeqMediaId", contentSeqId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.removeMedia, tag, jsonObject, new VolleyCallback() {
            private String returnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
//                delList.clear();
                try {
                    returnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (returnType != null && returnType.equals("1001")) {
                    for (int i = 0; i < newList.size(); i++) {
                        if (newList.get(i).getChecktype() == 1) {
                            newList.remove(i);
                        }
                    }
                    checkList.clear();
                    adapter.setVisible(false);
                } else {
                    ToastUtils.show_always(context, "删除失败，请检查网络或稍后重试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
//                delList.clear();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
