package com.demo.template.providers.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.demo.template.Config;
import com.demo.template.HolderActivity;
import com.demo.template.MainActivity;
import com.demo.template.R;
import com.demo.template.inherit.BackPressFragment;
import com.demo.template.inherit.CollapseControllingFragment;
import com.demo.template.inherit.ConfigurationChangeFragment;
import com.demo.template.inherit.PermissionsFragment;
import com.demo.template.providers.fav.FavDbAdapter;

/**
 * This activity is used to display webpages
 */

public class WebviewFragment extends Fragment implements BackPressFragment,
        CollapseControllingFragment, AdvancedWebView.Listener, ConfigurationChangeFragment, PermissionsFragment {

    //Static
    public static final String HIDE_NAVIGATION = "hide_navigation";
    public static final String LOAD_DATA = "loadwithdata";

    //References
    private Activity mAct;
    private FavDbAdapter mDbHelper;

    //Layout with interaction
    private AdvancedWebView browser;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //Layouts
    private ImageButton webBackButton;
    private ImageButton webForwButton;
    private FrameLayout ll;

    public WebviewFragment() {
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ll = (FrameLayout) inflater.inflate(R.layout.fragment_webview,
                container, false);

        setHasOptionsMenu(true);

        browser = ll.findViewById(R.id.webView);
        mSwipeRefreshLayout = ll.findViewById(R.id.refreshlayout);

        browser.setListener(getActivity(), this);
        browser.setGeolocationEnabled(Config.WEBVIEW_GEOLOCATION);
        browser.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUri(url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUri(request.getUrl().toString());
            }

            // Make sure any url clicked is opened in webview
            boolean handleUri(String url) {
                if (url.contains("market://") || url.contains("mailto:")
                        || url.contains("play.google") || url.contains("tel:") || url
                        .contains("vid:")) {
                    // Load new URL Don't override URL Link
                    startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    return true;
                }

                // Return true to override url loading (In this case do
                // nothing).
                return false;
            }

        });

        // setting an on touch listener
        /** browser.setOnTouchListener(new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_UP:
        if (!v.hasFocus()) {
        v.requestFocus();
        }
        break;
        }
        return false;
        }
        });**/

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                browser.reload();
            }
        });

        return ll;
    }// of oncreateview

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAct = getActivity();

        setRetainInstance(true);

        //browser.getSettings().setSupportMultipleWindows(false);
        //browser.setWebChromeClient(new FullscreenableChromeClient(mAct));

        String weburl = getArguments().getStringArray(MainActivity.FRAGMENT_DATA)[0];
        String data = getArguments().containsKey(LOAD_DATA) ? getArguments().getString(LOAD_DATA) : null;
        //if (weburl.startsWith("file:///android_asset/") || hasConnectivity()) {
        //If this is the first time, load the initial url, otherwise restore the view if necessairy
        //If we have HTML data to load, do so, else load the url.
        if (data != null) {
            browser.loadDataWithBaseURL(weburl, data, "text/html", "UTF-8", "");
        } else {
            browser.loadUrl(weburl);
        }
        //}

    }

    @Override
    public void onPause() {
        super.onPause();

        if (browser != null)
            browser.onPause();

        setMenuVisibility(false);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (mAct == null) return;

        if (visible) {
            if (navigationIsVisible()) {

                ActionBar actionBar = ((AppCompatActivity) mAct)
                        .getSupportActionBar();

                if (actionBar == null) return;

                if (mAct instanceof HolderActivity) {
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
                } else {
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                }

                View view = mAct.getLayoutInflater().inflate(R.layout.fragment_webview_actionbar, null);
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL);
                actionBar.setCustomView(view, lp);

                webBackButton = mAct.findViewById(R.id.goBack);
                webForwButton = mAct.findViewById(R.id.goForward);

                webBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (browser.canGoBack())
                            browser.goBack();
                    }
                });
                webForwButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (browser.canGoForward())
                            browser.goForward();
                    }
                });
            }
        } else {
            if (navigationIsVisible()
                    && getActivity() != null) {

                ActionBar actionBar = ((AppCompatActivity) getActivity())
                        .getSupportActionBar();
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        if (browser != null) {
            browser.onResume();
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }

        if (this.getArguments().containsKey(HIDE_NAVIGATION) &&
                this.getArguments().getBoolean(HIDE_NAVIGATION)) {
            mSwipeRefreshLayout.setEnabled(false);
        }

        adjustControls();
        if (isMenuVisible() || getUserVisibleHint())
            setMenuVisibility(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        browser.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setMenuVisibility(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                shareURL();
                return true;
            case R.id.favorite:
                mDbHelper = new FavDbAdapter(mAct);
                mDbHelper.open();

                String title = browser.getTitle();
                String url = browser.getUrl();

                if (mDbHelper.checkEvent(title, url, FavDbAdapter.KEY_WEB)) {
                    // This item is new
                    mDbHelper.addFavorite(title, url, FavDbAdapter.KEY_WEB);
                    Toast toast = Toast.makeText(mAct,
                            getResources().getString(R.string.favorite_success),
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(mAct,
                            getResources().getString(R.string.favorite_duplicate),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!this.getArguments().containsKey(HIDE_NAVIGATION) ||
                !this.getArguments().getBoolean(HIDE_NAVIGATION))
            inflater.inflate(R.menu.webview_menu, menu);

        //For local urls, we don't need a share item
        if (browser.getUrl() != null && browser.getUrl().startsWith("file:///android_asset/"))
            menu.findItem(R.id.share).setVisible(false);
    }

    // Checking for an internet connection
    private boolean hasConnectivity() {
        boolean enabled = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) mAct
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable())) {
            enabled = false;
            //Helper.noConnection(mAct);
        }

        return enabled;
    }

    public void adjustControls() {
        webBackButton = mAct.findViewById(R.id.goBack);
        webForwButton = mAct.findViewById(R.id.goForward);

        if (webBackButton == null || webForwButton == null || browser == null) return;

        if (browser.canGoBack()) {
            webBackButton.setColorFilter(Color.argb(255, 255, 255, 255));
        } else {
            webBackButton.setColorFilter(Color.argb(255, 0, 0, 0));
        }
        if (browser.canGoForward()) {
            webForwButton.setColorFilter(Color.argb(255, 255, 255, 255));
        } else {
            webForwButton.setColorFilter(Color.argb(255, 0, 0, 0));
        }
    }

    // sharing
    private void shareURL() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String appname = getString(R.string.app_name);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                (getResources().getString(R.string.web_share_begin)) + appname
                        + getResources().getString(R.string.web_share_end)
                        + browser.getUrl());
        startActivity(Intent.createChooser(shareIntent, getResources()
                .getString(R.string.share)));
    }

    @Override
    public boolean handleBackPress() {
        return !browser.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        browser.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean supportsCollapse() {
        return false;
    }

    public boolean navigationIsVisible() {
        //If override is on, always hide
        if (Config.FORCE_HIDE_NAVIGATION) return false;

        //Only hide navigation if key is provided and is true
        return (!this.getArguments().containsKey(HIDE_NAVIGATION) ||
                !this.getArguments().getBoolean(HIDE_NAVIGATION)
        );
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (navigationIsVisible())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onPageFinished(String url) {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);

        adjustControls();
        hideErrorScreen();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);

        if (failingUrl.startsWith("file:///android_asset/") || hasConnectivity()) {
            //It is a local error, or a we have connectivity
        } else {
            showErrorScreen();
        }
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        if (AdvancedWebView.handleDownload(mAct, url, suggestedFilename)) {
            // download successfully handled
        } else {
            Toast.makeText(mAct, R.string.download_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    @Override
    public String[] requiredPermissions() {
        if (Config.WEBVIEW_GEOLOCATION)
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        else
            return new String[0];
    }

    public void showErrorScreen() {
        final View stub = ll.findViewById(R.id.empty_view);
        stub.setVisibility(View.VISIBLE);

        stub.findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browser.loadUrl("javascript:document.open();document.close();");
                browser.reload();
            }
        });
    }

    public void hideErrorScreen() {
        final View stub = ll.findViewById(R.id.empty_view);
        if (stub.getVisibility() == View.VISIBLE)
            stub.setVisibility(View.GONE);
    }
}
