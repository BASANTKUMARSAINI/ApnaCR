package com.example.apnacr.student.ui.classroom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.apnacr.R;
import com.example.apnacr.SplashActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;


public class ClassRoomFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

FirebaseAuth mAuth;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_student_classroom, container, false);

        mAuth= FirebaseAuth.getInstance();
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle("Logout")
                .setMessage("Do you want logout?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mAuth.getCurrentUser()!=null)
                        {
                            mAuth.signOut();
                            Intent intent=new Intent(getContext(), SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        }

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
//        tabLayout=root.findViewById(R.id.tabLayout);
//        viewPager=root.findViewById(R.id.viewPager);
//
//        tabLayout.addTab(tabLayout.newTab().setText("Assignment"));
//        tabLayout.addTab(tabLayout.newTab().setText("Test"));
//        tabLayout.addTab(tabLayout.newTab().setText("Calender"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        final MyAdapter adapter = new MyAdapter(getContext(),getChildFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });



        return root;
    }
//    public class MyAdapter extends FragmentPagerAdapter {
//
//        private Context myContext;
//        int totalTabs;
//
//        public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
//            super(fm);
//            myContext = context;
//            this.totalTabs = totalTabs;
//        }
//
//        // this is for fragment tabs
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
////                case 0:
////                    AssignmentFragment assignmentFragment = new AssignmentFragment();
////                    return assignmentFragment;
////                case 1:
////                    TestFragment testFragment = new TestFragment();
////                    return testFragment;
////                case 2:
////                    CalenderFragment calenderFragment = new CalenderFragment();
////                    return calenderFragment;
//                default:
//                    return null;
//            }
//        }
//        // this counts total number of tabs
//        @Override
//        public int getCount() {
//            return totalTabs;
//        }
//    }
}