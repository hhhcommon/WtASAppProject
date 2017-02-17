package com.woting.ui.home.program.album.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.download.activity.DownloadActivity;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.fragment.DownLoadUnCompleted;
import com.woting.ui.download.model.FileInfo;
import com.woting.ui.download.service.DownloadService;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.album.adapter.AlbumAdapter;
import com.woting.ui.home.program.album.adapter.AlbumMainAdapter;
import com.woting.ui.home.program.album.model.ContentInfo;
import com.woting.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 专辑列表页
 * 作者：xinlong on 2016/11/16 17:40
 * 邮箱：645700751@qq.com
 */
public class ProgramFragment extends Fragment implements OnClickListener, TipView.WhiteViewClick {
    private Context context;
    private FileInfoDao FID;
    private SearchPlayerHistoryDao dbDao;
    private AlbumMainAdapter mainAdapter;
    private AlbumAdapter adapter;

    private List<ContentInfo> SubListAll = new ArrayList<>();
    private List<ContentInfo> urlList = new ArrayList<>();
    private List<ContentInfo> SubList;// 请求返回的网络数据值
    private List<FileInfo> fList;

    private View rootView;
    private Dialog dialog;
    private LinearLayout lin_quanxuan, lin_status2;
    private XListView lv_album;// 节目列表
    private ListView lv_download;// 下载列表
    private TipView tipView;// 没有数据、没有网络提示

    private TextView tv_quxiao, tv_download, tv_sum, textTotal;
    private ImageView img_download, img_quanxuan;// 下载 全选
    private ImageView imageSort;// 排序
    private ImageView imageSortDown;

    private int page = 1;
    private int sum = 0;// 计数项
    private boolean flag = false;// 标记全选的按钮
    private boolean isCancelRequest;
    private String tag = "PROGRAM_VOLLEY_REQUEST_CANCEL_TAG";
    private String userId;
    private String sequId;
    private String sequName;
    private String sequImg;
    private String sequDesc;

