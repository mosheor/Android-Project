package com.example.ben.final_project.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.List;

import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_DETAILS;


public class ArticlesListFragment extends Fragment{

    ListView list;
    List<Article> articlesData = Model.instance.getAllArticles();
    ArticleListAdapter adapter;
    FragmentsDelegate listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","ArticlesListFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_articles_list, container, false);
        adapter = new ArticleListAdapter();
        adapter.setInflater(inflater);
        list = (ListView) containerView.findViewById(R.id.articles_list_frag);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null)
                    listener.onAction(ARTICLE_DETAILS,articlesData.get(Model.instance.getArticleListSize() - position - 1).id);
                else
                    Log.d("TAG", "listener is null");
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("TAG", "on atach ArticlesListFragment - activity");
        if (activity instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement FragmentsDelegate");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "on atach ArticlesListFragment - context");
        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsDelegate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setDelegate(FragmentsDelegate d){
        this.listener = d;
    }

    class ArticleListAdapter extends BaseAdapter {
        public LayoutInflater inflater;

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return articlesData.size();
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
                convertView = inflater.inflate(R.layout.article_list_row,null);
            }

            ImageView articleImage = (ImageView) convertView.findViewById(R.id.row_article_image);
            TextView articleMainTitle = (TextView) convertView.findViewById(R.id.row_main_article_title);

            Article article = articlesData.get(getCount() - position - 1);
            articleImage.setImageResource(R.drawable.car);//TODO: change for specific image
            articleMainTitle.setText(article.mainTitle);
            Log.d("TAG","art num" + position);

            return convertView;
        }
    }
}
