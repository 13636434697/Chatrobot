package com.xu.chatrobot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;
import java.util.Random;
/*
* 聊天机器人
* */

public class MainActivity extends Activity {

    private ListView lvList;
    private StringBuffer mVoiceBuffer;

    //集合维护回答的集合，listview在这里去数据
    private ArrayList<TalkBean> mTalkList = new ArrayList<TalkBean>();
    private VoiceAdapter mAdapter;

    private String[] mAnswers = new String[] { "约吗?", "等你哦!!!", "没有更多美女了",
            "这是最后一张了!", "不要再要了", "人家害羞嘛" };

    private int[] mAnswerPics = new int[] { R.drawable.p1, R.drawable.p2,R.drawable.p3, R.drawable.p4 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn //
        // 请勿在“=”与appid之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=562c44bb");

        lvList = (ListView) findViewById(R.id.lv_list);
        mAdapter = new VoiceAdapter();
        lvList.setAdapter(mAdapter);
    }

    //开始语音，开始监听
    public void startVoice(View view) {
        // 1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        // 2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        mVoiceBuffer = new StringBuffer();

        //回调2个次这个方法，就是2句话
        // 3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                System.out.println("isLast:" + isLast);
                //这个是一句话的一部分
                String voiceStr = parseData(results.getResultString());
                // System.out.println(voiceStr);

                //这个是一句话的一部分
                //拼接文字，用StringBuffer
                mVoiceBuffer.append(voiceStr);

                //true说明话说完了
                if (isLast) {

                    //这就是完成的话
                    String askStr = mVoiceBuffer.toString();
                    System.out.println(askStr);

                    //语音识别之后
                    // 封装一个对象，参数：content，是否提问，有没有图片
                    TalkBean ask = new TalkBean(askStr, true, -1);
                    //添加到集合里面
                    mTalkList.add(ask);


                    // 初始化回答对象
                    //默认没有听清楚
                    String answerStr = "没听清";
                    int imageId = -1;
                    if (askStr.contains("你好")) {
                        answerStr = "你好呀!!!";
                    } else if (askStr.contains("你是谁")) {
                        answerStr = "我是你的小猪手";
                    } else if (askStr.contains("美女")) {
                        Random random = new Random();
                        // 随机回答
                        int strPos = random.nextInt(mAnswers.length);
                        answerStr = mAnswers[strPos];

                        // 随机图片
                        int imagePos = random.nextInt(mAnswerPics.length);
                        imageId = mAnswerPics[imagePos];
                    } else if (askStr.contains("天王盖地虎")) {
                        answerStr = "小鸡炖蘑菇";
                        imageId = R.drawable.m;
                    }

                    //流程下来就可以回答了。所以初始化一个对象，并添加到集合里面
                    TalkBean answer = new TalkBean(answerStr, false, imageId);
                    mTalkList.add(answer);


                    // 刷新listview
                    mAdapter.notifyDataSetChanged();

                    // 让listview定位到最后一个item
                    lvList.setSelection(mTalkList.size() - 1);

                    //把回答读出来
                    startSpeak(answerStr);
                }
            }

            @Override
            public void onError(SpeechError arg0) {

            }
        });

        // 4.显示dialog，接收语音输入
        mDialog.show();
    }

    //说出来的话，已经转成文字，但是是json格式需要解析成文字
    // 解析语音json
    private String parseData(String json) {
        Gson gson = new Gson();
        VoiceBean voiceBean = gson.fromJson(json, VoiceBean.class);

        //拼接文字，用StringBuffer
        StringBuffer sb = new StringBuffer();

        //拿到集合
        ArrayList<VoiceBean.WsBean> ws = voiceBean.ws;
        //遍历集合
        for (VoiceBean.WsBean wsBean : ws) {
            //这个集合不用遍历，因为只有一个元素，直接拿出第一个字段就可以了
            String w = wsBean.cw.get(0).w;
            //拼接文字，用StringBuffer
            sb.append(w);
        }

        return sb.toString();
    }

    // 语音合成
    public void startSpeak(String content) {
        // 1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        // 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        // 设置发音人（更多在线发音人，用户可参见 附录12.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); // 设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
        // 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        // 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        // 仅支持保存为pcm和wav格式，如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");

        // 3.开始合成
        mTts.startSpeaking(content, null);
    }

    //填充listView的adapter
    class VoiceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTalkList.size();
        }

        @Override
        public TalkBean getItem(int position) {
            return mTalkList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),R.layout.list_item, null);
                holder = new ViewHolder();
                holder.tvAsk = (TextView) convertView.findViewById(R.id.tv_ask);
                holder.tvAnswer = (TextView) convertView.findViewById(R.id.tv_answer);
                holder.llAnswer = (LinearLayout) convertView.findViewById(R.id.ll_answer);
                holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //更新数据，拿到相应的对象
            TalkBean item = getItem(position);
            if (item.isAsk) {
                // 提问
                holder.tvAsk.setVisibility(View.VISIBLE);
                holder.llAnswer.setVisibility(View.GONE);

                holder.tvAsk.setText(item.content);
            } else {
                // 回答
                holder.tvAsk.setVisibility(View.GONE);
                holder.llAnswer.setVisibility(View.VISIBLE);

                holder.tvAnswer.setText(item.content);

                // 有图片
                if (item.imageId > 0) {
                    holder.ivPic.setVisibility(View.VISIBLE);
                    holder.ivPic.setImageResource(item.imageId);
                } else {
                    // 没图片
                    holder.ivPic.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

    }

    //复用
    static class ViewHolder {
        public TextView tvAsk;
        public TextView tvAnswer;
        public ImageView ivPic;
        public LinearLayout llAnswer;
    }

}
