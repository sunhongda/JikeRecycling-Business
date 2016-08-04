package com.angm.jikeb.fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.angm.jikeb.R;
/**
 * Created by shd on 16-8-4.
 * 用于地图Fragment
 */
public class MapFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public String getTitle() {
        return "地图";
    }

    @Override
    protected int getFragmentLayout() {
        return  R.layout.fragment_vp_map;
    }

    @Override
    protected void initView() {

    }


}
