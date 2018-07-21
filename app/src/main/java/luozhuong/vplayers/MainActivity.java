package luozhuong.vplayers;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import luozhuong.vplayers.activity.BaseActivity;
import luozhuong.vplayers.fragment.DiscoverFragment;
import luozhuong.vplayers.fragment.HomeFragment;
import luozhuong.vplayers.fragment.PersonalFragment;

public class MainActivity extends BaseActivity {
    private HomeFragment homeFragment;
    private DiscoverFragment discoverFragment;
    private PersonalFragment personalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setBottomNavigation(R.id.homeNavigation);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onTabSelected(int position) {
        super.onTabSelected(position);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                transaction.replace(R.id.fragment_container, homeFragment);
                break;
            case 1:
                if (discoverFragment == null) {
                    discoverFragment = new DiscoverFragment();
                }
                transaction.replace(R.id.fragment_container, discoverFragment);
                break;
            case 2:
                if (personalFragment == null) {
                    personalFragment = new PersonalFragment();
                }
                transaction.replace(R.id.fragment_container, personalFragment);
                break;
            default:
                break;
        }
        // 事务提交
        transaction.commit();
    }
}
