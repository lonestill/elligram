package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class ElliGramSettingsActivity extends BaseFragment {

    private static final String PREFS = "elligram_settings";
    public static final String KEY_GHOST_MODE      = "ghost_mode";
    public static final String KEY_NO_SPONSORED    = "no_sponsored";
    public static final String KEY_CONFIRM_DELETE  = "confirm_delete";
    public static final String KEY_DISABLE_STORIES = "disable_stories";
    public static final String KEY_NO_TYPING       = "no_typing";
    public static final String KEY_MARK_READ       = "mark_read";

    public static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(String key) {
        return prefs().getBoolean(key, false);
    }

    public static void toggle(String key) {
        prefs().edit().putBoolean(key, !isEnabled(key)).apply();
    }

    // ─── quick toggle state ───────────────────────────────────────────
    private final ArrayList<ToggleView> toggleViews = new ArrayList<>();

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setTitle("");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) finishFragment();
            }
        });

        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        ScrollView scroll = new ScrollView(context);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, 0, 0, AndroidUtilities.dp(20));
        root.addView(scroll, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        // ── 1. Profile card ──────────────────────────────────────────
        content.addView(buildProfileCard(context), buildLp(16, 16, 16, 8));

        // ── 2. Quick toggles ─────────────────────────────────────────
        content.addView(buildToggleSection(context), buildLp(16, 0, 16, 8));

        // ── 3. Category grid ─────────────────────────────────────────
        content.addView(buildSectionLabel(context, "Features"), buildLp(16, 8, 16, 6));
        content.addView(buildCategoryGrid(context), buildLp(16, 0, 16, 8));

        // ── 4. Links ─────────────────────────────────────────────────
        content.addView(buildSectionLabel(context, "Links"), buildLp(16, 8, 16, 6));
        content.addView(buildLinksCard(context), buildLp(16, 0, 16, 0));

        // ── 5. Footer ────────────────────────────────────────────────
        TextView footer = new TextView(context);
        footer.setText("ElliGram v" + BuildVars.BUILD_VERSION_STRING);
        footer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        footer.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        footer.setGravity(Gravity.CENTER);
        content.addView(footer, buildLp(0, 16, 0, 4));

        fragmentView = root;
        return root;
    }

    // ─────────────────────────────────────────────────────────────────
    // Profile card
    // ─────────────────────────────────────────────────────────────────
    private View buildProfileCard(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(roundRect(Theme.getColor(Theme.key_windowBackgroundWhite), 16));
        card.setClipToOutline(true);

        // Avatar
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        BackupImageView avatarView = new BackupImageView(context);
        avatarView.setRoundRadius(dp(24));
        TLRPC.User user = MessagesController.getInstance(currentAccount)
                .getUser(UserConfig.getInstance(currentAccount).getClientUserId());
        if (user == null) user = UserConfig.getInstance(currentAccount).getCurrentUser();
        if (user != null) {
            avatarDrawable.setInfo(currentAccount, user);
            avatarView.setForUserOrChat(user, avatarDrawable);
        }
        card.addView(avatarView, dp(48), dp(48));

        // Name + username
        LinearLayout textCol = new LinearLayout(context);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setPadding(dp(14), 0, 0, 0);

        TextView nameView = new TextView(context);
        if (user != null) nameView.setText(user.first_name + (user.last_name != null && !user.last_name.isEmpty() ? " " + user.last_name : ""));
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setTypeface(AndroidUtilities.bold());
        nameView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        nameView.setSingleLine();
        textCol.addView(nameView);

        TextView usernameView = new TextView(context);
        if (user != null && user.username != null) usernameView.setText("@" + user.username);
        usernameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        usernameView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        textCol.addView(usernameView);

        card.addView(textCol, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));

        // Chevron
        ImageView arrow = new ImageView(context);
        arrow.setImageResource(R.drawable.arrow_more);
        arrow.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        card.addView(arrow, dp(20), dp(20));

        card.setOnClickListener(v -> presentFragment(new ProfileActivity(UserConfig.getInstance(currentAccount).getClientUserId())));
        return card;
    }

    // ─────────────────────────────────────────────────────────────────
    // Quick toggle section
    // ─────────────────────────────────────────────────────────────────
    private View buildToggleSection(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(roundRect(Theme.getColor(Theme.key_windowBackgroundWhite), 16));
        card.setClipToOutline(true);

        String[][] toggles = {
            {KEY_GHOST_MODE,      "\uD83D\uDC7B", "Ghost"},
            {KEY_NO_TYPING,       "\u270F\uFE0F",  "No Typing"},
            {KEY_MARK_READ,       "\uD83D\uDC40",  "Silent Read"},
            {KEY_NO_SPONSORED,    "\uD83D\uDEAB",  "No Ads"},
            {KEY_DISABLE_STORIES, "\uD83D\uDC94",  "No Stories"},
        };

        // Row
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(dp(8), dp(12), dp(8), dp(12));

        toggleViews.clear();
        for (String[] t : toggles) {
            ToggleView tv = new ToggleView(context, t[0], t[1], t[2]);
            toggleViews.add(tv);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutHelper.WRAP_CONTENT, 1f);
            row.addView(tv, lp);
        }
        card.addView(row);
        return card;
    }

    // ─────────────────────────────────────────────────────────────────
    // Category grid 2×3
    // ─────────────────────────────────────────────────────────────────
    private static final int[][] CATEGORIES = {
        // color top,    color bottom,  icon res (placeholder),  id
    };

    private static final Object[][] CAT_DATA = {
        {"⚙️", "General",    "Ghost, ads, stories...", 0xFF3D8BFF, 0xFF2462D4, 1},
        {"\uD83C\uDFA8", "Appearance", "Coming soon",           0xFFB659FF, 0xFF7C4DFF, 2},
        {"\uD83D\uDCAC", "Chats",      "Coming soon",           0xFF4CAF50, 0xFF2E7D32, 3},
        {"\uD83D\uDD12", "Privacy",    "Duress, fake seen...",  0xFFFF5252, 0xFFD32F2F, 4},
        {"\uD83E\uDD16", "Automation", "Coming soon",           0xFFFF9800, 0xFFF57C00, 5},
        {"\uD83D\uDCCA", "Statistics", "Coming soon",           0xFF00BCD4, 0xFF0097A7, 6},
    };

    private View buildCategoryGrid(Context context) {
        LinearLayout grid = new LinearLayout(context);
        grid.setOrientation(LinearLayout.VERTICAL);

        for (int row = 0; row < 3; row++) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int col = 0; col < 2; col++) {
                int idx = row * 2 + col;
                Object[] d = CAT_DATA[idx];
                View card = buildCatCard(context, (String) d[0], (String) d[1], (String) d[2],
                        (int) d[3], (int) d[4], (int) d[5]);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                        LayoutHelper.WRAP_CONTENT, 1f);
                if (col == 0) lp.rightMargin = dp(8);
                rowLayout.addView(card, lp);
            }

            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
            if (row < 2) rowLp.bottomMargin = dp(8);
            grid.addView(rowLayout, rowLp);
        }
        return grid;
    }

    private View buildCatCard(Context context, String emoji, String title, String sub,
                               int colorTop, int colorBottom, int catId) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(roundRect(Theme.getColor(Theme.key_windowBackgroundWhite), 14));
        card.setClipToOutline(true);

        // Icon circle
        TextView iconView = new TextView(context);
        iconView.setText(emoji);
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        iconView.setGravity(Gravity.CENTER);
        GradientDrawable iconBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{colorTop, colorBottom});
        iconBg.setCornerRadius(dp(12));
        iconView.setBackground(iconBg);
        iconView.setClipToOutline(true);
        card.addView(iconView, dp(44), dp(44));

        // Title
        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        titleView.setTypeface(AndroidUtilities.bold());
        titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleView.setSingleLine();
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        titleLp.topMargin = dp(10);
        card.addView(titleView, titleLp);

        // Subtitle
        TextView subView = new TextView(context);
        subView.setText(sub);
        subView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        subView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        subView.setMaxLines(2);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        subLp.topMargin = dp(2);
        card.addView(subView, subLp);

        card.setOnClickListener(v -> {
            if (catId == 1) {
                presentFragment(new ElliGramGeneralSettingsActivity());
            } else {
                android.widget.Toast.makeText(getParentActivity(),
                        title + " — coming soon \uD83D\uDE80", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        return card;
    }

    // ─────────────────────────────────────────────────────────────────
    // Links card
    // ─────────────────────────────────────────────────────────────────
    private View buildLinksCard(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(roundRect(Theme.getColor(Theme.key_windowBackgroundWhite), 16));
        card.setClipToOutline(true);

        card.addView(buildLinkRow(context, "\uD83D\uDCE2", "Channel", "@gittyblog", true));
        card.addView(buildLinkRow(context, "\uD83D\uDCAC", "Chat", "@gittyblog", false));
        return card;
    }

    private View buildLinkRow(Context context, String emoji, String label, String value, boolean divider) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));

        TextView iconV = new TextView(context);
        iconV.setText(emoji);
        iconV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        row.addView(iconV, dp(32), dp(32));

        TextView labelV = new TextView(context);
        labelV.setText(label);
        labelV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        labelV.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutHelper.WRAP_CONTENT, 1f);
        lp.leftMargin = dp(12);
        row.addView(labelV, lp);

        TextView valV = new TextView(context);
        valV.setText(value);
        valV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        valV.setTextColor(Theme.getColor(Theme.key_changephoneinfo_image2));
        row.addView(valV);

        row.setOnClickListener(v -> Browser.openUrl(getParentActivity(), "https://t.me/gittyblog"));

        if (divider) {
            FrameLayout wrap = new FrameLayout(context);
            wrap.addView(row, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            View div = new View(context);
            div.setBackgroundColor(Theme.getColor(Theme.key_divider));
            FrameLayout.LayoutParams divLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 1);
            divLp.gravity = Gravity.BOTTOM;
            divLp.leftMargin = dp(60);
            wrap.addView(div, divLp);
            return wrap;
        }
        return row;
    }

    // ─────────────────────────────────────────────────────────────────
    // Section label
    // ─────────────────────────────────────────────────────────────────
    private View buildSectionLabel(Context context, String text) {
        TextView tv = new TextView(context);
        tv.setText(text.toUpperCase());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        tv.setTypeface(AndroidUtilities.bold());
        tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        tv.setLetterSpacing(0.08f);
        return tv;
    }

    // ─────────────────────────────────────────────────────────────────
    // Toggle pill view
    // ─────────────────────────────────────────────────────────────────
    private class ToggleView extends LinearLayout {
        private final String key;
        private final TextView emojiView;
        private final TextView labelView;
        private final View dot;

        ToggleView(Context context, String key, String emoji, String label) {
            super(context);
            this.key = key;
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(dp(4), dp(4), dp(4), dp(4));

            // Circle icon
            FrameLayout circle = new FrameLayout(context);
            GradientDrawable circleBg = new GradientDrawable();
            circleBg.setShape(GradientDrawable.OVAL);
            circleBg.setColor(isEnabled(key) ? 0xFF3D8BFF : Theme.getColor(Theme.key_windowBackgroundGray));
            circle.setBackground(circleBg);

            emojiView = new TextView(context);
            emojiView.setText(emoji);
            emojiView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            emojiView.setGravity(Gravity.CENTER);
            circle.addView(emojiView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));
            addView(circle, dp(44), dp(44));

            labelView = new TextView(context);
            labelView.setText(label);
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            labelView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            labelView.setGravity(Gravity.CENTER);
            labelView.setSingleLine();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
            lp.topMargin = dp(4);
            addView(labelView, lp);

            // Active dot
            dot = new View(context);
            GradientDrawable dotBg = new GradientDrawable();
            dotBg.setShape(GradientDrawable.OVAL);
            dotBg.setColor(0xFF3D8BFF);
            dot.setBackground(dotBg);
            dot.setVisibility(isEnabled(key) ? VISIBLE : INVISIBLE);
            LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dp(5), dp(5));
            dotLp.topMargin = dp(3);
            dotLp.gravity = Gravity.CENTER_HORIZONTAL;
            addView(dot, dotLp);

            setOnClickListener(v -> {
                toggle(key);
                refresh();
            });
        }

        void refresh() {
            boolean on = isEnabled(key);
            dot.setVisibility(on ? VISIBLE : INVISIBLE);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(on ? 0xFF3D8BFF : Theme.getColor(Theme.key_windowBackgroundGray));
            ((FrameLayout) getChildAt(0)).setBackground(bg);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────
    private static GradientDrawable roundRect(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(AndroidUtilities.dp(radiusDp));
        return d;
    }

    private static LinearLayout.LayoutParams buildLp(int lDp, int tDp, int rDp, int bDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin  = AndroidUtilities.dp(lDp);
        lp.topMargin   = AndroidUtilities.dp(tDp);
        lp.rightMargin = AndroidUtilities.dp(rDp);
        lp.bottomMargin = AndroidUtilities.dp(bDp);
        return lp;
    }

    private static int dp(int v) { return AndroidUtilities.dp(v); }
}
