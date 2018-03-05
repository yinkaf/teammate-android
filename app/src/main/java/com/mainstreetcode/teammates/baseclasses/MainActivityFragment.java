package com.mainstreetcode.teammates.baseclasses;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mainstreetcode.teammates.activities.MainActivity;
import com.mainstreetcode.teammates.model.Message;
import com.mainstreetcode.teammates.model.Team;
import com.mainstreetcode.teammates.util.ScrollManager;
import com.mainstreetcode.teammates.viewmodel.ChatViewModel;
import com.mainstreetcode.teammates.viewmodel.EventViewModel;
import com.mainstreetcode.teammates.viewmodel.FeedViewModel;
import com.mainstreetcode.teammates.viewmodel.LocalRoleViewModel;
import com.mainstreetcode.teammates.viewmodel.LocationViewModel;
import com.mainstreetcode.teammates.viewmodel.MediaViewModel;
import com.mainstreetcode.teammates.viewmodel.RoleViewModel;
import com.mainstreetcode.teammates.viewmodel.TeamMemberViewModel;
import com.mainstreetcode.teammates.viewmodel.TeamViewModel;
import com.mainstreetcode.teammates.viewmodel.UserViewModel;

/**
 * Class for Fragments in {@link com.mainstreetcode.teammates.activities.MainActivity}
 */

public class MainActivityFragment extends TeammatesBaseFragment {

    protected ScrollManager scrollManager;
    protected FeedViewModel feedViewModel;
    protected RoleViewModel roleViewModel;
    protected UserViewModel userViewModel;
    protected TeamViewModel teamViewModel;
    protected EventViewModel eventViewModel;
    protected MediaViewModel mediaViewModel;
    protected ChatViewModel chatViewModel;
    protected LocationViewModel locationViewModel;
    protected LocalRoleViewModel localRoleViewModel;
    protected TeamMemberViewModel teamMemberViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localRoleViewModel = ViewModelProviders.of(this).get(LocalRoleViewModel.class);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onAttach(Context context) {
        super.onAttach(context);
        ViewModelProvider provider = ViewModelProviders.of(getActivity());
        feedViewModel = provider.get(FeedViewModel.class);
        roleViewModel = provider.get(RoleViewModel.class);
        userViewModel = provider.get(UserViewModel.class);
        teamViewModel = provider.get(TeamViewModel.class);
        eventViewModel = provider.get(EventViewModel.class);
        mediaViewModel = provider.get(MediaViewModel.class);
        chatViewModel = provider.get(ChatViewModel.class);
        locationViewModel = provider.get(LocationViewModel.class);
        teamMemberViewModel = provider.get(TeamMemberViewModel.class);

        defaultErrorHandler.addAction(() -> {if (scrollManager != null) scrollManager.reset();});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scrollManager != null) scrollManager.clear();
    }

    @Override
    protected void handleErrorMessage(Message message) {
        if (message.isUnauthorizedUser()) signOut();
        else super.handleErrorMessage(message);

        boolean isIllegalTeamMember = message.isIllegalTeamMember();
        boolean shouldGoBack = isIllegalTeamMember || message.isInvalidObject();

        if (isIllegalTeamMember) teamViewModel.updateDefaultTeam(Team.empty());

        Activity activity = getActivity();
        if (activity == null) return;

        if (shouldGoBack) activity.onBackPressed();
    }

    protected void toggleBottomSheet(boolean show) {
        PersistentUiController controller = getPersistentUiController();
        if (controller instanceof BottomSheetController) {
            ((BottomSheetController) controller).toggleBottomSheet(show);
        }
    }

    protected void signOut() {
        userViewModel.signOut().subscribe(
                success -> MainActivity.startRegistrationActivity(getActivity()),
                throwable -> MainActivity.startRegistrationActivity(getActivity())
        );
    }
}
