package imastudio.co.id.firebasedispatcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_scheduler)
    Button btnScheduler;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    //TODO 7 Deklarasi
    FirebaseJobDispatcher mDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //TODO 8 Hubungkan To GPD
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
    }

    @OnClick({R.id.btn_scheduler, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scheduler:
                //TODO 9 Create Method
                startDispatcher();
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cancel:
                cancelDispatcher();
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //TODO 10 Run Method Job
    private String DISPATCHER_TAG = "mydispatcher";
    private String CITY = "Jakarta";

    public void startDispatcher() {
        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString(MyJobService.EXTRAS_CITY, CITY);

        Job myJob = mDispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag(DISPATCHER_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(
                        Constraint.ON_UNMETERED_NETWORK,
                        Constraint.DEVICE_CHARGING)
                .setExtras(myExtrasBundle)
                .build();

        // jika gagal
        // menjadwalkan yang udah ada
        mDispatcher.mustSchedule(myJob);
    }

    public void cancelDispatcher() {
        mDispatcher.cancel(DISPATCHER_TAG);
    }
}