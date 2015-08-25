package com.phicdy.filfeed.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.phicdy.filfeed.R;
import com.phicdy.filfeed.db.DatabaseAdapter;
import com.phicdy.filfeed.rss.Feed;
import com.phicdy.filfeed.rss.UnreadCountManager;
import com.phicdy.filfeed.task.NetworkTaskManager;

import java.io.File;
import java.util.ArrayList;

public class CurationWordListFragment extends Fragment {

    private PullToRefreshListView feedsListView;
    private CurationWordListAdapter curationWordListAdapter;
    private OnFeedListFragmentListener mListener;

    private ArrayList<Feed> feeds = new ArrayList<>();
    private ArrayList<Feed> allFeeds = new ArrayList<>();
    private DatabaseAdapter dbAdapter;

    private static final String LOG_TAG = "FilFeed.CurationWordList";

    public static CurationWordListFragment newInstance() {
        return new CurationWordListFragment();
    }

    public CurationWordListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbAdapter = DatabaseAdapter.getInstance(getActivity());
        allFeeds = dbAdapter.getAllFeedsWithNumOfUnreadArticles();
        // For show/hide
        if (allFeeds.size() != 0) {
            addShowHideLine(allFeeds);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (NetworkTaskManager.getInstance(getActivity()).isUpdatingFeed()) {
            feedsListView.onRefreshComplete();
        }
    }

    private void setAllListener() {
        // When an feed selected, display unread articles in the feed
        feedsListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        mListener.onListClicked(position-1);
                    }

                });
        feedsListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mListener.onRefreshList();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_list, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFeedListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        feedsListView = (PullToRefreshListView) getActivity().findViewById(R.id.feedList);
        TextView emptyView = (TextView)getActivity().findViewById(R.id.emptyView);
        feedsListView.setEmptyView(emptyView);
        getActivity().registerForContextMenu(feedsListView.getRefreshableView());
        setAllListener();

        refreshList();

        // Set ListView
        curationWordListAdapter = new CurationWordListAdapter(feeds, getActivity());
        feedsListView.setAdapter(curationWordListAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refreshList() {
        curationWordListAdapter = new CurationWordListAdapter(allFeeds, getActivity());
        feedsListView.setAdapter(curationWordListAdapter);
        curationWordListAdapter.notifyDataSetChanged();
    }

    public void addFeed(Feed newFeed) {
        deleteShowHideLineIfNeeded();
        if (newFeed.getUnreadAriticlesCount() > 0) {
            feeds.add(newFeed);
            addShowHideLine(feeds);
        }
        allFeeds.add(newFeed);
        addShowHideLine(allFeeds);
        curationWordListAdapter.notifyDataSetChanged();
    }

    private void deleteShowHideLineIfNeeded() {
        if (feeds != null && feeds.size() > 0) {
            int lastIndex = feeds.size() - 1;
            if (feeds.get(lastIndex).getId() == Feed.DEFAULT_FEED_ID) {
                feeds.remove(lastIndex);
            }
        }
        if (allFeeds != null && allFeeds.size() > 0) {
            int lastIndex = allFeeds.size() - 1;
            if (allFeeds.get(lastIndex).getId() == Feed.DEFAULT_FEED_ID) {
                allFeeds.remove(lastIndex);
            }
        }
    }

    private void addShowHideLine(ArrayList<Feed> feeds) {
        feeds.add(new Feed());
    }

    public void removeFeedAtPosition(int position) {
        Feed deletedFeed = allFeeds.get(position);
        dbAdapter.deleteFeed(deletedFeed.getId());
        allFeeds.remove(position);
        for (int i = 0;i < feeds.size();i++) {
            if (feeds.get(i).getId() == deletedFeed.getId()) {
                feeds.remove(i);
            }
        }
        curationWordListAdapter.notifyDataSetChanged();
    }

    public int getFeedIdAtPosition (int position) {
        if (position < 0) {
            return -1;
        }

        if (allFeeds == null || position > allFeeds.size()-1) {
            return -1;
        }
        return allFeeds.get(position).getId();
    }

    public String getFeedTitleAtPosition (int position) {
        if (position < 0 || feeds == null || position > feeds.size()-1) {
            return null;
        }
        return allFeeds.get(position).getTitle();
    }


    public String getFeedUrlAtPosition (int position) {
        if (position < 0) {
            return null;
        }
        if (allFeeds == null || position > allFeeds.size()-1) {
            return null;
        }
        return allFeeds.get(position).getUrl();
    }

    public void updateFeedTitle(int feedId, String newTitle) {
        for (Feed feed : allFeeds) {
            if (feed.getId() == feedId) {
                feed.setTitle(newTitle);
                break;
            }
        }
        for (Feed feed : feeds) {
            if (feed.getId() == feedId) {
                feed.setTitle(newTitle);
                break;
            }
        }
        curationWordListAdapter.notifyDataSetChanged();
    }

    public interface OnFeedListFragmentListener {
        public void onListClicked(int position);
        public void onRefreshList();
    }

    /**
     *
     * @author kyamaguchi Display RSS Feeds List
     */
    class CurationWordListAdapter extends ArrayAdapter<Feed> {
        public CurationWordListAdapter(ArrayList<Feed> feeds, Context context) {
            super(context, R.layout.feeds_list, feeds);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            // Use contentView and setup ViewHolder
            View row = convertView;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.curation_word_list, parent, false);
                holder = new ViewHolder();
                holder.tvWord = (TextView) row.findViewById(R.id.tv_word);
                holder.btnDelete = (Button) row.findViewById(R.id.btn_delete);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            Feed feed = this.getItem(position);

            String iconPath = feed.getIconPath();
            holder.btnDelete.setVisibility(View.VISIBLE);
            if (iconPath == null || iconPath.equals(Feed.DEDAULT_ICON_PATH)) {
                holder.tvWord.setText(feed.getTitle());
            }else {
                File file = new File(iconPath);
                if (file.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                } else {
                    dbAdapter.saveIconPath(feed.getSiteUrl(), Feed.DEDAULT_ICON_PATH);
                }
                holder.tvWord.setText(feed.getTitle());
            }

            return row;
        }

        private class ViewHolder {
            TextView tvWord;
            Button btnDelete;
        }
    }

}