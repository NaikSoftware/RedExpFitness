package redexp.ua.redexp.prediction;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.model.Output;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import redexp.ua.redexp.BuildConfig;
import rx.Observable;

/**
 * Use this class to prepare and working with PredictionApi
 * Use like this:
 *
 * try {
 *          PredictionManager predictionManager = PredictionManager.getInstance(getContext());
 *          predictionManager.predictVdot("1500","510000") // VDOT = 30
 *                  .subscribeOn(Schedulers.newThread())
 *                  .observeOn(AndroidSchedulers.mainThread())
 *                  .subscribe(new Observer<Output>() {
 *                      @Override
 *                      public void onCompleted() {
 *                          Log.d("prediction", "onCompleted");
 *                      }
 *
 *                      @Override
 *                      public void onError(Throwable e) {
 *                          Log.e("prediction", "onError", e);
 *                      }
 *
 *                      @Override
 *                      public void onNext(Output output) {
 *                          Log.e("prediction", "onNext: " + output);
 *                      }
 *                  });
 *      } catch (GeneralSecurityException e) {
 *          e.printStackTrace();
 *      } catch (IOException e) {
 *          e.printStackTrace();
 *      }
 *
 * Created on 23.03.2016.
 */
public class PredictionManager {

    private static final String APPLICATION_NAME = BuildConfig.APPLICATION_ID;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    static final String VDOT_STORAGE_DATA_LOCATION = "redexp_bucket/vdot_training_data.txt";
    static final String VDOT_MODEL_ID = "vdot_model";
    static final String REC_STORAGE_DATA_LOCATION = "redexp_bucket/recomend_training_data.txt";
    static final String REC_MODEL_ID = "rec_model";


    private final HttpTransport mHttpTransport;
    private final GoogleCredential mGoogleCredential;
    private final Prediction mPrediction;

    private static PredictionManager sInstance;

    public static PredictionManager getInstance(Context context) throws GeneralSecurityException, IOException {
        if (sInstance == null){
            sInstance = new PredictionManager(context);
        }
        return sInstance;
    }

    private PredictionManager(Context context) throws GeneralSecurityException, IOException {
        mHttpTransport = PredictionUtils.getHttpTransport();
        mGoogleCredential = PredictionUtils.buildCredential(context, mHttpTransport, JSON_FACTORY);
        mPrediction = PredictionUtils.buildPredictionClient(mGoogleCredential, mHttpTransport, JSON_FACTORY);
    }

    public Observable<Output> predictVdot(String distanceMeter, String timeMillis) {
        return getPrediction(Arrays.asList(distanceMeter, timeMillis), VDOT_MODEL_ID, VDOT_STORAGE_DATA_LOCATION);
    }

    public Observable<Output> requestRecomendation(String vdot) {
        return getPrediction(Arrays.asList(vdot, "L-temp"), REC_MODEL_ID, REC_STORAGE_DATA_LOCATION);
    }

    @NonNull
    private Observable<Output> getPrediction(List<Object> params, String modelId, String storageDataLocation) {
        return PredictionUtils.getTrainingStatus(mPrediction, modelId)
                .flatMap(insert2 -> {
                   String status = insert2.getTrainingStatus();
                    switch (status) {
                        case "DONE": return PredictionUtils.predict(mPrediction, modelId, params);
                        case "RUNNING": return PredictionUtils.getStatusAndWhiteUntilDone(mPrediction, modelId)
                                .flatMap(insert21 -> PredictionUtils.predict(mPrediction, modelId, params));
                    }
                    return Observable.error(new Exception("Unknown error while getting model status. status = " + status));
                })
                .onErrorResumeNext(t -> {
                    if (t instanceof GoogleJsonResponseException && ((GoogleJsonResponseException) t).getStatusCode() == 404) {
                        return PredictionUtils.trainAndWhiteUntilDone(mPrediction, modelId, storageDataLocation)
                                .flatMap(insert21 -> PredictionUtils.predict(mPrediction, modelId, params));
                    }
                    return Observable.error(t);
                });
    }


}
