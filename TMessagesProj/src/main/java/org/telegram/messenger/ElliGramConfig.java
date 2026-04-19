package org.telegram.messenger;

import android.content.SharedPreferences;

/**
 * ElliGram — central config.
 * All flags default to true = standard Telegram behaviour.
 * Set to false to enable the "cleaner" version.
 */
public class ElliGramConfig {

    private static final String PREFS = "elligram_config";

    // ── Cleaner UI ────────────────────────────────────────────────────
    /** Show stories bar in chat list */
    public static final String SHOW_STORIES        = "show_stories";
    /** Show online member count in group subtitle */
    public static final String SHOW_ONLINE_COUNT   = "show_online_count";
    /** Show Gift button in profiles and channel bar */
    public static final String SHOW_GIFT_BUTTON    = "show_gift_button";
    /** Show Boost button in channels */
    public static final String SHOW_BOOST_BUTTON   = "show_boost_button";
    /** Show sponsored (ad) messages in channels */
    public static final String SHOW_SPONSORED      = "show_sponsored";

    // ─────────────────────────────────────────────────────────────────

    private static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext
                .getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE);
    }

    /** Returns the boolean value; defaultValue is what vanilla Telegram does. */
    public static boolean get(String key, boolean defaultValue) {
        return prefs().getBoolean(key, defaultValue);
    }

    public static void set(String key, boolean value) {
        prefs().edit().putBoolean(key, value).apply();
    }

    // Convenience helpers (default = true = show everything like stock TG)
    public static boolean showStories()      { return get(SHOW_STORIES,      true); }
    public static boolean showOnlineCount()  { return get(SHOW_ONLINE_COUNT, true); }
    public static boolean showGiftButton()   { return get(SHOW_GIFT_BUTTON,  true); }
    public static boolean showBoostButton()  { return get(SHOW_BOOST_BUTTON, true); }
    public static boolean showSponsored()    { return get(SHOW_SPONSORED,    true); }
}
