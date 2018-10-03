package com.earlier.yma.utilities;

import com.earlier.yma.data.SearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class SearchResultDeserializer implements JsonDeserializer<SearchResult> {

  @Override
  public SearchResult deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = (JsonObject) json;
    JsonObject resultObject = jsonObject.get("resultSVO").getAsJsonObject();
    JsonArray detailArray = resultObject.get("orgDVOList").getAsJsonArray();

    SearchResult.Detail[] results = context.deserialize(detailArray, SearchResult.Detail[].class);
    return new SearchResult(results);
  }

}
