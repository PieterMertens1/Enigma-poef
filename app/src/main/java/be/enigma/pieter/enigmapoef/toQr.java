package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class toQr extends AppCompatActivity {


    ImageView imageView;
    Button backButton;
    TextView textView;
    String EditTextValue ;
    Thread thread ;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;


    String gebruiker;
    String hoeveelheid;
    String reden;
    String tijd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_qr);


        imageView = (ImageView)findViewById(R.id.imageView);
        textView = (TextView)findViewById(R.id.editText);
        backButton = (Button)findViewById(R.id.BackButton);

    }



    @Override
    public void onStart() {

        super.onStart();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            gebruiker = extras.getString("Gebruiker");
            hoeveelheid = extras.getString("Hoeveelheid");
            reden = extras.getString("Reden");
            tijd = extras.getString("Tijd");
        }
        else {
            gebruiker = "fout";
            hoeveelheid = "fout";
            reden = "fout";
            tijd = "fout";
        }


        //hier moeten alle gegevens van de op te slagen poef inkomen
        textView.setText(gebruiker + "%" + hoeveelheid + "%"  + reden + "%"  + tijd );

        EditTextValue = textView.getText().toString();

        try {
            bitmap = TextToImageEncode(EditTextValue);

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        //getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
                        getResources().getColor(R.color.common_google_signin_btn_text_dark_focused):getResources().getColor(R.color.cardview_light_background);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    public void Backbutton_onclick(View view) {
        Intent intent = new Intent(this, Mainpage.class);
        startActivity(intent);

    }
}