    private int sortType = 1;// == 1 按卷号从大到小排序 默认排序；== 2 按卷号从小到大排序；

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        userId = CommonUtils.getUserId(context);
        initDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_album_program, container, false);
            initView(rootView);
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "正在获取数据");
                send();
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            }
        }
        return rootView;
    }

    // 初始化控件
    private void initView(View view) {
        tipView = (TipView) view.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        lv_album = (XListView) view.findViewById(R.id.lv_album);            // 专辑显示界面
        img_download = (ImageView) view.findViewById(R.id.img_download);
        img_download.setOnClickListener(this);
        tv_quxiao = (TextView) view.findViewById(R.id.tv_quxiao);            // 取消动画
        tv_quxiao.setOnClickListener(this);
        img_quanxuan = (ImageView) view.findViewById(R.id.img_quanxuan);    // img_quanxuan
        lin_quanxuan = (LinearLayout) view.findViewById(R.id.lin_quanxuan); // lin_quanxuan
        lin_quanxuan.setOnClickListener(this);
        lv_download = (ListView) view.findViewById(R.id.lv_download);        // lv_download
        tv_download = (TextView) view.findViewById(R.id.tv_download);        // 开始下载
        tv_download.setOnClickListener(this);
        tv_sum = (TextView) view.findViewById(R.id.tv_sum);                // 计数项
        lin_status2 = (LinearLayout) view.findViewById(R.id.lin_status2);    // 第二种状态
        textTotal = (TextView) view.findViewById(R.id.text_total);            // 下载列表的总计

        imageSort = (ImageView) view.findViewById(R.id.img_sort);            // 排序
        imageSort.setOnClickListener(this);

        imageSortDown = (ImageView) view.findViewById(R.id.img_sort_down);
        imageSortDown.setOnClickListener(this);

        lv_album.setPullLoadEnable(true);
        lv_album.setPullRefreshEnable(false);
        lv_album.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                if (CommonHelper.checkNetwork(context)) {
                    send();
                }
            }
        });

    }

    // ListView 的 Item 的监听事件
    private void setListener() {
        lv_album.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SubListAll != null && SubListAll.get(position - 1) != null && SubListAll.get(position - 1).getMediaType() != null) {
                    String MediaType = SubListAll.get(position - 1).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playerName = SubListAll.get(position - 1).getContentName();
                        String playerImage = SubListAll.get(position - 1).getContentImg();
                        String playUrl = SubListAll.get(position - 1).getContentPlay();
                        String playUrI = SubListAll.get(position - 1).getContentURI();
                        String playMediaType = SubListAll.get(position - 1).getMediaType();
                        String playContentShareUrl = SubListAll.get(position - 1).getContentShareURL();
                        String ContentId = SubListAll.get(position - 1).getContentId();
                        String playAllTime = SubListAll.get(position - 1).getContentTimes();
                        String playInTime = "0";
                        String playContentDesc = SubListAll.get(position - 1).getContentDescn();
                        String playNum = SubListAll.get(position - 1).getPlayCount();
                        String playZanType = "0";
                        String playFrom = SubListAll.get(position - 1).getContentPub();
                        String playFromId = "";
                        String playFromUrl = "";
                        String playAddTime = Long.toString(System.currentTimeMillis());
                        String bjUserId = CommonUtils.getUserId(context);
                        String ContentFavorite = SubListAll.get(position - 1).getContentFavorite();
                        String localUrl = SubListAll.get(position - 1).getLocalurl();
                        String sequName1 = sequName;
                        String sequId1 = sequId;
                        String sequDesc1 = sequDesc;
                        String sequImg1 = sequImg;
                        String ContentPlayType = SubListAll.get(position - 1).getContentPlayType();
                        String IsPlaying = SubListAll.get(position - 1).getIsPlaying();

                        PlayerHistory history = new PlayerHistory(
                                playerName, playerImage, playUrl, playUrI, playMediaType,
                                playAllTime, playInTime, playContentDesc, playNum,
                                playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                                ContentFavorite, ContentId, localUrl, sequName1, sequId1, sequDesc1, sequImg1, ContentPlayType, IsPlaying);
                        dbDao.deleteHistory(playUrl);
                        dbDao.addHistory(history);
                        if (PlayerFragment.context != null) {
                            MainActivity.change();
                            HomeActivity.UpdateViewPager();
                            Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("text", SubListAll.get(position - 1).getContentName());
                            push.putExtras(bundle1);
                            context.sendBroadcast(push);
                        } else {
                            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                            et.putString(StringConstant.PLAYHISTORYENTER, "true");
                            et.putString(StringConstant.PLAYHISTORYENTERNEWS, SubListAll.get(position - 1).getContentName());
                            et.commit();
                            MainActivity.change();
                            HomeActivity.UpdateViewPager();
                        }
                        getActivity().setResult(1);
                        getActivity().finish();
                    } else {
                        ToastUtils.show_always(context, "暂不支持播放");
                    }
                }
            }
        });
    }

    // 向服务器发送请求
    public void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("ContentId", AlbumActivity.id);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("PageSize", "20");
            jsonObject.put("SortType", String.valueOf(sortType));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.getSmSubMedias, tag, jsonObject, new VolleyCallback() {
            private String subList;
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultInfo")).nextValue();
                            try {
                                String total = arg1.getString("ContentSubCount");
                                textTotal.setText("共" + total + "集");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                subList = arg1.getString("SubList");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                SubList = new Gson().fromJson(subList, new TypeToken<List<ContentInfo>>() {}.getType());
                                if (SubList != null && SubList.size() > 0) {
                                    if (page == 1) SubListAll.clear();
                                    if (SubList.size() >= 20) page++;
                                    else lv_album.setPullLoadEnable(false);
                                    SubListAll.addAll(SubList);
                                    lv_album.setAdapter(mainAdapter = new AlbumMainAdapter(context, SubListAll));
                                    setListener();
                                    lv_download.setAdapter(adapter = new AlbumAdapter(context, SubListAll));
                                    setInterface();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            lv_album.stopLoadMore();
                            tipView.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "专辑中没有节目\n换个专辑看看吧");
                        }
                    } else {
                        lv_album.stopLoadMore();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "专辑中没有节目\n换个专辑看看吧");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    lv_album.stopLoadMore();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                lv_album.stopLoadMore();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    // 设置下载需要的专辑信息
    public void setInfo(String sequId, String sequImg, String sequName, String sequDesc) {
        this.sequId = sequId;
        this.sequImg = sequImg;
        this.sequName = sequName;
        this.sequDesc = sequDesc;
    }

    // 实现接口的方法
    private void setInterface() {
        lv_download.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SubListAll != null && SubListAll.get(position) != null) {
                    if (SubListAll.get(position).getCheckType() == 3) {
                        ToastUtils.show_always(context, "已经下载过");
                    } else {
                        if (SubListAll.get(position).getCheckType() == 1) {
                            SubListAll.get(position).setCheckType(2);
                        } else {
                            SubListAll.get(position).setCheckType(1);
                        }
                        int downLoadSum = 0;
                        sum = 0;
                        for (int i = 0; i < SubListAll.size(); i++) {
                            if (SubListAll.get(i).getCheckType() == 2) {
                                sum++;
                            }
                            if (SubListAll.get(i).getCheckType() == 3) {
                                downLoadSum++;
                            }
                            setSum();
                            adapter.notifyDataSetChanged();
                        }

                        // 更新全选图标
                        if (sum == (SubListAll.size() - downLoadSum)) {
                            flag = true;
                            img_quanxuan.setImageResource(R.mipmap.wt_group_checked);
                        } else {
                            flag = false;
                            img_quanxuan.setImageResource(R.mipmap.wt_group_nochecked);
                        }
                    }
                }
            }
        });
    }

    // 获取数据
    private void getData() {
        fList = FID.queryFileInfoAll(userId);
        Log.e("fList", fList.size() + "");
        ArrayList<FileInfo> seqList = new ArrayList<>();
        if (fList != null && fList.size() > 0) {
            for (int i = 0; i < fList.size(); i++) {
                if (fList.get(i).getSequimgurl() != null && fList.get(i).getSequimgurl().equals(AlbumActivity.ContentImg)) {
                    seqList.add(fList.get(i));
                }
            }
        }
        Log.e("seqList", seqList.size() + "");
        if (seqList.size() > 0) {
            for (int i = 0; i < seqList.size(); i++) {
                String temp = seqList.get(i).getUrl();
                if (temp != null && !temp.trim().equals("")) {
                    for (int j = 0; j < SubListAll.size(); j++) {
                        if (SubListAll.get(j).getContentPlay() != null && SubListAll.get(j).getContentPlay().equals(temp)) {
                            SubListAll.get(j).setCheckType(3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_download:    // 显示下载列表
                if (SubListAll.size() == 0) {
                    return;
                }
                getData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    lv_download.setAdapter(adapter = new AlbumAdapter(context, SubListAll));
                }
                lv_download.setSelection(0);
                lin_status2.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_quxiao:        // 取消
                lin_status2.setVisibility(View.GONE);
                for (int i = 0; i < SubListAll.size(); i++) {
                    if (SubListAll.get(i).getCheckType() != 3) {
                        img_quanxuan.setImageResource(R.mipmap.image_not_all_check);
                        SubListAll.get(i).setCheckType(1);
                    }
                }
                sum = 0;
                setSum();
                flag = false;
                break;
            case R.id.lin_quanxuan:    // 全选
                if (!flag) {    // 默认为未选中状态
                    sum = 0;
                    for (int i = 0; i < SubListAll.size(); i++) {
                        if (SubListAll.get(i).getCheckType() != 3) {
                            SubListAll.get(i).setCheckType(2);
                            sum++;
                        }
                    }
                    flag = true;
                    img_quanxuan.setImageResource(R.mipmap.wt_group_checked);
                    setSum();
                } else {
                    for (int i = 0; i < SubListAll.size(); i++) {
                        if (SubListAll.get(i).getCheckType() != 3) {
                            SubListAll.get(i).setCheckType(1);
                        }
                    }
                    flag = false;
                    img_quanxuan.setImageResource(R.mipmap.wt_group_nochecked);
                    sum = 0;
                    setSum();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_download:        // 下载
                urlList.clear();
                for (int i = 0; i < SubListAll.size(); i++) {
                    if (SubListAll.get(i).getCheckType() == 2) {
                        ContentInfo mContent = SubListAll.get(i);
                        mContent.setSequdesc(AlbumActivity.ContentDesc);
                        mContent.setSequname(AlbumActivity.ContentName);
                        mContent.setSequimgurl(AlbumActivity.ContentImg);
                        mContent.setSequid(AlbumActivity.id);
                        // 判断 userId 是否为空
                        mContent.setUserid(userId);
                        mContent.setDownloadtype("0");
                        FID.updataDownloadStatus(mContent.getContentPlay(), "0");// 将所有数据设置
                        urlList.add(mContent);
                    }
                }
                if (urlList.size() > 0) {
                    FID.insertFileInfo(urlList);
                    List<FileInfo> tempList = FID.queryFileInfo("false", userId);// 查询表中未完成的任务
                    // 未下载列表
                    for (int kk = 0; kk < tempList.size(); kk++) {
                        if (tempList.get(kk).getDownloadtype() == 1) {
                            DownloadService.workStop(tempList.get(kk));
                            FID.updataDownloadStatus(tempList.get(kk).getUrl(), "2");
                            Log.e("测试下载问题", " 暂停下载的单体" + (tempList.get(kk).getFileName()));
                        }
                    }
                    tempList.get(0).setDownloadtype(1);
                    FID.updataDownloadStatus(tempList.get(0).getUrl(), "1");
                    Log.e("数据库内数据", tempList.toString());
                    DownloadService.workStart(tempList.get(0));
                    if(DownloadActivity.isVisible){
                        DownLoadUnCompleted.dwType=true;
                    }
                    ToastUtils.show_always(context,"已经开始下载您所选择的数据");
                    // 发送更新界面数据广播
                    Intent p_intent = new Intent("push_down_uncompleted");
                    context.sendBroadcast(p_intent);
                    lin_status2.setVisibility(View.GONE);
                } else {
                    ToastUtils.show_always(context, "请重新选择数据");
                    return;
                }
                break;
            case R.id.img_sort:
                if (SubListAll.size() != 0 && mainAdapter != null) {
                    sortType = 2;
                    page = 1;
                    dialog = DialogUtils.Dialogph(context, "正在获取数据...");
                    send();

//                    Collections.reverse(SubListAll);            // 倒序
//                    mainAdapter.notifyDataSetChanged();
                    imageSortDown.setVisibility(View.VISIBLE);
                    imageSort.setVisibility(View.GONE);
                }
                break;
            case R.id.img_sort_down:
                if (SubListAll.size() != 0 && mainAdapter != null) {
                    sortType = 1;
                    page = 1;
                    dialog = DialogUtils.Dialogph(context, "正在获取数据...");
                    send();

//                    Collections.reverse(SubListAll);            // 倒序
//                    mainAdapter.notifyDataSetChanged();
                    imageSortDown.setVisibility(View.GONE);
                    imageSort.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    protected void setSum() {
        tv_sum.setText(sum + "");
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
        FID = new FileInfoDao(context);
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
        rootView = null;
        context = null;
        FID = null;
        dbDao = null;
        dialog = null;
        lv_album = null;
        lv_download = null;
        img_download = null;
        img_quanxuan = null;
        tv_quxiao = null;
        tv_download = null;
        tv_sum = null;
        textTotal = null;
        lin_quanxuan = null;
        lin_status2 = null;
        imageSort = null;
        imageSortDown = null;
        mainAdapter = null;
        adapter = null;
        SubListAll = null;
        urlList = null;
        SubList = null;
        fList = null;
        userId = null;
    }
}
