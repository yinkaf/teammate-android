package com.mainstreetcode.teammate.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mainstreetcode.teammate.persistence.entity.CompetitorEntity;
import com.mainstreetcode.teammate.util.ModelUtils;

import java.lang.reflect.Type;
import java.util.Date;

import static com.mainstreetcode.teammate.util.ModelUtils.areNotEmpty;

public class Competitor extends CompetitorEntity
        implements
        Competitive,
        Model<Competitor> {

    public static Competitor empty() {
        return new Competitor("", "", "", "", new EmptyCompetitor(), new Date(), -1, false, false);
    }

    public static Competitor empty(Competitive entity) {
        return new Competitor("", "", "", "", entity, new Date(), -1, false, false);
    }

    public Competitor(@NonNull String id, String refPath, String tournamentId, String gameId,
                      Competitive entity, Date created,
                      int seed, boolean accepted, boolean declined) {
        super(id, refPath, tournamentId, gameId, entity, created, seed, accepted, declined);
    }

    protected Competitor(Parcel in) {
        super(in);
    }

    boolean hasSameType(Competitor other) {
        return getRefType().equals(other.getRefType());
    }

    public Tournament getTournament() { return Tournament.withId(tournamentId); }

    public Game getGame() { return Game.withId(gameId); }

    @Override
    public boolean hasMajorFields() { return areNotEmpty(id, refPath); }

    @Override
    public String getRefType() { return entity.getRefType(); }

    @Override
    public CharSequence getName() { return entity.getName(); }

    @Override
    public Competitive makeCopy() { return entity.makeCopy(); }

    @Override
    public void update(Competitor updated) {
        this.seed = updated.seed;
        this.accepted = updated.accepted;
        this.declined = updated.declined;

        if (!TextUtils.isEmpty(updated.tournamentId)) this.tournamentId = updated.tournamentId;
        if (!TextUtils.isEmpty(updated.gameId)) this.gameId = updated.gameId;

        Competitive other = updated.entity;
        if (entity instanceof User && other instanceof User) ((User) entity).update(((User) other));
        else if (entity instanceof Team && other instanceof Team)
            ((Team) entity).update(((Team) other));
        else entity = updated.entity;
    }

    @Override
    public boolean isEmpty() { return TextUtils.isEmpty(id); }

    @Override
    public String getImageUrl() { return entity.getImageUrl(); }

    @Override
    public String getId() { return id; }

    @Override
    public boolean areContentsTheSame(Identifiable other) {
        if (!(other instanceof Competitor)) return id.equals(other.getId());
        Competitor casted = (Competitor) other;
        return entity.getClass().equals(casted.entity.getClass())
                && entity.getRefType().equals(casted.entity.getRefType())
                && entity.getId().equals(casted.entity.getId());
    }

    @Override
    public int compareTo(@NonNull Competitor competitor) {
        Competitive other = competitor.entity;
        if (entity instanceof User && other instanceof User)
            return ((User) entity).compareTo(((User) other));
        if (entity instanceof Team && other instanceof Team)
            return ((Team) entity).compareTo(((Team) other));
        return 0;
    }

    public static final Parcelable.Creator<Competitor> CREATOR = new Parcelable.Creator<Competitor>() {
        @Override
        public Competitor createFromParcel(Parcel in) {
            return new Competitor(in);
        }

        @Override
        public Competitor[] newArray(int size) {
            return new Competitor[size];
        }
    };


    public static class GsonAdapter
            implements
            JsonSerializer<Competitor>,
            JsonDeserializer<Competitor> {

        private static final String ID = "_id";
        private static final String REF_PATH = "refPath";
        private static final String ENTITY = "entity";
        private static final String TOURNAMENT = "tournament";
        private static final String GAME = "game";
        private static final String CREATED = "created";
        private static final String SEED = "seed";
        private static final String ACCEPTED = "accepted";
        private static final String DECLINED = "declined";

        @Override
        public JsonElement serialize(Competitor src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.entity.getId());
        }

        @Override
        public Competitor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (json.isJsonPrimitive()) {
                return new Competitor(json.getAsString(), "", "", "", new EmptyCompetitor(), new Date(), -1, false, false);
            }

            JsonObject jsonObject = json.getAsJsonObject();

            Tournament tournament = context.deserialize(jsonObject.get(TOURNAMENT), Tournament.class);
            Game game = context.deserialize(jsonObject.get(GAME), Game.class);

            if (tournament == null) tournament = Tournament.empty();
            if (game == null) game = Game.empty(Team.empty());

            String id = ModelUtils.asString(ID, jsonObject);
            String refPath = ModelUtils.asString(REF_PATH, jsonObject);
            String created = ModelUtils.asString(CREATED, jsonObject);
            String tournamentId = tournament.isEmpty() ? null : tournament.getId();
            String gameId = game.isEmpty() ? null : game.getId();

            int seed = (int) ModelUtils.asFloat(SEED, jsonObject);
            boolean accepted = ModelUtils.asBoolean(ACCEPTED, jsonObject);
            boolean declined = ModelUtils.asBoolean(DECLINED, jsonObject);

            Competitive competitive = context.deserialize(jsonObject.get(ENTITY),
                    User.COMPETITOR_TYPE.equals(refPath) ? User.class : Team.class);

            return new Competitor(id, refPath, tournamentId, gameId,
                    competitive, ModelUtils.parseDate(created),
                    seed, accepted, declined);
        }
    }
}
