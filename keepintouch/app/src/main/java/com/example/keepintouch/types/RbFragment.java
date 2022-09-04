package com.example.keepintouch.types;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.keepintouch.R;

public class RbFragment extends Fragment implements View.OnClickListener {
    int cId;
    PassDataInterface mPassDataInterface;

    public RbFragment(PassDataInterface passDataInterface) {
        mPassDataInterface = passDataInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_priority,container,false);
        Bundle data = getArguments();
        if (data != null) {
            cId = data.getInt("id");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioButton rb = view.findViewById(R.id.rb_week);
        rb.setOnClickListener(this);
        rb = view.findViewById(R.id.rb_month);
        rb.setOnClickListener(this);
        rb = view.findViewById(R.id.rb_half_year);
        rb.setOnClickListener(this);
        rb = view.findViewById(R.id.rb_year);
        rb.setOnClickListener(this);
        rb = view.findViewById(R.id.rb_never);
        rb.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
        {
            PriorityType type;
            switch (v.getId()) {
                case R.id.rb_week:
                    type = PriorityType.WEEKLY;
                    break;
                case R.id.rb_month:
                    type = PriorityType.MONTHLY;
                    break;
                case R.id.rb_half_year:
                    type = PriorityType.HALF_YEAR;
                    break;
                case R.id.rb_year:
                    type = PriorityType.YEARLY;
                    break;
                default:
                    type = PriorityType.NEVER;
            }
            mPassDataInterface.onDataReceived(new MyContact(cId, type));
        }

}
