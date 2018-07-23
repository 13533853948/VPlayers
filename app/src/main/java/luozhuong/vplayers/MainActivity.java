package luozhuong.vplayers;


import luozhuong.vplayers.activity.BaseActivity;
import luozhuong.vplayers.fragment.DiscoverFragment;
import luozhuong.vplayers.fragment.HomeFragment;
import luozhuong.vplayers.fragment.PersonalFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setBottomString(getString(R.string.homeOne), getString(R.string.homeTwo), getString(R.string.homeThree));
        setBottomImage(R.drawable.nav_jingxuan_pre, R.drawable.nav_home_pre, R.drawable.nav_mine_pre, R.drawable.nav_jingxuan_nor, R.drawable.nav_home_nor, R.drawable.nav_mine_nor);
        setBottomNavigation(R.id.homeNavigation, new HomeFragment(), new DiscoverFragment(), new PersonalFragment());
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

}
