package sinhala.novels.ebooks.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.SearchActivity;
import sinhala.novels.ebooks.Threads.HomeThread;

public class HomeFragment extends Fragment {

    public RecyclerView sliderRecyclerView,mainRecyclerView;
    private Context context;
    private RelativeLayout searchBtn;
    Animation clickAnim; //Click Animation
    public LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        context=getContext();
        clickAnim=AnimationUtils.loadAnimation(context,R.anim.click_animation);

        mainRecyclerView=view.findViewById(R.id.recyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        sliderRecyclerView=view.findViewById(R.id.sliderRecyclerView);
        layoutManager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        sliderRecyclerView.setLayoutManager(layoutManager);

        HomeThread homeThread=new HomeThread(context,HomeFragment.this);
        homeThread.start();

        searchBtn=view.findViewById(R.id.serachBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBtn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_animation));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), SearchActivity.class));
                    }
                },100);
            }
        });

        return view;
    }
}