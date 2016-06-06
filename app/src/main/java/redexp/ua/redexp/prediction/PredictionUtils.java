package redexp.ua.redexp.prediction;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.com.google.common.annotations.Beta;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Insert;
import com.google.api.services.prediction.model.Insert2;
import com.google.api.services.prediction.model.Output;
import com.google.api.services.storage.StorageScopes;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import redexp.ua.redexp.Config;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 24.03.2016.
 */
public class PredictionUtils {

    public static final String PROJECT_ID = Config.GOOGLE_PROJECT_ID;
    public static final String APPLICATION_NAME = Config.PREDICTION_APPLICATION_NAME;
    public static final String SERVICE_ACCT_KEYFILE = Config.GOOGLE_SERVICE_ACCT_KEYFILE;
    public static final String SERVICE_ACCT_EMAIL = Config.GOOGLE_SERVICE_ACCT_EMAIL;

    @NonNull
    public static Prediction buildPredictionClient(GoogleCredential _credential, HttpTransport httpTransport, JsonFactory jsonFactory) {
        return new Prediction.Builder(
                httpTransport, jsonFactory, _credential).setApplicationName(APPLICATION_NAME).build();
    }

    public static HttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
//        return GoogleNetHttpTransport.newTrustedTransport();
        return new com.google.api.client.http.javanet.NetHttpTransport();
    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    public static GoogleCredential buildCredential(Context context, HttpTransport httpTransport, JsonFactory jsonFactory) throws IOException, GeneralSecurityException {
        return new GoogleCredential
//                .fromStream(context.getAssets().open(SERVICE_ACCT_KEYFILE), httpTransport)
                .Builder()
                .setTransport(httpTransport)
                .setServiceAccountPrivateKey(
                        SecurityUtils.loadPrivateKeyFromKeyStore(
                                SecurityUtils.getPkcs12KeyStore(),
                                context.getAssets().open(SERVICE_ACCT_KEYFILE),
                                "notasecret", "privatekey", "notasecret"))
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(SERVICE_ACCT_EMAIL)
                .setServiceAccountScopes(Arrays.asList(
                        PredictionScopes.PREDICTION,
                        StorageScopes.DEVSTORAGE_READ_ONLY))
                .build();
    }

    public static Observable<HttpResponse> train(Prediction prediction, String modelId, String storageDataLocation) {
        return Observable.create((Observable.OnSubscribe<HttpResponse>) subscriber -> {
            Insert trainingData = new Insert();
            trainingData.setId(modelId);
            trainingData.setStorageDataLocation(storageDataLocation);
            try {
                HttpResponse response = prediction.trainedmodels().insert(PROJECT_ID, trainingData).executeUnparsed();
                if (response.getStatusCode() == 200) {
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Exception(response.getStatusMessage()));
                }
                response.ignore();
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                subscriber.onError(t);
            }
        });

    }

    public static Observable<Insert2> getTrainingStatus(Prediction prediction, String modelId) {
        return Observable.create((Observable.OnSubscribe<Insert2>) subscriber -> {
            try {
                HttpResponse response = prediction.trainedmodels().get(PROJECT_ID, modelId).executeUnparsed();
                if (response.getStatusCode() == 200) {
                    Insert2 insert2 = response.parseAs(Insert2.class);
                    subscriber.onNext(insert2);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Exception(response.getStatusMessage()));
                }
                response.ignore();
            } catch (IOException e) {
                Exceptions.throwIfFatal(e);
                subscriber.onError(e);
            }
        });
    }

    public static Observable<Insert2> trainAndWhiteUntilDone(Prediction prediction, String modelId, String storageDataLocation) {
        return PredictionUtils.train(prediction, modelId, storageDataLocation)
                .subscribeOn(Schedulers.newThread())
                .concatMap(httpResponse -> getStatusAndWhiteUntilDone(prediction, modelId)
                );

    }

    @NonNull
    public static Observable<Insert2> getStatusAndWhiteUntilDone(Prediction prediction, String modelId) {
        return PredictionUtils.getTrainingStatus(prediction, modelId)
                .repeatWhen(
                        observable -> observable.zipWith(Observable.range(1, 60), (aVoid, attempt) -> attempt)
                                .flatMap((Func1<Integer, Observable<?>>) repeatAttempt -> Observable.timer(5, TimeUnit.SECONDS)))
                .takeUntil(insert2 -> "DONE".equalsIgnoreCase(insert2.getTrainingStatus()))
                .filter(insert2 -> "DONE".equalsIgnoreCase(insert2.getTrainingStatus()));
    }

    public static Observable<Output> predict(Prediction prediction, String modelId, List<Object> params) {
        return Observable.create((Observable.OnSubscribe<Output>) subscriber -> {
            try {
                Input input = new Input();
                Input.InputInput inputInput = new Input.InputInput();
                inputInput.setCsvInstance(params);
                input.setInput(inputInput);
                Output output = prediction.trainedmodels().predict(PROJECT_ID, modelId, input).execute();
                subscriber.onNext(output);
                subscriber.onCompleted();
            } catch (IOException e) {
                Exceptions.throwIfFatal(e);
                subscriber.onError(e);
            }
        });
    }


}
