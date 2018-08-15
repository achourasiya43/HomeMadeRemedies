package com.demo.template;

import com.demo.template.drawer.SimpleMenu;

/**
 * This file is part of the Universal template
 * For license information, please check the LICENSE
 * file in the root of this project
 *
 * @author Sherdle
 * Copyright 2017
 */

public class Config {

    /**
     * The Config.json file that defines your app's content.
     * Point url to JSON or leave empty to use config.json from assets.
     */

    public static final String CONFIG_URL = "";

    /**
     * Options regarding the link behaviour in your app
     */

    //Explicit links, like 'open' buttons, should be opened outside the app
    public static final boolean OPEN_EXPLICIT_EXTERNAL = true;
    //Inline links, like links in descriptions, should be opened outside the app
    public static final boolean OPEN_INLINE_EXTERNAL = false;

    /**
     * Options related to how your app looks
     */

    //Show category chips in WordPress
    public static final boolean WP_CHIPS = true;
    //Use immersive row layout with WordPress
    public static final boolean WP_ROW_IMMERSIVE = false;
    //Show a attachments fab for WordPress posts
    public static final boolean WP_ATTACHMENTS_BUTTON = false;

    //Show category chips in WooCommerce
    public static final boolean WC_CHIPS = true;

    //Use immersive row layout with Youtube
    public static final boolean YT_ROW_IMMERSIVE = true;

    //Hide the navigation drawer
    public static final boolean HIDE_DRAWER = false;
    //If a tablet layout should be used on tablets, or just a regular layout
    public static final boolean TABLET_LAYOUT = true;
    //Force show menu on app start
    public static final boolean DRAWER_OPEN_START = false;
    //Hide the toolbar (will disable access to toolbar items)
    public static final boolean HIDE_TOOLBAR = false;

    //If the WebView navigation buttons should be hidden
    public static final boolean FORCE_HIDE_NAVIGATION = false;
    //If the WebView should support Geolocation
    public static final boolean WEBVIEW_GEOLOCATION = false;

    //If a visualiser should be shown for the Radio player
    public static boolean VISUALIZER_ENABLED = true;
    //If the visualiser is not show, a background image resource id
    public static int BACKGROUND_IMAGE_ID = R.drawable.radio_background;

    /**
     * Options related to Advertisements in your app
     */

    //The frequency in which interstitial ads are shown
    //('-1' to never show, '1' to always show, '2' to show 1 out of 2, etc)
    public static final int INTERSTITIAL_INTERVAL = 2;
    //If ads are enabled, also show them on the youtube layout
    public static final boolean ADMOB_YOUTUBE = false;

    /**
     * Options regarding the use of a Hardcoded Configuration
     */

    //Will load configuration from hardcoded Config class instead of JSON.
    public static boolean USE_HARDCODED_CONFIG = false;
    //If you use a hardcoded config, initialise it below
    public static void configureMenu(SimpleMenu menu, ConfigParser.CallBack callback){

        /**
        List<NavItem> tabs = new ArrayList<NavItem>();
        tabs.add(new NavItem("First Name", FirstFragment.class,
                new String[]{"parameter 1", "parameter 2"}));
         tabs.add(new NavItem("Second Name", SecondFragment.class,
                new String[]{"parameter 1", "parameter 2"}));
        menu.add("Menu Item", R.drawable.ic_details, tabs);
         **/

        //Return the configuration
        callback.configLoaded(false);
    }

}