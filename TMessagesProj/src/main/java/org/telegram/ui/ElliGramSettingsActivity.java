package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
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

public class ElliGramSettingsActivity extends BaseFragment {

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
        scroll.setPadding(0, 0, 0, dp(20));
        root.addView(scroll, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        // Profile card
        content.addView(buildProfileCard(context), lp(16, 16, 16, 8));

        // Features grid
        content.addView(sectionLabel(context, "Features"), lp(16, 12, 16, 6));
        content.addView(buildGrid(context), lp(16, 0, 16, 8));

        // Links
        content.addView(sectionLabel(context, "Links"), lp(16, 8, 16, 6));
        content.addView(buildLinksCard(context), lp(16, 0, 16, 0));

        // Footer
        TextView footer = new TextView(context);
        footer.setText("ElliGram v" + BuildVars.BUILD_VERSION_STRING);
        footer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        footer.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        footer.setGravity(Gravity.CENTER);
        content.addView(footer, lp(0, 16, 0, 4));

        fragmentView = root;
        return root;
    }

    // ── Profile card ──────────────────────────────────────────────────
    private View buildProfileCard(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(roundRect(Theme.getColor(Theme.key_windowBackgroundWhite), 16));
        card.setClipToOutline(true);

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

        LinearLayout textCol = new LinearLayout(context);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setPadding(dp(14), 0, 0, 0);

        TextView nameView = new TextView(context);
        if (user != null) {
            String name = user.first_name != null ? user.first_name : "";
            if (user.last_name != null && !user.last_name.isEmpty()) name += " " + user.last_name;
            nameView.setText(name);
        }
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

        ImageView arrow = new ImageView(context);
        arrow.setImageResource(R.drawable.ic_action_next);
        arrow.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        card.addView(arrow, dp(20), dp(20));

        card.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("user_id", UserConfig.getInstance(currentAccount).getClientUserId());
            presentFragment(new ProfileActivity(args));
        });
        return card;
    }

    // ── Category grid 2×3 ────────────────────────────────────────────
    private static final Object[][] CAT = {
        {"⚙️", "General",    "Ghost, ads, stories...", 0xFF3D8BFF, 0xFF2462D4, 1},
        {"\uD83C\uDFA8", "Appearance", "Themes, fonts",          0xFFB659FF, 0xFF7C4DFF, 2},
        {"\uD83D\uDCAC", "Chats",      "Formatting, bubbles",    0xFF4CAF50, 0xFF2E7D32, 3},
        {"\uD83D\uDD12", "Privacy",    "Duress, fake seen",      0xFFFF5252, 0xFFD32F2F, 4},
        {"\uD83E\uDD16", "Automation", "Auto-reply, schedule",   0xFFFF9800, 0xFFF57C00, 5},
        {"\uD83D\uDCCA", "Statistics", "Usage, activity",        0xFF00BCD4, 0xFF0097A7, 6},
    };

    private View buildGrid(Context context) {
        LinearLayout grid = new LinearLayout(context);
        grid.setOrientation(LinearLayout.VERTICAL);

        for (int row = 0; row < 3; row++) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int col = 0; col < 2; col++) {
                Object[] d = CAT[row * 2 + col];
                View card = buildCatCard(context, (String)d[0], (String)d[1],
                        (String)d[2], (int)d[3], (int)d[4], (int)d[5]);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutHelper.WRAP_CONTENT, 1f);
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

    private View buildCatCard(Context context, String emoji, String title,
                               String sub, int colorTop, int colorBot, int catId) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(roundRect(Theme.getColor(Theme.key_windowBackgroundWhite), 14));
        card.setClipToOutline(true);

        TextView iconView = new TextView(context);
        iconView.setText(emoji);
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        iconView.setGravity(Gravity.CENTER);
        GradientDrawable iconBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{colorTop, colorBot});
        iconBg.setCornerRadius(dp(12));
        iconView.setBackground(iconBg);
        iconView.setClipToOutline(true);
        card.addView(iconView, dp(44), dp(44));

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

        TextView subView = new TextView(context);
        subView.setText(sub);
        subView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        subView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        subView.setMaxLines(2);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        subLp.topMargin = dp(2);
        card.addView(subView, subLp);

        card.setOnClickListener(v ->
            android.widget.Toast.makeText(getParentActivity(),
                title + " — coming soon", android.widget.Toast.LENGTH_SHORT).show()
        );
        return card;
    }

    // ── Links ─────────────────────────────────────────────────────────
    private View buildLinksCard(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(roundRect(Theme.getColor(Theme.key_windowBackgroundWhite), 16));
        card.setClipToOutline(true);
        card.addView(linkRow(context, "\uD83D\uDCE2", "Channel", "@gittyblog", true));
        card.addView(linkRow(context, "\uD83D\uDCAC", "Chat",    "@gittyblog", false));
        return card;
    }

    private View linkRow(Context context, String emoji, String label, String val, boolean divider) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));

        TextView e = new TextView(context);
        e.setText(emoji);
        e.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        row.addView(e, dp(32), dp(32));

        TextView l = new TextView(context);
        l.setText(label);
        l.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        l.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutHelper.WRAP_CONTENT, 1f);
        lp.leftMargin = dp(12);
        row.addView(l, lp);

        TextView v = new TextView(context);
        v.setText(val);
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        v.setTextColor(Theme.getColor(Theme.key_changephoneinfo_image2));
        row.addView(v);

        row.setOnClickListener(x -> Browser.openUrl(getParentActivity(), "https://t.me/gittyblog"));

        if (divider) {
            FrameLayout wrap = new FrameLayout(context);
            wrap.addView(row, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            View div = new View(context);
            div.setBackgroundColor(Theme.getColor(Theme.key_divider));
            FrameLayout.LayoutParams dp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 1);
            dp.gravity = Gravity.BOTTOM;
            dp.leftMargin = dp(60);
            wrap.addView(div, dp);
            return wrap;
        }
        return row;
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private View sectionLabel(Context context, String text) {
        TextView tv = new TextView(context);
        tv.setText(text.toUpperCase());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        tv.setTypeface(AndroidUtilities.bold());
        tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        tv.setLetterSpacing(0.08f);
        return tv;
    }

    private static GradientDrawable roundRect(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(AndroidUtilities.dp(radiusDp));
        return d;
    }

    private static LinearLayout.LayoutParams lp(int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.leftMargin = AndroidUtilities.dp(l);
        p.topMargin = AndroidUtilities.dp(t);
        p.rightMargin = AndroidUtilities.dp(r);
        p.bottomMargin = AndroidUtilities.dp(b);
        return p;
    }

    private static int dp(int v) { return AndroidUtilities.dp(v); }
}
