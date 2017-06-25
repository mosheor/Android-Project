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
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_EDIT;


public class ArticleEditFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";//article id
    private String mParam1;
    Article article;
    private FragmentsDelegate listener;

    public static ArticleEditFragment newInstance(String param1) {
        ArticleEditFragment fragment = new ArticleEditFragment();
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
        Log.d("TAG","ArticleEditFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_edit_article, container, false);

        Button saveButton = (Button) containerView.findViewById(R.id.edit_article_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.edit_article_cancel_button);
        Button deleteButton = (Button) containerView.findViewById(R.id.edit_article_delete_button);

        final EditText mainTitle = (EditText) containerView.findViewById(R.id.edit_article_main_title);
        final EditText author = (EditText) containerView.findViewById(R.id.edit_article_author);
        final EditText content = (EditText) containerView.findViewById(R.id.edit_article_content);
        final EditText date = (EditText) containerView.findViewById(R.id.edit_article_date);
        final EditText imageUrl = (EditText) containerView.findViewById(R.id.edit_article_image);
        final EditText subTitle = (EditText) containerView.findViewById(R.id.edit_article_sub_title);

        article = new Article();
        article = Model.instance.getArticle(mParam1);

        mainTitle.setText(article.mainTitle);
        author.setText(article.author);
        content.setText(article.content);
        date.setText(article.publish_date);
        date.setEnabled(false);
        imageUrl.setText(article.imageUrl);
        subTitle.setText(article.subTitle);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","ArticleAddFragment Btn Save click");

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
                else{
                    if(author.getText().toString().compareTo(article.author) == 0)
                        if(mainTitle.getText().toString().compareTo(article.mainTitle) == 0)
                            if(subTitle.getText().toString().compareTo(article.subTitle) == 0)
                                if(imageUrl.getText().toString().compareTo(article.imageUrl) == 0)
                                    if(content.getText().toString().compareTo(article.content) == 0)
                                            save = false;

                    }

                if(save == true) {
                    article.author = author.getText().toString();
                    article.mainTitle = mainTitle.getText().toString();
                    article.subTitle = subTitle.getText().toString();
                    article.imageUrl = imageUrl.getText().toString();
                    article.content = content.getText().toString();

                    if(Model.instance.editArticle(article))
                        listener.onAction(ARTICLE_EDIT,null);
                }
                else{
                    Log.d("TAG","ArticleAddFragment did not save edited article");
                    listener.onAction(ARTICLE_EDIT,null);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","ArticleAddFragment Btn Cancle click");
                listener.onAction(ARTICLE_EDIT,null);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Model.instance.removeArticle(article.id) == true){
                    Log.d("TAG","ArticleAddFragment Delete article");
                    listener.onAction(ARTICLE_EDIT,null);
                }
                else{
                    Log.d("TAG","ArticleAddFragment Delete article did not succed");
                }
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "on attach ArticleEditFragment - context");
        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        Log.d("TAG", "on attach ArticleEditFragment - activity");
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
