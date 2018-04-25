package io.hasura.songapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by HARIHARAN on 28-06-2017.
 */

public class GuysYouMayKnowFragment extends Fragment {
    TextView desc, name;
    public Button add_or_remove_button;
    Helper helper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView= inflater.inflate(R.layout.fragment_guys_you_may_know,container,false);
        desc= (TextView) itemView.findViewById(R.id.desc);
        name=(TextView) itemView.findViewById(R.id.name);
        add_or_remove_button= (Button) itemView.findViewById(R.id.add_or_remove);

        String desc= getArguments().getString("desc");
        final String name= getArguments().getString("name");
        final String add_or_remove_button= getArguments().getString("add_or_remove_button");

        this.desc.setText(Html.fromHtml(desc));
        this.name.setText(name);
        this.add_or_remove_button.setText(add_or_remove_button);
        this.add_or_remove_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i("clicked", "clik");
                helper.onButtonClick(add_or_remove_button,name);
            }
        });

        return itemView;
    }

    public interface Helper{
        public void onButtonClick(String s, String fname);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            helper=(Helper) activity;
        }catch(ClassCastException c){
            throw new ClassCastException(activity.toString()+"must implement Helper");
        }
    }
}
