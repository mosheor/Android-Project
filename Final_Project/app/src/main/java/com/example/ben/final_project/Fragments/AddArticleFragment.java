package com.example.ben.final_project.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Comment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.LinkedList;

public class AddArticleFragment extends Fragment {

    public final static int RESULT_SUCCESS = 1;
    public final static int RESULT_FAIL = 0;

    private static final String ARG_PARAM1 = "param1";//last article id
    private String mParam1;

    private AddArticleFragmentDelegate listener;

    public static AddArticleFragment newInstance(String param1) {
        AddArticleFragment fragment = new AddArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDelegate(AddArticleFragmentDelegate listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","AddArticleFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_add_article, container, false);

        final String newArticleID;
        if(Integer.parseInt(mParam1) > 0)
            newArticleID = Model.instance.getAllArticles().get(Integer.parseInt(mParam1) - 1).id + 1;
        else
            newArticleID = "0";
        Log.d("TAG","new article id = " + newArticleID);

        Button saveButton = (Button) containerView.findViewById(R.id.add_article_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.add_article_cancel_button);
        final EditText mainTitle = (EditText) containerView.findViewById(R.id.add_article_main_title);
        final EditText author = (EditText) containerView.findViewById(R.id.add_article_author);
        final EditText content = (EditText) containerView.findViewById(R.id.add_article_content);
        final EditText date = (EditText) containerView.findViewById(R.id.add_article_date);
        final EditText imageUrl = (EditText) containerView.findViewById(R.id.add_article_image);
        final EditText subTitle = (EditText) containerView.findViewById(R.id.add_article_sub_title);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","AddArticleFragment Btn Save click");

                int ok = 0;
                boolean save = true;

                ok += valied(mainTitle,"Main title is required!");//TODO:write errors in hebrew
                ok += valied(author,"Author name is required!");
                ok += valied(content,"Content is required!");
                ok += valied(date,"Date is required!");
                ok += valied(imageUrl,"Image is required!");
                ok += valied(subTitle,"Sub title is required!");

                if(ok != 6)
                    save = false;

                if(save == true) {
                    Article article = new Article();
                    article.comments = new LinkedList<Comment>();
                    article.id = newArticleID;
                    article.publish_date = date.getText().toString();
                    article.author = author.getText().toString();
                    article.mainTitle = mainTitle.getText().toString();
                    article.subTitle = subTitle.getText().toString();
                    article.imageUrl = imageUrl.getText().toString();
                    article.content = content.getText().toString();

                    Model.instance.addNewArticle(article);
                    listener.returnAddResult(RESULT_SUCCESS);
                }
                else{
                    Log.d("TAG","AddArticleFragment Cant save new article");
                    //listener.returnResult(RESULT_FAIL);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","AddArticleFragment Btn Cancle click");
                listener.returnAddResult(RESULT_FAIL);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AddArticleFragmentDelegate) {
            listener = (AddArticleFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface AddArticleFragmentDelegate {
        void returnAddResult(int result);//todo if add new article succeeded - result == 1, else result == 0 -  add static final vars
    }

    public int valied(EditText et, String errorMessage){
        if(et.getText().toString().length() == 0){
            Log.d("TAG","12");
            et.setError(errorMessage);
            return 0;
        }
        else
            return 1;
    }
}
