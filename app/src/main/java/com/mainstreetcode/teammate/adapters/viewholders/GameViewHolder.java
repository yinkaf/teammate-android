package com.mainstreetcode.teammate.adapters.viewholders;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mainstreetcode.teammate.R;
import com.mainstreetcode.teammate.adapters.GameAdapter;
import com.mainstreetcode.teammate.model.Competitor;
import com.mainstreetcode.teammate.model.Game;
import com.squareup.picasso.Picasso;
import com.tunjid.androidbootstrap.core.abstractclasses.BaseViewHolder;

import static com.mainstreetcode.teammate.util.ViewHolderUtil.THUMBNAIL_SIZE;


public class GameViewHolder extends BaseViewHolder<GameAdapter.AdapterListener> {

    protected Game model;

    TextView homeText;
    TextView awayText;
    TextView score;
    public ImageView homeThumbnail;
    public ImageView awayThumbnail;

    public GameViewHolder(View itemView, GameAdapter.AdapterListener adapterListener) {
        super(itemView, adapterListener);
        score = itemView.findViewById(R.id.score);
        homeText = itemView.findViewById(R.id.home);
        awayText = itemView.findViewById(R.id.away);
        homeThumbnail = itemView.findViewById(R.id.home_thumbnail);
        awayThumbnail = itemView.findViewById(R.id.away_thumbnail);

        itemView.setOnClickListener(view -> adapterListener.onGameClicked(model));
    }

    public void bind(Game model) {
        this.model = model;
        Competitor home = model.getHome();
        Competitor away = model.getAway();
        Competitor winner = model.getWinner();

        score.setText(model.getScore());
        homeText.setText(home.getName());
        awayText.setText(away.getName());
        homeText.setTypeface(homeText.getTypeface(), home.equals(winner) ? Typeface.BOLD : Typeface.NORMAL);
        awayText.setTypeface(awayText.getTypeface(), away.equals(winner) ? Typeface.BOLD : Typeface.NORMAL);

        String homeUrl = home.getImageUrl();
        String awayUrl = away.getImageUrl();

        if (TextUtils.isEmpty(homeUrl)) homeThumbnail.setImageResource(R.color.dark_grey);
        else Picasso.with(itemView.getContext()).load(homeUrl)
                .resize(THUMBNAIL_SIZE, THUMBNAIL_SIZE).centerInside().into(homeThumbnail);

        if (TextUtils.isEmpty(awayUrl)) awayThumbnail.setImageResource(R.color.dark_grey);
        else Picasso.with(itemView.getContext()).load(awayUrl).
                resize(THUMBNAIL_SIZE, THUMBNAIL_SIZE).centerInside().into(awayThumbnail);
    }
}
