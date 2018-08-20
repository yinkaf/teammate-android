package com.mainstreetcode.teammate.model;

import android.annotation.SuppressLint;
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

@SuppressLint("ParcelCreator")
public class Competitor extends CompetitorEntity
        implements
        Competitive,
        Model<Competitor>{

    public static Competitor empty() {
        return new Competitor("", "", "", new N(), new Date());
    }

    public static Competitor empty(Competitive entity) {
        return new Competitor("", "", "", entity, new Date());
    }

    public Competitor(@NonNull String id, String refPath, String tournamentId, Competitive entity, Date created) {
        super(id, refPath, tournamentId, entity, created);
    }

    boolean hasSameType(Competitor other) {
        return getRefType().equals(other.getRefType());
    }

    @Override
    public String getRefType() {
        return entity.getRefType();
    }

    @Override
    public CharSequence getName() {
        return entity.getName();
    }

    @Override
    public void update(Competitor updated) {
        Competitive other = updated.entity;
        if (entity instanceof User && other instanceof User) ((User) entity).update(((User) other));
        if (entity instanceof Team && other instanceof Team) ((Team) entity).update(((Team) other));
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

    public static class N implements Competitive {
        @Override
        public String getId() { return ""; }

        @Override
        public String getRefType() { return ""; }

        @Override
        public String getImageUrl() { return ""; }

        @Override
        public CharSequence getName() { return ""; }
    }

    public static class GsonAdapter
            implements
            JsonSerializer<Competitor>,
            JsonDeserializer<Competitor> {

        private static final String ID = "_id";
        private static final String REF_PATH = "refPath";
        private static final String ENTITY = "entity";
        private static final String TOURNAMENT = "tournament";
        private static final String CREATED = "created";

        @Override
        public JsonElement serialize(Competitor src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.entity.getId());
        }

        @Override
        public Competitor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (json.isJsonPrimitive()) {
                return new Competitor(json.getAsString(), "", "", new N(), new Date());
            }

            JsonObject jsonObject = json.getAsJsonObject();

            String id = ModelUtils.asString(ID, jsonObject);
            String refPath = ModelUtils.asString(REF_PATH, jsonObject);
            String tournament = ModelUtils.asString(TOURNAMENT, jsonObject);
            String created = ModelUtils.asString(CREATED, jsonObject);
            Competitive competitive = context.deserialize(jsonObject.get(ENTITY),
                    User.COMPETITOR_TYPE.equals(refPath) ? User.class : Team.class);

            return new Competitor(id, refPath, tournament, competitive, ModelUtils.parseDate(created));
        }
    }
}
