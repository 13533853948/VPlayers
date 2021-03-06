package luozhuong.vplayers.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.Stack;

import luozhuong.vplayers.R;

/**
 * Author: Tt_Al-xi
 * Time: 2018/7/20 17:47
 * This is BaseActivity
 */

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 底部导航三个值
     **/
    private String str1, str2, str3;
    /**
     * 底部导航三个未点击图片
     **/
    private int img1, img2, img3;
    /**
     * 底部导航三个点击图片
     **/
    private int isImg1, isImg2, isImg3;
    /**
     * 底部导航栏
     **/
    private BottomNavigationBar bottomNavigationBar;
    /**
     * 标题栏
     **/
    protected ImmersionBar mImmersionBar;
    /**
     * 是否允许全屏
     **/
    private boolean mAllowFullScreen = false;
    /**
     * 是否禁止旋转屏幕
     **/
    private boolean isAllowScreenRoate = true;
    /**
     * 是否沉浸状态栏
     **/
    private boolean isImmersionBarEnabled = true;
    /**
     * 记录上次点击按钮的时间
     **/
    private long lastClickTime;
    /**
     * 按钮连续点击最低间隔时间 单位：毫秒
     **/
    public final static int CLICK_TIME = 500;
    /**
     * 用来保存所有已打开的Activity
     **/
    private static Stack<Activity> listActivity = new Stack<Activity>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        if (mAllowFullScreen) {
            this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        setContentView(setLayoutId());
        //旋转屏幕
        if (!isAllowScreenRoate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //沉浸式
        if (isImmersionBarEnabled) {
            initImmersionBar();
        }
        // 将activity推入栈中
        listActivity.push(this);
        // 初始化ui
        initView();
        // 初始化数据
        initData();
        // 添加监听器
        initListener();
    }

    protected abstract int setLayoutId();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * 保存activity状态
     **/
    protected void saveInstanceState(Bundle outState) {

    }

    protected void initImmersionBar() {
        /**
         状态栏：是指手机左上最顶上，显示中国移动、安全卫士、电量、网速等等，在手机的顶部。下拉就会出现通知栏。
         标题栏：是指一个APP程序最上部的titleBar，从名字就知道它显然就是一个应用程序一个页面的标题了，例如打开QQ消息主页，最上面显示消息那一栏就是标题栏。
         导航栏：是手机最下面的返回，HOME，主页三个键，有些是一个按钮。

         compile 'com.gyf.barlibrary:barlibrary:2.3.0'
         需要添加混淆-keep class com.gyf.barlibrary.* {*;}
         */
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this)
//                .transparentStatusBar()  //透明状态栏，不写默认透明色
//                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
//                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
//                .statusBarColor(R.color.transparent)     //状态栏颜色，不写默认透明色
//                .navigationBarColor(R.color.colorPrimary) //导航栏颜色，不写默认黑色
//                .barColor(R.color.colorPrimary)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
                .statusBarAlpha(0.3f)  //状态栏透明度，不写默认0.0f
//                .navigationBarAlpha(0.4f)  //导航栏透明度，不写默认0.0F
//                .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认0.0f
//                .statusBarDarkFont(true,0.2f)   //状态栏字体是深色，不写默认为亮色 true //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
//                .flymeOSStatusBarFontColor(R.color.black)  //修改flyme OS状态栏字体颜色
//                .fullScreen(true)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
//                .hideBar(BarHide.FLAG_HIDE_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
//                .addViewSupportTransformColor(toolbar)  //设置支持view变色，可以添加多个view，不指定颜色，默认和状态栏同色，还有两个重载方法
//                .titleBar(view)    //解决状态栏和布局重叠问题，任选其一
//                .titleBarMarginTop(view)     //解决状态栏和布局重叠问题，任选其一
//                .statusBarView(view)  //解决状态栏和布局重叠问题，任选其一
                .fitsSystemWindows(true)    //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色，还有一些重载方法
//                .supportActionBar(true) //支持ActionBar使用
//                .statusBarColorTransform(R.color.orange)  //状态栏变色后的颜色
//                .navigationBarColorTransform(R.color.orange) //导航栏变色后的颜色
//                .barColorTransform(R.color.orange)  //状态栏和导航栏变色后的颜色
//                .removeSupportView(toolbar)  //移除指定view支持
//                .removeSupportAllView() //移除全部view支持
//                .navigationBarEnable(true)   //是否可以修改导航栏颜色，默认为true
//                .navigationBarWithKitkatEnable(true)  //是否可以修改安卓4.4和emui3.1手机导航栏颜色，默认为true
//                .fixMarginAtBottom(true)   //已过时，当xml里使用android:fitsSystemWindows="true"属性时,解决4.4和emui3.1手机底部有时会出现多余空白的问题，默认为false，非必须
//                .addTag("tag")  //给以上设置的参数打标记
//                .getTag("tag")  //根据tag获得沉浸式参数
//                .reset()  //重置所以沉浸式参数
//                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  //单独指定软键盘模式
//                .setOnKeyboardListener(new OnKeyboardListener() {    //软键盘监听回调
//                    @Override
//                    public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
//                        Logger.e(isPopup + "");  //isPopup为true，软键盘弹出，为false，软键盘关闭
//                    }
//                })
        ;
        mImmersionBar.init();
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @param isImmersionBarEnabled
     */
    public void setImmersionBarEnabled(boolean isImmersionBarEnabled) {
        this.isImmersionBarEnabled = isImmersionBarEnabled;
    }

    /**
     * [是否允许全屏]
     *
     * @param allowFullScreen
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }


    /**
     * [是否允许屏幕旋转]
     *
     * @param isAllowScreenRoate
     */
    public void setScreenRoate(boolean isAllowScreenRoate) {
        this.isAllowScreenRoate = isAllowScreenRoate;
    }

    /**
     * [沉浸状态栏]
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //在BaseActivity里销毁
        // 从栈中移除当前activity
        if (listActivity.contains(this)) {
            listActivity.remove(this);
        }
    }

    // 初始化ui   用来出来初始化视图   别忘了setContentView(R.layout.XXX);加载布局文件
    protected abstract void initView();

    // 初始化数据  用来初始化数据以及处理数据
    protected abstract void initData();

    // 添加监听器  用来给控件添加监听器，可以在里面写如btn.setonClickListener(){}; .....等监听器。
    protected abstract void initListener();

    //这样我们就不用每次跳转Activity的时候都去new一个Intent，然后startActivity(intent)；有了它我们只需一行代码就搞定:openActivity(xxxActivity,bundler)

    /********************** activity跳转 **********************************/
    public void openActivity(Class<?> targetActivityClass) {
        openActivity(targetActivityClass, null);
    }

    public void openActivity(Class<?> targetActivityClass, Bundle bundle) {
        Intent intent = new Intent(this, targetActivityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void openActivityAndCloseThis(Class<?> targetActivityClass) {
        openActivity(targetActivityClass);
        this.finish();
    }

    public void openActivityAndCloseThis(Class<?> targetActivityClass, Bundle bundle) {
        openActivity(targetActivityClass, bundle);
        this.finish();
    }

    /***************************************************************/

    /**
     * 验证上次点击按钮时间间隔，防止重复点击
     */
    public boolean verifyClickTime() {
        if (System.currentTimeMillis() - lastClickTime <= CLICK_TIME) {
            return false;
        }
        lastClickTime = System.currentTimeMillis();
        return true;
    }

    // short吐司
    public void showShort(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // long吐司
    public void showLong(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 关闭所有(前台、后台)Activity,注意：请已BaseActivity为父类
     */
    protected static void finishAll() {
        int len = listActivity.size();
        for (int i = 0; i < len; i++) {
            Activity activity = listActivity.pop();
            activity.finish();
        }
    }

    /***************** 双击退出程序 ************************************************/
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == keyCode) {
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), getString(R.string.tryAgain), Toast.LENGTH_SHORT).show();
                // 将系统当前的时间赋值给exitTime
                exitTime = System.currentTimeMillis();
            } else {
                finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /***************** 底部导航栏 ************************************************/
    //compile 'com.ashokvarma.android:bottom-navigation-bar:2.0.4'
    public void setBottomString(String str1, String str2, String str3) {
        this.str1 = str1;
        this.str2 = str2;
        this.str3 = str3;
    }

    public void setBottomImage(int img1, int img2, int img3, int isImg1, int isImg2, int isImg3) {
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.isImg1 = isImg1;
        this.isImg2 = isImg2;
        this.isImg3 = isImg3;
    }

    public void setBottomNavigation(int homeNavigation, final Fragment fr1, final Fragment fr2, final Fragment fr3) {
        //底部导航栏 https://blog.csdn.net/weihua_li/article/details/78561323   https://www.jianshu.com/p/249c78b497a3
        bottomNavigationBar = (BottomNavigationBar) findViewById(homeNavigation);//R.id.homeNavigation
        //监听
        //bottomNavigationBar.setTabSelectedListener(this);
        //模式Modes        /* mode_default:如果选项大于3个，使用mode_shifting，否则使用mode_fixed        mode_fixed:每个item对应名称，不选中也会显示        mode_shifting:每个item对应名称，只有选中才会显示，不选中隐藏        mode_fixed_no_title:相当于mode_fixed只是不显示所有文字        mode_shifting_no_title:相当于mode_shifting只是不显示所有文字*/
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        //背景样式Background Styles        /*background_style_default:如果mode设为MODE_FIXED，默认使用BACKGROUND_STYLE_STATIC；如果mode设为MODE_SHIFTING，默认使用 BACKGROUND_STYLE_RIPPLE         background_style_static:点击的时候没有水波纹效果         background_style_ripple:点击的时候有水波纹效果*/
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        // bottomNavigationBar.setActiveColor(R.color.primary).setInActiveColor("#FFFFFF").setBarBackgroundColor("#ECECEC")        /*in-active color：图标和文本未被激活或选中的颜色；默认颜色为Theme’s Primary Color        active color : 在BACKGROUND_STYLE_STATIC下，为图标和文本激活或选中的颜色；在BACKGROUND_STYLE_RIPPLE下，为整个控件的背景颜色；默认颜色为Color.LTGRAY        background color :在BACKGROUND_STYLE_STATIC 下，为整个空控件的背景色；在 BACKGROUND_STYLE_RIPPLE 下为图标和文本被激活或选中的颜色；默认颜色为Color.WHITE*/
        bottomNavigationBar.setAutoHideEnabled(false);
        //自动隐藏：        /*如果容器在Co - ordinator Layout布局内，默认情况下，向下滚动会隐藏，向上滚动会展示；通过调用方法setAutoHideEnabled(false) 可以关闭该特性        isHidden() 返回是否隐藏 手动隐藏：bottomNavigationBar.hide();//隐藏bottomNavigationBar.show();//显示        默认都是动画模式，参数传false可以关闭动画 bottomNavigationBar.hide(false);//关闭动画效果 bottomNavigationBar.show(false);//关闭动画效果 */
        //TextBadgeItem numberBadgeItem = new TextBadgeItem(); 添加小红点
        //修改阴影
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //bottomNavigationBar.setElevation(0);}

        bottomNavigationBar.addItem(new BottomNavigationItem(img1, str1).setActiveColor(R.color.black)
                .setInactiveIcon(ContextCompat.getDrawable(this, isImg1)))
                .addItem(new BottomNavigationItem(img2, str2)//未选择图标
                        .setActiveColor(R.color.black)//选择颜色
                        .setInactiveIcon(ContextCompat.getDrawable(this, isImg2)))//选择图标
                .addItem(new BottomNavigationItem(img3, str3)
                        .setActiveColor(R.color.black)
                        .setInactiveIcon(ContextCompat.getDrawable(this, isImg3)))
                .setFirstSelectedPosition(0)//默认选择第一个item，但是仅仅是显示效果，并不是真正的点击了第一个item，所以如果默认显示第一个fragment,人仍需要我们自己处理。
                .initialise();

        //setBottomNavigationItem(bottomNavigationBar,6, 26, 14);

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                //未选中->选中
                Logger.e("onTabSelected: " + position);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                switch (position) {
                    case 0:
                        transaction.replace(R.id.fragment_container, fr1);
                        break;
                    case 1:
                        transaction.replace(R.id.fragment_container, fr2);
                        break;
                    case 2:
                        transaction.replace(R.id.fragment_container, fr3);
                        break;
                    default:
                        break;
                }
                // 事务提交
                transaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {
                //选中->未选中
                //Logger.e("onTabUnselected: " + position);
            }

            @Override
            public void onTabReselected(int position) {
                //选中->选中
                //Logger.e("onTabReselected: " + position);
            }
        });

        bottomNavigationBar.selectTab(0);//设置默认第一个
    }


    /**
     * @param bottomNavigationBar，需要修改的 BottomNavigationBar
     * @param space                     图片与文字之间的间距
     * @param imgLen                    单位：dp，图片大小，应 <= 36dp
     * @param textSize                  单位：dp，文字大小，应 <= 20dp
     *                                  <p>
     *                                  使用方法：直接调用setBottomNavigationItem(bottomNavigationBar, 6, 26, 10);
     *                                  代表将bottomNavigationBar的文字大小设置为10dp，图片大小为26dp，二者间间距为6dp
     **/

    private void setBottomNavigationItem(BottomNavigationBar bottomNavigationBar, int space, int imgLen, int textSize) {
        Class barClass = bottomNavigationBar.getClass();
        Field[] fields = barClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (field.getName().equals("mTabContainer")) {
                try {
                    //反射得到 mTabContainer
                    LinearLayout mTabContainer = (LinearLayout) field.get(bottomNavigationBar);
                    for (int j = 0; j < mTabContainer.getChildCount(); j++) {
                        //获取到容器内的各个Tab
                        View view = mTabContainer.getChildAt(j);
                        //获取到Tab内的各个显示控件
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(56));
                        FrameLayout container = (FrameLayout) view.findViewById(R.id.fixed_bottom_navigation_container);
                        container.setLayoutParams(params);
                        container.setPadding(dip2px(12), dip2px(0), dip2px(12), dip2px(0));

                        //获取到Tab内的文字控件
                        TextView labelView = (TextView) view.findViewById(com.ashokvarma.bottomnavigation.R.id.fixed_bottom_navigation_title);
                        //计算文字的高度DP值并设置，setTextSize为设置文字正方形的对角线长度，所以：文字高度（总内容高度减去间距和图片高度）*根号2即为对角线长度，此处用DP值，设置该值即可。
                        labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                        labelView.setIncludeFontPadding(false);
                        labelView.setPadding(0, 0, 0, dip2px(20 - textSize - space / 2));

                        //获取到Tab内的图像控件
                        ImageView iconView = (ImageView) view.findViewById(com.ashokvarma.bottomnavigation.R.id.fixed_bottom_navigation_icon);
                        //设置图片参数，其中，MethodUtils.dip2px()：换算dp值
                        params = new FrameLayout.LayoutParams(dip2px(imgLen), dip2px(imgLen));
                        params.setMargins(0, 0, 0, space / 2);
                        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        iconView.setLayoutParams(params);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /*********************隐藏和显示键盘************************/
    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 点击软键盘之外的空白处，隐藏软件盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 显示软键盘
     */
    public void showInputMethod() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
