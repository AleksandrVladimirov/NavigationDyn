package com.vladimirov.navigationdin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class TextFragment extends Fragment {

    private TextView textView;
    private String param;

    public TextFragment() {
    }

    public TextFragment(String param) {
        this.param = param;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_item, container, false);

        textView = view.findViewById(R.id.textView);

        if(param != null) {
            textView.setText(param);
        } else {
            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

}
