package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Comment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.LinkedList;

import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_ADD;

public class ArticleAddFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";//last article id
    private String mParam1;
    private FragmentsDelegate listener;

    public static ArticleAddFragment newInstance(String param1) {
        ArticleAddFragment fragment = new ArticleAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDelegate(FragmentsDelegate listener){
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
        Log.d("TAG","ArticleAddFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_add_article, container, false);

        final String newArticleID;
        if(Integer.parseInt(mParam1) > 0)
            newArticleID = Model.instance.getAllArticles().get(Integer.parseInt(mParam1) - 1).id + 1;
        else
            newArticleID = "0";

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
                Log.d("TAG","ArticleAddFragment Btn Save click");

                int ok = 0;
                boolean save = true;

                ok += valid(mainTitle,"Main title is required!");//TODO:write errors in hebrew
                ok += valid(author,"Author name is required!");
                ok += valid(content,"Content is required!");
                ok += valid(date,"Date is required!");
                ok += valid(imageUrl,"Image is required!");
                ok += valid(subTitle,"Sub title is required!");

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
                    listener.onAction(ARTICLE_ADD,null);
                }
                else{
                    Log.d("TAG","ArticleAddFragment Cant save new article");
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","ArticleAddFragment Btn Cancle click");
                listener.onAction(ARTICLE_ADD,null);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "on attach ArticleAddFragment - context");
        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        Log.d("TAG", "on attach ArticleAddFragment - activity");
        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public int valid(EditText et, String errorMessage){
        if(et.getText().toString().length() == 0){
            et.setError(errorMessage);
            return 0;
        }
        else
            return 1;
    }
}
