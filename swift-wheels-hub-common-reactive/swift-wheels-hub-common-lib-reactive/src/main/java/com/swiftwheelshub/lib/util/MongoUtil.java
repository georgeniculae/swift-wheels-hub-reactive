package com.swiftwheelshub.lib.util;

import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

@UtilityClass
public class MongoUtil {

    public static ObjectId getObjectId(String id) {
        return new ObjectId(id);
    }

}
