package luozhuong.vplayers.application;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * Author: Tt_Al-xi
 * Time: 2018/7/16 19:53
 * This is MyApplication
 */

public class MyApplication extends Application {
    private static Context context;
    public static final String TAG = "VPlayers";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  //（可选）是否显示线程信息。 默认值为true
                .methodCount(2)         // （可选）要显示的方法行数。 默认2
                .methodOffset(7)        // （可选）隐藏内部方法调用到偏移量。 默认5
                //.logStrategy(customLog) //（可选）更改要打印的日志策略。 默认LogCat
                .tag(TAG)   //（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        //Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));//日志保存到本地

        Logger.e("初始化完成！");
    }

    public static Context getInstance() {
        return context;
    }
}
