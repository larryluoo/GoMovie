package im.zego.gomovie.client.manager;

import android.os.SystemClock;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import im.zego.gomovie.client.R;
import im.zego.gomovie.client.model.UserInfo;

public class UserManager {

    public final static int MIC_CAMERA_CLOSE = 1; // 摄像头/麦克风 1：关闭  2:打开
    public final static int MIC_CAMERA_OPEN = 2; // 摄像头/麦克风 1：关闭  2:打开

    public static final int[] headPortraits = new int[]{R.mipmap.movie_head_1, R.mipmap.movie_head_2, R.mipmap.movie_head_3,
            R.mipmap.movie_head_4, R.mipmap.movie_head_5, R.mipmap.movie_head_6};
    public static final int[] backgroundList = new int[]{R.mipmap.movie_head_bg_1, R.mipmap.movie_head_bg_2, R.mipmap.movie_head_bg_3,
            R.mipmap.movie_head_bg_4, R.mipmap.movie_head_bg_5, R.mipmap.movie_head_bg_6};

    public static final String[] surnames = new String[]{
            "李", "王", "张", "刘", "陈", "杨", "黄", "赵", "周", "吴", "徐", "孙", "朱", "马", "胡", "郭", "林", "何", "高", "梁",
            "郑", "罗", "宋", "谢", "唐", "韩", "曹", "许", "邓", "萧", "冯", "曾", "程", "蔡", "彭", "潘", "袁", "于", "董", "余",
            "苏", "叶", "吕", "魏", "蒋", "田", "杜", "丁", "沈", "姜", "范", "江", "傅", "钟", "卢", "汪", "戴", "崔", "任", "陆",
            "廖", "姚", "方", "金", "邱", "夏", "谭", "韦", "贾", "邹", "石", "熊", "孟", "秦", "阎", "薛", "侯", "雷", "白", "龙",
    };
    public static final String[] names = new String[]{
            "伟", "芳", "娜", "敏", "静", "秀英", "丽", "强", "磊", "洋", "艳", "勇", "军", "杰", "娟", "涛", "明", "霞", "秀兰", "刚",
            "平", "燕", "辉", "静", "玲", "桂英", "丹", "萍", "鹏", "华", "红", "超", "玉兰", "飞", "桂兰", "梅", "鑫", "波", "斌", "莉",
            "浩", "凯", "秀珍", "俊", "帆", "雪", "帅", "婷", "玉梅", "浩然", "子轩", "宇轩", "浩宇", "一诺", "子墨", "博文", "宇涵", "雨泽", "子豪", "明轩",
            "诗涵", "可鑫", "雨宣", "欣妍", "可欣", "紫涵", "思涵", "亦菲", "淑华", "佳怡", "慧嘉", "诗悦", "清妍", "佳钰", "昕蕊", "熙涵", "佳毅", "天昊", "佳昊", "文杰",
    };

    private UserInfo mUserInfo;

    private static final class Holder {
        private static final UserManager INSTANCE = new UserManager();
    }

    public static UserManager getInstance() {
        return UserManager.Holder.INSTANCE;
    }

    public String generateUserName() {
        Random rand = new java.util.Random();
        int p1 = rand.nextInt(surnames.length);
        int p2 = rand.nextInt(names.length);
        return surnames[p1] + names[p2];
    }

    public String getAvatar(String nickName) {
        return String.valueOf(getValue(nickName));
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void reset() {
        String name;
        if (mUserInfo != null) {
            name = mUserInfo.getNickName();
        } else {
            name = generateUserName();
        }

        String avatar = UserManager.getInstance().getAvatar(name);
        setUserInfo(new UserInfo(getBoostTimeMillis(), avatar, name, MIC_CAMERA_OPEN, MIC_CAMERA_OPEN));
    }

    public long getUserId() {
        if (mUserInfo == null) {
            return 0;
        }
        return mUserInfo.getUid();
    }

    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
    }

    /**
     * 使用开机计时
     * 替代System.currentTimeMillis()
     * 可以防止修改手机时间导致判断时间间隔不对
     *
     * @return
     */
    public long getBoostTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    private static byte[] md5(String input) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5").digest(input.getBytes());
    }

    private static int getValue(String input) {
        byte[] b;
        try {
            b = md5(input);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }

        if (b.length > 0) {
            return Math.abs(b[0] % headPortraits.length) + 1;
        } else {
            return 0;
        }
    }

}
