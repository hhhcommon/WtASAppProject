package com.woting.ui.musicplay.album.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.woting.ui.music.model.album;
import com.woting.ui.music.model.content;
import com.woting.ui.musicplay.download.main.DownloadFragment;
import com.woting.ui.musicplay.download.dao.FileInfoDao;
import com.woting.ui.musicplay.download.fragment.DownLoadUnCompletedFragment;
import com.woting.ui.musicplay.download.model.FileInfo;
import com.woting.ui.musicplay.download.service.DownloadService;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.album.adapter.AlbumAdapter;
import com.woting.ui.musicplay.album.adapter.AlbumMainAdapter;
import com.woting.ui.musicplay.album.main.AlbumFragment;
import com.woting.ui.main.MainActivity;
import com.woting.ui.musicplay.play.model.PlayerHistory;

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

    private List<content> SubListAll = new ArrayList<>();
    private List<content> urlList = new ArrayList<>();

    private View rootView;
    private Dialog dialog;
    private LinearLayout lin_quanxuan, lin_status2;
    private XListView lv_album;    // 节目列表
    private ListView lv_download;  // 下载列表
    private TipView tipView;       // 没有数据、没有网络提示

    private TextView tv_quxiao, tv_download, tv_sum, textTotal;
    private ImageView img_download, img_quanxuan;// 下载 全选
    private ImageView imageSort;// 排序
    private ImageView imageSortDown;
    private ImageView img_play_all;

    private int page = 1;
    private int sum = 0;// 计数项
    private int sortType = 1;// == 1 按卷号从大到小排序 默认排序；== 2 按卷号从小到大排序；
    private boolean flag = false;// 标记全选的按钮
    private boolean isCancelRequest;
    private String tag = "PROGRAM_VOLLEY_REQUEST_CANCEL_TAG";
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        userId = CommonUtils.getUserId(context);
        initDao();
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
        FID = new FileInfoDao(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_album_program, container, false);
            initView(rootView);
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialog(context);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        send();
                    }
                }, 2000);
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            }
        }
        return rootView;
    }

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
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
        tv_sum = (TextView) view.findViewById(R.id.tv_sum);                  // 计数项
        lin_status2 = (LinearLayout) view.findViewById(R.id.lin_status2);    // 第二种状态
        textTotal = (TextView) view.findViewById(R.id.text_total);           // 下载列表的总计

        imageSort = (ImageView) view.findViewById(R.id.img_sort);            // 排序
        imageSort.setOnClickListener(this);

        img_play_all = (ImageView) view.findViewById(R.id.img_play_all);
        img_play_all.setOnClickListener(this);                               // 播放专辑内全部内容

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

    // 向服务器发送请求
    public void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("ContentId", AlbumFragment.id);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("PageSize", "20");
            jsonObject.put("SortType", String.valueOf(sortType));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getSmSubMedias, tag, jsonObject, new VolleyCallback() {
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
                                textTotal.setText("共0集");
                            }
                            try {
                                String subList = arg1.getString("SubList");
                                try {
                                    List<content> SubList = new Gson().fromJson(subList, new TypeToken<List<content>>() {
                                    }.getType());
                                    if (SubList != null && SubList.size() > 0) {
                                        if (page == 1) SubListAll.clear();
                                        page++;
                                        SubListAll.addAll(SubList);
                                        setDataSeqInfo(); // 添加专辑数据
                                        setDataTime();    // 组装播放进度
                                        // 数据展示
                                        if (mainAdapter != null) {
                                            mainAdapter.notifyDataSetChanged();
                                        } else {
                                            mainAdapter = new AlbumMainAdapter(context, SubListAll);
                                            lv_album.setAdapter(mainAdapter);
                                        }
                                        setListener();

                                        // 下载展示
                                        if (adapter != null) {
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            adapter = new AlbumAdapter(context, SubListAll);
                                            lv_download.setAdapter(adapter);
                                        }
                                        setInterface();

                                        tipView.setVisibility(View.GONE);
                                        lv_album.stopLoadMore();
                                    } else {
                                        if (page == 1) {
                                            lv_album.stopLoadMore();
                                            tipView.setVisibility(View.VISIBLE);
                                            tipView.setTipView(TipView.TipStatus.NO_DATA, "专辑中没有节目\n换个专辑看看吧");
                                        } else {
                                            lv_album.stopLoadMore();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (page == 1) {
                                        lv_album.stopLoadMore();
                                        tipView.setVisibility(View.VISIBLE);
                                        tipView.setTipView(TipView.TipStatus.NO_DATA, "专辑中没有节目\n换个专辑看看吧");
                                    } else {
                                        lv_album.stopLoadMore();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (page == 1) {
                                    lv_album.stopLoadMore();
                                    tipView.setVisibility(View.VISIBLE);
                                    tipView.setTipView(TipView.TipStatus.NO_DATA, "专辑中没有节目\n换个专辑看看吧");
                                } else {
                                    lv_album.stopLoadMore();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (page == 1) {
                                lv_album.stopLoadMore();
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.NO_DATA, "专辑中没有节目\n换个专辑看看吧");
                            } else {
                                lv_album.stopLoadMore();
                            }
                        }
                    } else if (ReturnType != null && ReturnType.equals("1011")) {
                        if (page == 1) {
                            lv_album.setPullLoadEnable(false);
                            ToastUtils.show_always(context, getString(R.string.no_data));
                        } else {
                            lv_album.stopLoadMore();
                        }
                    } else {
                        if (page == 1) {
                            lv_album.stopLoadMore();
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "专辑中没有节目\n换个专辑看看吧");
                        } else {
                            lv_album.stopLoadMore();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (page == 1) {
                        lv_album.stopLoadMore();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    } else {
                        lv_album.stopLoadMore();
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                if (page == 1) {
                    lv_album.stopLoadMore();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                } else {
                    lv_album.stopLoadMore();
                }
            }
        });
    }

    // 数据展示ListView 的 Item 的监听事件
    private void setListener() {
        lv_album.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SubListAll != null && SubListAll.get(position - 1) != null && SubListAll.get(position - 1).getMediaType() != null) {
                    String MediaType = SubListAll.get(position - 1).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(MediaType, SubListAll, position - 1);// 保存播放历史

                        MainActivity.change();
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, SubListAll.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    } else {
                        ToastUtils.show_always(context, "暂不支持播放");
                    }
                }
            }
        });
    }

    // 下载界面的数据监听
    private void setInterface() {
        lv_download.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SubListAll != null && SubListAll.get(position) != null) {
                    if (SubListAll.get(position).getChecktype() == 3) {
                        ToastUtils.show_always(context, "已经下载过");
                    } else {
                        if (SubListAll.get(position).getChecktype() == 1) {
                            SubListAll.get(position).setChecktype(2);
                        } else {
                            SubListAll.get(position).setChecktype(1);
                        }
                        int downLoadSum = 0;
                        sum = 0;
                        for (int i = 0; i < SubListAll.size(); i++) {
                            if (SubListAll.get(i).getChecktype() == 2) {
                                sum++;
                            }
                            if (SubListAll.get(i).getChecktype() == 3) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_download:    // 显示下载列表
                if (SubListAll.size() == 0) {
                    return;
                }
                getData();             // 组装数据
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    lv_download.setAdapter(adapter = new AlbumAdapter(context, SubListAll));
                }
                lv_download.setSelection(0);
                lin_status2.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_quxiao:       // 取消
                lin_status2.setVisibility(View.GONE);
                for (int i = 0; i < SubListAll.size(); i++) {
                    if (SubListAll.get(i).getChecktype() != 3) {
                        img_quanxuan.setImageResource(R.mipmap.image_not_all_check);
                        SubListAll.get(i).setChecktype(1);
                    }
                }
                sum = 0;
                setSum();
                flag = false;
                break;
            case R.id.lin_quanxuan:    // 全选
                if (!flag) {           // 默认为未选中状态
                    sum = 0;
                    for (int i = 0; i < SubListAll.size(); i++) {
                        if (SubListAll.get(i).getChecktype() != 3) {
                            SubListAll.get(i).setChecktype(2);
                            sum++;
                        }
                    }
                    flag = true;
                    img_quanxuan.setImageResource(R.mipmap.wt_group_checked);
                    setSum();
                } else {
                    for (int i = 0; i < SubListAll.size(); i++) {
                        if (SubListAll.get(i).getChecktype() != 3) {
                            SubListAll.get(i).setChecktype(1);
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
                download();               // 下载数据方法
                break;
            case R.id.img_sort:
                if (SubListAll.size() != 0 && mainAdapter != null) {
                    sortType = 2;
                    page = 1;
                    dialog = DialogUtils.Dialog(context);
                    send();
                    imageSortDown.setVisibility(View.VISIBLE);
                    imageSort.setVisibility(View.GONE);
                }
                break;
            case R.id.img_sort_down:
                if (SubListAll.size() != 0 && mainAdapter != null) {
                    sortType = 1;
                    page = 1;
                    dialog = DialogUtils.Dialog(context);
                    send();
                    imageSortDown.setVisibility(View.GONE);
                    imageSort.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_play_all:
                if (!TextUtils.isEmpty(AlbumFragment.id)) {

                    dbDao.savePlayerHistory(StringConstant.TYPE_AUDIO, SubListAll, 0);// 保存播放历史

                    Intent intent = new Intent(BroadcastConstants.PLAY_SEQU_LIST);
                    Bundle bundle = new Bundle();
                    // 组装需要传递的专辑数据
                    album s = new album();
                    s.setContentDescn(AlbumFragment.ContentDesc);
                    s.setContentName(AlbumFragment.ContentName);
                    s.setContentImg(AlbumFragment.ContentImg);
                    s.setContentId(AlbumFragment.id);
                    bundle.putSerializable("album", s);
                    // 上次版本传递的数据，待修改
                    bundle.putString(StringConstant.ID_CONTENT, AlbumFragment.id);
                    bundle.putString("SortType", String.valueOf(sortType));
                    bundle.putInt(StringConstant.SEQU_LIST_SIZE, getListSize());
                    intent.putExtras(bundle);
                    context.sendBroadcast(intent);
                    MainActivity.change();
                }
                break;
        }
    }

    // 下载数据
    private void download() {
        urlList.clear();
        for (int i = 0; i < SubListAll.size(); i++) {
            if (SubListAll.get(i).getChecktype() == 2) {
                content mContent = SubListAll.get(i);

                // 判断 userId 是否为空
                mContent.setUserid(userId);
                mContent.setDownloadtype("0");
                // 组装需要传递的专辑数据
                album s = new album();
                s.setContentDescn(AlbumFragment.ContentDesc);
                s.setContentName(AlbumFragment.ContentName);
                s.setContentImg(AlbumFragment.ContentImg);
                s.setContentId(AlbumFragment.id);
                mContent.setSeqInfo(s);

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
            if (DownloadFragment.isVisible) {
                DownLoadUnCompletedFragment.dwType = true;
            }

            ToastUtils.show_always(context, "已经开始下载您所选择的数据");

            // 发送更新界面数据广播
            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_UNCOMPLETED));
            lin_status2.setVisibility(View.GONE);
        } else {
            ToastUtils.show_always(context, "请重新选择数据");
            return;
        }
    }

    protected void setSum() {
        tv_sum.setText(sum + "");
    }

    // 获取列表数量
    public int getListSize() {
        return SubListAll.size();
    }

    // 组装数据
    private void getData() {
        List<FileInfo> fList = FID.queryFileInfoAll(userId);
        Log.e("fList", fList.size() + "");
        ArrayList<FileInfo> seqList = new ArrayList<>();
        if (fList != null && fList.size() > 0) {
            for (int i = 0; i < fList.size(); i++) {
                if (fList.get(i).getSequimgurl() != null && fList.get(i).getSequimgurl().equals(AlbumFragment.ContentImg)) {
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
                            SubListAll.get(j).setChecktype(3);
                        }
                    }
                }
            }
        }
    }

    // 添加专辑数据
    private void setDataSeqInfo() {
        if (SubListAll != null && SubListAll.size() > 0) {
            album s = new album();
            s.setContentDescn(AlbumFragment.ContentDesc);
            s.setContentName(AlbumFragment.ContentName);
            s.setContentImg(AlbumFragment.ContentImg);
            s.setContentId(AlbumFragment.id);
            for (int j = 0; j < SubListAll.size(); j++) {
                SubListAll.get(j).setSeqInfo(s);
            }
        }
    }

    // 组装播放进度
    private void setDataTime() {
        List<PlayerHistory> history = dbDao.queryHistory();
        Log.e("history", history.size() + "");
        // 组装播放时间
        if (history.size() > 0) {
            for (int i = 0; i < history.size(); i++) {
                String url = history.get(i).getPlayerUrl();
                String time = history.get(i).getPlayerInTime();
                String _time = history.get(i).getPlayerAllTime();
                if (time != null && !time.trim().equals("")) {
                    for (int j = 0; j < SubListAll.size(); j++) {
                        if (SubListAll.get(j).getContentPlay() != null && SubListAll.get(j).getContentPlay().equals(url)) {
                            SubListAll.get(j).setPlayerInTime(time);
                            SubListAll.get(j).setPlayerAllTime(_time);
                        }
                    }
                }
            }
        }
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
        userId = null;
    }
}
