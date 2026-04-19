package org.telegram.ui;

import android.view.View;

import org.telegram.messenger.ElliGramConfig;
import org.telegram.messenger.R;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class ElliGramGeneralSettingsActivity extends UniversalFragment {

    private static final int ID_STORIES      = 1;
    private static final int ID_ONLINE_COUNT = 3;
    private static final int ID_GIFT_BUTTON  = 4;
    private static final int ID_BOOST_BUTTON = 5;
    private static final int ID_SPONSORED    = 6;

    @Override
    protected CharSequence getTitle() {
        return "General";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Interface"));
        items.add(checkItem(ID_STORIES,      R.drawable.settings_channel,  "Stories Bar",        "Show stories at the top of chat list",      ElliGramConfig.showStories()));
        items.add(checkItem(ID_ONLINE_COUNT, R.drawable.settings_chat,     "Online Count",       "Show online member count in group header",   ElliGramConfig.showOnlineCount()));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Monetization"));
        items.add(checkItem(ID_GIFT_BUTTON,  R.drawable.settings_premium,  "Gift Button",        "Show gift button in profiles and channels",  ElliGramConfig.showGiftButton()));
        items.add(checkItem(ID_BOOST_BUTTON, R.drawable.settings_stars,    "Boost Button",       "Show boost button in channels",              ElliGramConfig.showBoostButton()));
        items.add(checkItem(ID_SPONSORED,    R.drawable.settings_data,     "Sponsored Messages", "Show ads in channels",                       ElliGramConfig.showSponsored()));
        items.add(UItem.asShadow("Changes take effect after restarting the app."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        switch (item.id) {
            case ID_STORIES:      toggle(ElliGramConfig.SHOW_STORIES,      item); break;

            case ID_ONLINE_COUNT: toggle(ElliGramConfig.SHOW_ONLINE_COUNT, item); break;
            case ID_GIFT_BUTTON:  toggle(ElliGramConfig.SHOW_GIFT_BUTTON,  item); break;
            case ID_BOOST_BUTTON: toggle(ElliGramConfig.SHOW_BOOST_BUTTON, item); break;
            case ID_SPONSORED:    toggle(ElliGramConfig.SHOW_SPONSORED,    item); break;
        }
    }

    private void toggle(String key, UItem item) {
        boolean newVal = !ElliGramConfig.get(key, true);
        ElliGramConfig.set(key, newVal);
        listView.adapter.update(true);
    }

    private UItem checkItem(int id, int icon, String title, String subtitle, boolean checked) {
        UItem item = UItem.asCheck(id, title);
        item.iconResId = icon;
        item.subtext   = subtitle;
        item.checked   = checked;
        return item;
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
