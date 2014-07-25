package com.timothyblumberg.autodidacticism.learnthings.user;

import android.content.ContentValues;
import android.text.TextUtils;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;



/**
 * Created by Tim on 7/24/14.
 */
public class UserDAO {

    public static User getUserById(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        } else {
            final String selectionStr = "question_id = ?";
            return getQueryBuilder().withSelection(selectionStr, userId).get();
        }
    }

    public static void save(User user) {

        // See if the group already exists
        final User existingUser = getUserById(user.getUserId());
        if (existingUser != null) {

            // If the group does exists, then update the _id. We need to do this
            // since calling .put() checks for the _id (Long), and not the
            // group_id (String), so that it updates the group fields accordingly.
            user._id = existingUser._id;
        }

        cupboard().withDatabase(App.getWritableDB()).put(user);
    }

    public static String getCurTrue(){
        return cupboard().withDatabase(App.getWritableDB()).get(Globals.curUser).curTrue;
    }

    public static void setCurTrue(String curTrue){
        ContentValues values = new ContentValues(1);
        values.put("curTrue", curTrue);
        cupboard().withDatabase(App.getWritableDB()).update(User.class, values, "user_id = ?", Globals.curUser.getUserId());
    }


    // Private Methods
    private static DatabaseCompartment.QueryBuilder<User> getQueryBuilder() {
        return cupboard().withDatabase(App.getWritableDB()).query(User.class);
    }
}
