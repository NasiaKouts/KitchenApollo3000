package aueb.nasia_kouts.gr.kitchenapollo;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnDoubleClickListener implements OnClickListener {

    // The time in which the second tap should be done in order to qualify as
    // a double click
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;

    public OnDoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public OnDoubleClickListener(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }

    public boolean isDoubleClick(){
        return (SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis;
    }

    public void updateTime(){
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick(View v);

}