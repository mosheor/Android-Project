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


public class EditArticleFragment extends Fragment {

    public final static int RESULT_SUCCESS_DELETE = 2;
    public final static int RESULT_SUCCESS_EDIT = 1;
    public final static int RESULT_FAIL = 0;

    private static final String ARG_PARAM1 = "param1";//article id
    private String mParam1;
    Article article;

    private EditArticleFragmentDelegate listener;

    public static EditArticleFragment newInstance(String param1) {
        EditArticleFragment fragment = new EditArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDelegate(EditArticleFragmentDelegate listener){
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
        Log.d("TAG","EditArticleFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_edit_article, container, false);

        Log.d("TAG","article id = " + mParam1);

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
        date.setText(article.published_date);
        date.setEnabled(false);
        imageUrl.setText(article.imageUrl);
        subTitle.setText(article.subTitle);

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
                        listener.returnEditResult(RESULT_SUCCESS_EDIT);
                }
                else{
                    Log.d("TAG","AddArticleFragment did not save edited article");
                    listener.returnEditResult(RESULT_FAIL);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","AddArticleFragment Btn Cancle click");
                listener.returnEditResult(RESULT_FAIL);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Model.instance.removeArticle(article.id) == true){
                    Log.d("TAG","AddArticleFragment Delete article");
                    listener.returnEditResult(RESULT_SUCCESS_DELETE);
                }
                else{
                    Log.d("TAG","AddArticleFragment Delete article did not succed");
                    //listener.returnEditResult(RESULT_FAIL);
                }
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EditArticleFragmentDelegate) {
            listener = (EditArticleFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface EditArticleFragmentDelegate {
        void returnEditResult(int result);//if add new article succed - result == 1, else result == 0
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
