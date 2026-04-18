package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.RecyclerListView;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ElliGramSettingsActivity extends BaseFragment {

    private static final String PREFS = "elligram_settings";

    public static final String KEY_GHOST_MODE       = "ghost_mode";
    public static final String KEY_NO_SPONSORED     = "no_sponsored";
    public static final String KEY_CONFIRM_DELETE   = "confirm_delete";
    public static final String KEY_DISABLE_STORIES  = "disable_stories";

    private RecyclerListView listView;
    private Adapter adapter;

    private static final int ROW_GHOST          = 0;
    private static final int ROW_NO_SPONSORED   = 1;
    private static final int ROW_CONFIRM_DELETE = 2;
    private static final int ROW_STORIES        = 3;
    private static final int ROW_INFO           = 4;
    private static final int ROW_COUNT          = 5;

    public static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(String key) {
        return prefs().getBoolean(key, false);
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle("ElliGram");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) finishFragment();
            }
        });

        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setAdapter(adapter = new Adapter(context));
        listView.setOnItemClickListener((view, position) -> {
            if (view instanceof TextCheckCell) {
                TextCheckCell cell = (TextCheckCell) view;
                String key = keyForRow(position);
                if (key == null) return;
                boolean newVal = !isEnabled(key);
                prefs().edit().putBoolean(key, newVal).apply();
                cell.setChecked(newVal);
            }
        });

        frameLayout.addView(listView);
        return fragmentView;
    }

    private String keyForRow(int position) {
        switch (position) {
            case ROW_GHOST:          return KEY_GHOST_MODE;
            case ROW_NO_SPONSORED:   return KEY_NO_SPONSORED;
            case ROW_CONFIRM_DELETE: return KEY_CONFIRM_DELETE;
            case ROW_STORIES:        return KEY_DISABLE_STORIES;
            default: return null;
        }
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_CHECK = 0;
        private static final int TYPE_INFO  = 1;

        private final Context context;

        Adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            return position == ROW_INFO ? TYPE_INFO : TYPE_CHECK;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_INFO) {
                view = new TextInfoPrivacyCell(context);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            } else {
                view = new TextCheckCell(context);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof TextCheckCell) {
                TextCheckCell cell = (TextCheckCell) holder.itemView;
                switch (position) {
                    case ROW_GHOST:
                        cell.setTextAndValueAndCheck("Ghost Mode", "Read messages without sending read receipts", isEnabled(KEY_GHOST_MODE), true, true);
                        break;
                    case ROW_NO_SPONSORED:
                        cell.setTextAndValueAndCheck("Hide Sponsored Messages", "Remove all sponsored messages from channels", isEnabled(KEY_NO_SPONSORED), true, true);
                        break;
                    case ROW_CONFIRM_DELETE:
                        cell.setTextAndValueAndCheck("Confirm Before Delete", "Always ask before deleting messages", isEnabled(KEY_CONFIRM_DELETE), true, true);
                        break;
                    case ROW_STORIES:
                        cell.setTextAndValueAndCheck("Hide Stories Bar", "Remove stories bar from the chat list", isEnabled(KEY_DISABLE_STORIES), true, true);
                        break;
                }
            } else if (holder.itemView instanceof TextInfoPrivacyCell) {
                ((TextInfoPrivacyCell) holder.itemView).setText("ElliGram v" + org.telegram.messenger.BuildVars.BUILD_VERSION_STRING);
            }
        }

        @Override
        public int getItemCount() {
            return ROW_COUNT;
        }
    }
}
