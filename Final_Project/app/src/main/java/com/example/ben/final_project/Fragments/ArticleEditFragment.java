package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Activities.GetPicture;
import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.GONE;
import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_ADD;
import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_EDIT;
import static com.example.ben.final_project.Activities.ArticlesActivity.ADD_PICTURE;


public class ArticleEditFragment extends Fragment implements GetPicture {
    private static final String ARG_PARAM1 = "param1";//article articleID
    private String mParam1;
    Article article;
    private FragmentsDelegate listener;
    ImageView imageUrl;
    ProgressBar progressBar;
    Bitmap imageBitmap;

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
        final EditText subTitle = (EditText) containerView.findViewById(R.id.edit_article_sub_title);
        imageUrl = (ImageView) containerView.findViewById(R.id.edit_article_image);
        progressBar = (ProgressBar) containerView.findViewById(R.id.edit_article_progressBar);
        progressBar.setVisibility(GONE);
        final ProgressBar progressBarAllLayout = (ProgressBar) containerView.findViewById(R.id.edit_article_progressBar_all_layout);
        progressBarAllLayout.setVisibility(View.VISIBLE);

        article = new Article();
        Model.instance.getArticle(mParam1, new Model.GetArticleCallback() {
            @Override
            public void onComplete(Article article) {
                ArticleEditFragment.this.article = article;
                mainTitle.setText(article.mainTitle);
                author.setText(article.author);
                content.setText(article.content);
                subTitle.setText(article.subTitle);
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Log.d("TAG","article date = " + sfd.format(new Date((long)article.publishDate + 3600000 * 7)));

                date.setText(sfd.format(new Date((long)article.publishDate + 3600000 * 7)));
                date.setEnabled(false);
                progressBarAllLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if(!article.imageUrl.equals("")) {
                    Model.instance.getImage(article.imageUrl, new Model.GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            imageUrl.setImageBitmap(image);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
                else{
                    imageUrl.setImageResource(R.drawable.blue_plus_icon);
                    progressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancel() {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Model.instance.isNetworkAvailable()) {
                    Log.d("TAG", "ArticleAddFragment Btn Save click");
                    progressBarAllLayout.setVisibility(View.VISIBLE);

                    int ok = 0;
                    boolean save = true;

                    ok += valied(mainTitle, "Main title is required!");
                    ok += valied(author, "Author name is required!");
                    ok += valied(content, "Content is required!");
                    ok += valied(subTitle, "Sub title is required!");

                    if (ok != 4)
                        save = false;
                    else {
                        if (author.getText().toString().compareTo(article.author) == 0)
                            if (mainTitle.getText().toString().compareTo(article.mainTitle) == 0)
                                if (subTitle.getText().toString().compareTo(article.subTitle) == 0)
                                    if (content.getText().toString().compareTo(article.content) == 0)
                                        if(imageBitmap == null)
                                            save = false;

                    }

                    if (save == true) {
                        article.author = author.getText().toString();
                        article.mainTitle = mainTitle.getText().toString();
                        article.subTitle = subTitle.getText().toString();
                        article.content = content.getText().toString();

                        Log.d("TAG","bitmap = " + imageBitmap);

                        if (imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, Model.generateRandomId() + ".jpeg", new Model.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    article.imageUrl = url;
                                    Model.instance.editArticle(article);
                                    progressBar.setVisibility(GONE);
                                    progressBarAllLayout.setVisibility(View.GONE);
                                    listener.onAction(ARTICLE_EDIT, null);
                                }

                                @Override
                                public void fail() {
                                    //notify operation fail,...
                                    progressBar.setVisibility(GONE);
                                    progressBarAllLayout.setVisibility(View.GONE);
                                    listener.onAction(ARTICLE_ADD, null);
                                }
                            });
                        }else{
                            article.imageUrl = "";
                            Model.instance.editArticle(article);
                            progressBar.setVisibility(GONE);
                            progressBarAllLayout.setVisibility(View.GONE);
                            listener.onAction(ARTICLE_EDIT, null);
                        }
                    } else {
                        Log.d("TAG", "ArticleAddFragment did not save edited article");
                        listener.onAction(ARTICLE_EDIT, null);
                    }
            }
            else {
                Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT);
                listener.onAction(ARTICLE_EDIT,null);
            }
            }
        });

        imageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);
            listener.onAction(ADD_PICTURE,null);
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
                if(Model.instance.isNetworkAvailable()) {
                    Model.instance.removeArticle(article);
                    Log.d("TAG", "ArticleAddFragment Delete article");
                }
                else
                    Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT);
                listener.onAction(ARTICLE_EDIT,null);
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

    @Override
    public void getPicture(Bitmap bitmap) {
        progressBar.setVisibility(GONE);
        imageBitmap = bitmap;
        if(bitmap != null)
            imageUrl.setImageBitmap(bitmap);
    }
}
