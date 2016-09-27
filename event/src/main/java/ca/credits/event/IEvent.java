package ca.credits.event;

/**
 * Created by chenwen on 16/8/25.
 */
public interface IEvent {
    interface IEventListener{
        /**
         * when event start run
         */
        void onStart(IEvent event);

        /**
         * when event success run
         */
        void onComplete(IEvent event);

        /**
         * when throw Exception on event running
         */
        void onThrowable(IEvent event,Throwable throwable);
    }
}
