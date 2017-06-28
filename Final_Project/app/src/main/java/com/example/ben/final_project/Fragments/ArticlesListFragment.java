package com.example.ben.final_project.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_DETAILS;


public class ArticlesListFragment extends Fragment{

    ListView list;
    List<Article> articlesData = new LinkedList<Article>();
    ArticleListAdapter adapter;
    FragmentsDelegate listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //subscribe to the EventBus
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.UpdateArticleEvent event) {
        Log.d("TAG","new article");
        boolean exist = false;
        for (Article article : articlesData) {
            if (article.articleID.equals(event.article.articleID)) {
                if(!event.article.wasDeleted)
                    article = event.article;
                else
                    articlesData.remove(articlesData.indexOf(article));
                //sarticle = event.article;
                exist = true;
                break;
            }
        }
        if (!exist && !event.article.wasDeleted) {
            articlesData.add(event.article);
        }
        adapter.notifyDataSetChanged();
        list.setSelection(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","ArticlesListFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_articles_list, container, false);
        adapter = new ArticleListAdapter();
        adapter.setInflater(inflater);
        list = (ListView) containerView.findViewById(R.id.articles_list_frag);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null)
                    listener.onAction(ARTICLE_DETAILS,articlesData.get(articlesData.size() - position - 1).articleID);
                else
                    Log.d("TAG", "listener is null");
            }
        });

        articlesData = Model.instance.getAllArticles();
        if(articlesData.size() == 0)
            Toast.makeText(getActivity(),"There are no articles",Toast.LENGTH_SHORT).show();
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    @Override
    public void onDestroy() {
        //unsubscribe the EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
            //if(articlesData == null)
            //    return 0;
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

            final ImageView articleImage = (ImageView) convertView.findViewById(R.id.row_article_image);
            TextView articleMainTitle = (TextView) convertView.findViewById(R.id.row_main_article_title);
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.row_article_progressBar);

            final Article article = articlesData.get(getCount() - position - 1);
            articleMainTitle.setText(article.mainTitle);

            articleImage.setTag(article.imageUrl);

            if (article.imageUrl != null && !article.imageUrl.isEmpty() && !article.imageUrl.equals("")){
                progressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(article.imageUrl, new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        String tagUrl = articleImage.getTag().toString();
                        if (tagUrl.equals(article.imageUrl)) {
                            articleImage.setImageBitmap(image);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            else
                articleImage.setImageResource(R.drawable.car_icon);

            return convertView;
        }
    }
}
