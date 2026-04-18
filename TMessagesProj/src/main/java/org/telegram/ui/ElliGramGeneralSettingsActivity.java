package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.RecyclerListView;

public class ElliGramGeneralSettingsActivity extends BaseFragment {

    private static final int ROW_HEADER_STEALTH    = 0;
    private static final int ROW_GHOST             = 1;
    private static final int ROW_NO_TYPING         = 2;
    private static final int ROW_MARK_READ         = 3;
    private static final int ROW_SHADOW_STEALTH    = 4;
    private static final int ROW_HEADER_CHATS      = 5;
    private static final int ROW_NO_SPONSORED      = 6;
    private static final int ROW_CONFIRM_DELETE    = 7;
    private static final int ROW_DISABLE_STORIES   = 8;
    private static final int ROW_SHADOW_CHATS      = 9;
    private static final int ROW_COUNT             = 10;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK  = 1;
    private static final int TYPE_SHADOW = 2;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle("General");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) finishFragment();
            }
        });

        FrameLayout frame = new FrameLayout(context);
        fragmentView = frame;

        RecyclerListView listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setAdapter(new Adapter(context));
        listView.setOnItemClickListener((view, position) -> {
            if (view instanceof TextCheckCell) {
                String key = keyForRow(position);
                if (key == null) return;
                boolean val = !ElliGramSettingsActivity.isEnabled(key);
                ElliGramSettingsActivity.prefs().edit().putBoolean(key, val).apply();
                ((TextCheckCell) view).setChecked(val);
            }
        });
        frame.addView(listView);
        return fragmentView;
    }

    private String keyForRow(int pos) {
        switch (pos) {
            case ROW_GHOST:           return ElliGramSettingsActivity.KEY_GHOST_MODE;
            case ROW_NO_TYPING:       return ElliGramSettingsActivity.KEY_NO_TYPING;
            case ROW_MARK_READ:       return ElliGramSettingsActivity.KEY_MARK_READ;
            case ROW_NO_SPONSORED:    return ElliGramSettingsActivity.KEY_NO_SPONSORED;
            case ROW_CONFIRM_DELETE:  return ElliGramSettingsActivity.KEY_CONFIRM_DELETE;
            case ROW_DISABLE_STORIES: return ElliGramSettingsActivity.KEY_DISABLE_STORIES;
            default: return null;
        }
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Context ctx;
        Adapter(Context ctx) { this.ctx = ctx; }

        @Override
        public int getItemViewType(int pos) {
            switch (pos) {
                case ROW_HEADER_STEALTH:
                case ROW_HEADER_CHATS:  return TYPE_HEADER;
                case ROW_SHADOW_STEALTH:
                case ROW_SHADOW_CHATS:  return TYPE_SHADOW;
                default:                return TYPE_CHECK;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            View v;
            if (type == TYPE_HEADER) {
                v = new HeaderCell(ctx);
                v.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (type == TYPE_CHECK) {
                v = new TextCheckCell(ctx);
                v.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                v = new ShadowSectionCell(ctx);
            }
            return new RecyclerListView.Holder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
            if (holder.itemView instanceof HeaderCell) {
                HeaderCell h = (HeaderCell) holder.itemView;
                if (pos == ROW_HEADER_STEALTH) h.setText("Stealth");
                if (pos == ROW_HEADER_CHATS)   h.setText("Chats");
            } else if (holder.itemView instanceof TextCheckCell) {
                TextCheckCell c = (TextCheckCell) holder.itemView;
                switch (pos) {
                    case ROW_GHOST:
                        c.setTextAndValueAndCheck("Ghost Mode", "No read receipts + no typing status", ElliGramSettingsActivity.isEnabled(ElliGramSettingsActivity.KEY_GHOST_MODE), true, true);
                        break;
                    case ROW_NO_TYPING:
                        c.setTextAndValueAndCheck("Hide Typing Status", "Others won't see when you type", ElliGramSettingsActivity.isEnabled(ElliGramSettingsActivity.KEY_NO_TYPING), true, true);
                        break;
                    case ROW_MARK_READ:
                        c.setTextAndValueAndCheck("No Read Receipts", "Read without sending blue ticks", ElliGramSettingsActivity.isEnabled(ElliGramSettingsActivity.KEY_MARK_READ), true, false);
                        break;
                    case ROW_NO_SPONSORED:
                        c.setTextAndValueAndCheck("Hide Sponsored Messages", "Remove ads from channels", ElliGramSettingsActivity.isEnabled(ElliGramSettingsActivity.KEY_NO_SPONSORED), true, true);
                        break;
                    case ROW_CONFIRM_DELETE:
                        c.setTextAndValueAndCheck("Confirm Before Delete", "Ask before deleting messages", ElliGramSettingsActivity.isEnabled(ElliGramSettingsActivity.KEY_CONFIRM_DELETE), true, true);
                        break;
                    case ROW_DISABLE_STORIES:
                        c.setTextAndValueAndCheck("Hide Stories Bar", "Remove stories from chat list", ElliGramSettingsActivity.isEnabled(ElliGramSettingsActivity.KEY_DISABLE_STORIES), true, false);
                        break;
                }
            }
        }

        @Override
        public int getItemCount() { return ROW_COUNT; }
    }
}
