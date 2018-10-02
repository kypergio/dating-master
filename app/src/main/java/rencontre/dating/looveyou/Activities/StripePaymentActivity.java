package rencontre.dating.looveyou.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import rencontre.dating.looveyou.R;
import rencontre.dating.looveyou.Services.NotificationUtils;
import rencontre.dating.looveyou.Utils.Const;
import rencontre.dating.looveyou.Utils.HelperFunctions;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anurag on 4/10/2017.
 */

public class StripePaymentActivity extends AppCompatActivity{

    private CardInputWidget mCardInputWidget;
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        getSupportActionBar().setTitle("Card Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        LinearLayout cont = (LinearLayout) findViewById(R.id.continue_layout);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card = mCardInputWidget.getCard();
                if (card == null) {
                    HelperFunctions.ShowSimpleDialogMessage(StripePaymentActivity.this, "Card Error", "You have entered invalid card details");
                    return;
                }

                if(!card.validateNumber()){
                    HelperFunctions.ShowSimpleDialogMessage(StripePaymentActivity.this, "Card Error", "Card Validation Failed");
                    return;
                }

                if(!card.validateCVC()){
                    HelperFunctions.ShowSimpleDialogMessage(StripePaymentActivity.this, "Card Error", "CVC Verification Failed");
                    return;
                }

                try{
                    final ProgressDialog progressDialog = new ProgressDialog(StripePaymentActivity.this);
                    progressDialog.setTitle("Creating session");
                    progressDialog.setMessage("Please wait while creating session");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    /**
                     * Replace the API key with the publishable API key.
                     * This is simply test API key.
                     */
                    Stripe stripe = new Stripe(StripePaymentActivity.this, "pk_test_6pRNASCoBOKtIshFeQd4XMUh");
                    stripe.createToken(card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    progressDialog.dismiss();
                                    //Send the token to the backend server.
                                }
                                public void onError(Exception error) {
                                    // Show localized error message
                                    progressDialog.dismiss();
                                    HelperFunctions.ShowSimpleDialogMessage(StripePaymentActivity.this, "Unknown Error", "" + error.toString());
                                }
                            }
                    );
                }catch(Exception e){
                    HelperFunctions.ShowSimpleDialogMessage(StripePaymentActivity.this, "Auth Error", "Authorization Failed, try again.");
                }
            }
        });
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.Params.FCM_PUSHING_MESSAGE)) {
                    try {
                        String data_ = intent.getStringExtra("data");
                        JSONObject data = new JSONObject(data_);
                        NotificationUtils.MostrarNotificacion(StripePaymentActivity.this, data);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(Const.Params.FCM_PUSHING_MESSAGE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity.
        }
        return super.onOptionsItemSelected(item);
    }
}
