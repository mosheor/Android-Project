package com.example.ben.final_project.Fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Comment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.GONE;

public class ArticleDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String articleId;
    CommentsListAdapter adapter;
    Article articleData = new Article();
    ListView commentsList;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.UpdateCommentEvent event) {
        Log.d("TAG","new comment");
        boolean exist = false;
        for (Comment comment : articleData.comments) {
            if (comment.articleID.equals(event.comment.articleID) && comment.commentID.equals(event.comment.commentID)) {
                comment = event.comment;
                exist = true;
                break;
            }
        }
        if (!exist && articleId.equals(event.comment.articleID)) {
            articleData.comments.add(event.comment);
        }

       /*articleData.comments.sort(new Comparator<Comment>() {
            @Override
            public int compare(Comment comment, Comment t1) {
                return (int)(comment.lastUpdatedDate-t1.lastUpdatedDate);
            }
        });*/
        adapter.notifyDataSetChanged();
    }

    public static ArticleDetailsFragment newInstance(String param1) {
        ArticleDetailsFragment fragment = new ArticleDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //subscribe the EventBus
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            articleId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","ArticleDetailsFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_articles_detailes, container, false);

        ImageButton addCommentButton = (ImageButton) containerView.findViewById(R.id.article_detailes_add_button);

        final ImageView image = (ImageView) containerView.findViewById(R.id.article_details_image);
        final TextView mainTitle = (TextView) containerView.findViewById(R.id.article_detailes_main_title);
        final TextView subTitle = (TextView) containerView.findViewById(R.id.article_details_sub_title);
        final TextView author = (TextView) containerView.findViewById(R.id.article_detailes_author);
        final TextView publishedDate = (TextView) containerView.findViewById(R.id.article_detailes_published_date);
        final TextView content = (TextView) containerView.findViewById(R.id.article_detailes_content_article);
        final TextView newComment = (TextView) containerView.findViewById(R.id.article_detailes_new_comment);
        final ProgressBar progressBar = (ProgressBar) containerView.findViewById(R.id.article_details_progressBar);
        progressBar.setVisibility(GONE);

        adapter = new CommentsListAdapter();
        adapter.setInflater(inflater);
        commentsList = (ListView) containerView.findViewById(R.id.article_detailes_comments);
        commentsList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(commentsList);

        Model.instance.getArticle(articleId, new Model.GetArticleCallback() {
            @Override
            public void onComplete(Article article) {
                articleData = article;
                mainTitle.setText(articleData.mainTitle);
                subTitle.setText(articleData.subTitle);
                author.setText(articleData.author);

                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Log.d("TAG","article date = " + sfd.format(new Date((long)article.publishDate + 3600000 * 7)));

                publishedDate.setText(sfd.format(new Date((long)article.publishDate + 3600000 * 7)));
                content.setText(articleData.content);
                articleData.comments = new LinkedList<Comment>();

                progressBar.setVisibility(View.VISIBLE);
                if(!article.imageUrl.equals("")) {
                    Model.instance.getImage(article.imageUrl, new Model.GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap imageLoad) {
                            image.setImageBitmap(imageLoad);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
                else {
                    image.setImageResource(R.drawable.car_icon);
                    progressBar.setVisibility(View.GONE);
                }

                Model.instance.getArticleComments(articleId, new Model.GetArticleCommentsAndObserveCallback() {
                    @Override
                    public void onComplete(List<Comment> list) {
                        articleData.comments = list;
                        adapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(commentsList);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Model.instance.isNetworkAvailable()) {
                    Log.d("TAG", "ArticleDetailsFragment add comment click");

                    int ok = 0;
                    boolean save = true;

                    if(Model.instance.isConnectedUser()) {
                        if (newComment.getText().toString().equals(""))
                            newComment.setError("You need write text");
                        else {
                            Comment comment = new Comment();
                            comment.commentContent = newComment.getText().toString();
                            comment.author = Model.instance.getConnectedUserUsername();
                            comment.articleID = articleId;

                            comment.commentID = Model.generateRandomId();
                            newComment.setText("");

                            Model.instance.addNewCommentToArticle(comment);
                            setListViewHeightBasedOnChildren(commentsList);
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "You are not logged in", Toast.LENGTH_SHORT).show();
                        newComment.setText("");
                    }
                }
                else
                    Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT).show();
            }
        });

        return containerView;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    class CommentsListAdapter extends BaseAdapter {
        public LayoutInflater inflater;

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            if(articleData.comments == null) {
                Log.d("TAG","comment 0");
                return 0;
            }
            else
                return articleData.comments.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.article_comment_row,null);
            }

            TextView commentAuthor = (TextView) convertView.findViewById(R.id.comment_author);
            TextView commentDate = (TextView) convertView.findViewById(R.id.comment_date);
            TextView commentContent = (TextView) convertView.findViewById(R.id.comment_content);

            Comment comment = articleData.comments.get(position);
            commentAuthor.setText(comment.author);
            commentContent.setText(comment.commentContent);

            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Log.d("TAG","article date = " + sfd.format(new Date((long)comment.lastUpdatedDate + 3600000 * 7)));

            commentDate.setText(sfd.format(new Date((long)comment.lastUpdatedDate + 3600000 * 7)));

            Log.d("TAG","comment row");
            return convertView;
        }
    }

    @Override
    public void onDestroy() {
        //unsubscribe to the EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
