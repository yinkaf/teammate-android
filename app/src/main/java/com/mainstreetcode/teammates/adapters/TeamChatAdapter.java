package com.mainstreetcode.teammates.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mainstreetcode.teammates.R;
import com.mainstreetcode.teammates.adapters.viewholders.TeamChatViewHolder;
import com.mainstreetcode.teammates.model.Chat;
import com.mainstreetcode.teammates.model.Team;
import com.mainstreetcode.teammates.model.User;
import com.tunjid.androidbootstrap.core.abstractclasses.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Adapter for {@link Team}
 * <p>
 * Created by Shemanigans on 6/3/17.
 */

public class TeamChatAdapter extends BaseRecyclerViewAdapter<TeamChatViewHolder, TeamChatAdapter.ChatAdapterListener> {
    private final List<Chat> chats;
    private final User signedInUser;

    public TeamChatAdapter(List<Chat> chats, User signedInUser,
                           TeamChatAdapter.ChatAdapterListener listener) {
        super(listener);
        this.chats = chats;
        this.signedInUser = signedInUser;
    }

    @Override
    public TeamChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        @LayoutRes int layoutRes = R.layout.viewholder_chat;
        View itemView = LayoutInflater.from(context).inflate(layoutRes, viewGroup, false);

        return new TeamChatViewHolder(itemView, adapterListener);
    }

    @Override
    public void onBindViewHolder(TeamChatViewHolder viewHolder, int i) {
        int size = chats.size();

        Chat chat = chats.get(i);
        Chat next = i < size - 1 ? chats.get(i + 1) : null;

        User chatUser = chat.getUser();
        boolean hideDetails = (next != null && chatUser.equals(next.getUser()));

        viewHolder.bind(chat, signedInUser.equals(chat.getUser()), !hideDetails);
    }

    @Override
    public void onViewRecycled(TeamChatViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public long getItemId(int position) {
        return chats.get(position).hashCode();
    }

    public interface ChatAdapterListener extends BaseRecyclerViewAdapter.AdapterListener {
        void onChatClicked(Chat chat);
    }
}
