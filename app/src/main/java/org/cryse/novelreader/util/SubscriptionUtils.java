package org.cryse.novelreader.util;

import rx.Subscription;

public class SubscriptionUtils {
    public static void checkAndUnsubscribe(Subscription subscription){
        if(subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
