package com.demo.template.providers.wordpress.api;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.demo.template.R;
import com.demo.template.providers.wordpress.PostItem;
import com.demo.template.providers.wordpress.api.providers.JsonApiProvider;
import com.demo.template.providers.wordpress.ui.WordpressDetailActivity;
import com.demo.template.util.Helper;
import com.demo.template.util.InfiniteRecyclerViewAdapter;

import java.util.ArrayList;

import static com.demo.template.MainActivity.deepLinkUrl;

/**
 * Simply loads data from an url (gotten from a provider) and loads it into a list.
 * Various attributes of this list and the way to load are defined in a WordpressGetTaskInfo.
 */
public class WordpressPostsLoader implements WordpressPostsTask.WordpressPostsCallback{

    private String url;
    private boolean initialload;
    private WordpressGetTaskInfo info;

    public static String getRecentPosts(WordpressGetTaskInfo info) {
        //Let the provider compose an API url
        String url = info.provider.getRecentPosts(info);

        new WordpressPostsLoader(url, true, info).load();

        return url;
    }

    public static String getTagPosts(WordpressGetTaskInfo info, String tag) {
        //Let the provider compose an API url
        String url = info.provider.getTagPosts(info, tag);

        new WordpressPostsLoader(url, true, info).load();

        return url;
    }

    public static String getCategoryPosts(WordpressGetTaskInfo info, String category, Activity mAct) {
        //Let the provider compose an API url
        String url = info.provider.getCategoryPosts(info, category);

        new WordpressPostsLoader(url, true, info).load();

        return url;
    }

    public static String getSearchPosts(WordpressGetTaskInfo info, String query) {
        //A search request might interfere with a current loading therefore
        //we disable loading to ensure we can start a new request
        if (info.isLoading) {
            info.isLoading = false;
        }

        //Let the provider compose an API url
        String url = info.provider.getSearchPosts(info, query);

        new WordpressPostsLoader(url, true, info).load();

        return url;
    }


    public static void loadMorePosts(WordpressGetTaskInfo info, String withUrl) {
        new WordpressPostsLoader(withUrl, false, info).load();
    }

    private WordpressPostsLoader(String url, boolean firstload, WordpressGetTaskInfo info) {
        this.url = url;
        this.initialload = firstload;
        this.info = info;
    }

    private void load() {
        if (info.isLoading) {
            return;
        } else {
            info.isLoading = true;
        }

        if (initialload) {
            //Show the full screen loading layout
            info.adapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_PROGRESS);
            info.adapter.setHasMore(true);
            info.posts.clear();

            //Reset the page parameter and listview
            info.curpage = 0;
        }

        //Fetch the posts
        new WordpressPostsTask(url, info, this).execute();
    }

    private void complete() {
        info.isLoading = false;
    }

    private void updateList(ArrayList<PostItem> posts) {
        if (posts.size() > 0) {
            info.posts.addAll(posts);

            if(deepLinkUrl != null){
                for(int i =0; i <posts.size();i++){
                    if(deepLinkUrl.equals(info.posts.get(i).getUrl())){
                        Intent intent = new Intent(info.context, WordpressDetailActivity.class);
                        intent.putExtra(WordpressDetailActivity.EXTRA_POSTITEM, info.posts.get(i));
                        intent.putExtra(WordpressDetailActivity.EXTRA_API_BASE, info.baseurl);
                        //If a disqus parse-able is provided, pass it to the detailActivity
                        info.context.startActivity(intent);
                    }

                }

                deepLinkUrl = null;
            }
        }

        if (info.curpage >= info.pages)
            info.adapter.setHasMore(false);

        info.adapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_LIST);
    }

    private void showErrorMessage(){
        String message;
        if ((!info.baseurl.startsWith("http") || info.baseurl.endsWith("/")) && info.provider instanceof JsonApiProvider) {
            message =  info.baseurl + "' is most likely not a valid API base url.";
        } else {
            message = "The result of '" + url + "' does not appear to return valid JSON or at least not in the expected format.";
        }

        if (info.posts == null || info.posts.size() == 0)
            info.adapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_EMPTY);

        Helper.noConnection(info.context, message);
    }

    @Override
    public void postsLoaded(ArrayList<PostItem> result) {
        updateList(result);

        //Alert if we have simply 0 posts, but a valid response
        if (null != result && result.size() < 1 && !info.simpleMode) {
            Toast.makeText(
                    info.context,
                    info.context.getResources().getString(R.string.no_results),
                    Toast.LENGTH_LONG).show();
            info.adapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_LIST);
        } else if (result != null && result.size() > 0){
            info.completedWithPosts();
        }

        complete();
    }

    @Override
    public void postsFailed() {
        showErrorMessage();
        complete();
    }

}
