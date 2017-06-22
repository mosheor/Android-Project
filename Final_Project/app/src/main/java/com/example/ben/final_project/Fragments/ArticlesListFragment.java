package com.example.ben.final_project.Fragments;

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

import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.List;


public class ArticlesListFragment extends Fragment{

    ListView list;
    List<Article> articlesData = Model.instance.getAllArticles();
    ArticleListAdapter adapter;
    ArticlesListFragmentDelegate listener;

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
                listener.selectDetilesClick(articlesData.get(position).id);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArticlesListFragmentDelegate) {
            listener = (ArticlesListFragmentDelegate) context;
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

    public interface ArticlesListFragmentDelegate{
        void selectDetilesClick(String id);
    }

    public void setDelegate(ArticlesListFragmentDelegate d){
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

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }
}
