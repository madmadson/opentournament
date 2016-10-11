package madson.org.opentournament;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import madson.org.opentournament.tournaments.OrganizedTournamentListAdapter;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.Environment;
import madson.org.opentournament.viewHolder.NewsViewHolder;
import madson.org.opentournament.viewHolder.TournamentViewHolder;


public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View frag_view = inflater.inflate(R.layout.fragment_home, container, false);

        BaseActivity activity = (BaseActivity) getActivity();

        if (activity.getBaseApplication().getEnvironment() != Environment.PROD) {
            activity.getToolbar().setTitle(R.string.toolbar_title_home_DEMO);
            frag_view.findViewById(R.id.demo_label).setVisibility(View.VISIBLE);
        } else {
            activity.getToolbar().setTitle(R.string.toolbar_title_home);
            frag_view.findViewById(R.id.demo_label).setVisibility(View.GONE);
        }

        RecyclerView recyclerView = (RecyclerView) frag_view.findViewById(R.id.news_list_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplication());
        recyclerView.setLayoutManager(linearLayoutManager);

        String[] newsHeader = getResources().getStringArray(R.array.news_header);
        String[] news = getResources().getStringArray(R.array.news);

        NewsAdapter newsAdapter = new NewsAdapter(newsHeader, news);

        recyclerView.setAdapter(newsAdapter);

        return frag_view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

        private final String[] news;
        private String[] newsHeaders;

        public NewsAdapter(String[] newsHeaders, String[] news) {

            this.newsHeaders = newsHeaders;

            this.news = news;
        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news, parent, false);

            return new NewsViewHolder(v);
        }


        @Override
        public void onBindViewHolder(NewsViewHolder holder, int position) {

            holder.getNewsItem().setText(news[position]);
            holder.getNewsHeaderItem().setText(newsHeaders[position]);
        }


        @Override
        public int getItemCount() {

            return news.length;
        }
    }
}
