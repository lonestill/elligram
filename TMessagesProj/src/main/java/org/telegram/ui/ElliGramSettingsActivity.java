package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;
import org.telegram.ui.SettingsActivity.SettingCell;

import java.util.ArrayList;

public class ElliGramSettingsActivity extends UniversalFragment {

    private static final String PREFS = "elligram_settings";

    public static final String KEY_GHOST_MODE       = "ghost_mode";
    public static final String KEY_NO_SPONSORED     = "no_sponsored";
    public static final String KEY_CONFIRM_DELETE   = "confirm_delete";
    public static final String KEY_DISABLE_STORIES  = "disable_stories";
    public static final String KEY_NO_TYPING        = "no_typing";
    public static final String KEY_MARK_READ        = "mark_read";

    // Item IDs
    private static final int ID_GENERAL    = 1;
    private static final int ID_APPEARANCE = 2;
    private static final int ID_CHATS      = 3;
    private static final int ID_PRIVACY    = 4;
    private static final int ID_AUTOMATION = 5;
    private static final int ID_STATS      = 6;
    private static final int ID_CHANNEL    = 10;
    private static final int ID_CHAT       = 11;

    private LinearLayout topView;

    public static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(String key) {
        return prefs().getBoolean(key, false);
    }

    @Override
    protected CharSequence getTitle() {
        return "";
    }

    private LinearLayout buildTopView(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(0, AndroidUtilities.dp(24), 0, AndroidUtilities.dp(16));

        // App icon (rounded square with gradient)
        ImageView icon = new ImageView(context);
        icon.setImageResource(R.mipmap.icon_2_launcher);
        icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
            new int[]{0xFF5CA7E4, 0xFF3D8BFF}
        );
        bg.setCornerRadius(AndroidUtilities.dp(22));
        icon.setBackground(bg);
        icon.setClipToOutline(true);
        layout.addView(icon, AndroidUtilities.dp(80), AndroidUtilities.dp(80));

        // App name
        TextView name = new TextView(context);
        name.setText("ElliGram");
        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        name.setTypeface(AndroidUtilities.bold());
        name.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameLp.topMargin = AndroidUtilities.dp(12);
        layout.addView(name, nameLp);

        // Version
        TextView version = new TextView(context);
        version.setText(BuildVars.BUILD_VERSION_STRING);
        version.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        version.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        version.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams vLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        vLp.topMargin = AndroidUtilities.dp(4);
        layout.addView(version, vLp);

        return layout;
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        if (topView == null) {
            topView = buildTopView(getContext());
        }
        items.add(UItem.asCustomShadow(topView));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Categories"));
        items.add(SettingCell.Factory.of(ID_GENERAL,    0xFF3D8BFF, 0xFF2462D4, R.drawable.settings_chat,     "General",    "Ghost mode, sponsored msgs..."));
        items.add(SettingCell.Factory.of(ID_APPEARANCE, 0xFFB659FF, 0xFF617CFF, R.drawable.settings_premium,  "Appearance", "Coming soon \uD83D\uDE80"));
        items.add(SettingCell.Factory.of(ID_CHATS,      0xFF4CAF50, 0xFF388E3C, R.drawable.settings_folders,  "Chats",      "Coming soon \uD83D\uDE80"));
        items.add(SettingCell.Factory.of(ID_PRIVACY,    0xFFFF6B35, 0xFFE53935, R.drawable.settings_privacy,  "Privacy",    "Duress mode, fake last seen..."));
        items.add(SettingCell.Factory.of(ID_AUTOMATION, 0xFFFF9800, 0xFFF57C00, R.drawable.settings_language, "Automation", "Coming soon \uD83D\uDE80"));
        items.add(SettingCell.Factory.of(ID_STATS,      0xFF00BCD4, 0xFF0097A7, R.drawable.settings_data,     "Statistics", "Coming soon \uD83D\uDE80"));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Links"));
        items.add(SettingCell.Factory.of(ID_CHANNEL, 0xFF5CA7E4, 0xFF3D8BFF, R.drawable.settings_sounds, "Channel", "@gittyblog"));
        items.add(SettingCell.Factory.of(ID_CHAT,    0xFF5CA7E4, 0xFF3D8BFF, R.drawable.settings_ask,    "Chat",    "@gittyblog"));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        switch (item.id) {
            case ID_GENERAL:
                presentFragment(new ElliGramGeneralSettingsActivity());
                break;
            case ID_APPEARANCE:
            case ID_CHATS:
            case ID_PRIVACY:
            case ID_AUTOMATION:
            case ID_STATS:
                android.widget.Toast.makeText(getParentActivity(), item.text + " \u2014 coming soon \uD83D\uDE80", android.widget.Toast.LENGTH_SHORT).show();
                break;
            case ID_CHANNEL:
            case ID_CHAT:
                Browser.openUrl(getParentActivity(), "https://t.me/gittyblog");
                break;
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
