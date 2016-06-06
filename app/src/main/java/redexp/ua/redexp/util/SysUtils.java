package redexp.ua.redexp.util;

import redexp.ua.redexp.listener.Destroyable;
import rx.Subscription;

public class SysUtils {

    public static void destroyIfNotNull(final Destroyable _loaderRx) {
        if (_loaderRx != null) {
            _loaderRx.destroy();
        }
    }

    public static void unsubscribeIfNotNull(final Subscription _subscription) {
        if (_subscription != null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
        }
    }

}
