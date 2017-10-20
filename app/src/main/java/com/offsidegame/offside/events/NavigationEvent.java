package com.offsidegame.offside.events;

import android.view.MenuItem;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.OffsideApplication;

/**
 * Created by KFIR on 11/20/2016.
 */

public class NavigationEvent {
    private int navigationItemId;

    public NavigationEvent(int navigationItemId) {
        this.navigationItemId = navigationItemId;
    }


    public int getNavigationItemId() {
        return getItemPositionByNavigatinId();
    }

    public int getItemPositionByNavigatinId() {

        switch (navigationItemId) {
            case R.id.nav_action_groups:
                return 0;
            case R.id.nav_action_profile:
                return 1;

            case R.id.nav_action_shop:
                return 2;

            case R.id.nav_action_play:

                return 3;

            case R.id.nav_action_news:

                return 4;
        }

        return -1;


    }
}
