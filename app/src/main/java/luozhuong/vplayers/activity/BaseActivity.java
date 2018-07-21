package luozhuong.vplayers.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private BottomNavigationBar bottomNavigationBar;
    protected ImmersionBar mImmersionBar;
    private InputMethodManager imm;
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
     */
    private static Stack<Activity> listActivity = new Stack<Activity>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置activity为无标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(setLayoutId());
        //初始化沉浸式
        if (isImmersionBarEnabled())
            initImmersionBar();
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
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.imm = null;
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

    /**
     * 收起键盘
     */
    public void closeInputMethod() {
        // 收起键盘
        View view = getWindow().peekDecorView();// 用于判断虚拟软键盘是否是显示的
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
    public void setBottomNavigation(int homeNavigation) {
        //底部导航栏 https://blog.csdn.net/weihua_li/article/details/78561323   https://www.jianshu.com/p/249c78b497a3
        bottomNavigationBar = (BottomNavigationBar) findViewById(homeNavigation);//R.id.homeNavigation
        //监听
        bottomNavigationBar.setTabSelectedListener(this);
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
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.nav_jingxuan_pre, getString(R.string.homeOne)).setActiveColor(R.color.black)
                .setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.nav_jingxuan_nor)))
                .addItem(new BottomNavigationItem(R.drawable.nav_home_pre, getString(R.string.homeTwo))//未选择图标
                        .setActiveColor(R.color.black)//选择颜色
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.nav_home_nor)))//选择图标
                .addItem(new BottomNavigationItem(R.drawable.nav_mine_pre, getString(R.string.homeThree))
                        .setActiveColor(R.color.black)
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.nav_mine_nor)))
                .setFirstSelectedPosition(0)//默认选择第一个item，但是仅仅是显示效果，并不是真正的点击了第一个item，所以如果默认显示第一个fragment,人仍需要我们自己处理。
                .initialise();
        bottomNavigationBar.selectTab(0);//设置默认第一个

        //setBottomNavigationItem(bottomNavigationBar,6, 26, 14);
    }

    @Override
    public void onTabSelected(int position) {
        //未选中->选中
//        Logger.e("onTabSelected: " + position);
    }

    @Override
    public void onTabUnselected(int position) {
        //选中->未选中
//        Logger.e("onTabUnselected: " + position);
    }

    @Override
    public void onTabReselected(int position) {
        //选中->选中
//        Logger.e("onTabReselected: " + position);
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
}
