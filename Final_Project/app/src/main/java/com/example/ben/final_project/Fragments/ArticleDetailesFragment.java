package com.example.ben.final_project.Fragments;


import android.app.Fragment;
import android.content.Context;
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
import android.widget.TextView;

import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Comment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class ArticleDetailesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    CommentsListAdapter adapter;
    Article articleData;
    ListView commentsList;

    private ArticleDetailesFragmentDelegate listener;

    public void setDelegate(ArticleDetailesFragmentDelegate l){
        this.listener = l;
    }

    public static ArticleDetailesFragment newInstance(String param1) {
        ArticleDetailesFragment fragment = new ArticleDetailesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
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
        Log.d("TAG","ArticleDetailesFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_articles_detailes, container, false);

        articleData = Model.instance.getArticle(mParam1);

        adapter = new CommentsListAdapter();
        adapter.setInflater(inflater);
        commentsList = (ListView) containerView.findViewById(R.id.article_detailes_comments);
        commentsList.setAdapter(adapter);

        ImageButton addCommentButton = (ImageButton) containerView.findViewById(R.id.article_detailes_add_button);

        final ImageView image = (ImageView) containerView.findViewById(R.id.article_detailes_image);
        final TextView mainTitle = (TextView) containerView.findViewById(R.id.article_detailes_main_title);
        final TextView subTitle = (TextView) containerView.findViewById(R.id.article_detailes_sub_title);
        final TextView author = (TextView) containerView.findViewById(R.id.article_detailes_author);
        final TextView publishedDate = (TextView) containerView.findViewById(R.id.article_detailes_published_date);
        final TextView content = (TextView) containerView.findViewById(R.id.article_detailes_content_article);
        final TextView newComment = (TextView) containerView.findViewById(R.id.article_detailes_new_comment);

        image.setImageResource(R.drawable.car);//TODO : change for every image
        mainTitle.setText(articleData.mainTitle);
        subTitle.setText(articleData.subTitle);
        author.setText(articleData.author);
        publishedDate.setText(articleData.publish_date);
        content.setText(articleData.content);

        setListViewHeightBasedOnChildren(commentsList);

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "ArticleDetailesFragment add comment click");

                int ok = 0;
                boolean save = true;

                if(newComment.getText().toString().equals(""))
                    newComment.setError("You need write text");
                else{
                    Comment comment = new Comment();
                    comment.date = "20/05/2008";//TODO:current date
                    comment.commentContent = newComment.getText().toString();
                    comment.author = "bobo on fire";//TODO:current author
                    newComment.setText("");

                    Model.instance.addNewCommentToArticle(mParam1,comment);

                    notifyAdapter();
                    setListViewHeightBasedOnChildren(commentsList);
                }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArticleDetailesFragmentDelegate) {
            listener = (ArticleDetailesFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface ArticleDetailesFragmentDelegate {
    }

    class CommentsListAdapter extends BaseAdapter {
        public LayoutInflater inflater;

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            if(articleData.comments == null)
                return 0;
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

            Comment comment = Model.instance.getAllCommentForArticle(articleData.id,position);
            commentAuthor.setText(comment.author);
            commentDate.setText(comment.date);
            commentContent.setText(comment.commentContent);
            return convertView;
        }
    }

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

}
